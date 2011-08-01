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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.color.ColorRegistry;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
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
	

	public Dem3dGenerator()
	{
		super();
	}
	
	public Dem3dGenerator(DataPackage dataPackage, ModelOptions modelOptions)
	{
		super(dataPackage, modelOptions);
	}

	@Override
	public OutputProduct<DemCanvas> generate() throws RenderEngineException
	{
		try {
			return generate(false);
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
	}
	
	@Override
	public OutputProduct<DemCanvas> generate(boolean skipElevation) throws RenderEngineException
	{
		Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
		
		OutputProduct<DemCanvas> product2d = dem2d.generate(skipElevation);
		DemCanvas canvas2d = product2d.getProduct();
		
		DemPoint point = new DemPoint();
		
		double[] vectorFrontLeft = new double[3];
		double[] vectorFrontRight = new double[3];
		double[] vectorBackLeft = new double[3];
		double[] vectorBackRight = new double[3];
		
		double elevationMultiple = modelOptions.getElevationMultiple();
		double elevationMax = dataPackage.getMaxElevation() * elevationMultiple;
		double elevationMin = dataPackage.getMinElevation() * elevationMultiple;
		double elevationDelta = elevationMax - elevationMin;
		double resolution = dataPackage.getAverageResolution();
		
		
		double startZ =  -(dataPackage.getRows() / 2.0);
		double startX = -(dataPackage.getColumns() / 2.0);
		
		
		double rotateX = modelOptions.getProjection().getRotateX();
		double rotateY = modelOptions.getProjection().getRotateY();

		double[] eyeVector = {0, 0, dataPackage.getColumns()};
		double[] nearVector = {0, 0, (dataPackage.getColumns()/2.0f)};
		
		BufferedImage image = new BufferedImage((int)dataPackage.getColumns(), (int) dataPackage.getRows(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(getDefinedColor(modelOptions.getBackgroundColor()));
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		//DemCanvas canvas3d = new DemCanvas(dataPaka)
		
		//int[] xPoints = new int[4];
		//int[] yPoints = new int[4];
		Path2D.Double path = new Path2D.Double();
		
		for (int row = 0; row < dataPackage.getRows(); row++) {
			for (int column = 0; column < dataPackage.getColumns(); column++) {
				getPoint(row, column, 1, point);
				
				if (point.getCondition() == DemConstants.STAT_SUCCESSFUL) {
					double x = (dataPackage.getColumns() - column) + startX;
					double z = row + startZ;
					
					//log.info("x/z: " + x + "/" + z);
					
					double yBL = ((point.getBackLeftElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);
					double yBR = ((point.getBackRightElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);
					double yFL = ((point.getFrontLeftElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);
					double yFR = ((point.getFrontRightElevation() * elevationMultiple - elevationMax) / resolution) + Math.abs(elevationMin);
	
					/*
					yBL = yBL + Math.abs(elevationMin);
					yBR = yBR + Math.abs(elevationMin);
					yFL = yFL + Math.abs(elevationMin);
					yFR = yFR + Math.abs(elevationMin);
					*/
					
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
					
					vectorBackLeft[0] -= startX;
					vectorBackRight[0] -= startX;
					vectorFrontLeft[0] -= startX;
					vectorFrontRight[0] -= startX;
					
					vectorBackLeft[1] -= startZ;
					vectorBackRight[1] -= startZ;
					vectorFrontLeft[1] -= startZ;
					vectorFrontRight[1] -= startZ;
					

					path.reset();
					path.moveTo(vectorBackLeft[0], vectorBackLeft[1]);
					path.lineTo(vectorFrontLeft[0], vectorFrontLeft[1]);
					path.lineTo(vectorFrontRight[0], vectorFrontRight[1]);
					path.lineTo(vectorBackRight[0], vectorBackRight[1]);
					path.closePath();
					
					int color = canvas2d.getColor(column, row);
					g2d.setColor(new Color(color));
					//g2d.fillPolygon(xPoints, yPoints, 4);
					
					g2d.fill(path);
					
					
				}
				
			}
			//log.info("Rendered row #" + row);
		}
		
		DemCanvas canvas3d = new DemCanvas(image);
		canvas3d = autoCrop(canvas3d);
		return new OutputProduct<DemCanvas>(OutputProduct.IMAGE, canvas3d);
	}
	
	public DemCanvas autoCrop(DemCanvas original)
	{
		
		int top = -1;
		int left = -1;
		int bottom = original.getHeight();
		int right = original.getWidth();
		
		int bgRGB = getDefinedColor(modelOptions.getBackgroundColor()).getRGB();
		
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
	
	/*
	@Override
	public OutputProduct<DemCanvas> generate(boolean skipElevation) throws RenderEngineException
	{
		try {
			Color background = ColorRegistry.getInstance(modelOptions.getBackgroundColor()).getColor();
			
			double width = dataPackage.getColumns();
			double height = dataPackage.getRows();
			
			BufferedImage image = new BufferedImage((int)dataPackage.getColumns(), (int) dataPackage.getRows(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			
			
			g2d.setColor(background);
			g2d.fillRect(0, 0, (int)width, (int) height);
			
			int trueStartZ = (int) Math.round(-(height / 2.0));
			int trueEndZ = (int) Math.round(height / 2.0);
			
			int tileSize = (int)Math.ceil(height);//modelOptions.getTileSize();
			double numTiles = Math.ceil(((double)height / (double)tileSize));
			log.info("Rendering " + numTiles + " tiles");
			
			double tileNum = 0;
			
			ViewportBuffer buffer = new ViewportBuffer((int)width, (int)height);
	
			int[] tileYs = new int[(int)numTiles];
			
			for (int z = trueStartZ; z < trueEndZ; z+=tileSize) {
				int endZ = z + tileSize;
				if (endZ > trueEndZ)
					endZ = trueEndZ;
	
				tileNum++;
				
				log.info("Rendering tile #" + (int)tileNum + " of " + (int)numTiles + " (" + z + " to " + endZ + ")");
				
				renderTile(buffer, z, endZ);
				
				if (isCancelled()) {
					log.warn("Render process cancelled, model not complete.");
					break;
				}
			}
			
			log.info("Transferring pixel data to image");
	
			buffer.paint(image, 0);
			buffer.dispose();
	
			
			g2d.dispose();
			
			DemCanvas canvas = new DemCanvas(image);//background, modelOptions.getWidth(), modelOptions.getHeight());
			return new OutputProduct<DemCanvas>(OutputProduct.IMAGE, canvas);
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
	}
	
	protected void renderTile(ViewportBuffer buffer, int startZ, int endZ) throws RenderEngineException
	{
		//List<Renderable> polygons = new LinkedList<Renderable>();
		
		Color lineColor = Color.BLACK;
		
		double elevationMax = dataPackage.getMaxElevation();
		double elevationMin = dataPackage.getMinElevation();
		double elevationDelta = elevationMax - elevationMin;
		double resolution = dataPackage.getAverageResolution();
		
		int width = (int) dataPackage.getColumns();
		int height = (int) dataPackage.getRows();
		int tileHeight = modelOptions.getTileSize();
		
		
		Vector eye = new Vector(0, 0, (int) dataPackage.getColumns());
		Vector near = new Vector(0, 0, (int) Math.round((dataPackage.getColumns()/2.0f)));
		double translateZ = -(dataPackage.getColumns() / 2.0f) / Math.tan(45.0 / 2.0);
		
		Vector translate = new Vector(0, 0, 0);//translateZ);
		log.info("Translate Z: " + translateZ);
		
		//double nearWidth = 50;
		//double nearHeight = 50;
		//double farDistance = 50;
		double sunsource[] = {0.0, 0.0, 0.0};
		
		Vector sun = new Vector(0.0, 0.0, -1.0);
		double solarElevation = modelOptions.getLightingElevation();
		double solarAzimuth = modelOptions.getLightingAzimuth();

		sun.rotate(solarElevation, Vector.X_AXIS);
		sun.rotate(-solarAzimuth, Vector.Y_AXIS);
		
		sunsource[0] = sun.getX();
		sunsource[1] = sun.getY();
		sunsource[2] = sun.getZ();
		
		double startX = -(width / 2.0);
		double endX = (width / 2.0);
		
		int trueEndZ = (int) Math.round(height / 2.0);
		
		Vector rotateX = new Vector(modelOptions.getProjection().getRotateX(), 0, 0);
		Vector rotateY = new Vector(0, modelOptions.getProjection().getRotateY(), 0);
		DemPoint point = new DemPoint();
		
		ModelColoring modelColoring = modelColoring = ColoringRegistry.getInstance(modelOptions.getColoringType()).getImpl();

		double elevationMultiple = modelOptions.getElevationMultiple();
		
		int gridSize = 1;
		int polyCount = 0;
		
		int[] cBL = {0, 0, 0};
		int[] cBR = {0, 0, 0};
		int[] cFL = {0, 0, 0};
		int[] cFR = {0, 0, 0};
		int[] cSq = {0, 0, 0};
		
		Vector vBL = new Vector(cBL, 0, 0, 0);
		Vector vBR = new Vector(cBR, 0, 0, 0);
		Vector vFL = new Vector(cFL, 0, 0, 0);
		Vector vFR = new Vector(cFR, 0, 0, 0);
		Square square = new Square(cSq, vBL, vFL, vFR, vBR);
		
		int[] clrBL = {0, 0, 0, 255};
		int[] clrBR = {0, 0, 0, 255};
		int[] clrFL = {0, 0, 0, 255};
		int[] clrFR = {0, 0, 0, 255};
		
		double[] normal = {0.0, 0.0, 0.0};
		
		double[] pBL = {0, 0, 0};
		double[] pBR = {0, 0, 0};
		double[] pFL = {0, 0, 0};
		double[] pFR = {0, 0, 0};
		
		double progress = 0;
		
		for (double z = startZ; z < endZ; z+=gridSize) {
			double tileZ = z;
			int row = (int) (trueEndZ + z);
			
			for (double x = startX; x < endX; x+=gridSize) {
				int column = (int) (endX - x);

				getPoint(row, column, gridSize, point);
	
				if (point.getCondition() == DemConstants.STAT_SUCCESSFUL) {
					double yBL = (point.getBackLeftElevation() - elevationMax) / resolution;
					double yBR = (point.getBackRightElevation() - elevationMax) / resolution;
					double yFL = (point.getFrontLeftElevation() - elevationMax) / resolution;
					double yFR = (point.getFrontRightElevation() - elevationMax) / resolution;
	
					
					yBL = yBL + Math.abs(elevationMin);
					yBR = yBR + Math.abs(elevationMin);
					yFL = yFL + Math.abs(elevationMin);
					yFR = yFR + Math.abs(elevationMin);
	
					modelColoring.getGradientColor(point.getBackLeftElevation(), (float)elevationMin, (float)elevationMax, clrBL);
					modelColoring.getGradientColor(point.getBackRightElevation(), (float)elevationMin, (float)elevationMax, clrBR);
					modelColoring.getGradientColor(point.getFrontLeftElevation(), (float)elevationMin, (float)elevationMax, clrFL);
					modelColoring.getGradientColor(point.getFrontRightElevation(), (float)elevationMin, (float)elevationMax, clrFR);
					
					
					square.getVector(0).setColor(clrBL[0], clrBL[1], clrBL[2]);
					square.getVector(0).setX(x);
					square.getVector(0).setY(yBL * elevationMultiple);
					square.getVector(0).setZ(tileZ);
					
					square.getVector(1).setColor(clrFL[0], clrFL[1], clrFL[2]);
					square.getVector(1).setX(x);
					square.getVector(1).setY(yFL * elevationMultiple);
					square.getVector(1).setZ(tileZ+gridSize);
					
					square.getVector(2).setColor(clrFR[0], clrFR[1], clrFR[2]);
					square.getVector(2).setX(x+gridSize);
					square.getVector(2).setY(yFR * elevationMultiple);
					square.getVector(2).setZ(tileZ+gridSize);
					
					square.getVector(3).setColor(clrBR[0], clrBR[1], clrBR[2]);
					square.getVector(3).setX(x+gridSize);
					square.getVector(3).setY(yBR * elevationMultiple);
					square.getVector(3).setZ(tileZ);
	
					
					pBL[0] = x;				pBL[1] = yBL; 	pBL[2] = z;
					pBR[0] = x+gridSize; 	pBR[1] = yBR; 	pBR[2] = z;
					pFL[0] = x; 			pFL[1] = yFL; 	pFL[2] = z+gridSize;
					pFR[0] = x+gridSize; 	pFR[1] = yFR; 	pFR[2] = z+gridSize;
	
					Perspectives.calcNormal(pBL, pFL, pBR, normal);
					square.setNormal(normal);
					
					square.rotate(rotateY);
					square.rotate(rotateX);
					square.translate(translate);
					square.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
					square.prepareForRender(sunsource, 1.0);
	
					square.render(buffer, width, height);
	
					polyCount++;
				}
				
				
				if (isCancelled()) {
					log.warn("Render process cancelled, model not complete.");
					break;
				}
			}
			
			
			if (z % 100 == 0) {
				double pctComplete = ((double)z - startZ) / ((double)(endZ - startZ));
				pctComplete = Math.floor(pctComplete * 100);
				if (pctComplete > progress) {
					log.info("Percent Complete: " + pctComplete + "%");
					
					//System.out.println("Percent Complete: " + (pctComplete) + "%");
				}
				progress = pctComplete;
				//if (progress > 70)
				//	break;
			}
			
			if (isCancelled()) {
				log.warn("Render process cancelled, model not complete.");
				break;
			}
		}
	}
	*/
	
	


	
	
	
}
