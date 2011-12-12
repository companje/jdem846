/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.color.ColorRegistry;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.gfx.Line;
import us.wthr.jdem846.render.gfx.Renderable;
import us.wthr.jdem846.render.gfx.Square;
import us.wthr.jdem846.render.gfx.StaticPolygonList;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.gfx.ViewportBuffer;

@DemEngine(name="us.wthr.jdem846.render.demEngine3D.name", identifier="dem3d-gen", usesProjection=true, enabled=true)
public class Dem3dGenerator extends BasicRenderEngine
{
	private static Log log = Logging.getLog(Dem3dGenerator.class);
	private static final int COUNTERCLOCKWISE = -1;
	private static final int UNDEFINED = 0;
	private static final int CLOCKWISE = 1;
	
	public Dem3dGenerator()
	{
		super();
	}
	
	
	public Dem3dGenerator(ModelContext modelContext)
	{
		super(modelContext);
	}
	
	//public Dem3dGenerator(DataPackage dataPackage, ModelOptions modelOptions)
	//{
	//	super(dataPackage, modelOptions);
	//}

	@Override
	public OutputProduct<ModelCanvas> generate() throws RenderEngineException
	{
		try {
			return generate(false, false);
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
	}
	
	@Override
	public OutputProduct<ModelCanvas> generate(boolean skipElevation, boolean skipShapes) throws RenderEngineException
	{
		Dem2dGenerator dem2d = new Dem2dGenerator(getModelContext());
		
		OutputProduct<ModelCanvas> product2d = dem2d.generate(skipElevation, skipShapes);
		ModelCanvas canvas2d = product2d.getProduct();
		
		RasterDataContext rasterDataContext = getRasterDataContext();
		ModelOptions modelOptions = getModelOptions();
		
		/*
		// Testing an orthoimage overlay...
		DemCanvas canvas2d = null;
		try {
			File input = new File("C:/srv/elevation/Pawtuckaway II//13353220-scaled.jpg");
			BufferedImage testImage = ImageIO.read(input);
			canvas2d = new DemCanvas(testImage);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RenderEngineException("Failed to load orthoimage", ex);
		}
		*/
		
		DemPoint point = new DemPoint();
		
		double[] vectorFrontLeft = new double[3];
		double[] vectorFrontRight = new double[3];
		double[] vectorBackLeft = new double[3];
		double[] vectorBackRight = new double[3];
		
		double elevationMultiple = modelOptions.getElevationMultiple();
		double elevationMax = rasterDataContext.getDataMaximumValue() * elevationMultiple;//dataPackage.getMaxElevation() * elevationMultiple;
		double elevationMin = rasterDataContext.getDataMinimumValue() * elevationMultiple;
		double elevationDelta = elevationMax - elevationMin;
		double resolution = (rasterDataContext.getLatitudeResolution() + rasterDataContext.getLongitudeResolution()) / 2.0;
		
		
		double startZ =  -(rasterDataContext.getDataRows() / 2.0);
		double startX = -(rasterDataContext.getDataColumns() / 2.0);
		
		
		double rotateX = modelOptions.getProjection().getRotateX();
		double rotateY = modelOptions.getProjection().getRotateY();

		double[] eyeVector = {0, 0, rasterDataContext.getDataColumns()};
		double[] nearVector = {0, 0, (rasterDataContext.getDataColumns()/2.0f)};
		
		BufferedImage image = new BufferedImage(rasterDataContext.getDataColumns(),  rasterDataContext.getDataRows(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		//g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		if (modelOptions.isAntialiased()) {
			log.info("Enabling antialiased rendering");
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
		//BufferedImage lineBuffer = new BufferedImage((int)dataPackage.getColumns(), (int) dataPackage.getRows(), BufferedImage.TYPE_INT_ARGB);
		//Graphics2D g2dLineBuffer = (Graphics2D) image.getGraphics();
		//g2dLineBuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		Color backgroundColor = getDefinedColor(modelOptions.getBackgroundColor());
		
		g2d.setColor(new Color(0, 0, 0, 0));//getDefinedColor(modelOptions.getBackgroundColor()));
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		//DemCanvas canvas3d = new DemCanvas(dataPaka)
		
		int[] xPoints = new int[4];
		int[] yPoints = new int[4];
		Path2D.Double path = new Path2D.Double();
		
		log.info("Projecting image onto 3D model");
		for (int row = 0; row < rasterDataContext.getDataRows() - 1; row++) {
	//	for (int row = (int)dataPackage.getRows() - 2; row >= 0; row--) {
			//resetImage(lineBuffer);
			
			for (int column = 0; column < rasterDataContext.getDataColumns() - 1; column++) {
				try {
					getPoint(row, column, 1, point);
				} catch (DataSourceException ex) {
					throw new RenderEngineException("Error loading elevation data: " + ex.getMessage(), ex);
				}
				
				//if (point.getCondition() == DemConstants.STAT_SUCCESSFUL) {
					double x = (rasterDataContext.getDataColumns() - column) + startX;
					double z = row + startZ;
					

					double yBL = ((point.getBackLeftElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);
					double yBR = ((point.getBackRightElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);
					double yFL = ((point.getFrontLeftElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);
					double yFR = ((point.getFrontRightElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);

					vectorBackLeft[0] = x;
					vectorBackLeft[1] = yBL;
					vectorBackLeft[2] = z;

					vectorFrontLeft[0] = x;
					vectorFrontLeft[1] = yFL;
					vectorFrontLeft[2] = z+1;

					vectorFrontRight[0] = x+1;
					vectorFrontRight[1] = yFR;
					vectorFrontRight[2] = z+1;
					
					vectorBackRight[0] = x+1;
					vectorBackRight[1] = yBR;
					vectorBackRight[2] = z;
					
					Vector.rotate(0, rotateY, 0, vectorBackLeft);
					Vector.rotate(rotateX, 0, 0, vectorBackLeft);
					
					Vector.rotate(0, rotateY, 0, vectorBackRight);
					Vector.rotate(rotateX, 0, 0, vectorBackRight);
					
					Vector.rotate(0, rotateY, 0, vectorFrontLeft);
					Vector.rotate(rotateX, 0, 0, vectorFrontLeft);
					
					Vector.rotate(0, rotateY, 0, vectorFrontRight);
					Vector.rotate(rotateX, 0, 0, vectorFrontRight);
					
					
					projectTo(vectorBackLeft, eyeVector, nearVector);
					projectTo(vectorBackRight, eyeVector, nearVector);
					projectTo(vectorFrontLeft, eyeVector, nearVector);
					projectTo(vectorFrontRight, eyeVector, nearVector);
					
					
					//if (CLOCKWISE == pointOrder(vectorBackLeft, vectorFrontLeft, vectorFrontRight, vectorBackRight)) {
					//	continue;
					//}
					
					vectorBackLeft[0] -= startX;
					vectorBackRight[0] -= startX;
					vectorFrontLeft[0] -= startX;
					vectorFrontRight[0] -= startX;
					
					vectorBackLeft[1] -= startZ;
					vectorBackRight[1] -= startZ;
					vectorFrontLeft[1] -= startZ;
					vectorFrontRight[1] -= startZ;
					
					//pointOrder(double[] ... vectors)
					
					//if (!isFacingCamera(vectorBackLeft, vectorFrontLeft, vectorFrontRight, vectorBackRight)) {
					//	continue;
					//}
					
					
					g2d.setColor(new Color(canvas2d.getColor(column, row)));
					
					
					xPoints[0] = (int) Math.floor(vectorBackLeft[0]);
					xPoints[1] = (int) Math.floor(vectorFrontLeft[0]);
					xPoints[2] = (int) Math.ceil(vectorFrontRight[0]);
					xPoints[3] = (int) Math.ceil(vectorBackRight[0]);
					
					yPoints[0] = (int) Math.floor(vectorBackLeft[1]);
					yPoints[1] = (int) Math.ceil(vectorFrontLeft[1]);
					yPoints[2] = (int) Math.ceil(vectorFrontRight[1]);
					yPoints[3] = (int) Math.floor(vectorBackRight[1]);
					
					
					if (xPoints[0] >= xPoints[3])
						xPoints[3] += 1;
					if (xPoints[1] >= xPoints[2])
						xPoints[2] += 1;
					
					if (yPoints[0] >= yPoints[1])
						yPoints[1] += 1;
					if (yPoints[3] >= yPoints[2])
						yPoints[2] += 1;
					
					
					//g2d.fillPolygon(xPoints, yPoints, 4);
					
					/*
					
					*/
					
					path.reset();
					/*
					path.moveTo(vectorBackLeft[0], vectorBackLeft[1]);
					path.lineTo(vectorFrontLeft[0], vectorFrontLeft[1]);
					path.lineTo(vectorFrontRight[0], vectorFrontRight[1]);
					path.lineTo(vectorBackRight[0], vectorBackRight[1]);
					*/
					
					path.moveTo(xPoints[0], yPoints[0]);
					path.lineTo(xPoints[1], yPoints[1]);
					path.lineTo(xPoints[2], yPoints[2]);
					path.lineTo(xPoints[3], yPoints[3]);
					
					path.closePath();
					g2d.fill(path);
					
					
					//removeAlphaChannel(lineBuffer);
					
					//g2d.drawImage(lineBuffer, 0, 0, this);
					
					
					
				//}
				
				if (isCancelled()) {
					log.warn("Render process cancelled, model not complete.");
					break;
				}
			}
			
			if (isCancelled()) {
				log.warn("Render process cancelled, model not complete.");
				break;
			}
			//log.info("Rendered row #" + row);
		}
		

		if (!isCancelled()) {
			
			log.info("Removing alpha channel");
			removeAlphaChannel(image);
			
			log.info("Finalizing image");
			WritableRaster raster = image.getRaster();
			int[] rasterPixel = new int[4];
			
			for (int row = 0; row < raster.getHeight(); row++) {
				for (int column = 0; column < raster.getWidth(); column++) {
					raster.getPixel(column, row, rasterPixel);
					
					if (rasterPixel[0] == 0 && rasterPixel[1] == 0 && rasterPixel[2] == 0 && rasterPixel[3] == 0) {
						// Apply background color
						
						rasterPixel[0] = backgroundColor.getRed();
						rasterPixel[1] = backgroundColor.getGreen();
						rasterPixel[2] = backgroundColor.getBlue();
						rasterPixel[3] = backgroundColor.getAlpha();
						
						raster.setPixel(column, row, rasterPixel);
					} 
				}
				
			}
		}
		
		
		
		
		// TODO: Recreate image allocate
		ModelCanvas canvas3d = null;// new ModelCanvas(image);
		
		if (!isCancelled()) {
			log.info("Cropping image");
			// TODO: Restore autoCrop
			//canvas3d = autoCrop(canvas3d);
		}

		return new OutputProduct<ModelCanvas>(OutputProduct.IMAGE, canvas3d);
	}
	
	public void resetImage(BufferedImage image)
	{
		WritableRaster raster = image.getRaster();
		int[] rasterPixel = {0, 0, 0, 0};
		
		for (int row = 0; row < raster.getHeight(); row++) {
			for (int column = 0; column < raster.getWidth(); column++) {
				raster.setPixel(column, row, rasterPixel);
			}
		}
	}
	
	public void removeAlphaChannel(BufferedImage image)
	{
		WritableRaster raster = image.getRaster();
		int[] rasterPixel = new int[4];
		
		for (int row = 0; row < raster.getHeight(); row++) {
			for (int column = 0; column < raster.getWidth(); column++) {
				raster.getPixel(column, row, rasterPixel);
				
				if (!(rasterPixel[0] == 0 && rasterPixel[1] == 0 && rasterPixel[2] == 0 && rasterPixel[3] == 0)) {
					// Remove alpha channel
					
					rasterPixel[3] = 255;
					raster.setPixel(column, row, rasterPixel);
				}
			}
			
		}
	}
	
	
	public DemCanvas autoCrop(DemCanvas original)
	{
		
		int top = -1;
		int left = -1;
		int bottom = original.getHeight();
		int right = original.getWidth();
		
		int bgRGB = getDefinedColor(getModelContext().getModelOptions().getBackgroundColor()).getRGB();
		
		for (int y = 0; y < original.getHeight(); y++) {
			for (int x = 0; x < original.getWidth(); x++) {
				int imageColor = original.getColor(x, y);
				if (imageColor != bgRGB) {
					top = y;
					break;
				}
			}
			
			if (top >= 0)
				break;
		}
		
		for (int y = original.getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < original.getWidth(); x++) {
				int imageColor = original.getColor(x, y);
				if (imageColor != bgRGB) {
					bottom = y;
					break;
				}
			}
			
			if (bottom < original.getHeight())
				break;
		}
		
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {
				int imageColor = original.getColor(x, y);
				if (imageColor != bgRGB) {
					left = x;
					break;
				}
			}
			if (left >= 0)
				break;
		}
		
		for (int x = original.getWidth() - 1; x >= 0 ; x--) {
			for (int y = 0; y < original.getHeight(); y++) {
				int imageColor = original.getColor(x, y);
				if (imageColor != bgRGB) {
					right = x;
					break;
				}
			}
			if (right < original.getWidth())
				break;
		}
		
		int cropHeight = bottom - top;
		int cropWidth = right - left;
		
		// One dimension would be too small (0). Return the original let the user crop manually.
		if (cropHeight == 0 || cropWidth == 0) {
			return original;
		}
		BufferedImage cropped = new BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) cropped.getGraphics();
		

		g2d.drawImage(original.getImage(),
                0,
                0,
                cropWidth,
                cropHeight,
                left,
                top,
                right,
                bottom,
                new ImageObserver() {
					public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
						return true;
					}
		});
		
		
		g2d.dispose();
		
		
		log.info("Auto Crop Top Y: " + top + ", buttom Y: " + bottom);
		
		return new DemCanvas(cropped);
	}
	
	public boolean isFacingCamera(double[] bl, double[] fl, double[] fr, double[] br)
	{
		
		if (bl[1] >= fl[1] || br[1] >= fr[1]) {
			return false;
		}
		
		return true;
	}
	
	public int pointOrder(double[] ... vectors)
	{
		int i,j,k;
		int count = 0;
		int n = vectors.length;
		double z;

		for (i=0;i<n;i++) {

			
			j = (i + 1) % n;
			k = (i + 2) % n;
			z  = (vectors[j][0] - vectors[i][0]) * (vectors[k][1] - vectors[j][1]);
			z -= (vectors[j][1] - vectors[i][1]) * (vectors[k][0] - vectors[j][0]);
			if (z < 0)
				count--;
			else if (z > 0)
				count++;
		}
		if (count > 0)
			return(COUNTERCLOCKWISE);
		else if (count < 0)
			return(CLOCKWISE);
		else
			return(UNDEFINED);
	}
	
	public void projectTo(double[] vector, double[] eye, double[] near) //Vector eye, Vector near)
	{
		//double thetaX = 0; // Orientation of the camera
		//double thetaY = 0;
		//double thetaZ = 0;
		
		double[] a = vector;   // 3D position of points being projected
		double[] e = near;     // Viewer's position relative to the display surface
		double[] c = eye;      // Camera position
		
		
		
		/*
		double sinTX = sin(thetaX);
		double sinTY = sin(thetaY);
		double sinTZ = sin(thetaZ);
		
		double cosTX = cos(thetaX);
		double cosTY = cos(thetaY);
		double cosTZ = cos(thetaZ);
		
		double dX = cosTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0])) - sinTY * (a[2] - c[2]);
		double dY = sinTX * (cosTY * (a[2] - c[2]) + sinTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0]))) + cosTX * (cosTZ * (a[1] - c[1]) - sinTZ * (a[0] - c[0]));
		double dZ = cosTX * (cosTY * (a[2] - c[2]) + sinTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0]))) - sinTX * (cosTZ * (a[1] - c[1]) - sinTZ * (a[0] - c[0]));
		*/
		
		vector[0] = ((a[0] - c[0]) - e[0]) * (e[2] / (a[2] - c[2]));
		vector[1] = ((a[1] - c[1]) - e[1]) * (e[2] / (a[2] - c[2]));
		
		//vector[0] = bX;
		///vector[1] = bY;
	}


	
	


	
	
	
}
