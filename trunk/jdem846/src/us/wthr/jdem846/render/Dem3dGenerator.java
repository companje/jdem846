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
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
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
public class Dem3dGenerator extends RenderEngine
{
	private static Log log = Logging.getLog(Dem3dGenerator.class);
	
	private DataPackage dataPackage;
	private ModelOptions modelOptions;
	
	public Dem3dGenerator()
	{
		
	}
	
	public Dem3dGenerator(DataPackage dataPackage, ModelOptions modelOptions)
	{
		this.dataPackage = dataPackage;
		this.modelOptions = modelOptions;
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
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		//DemCanvas canvas3d = new DemCanvas(dataPaka)
		
		
		
		g2d.setColor(Color.YELLOW);
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
					
					Path2D.Double path = new Path2D.Double();
					path.moveTo(vectorBackLeft[0], vectorBackLeft[1]);
					path.lineTo(vectorFrontLeft[0], vectorFrontLeft[1]);
					path.lineTo(vectorFrontRight[0], vectorFrontRight[1]);
					path.lineTo(vectorBackRight[0], vectorBackRight[1]);
					path.closePath();
					
					int color = canvas2d.getColor(column, row);
					g2d.setColor(new Color(color));
					
					g2d.fill(path);
					
					
				}
				
			}
			//log.info("Rendered row #" + row);
		}
		
		DemCanvas canvas3d = new DemCanvas(image);
		return new OutputProduct<DemCanvas>(OutputProduct.IMAGE, canvas3d);
	}
	
	
	public void projectTo(double[] vector, double[] eye, double[] near) //Vector eye, Vector near)
	{
		double thetaX = 0; // Orientation of the camera
		double thetaY = 0;
		double thetaZ = 0;
		
		double[] a = vector;
		double[] e = near;
		double[] c = eye;
		//Vector e = near; // Viewer's position relative to the display surface
		//Vector a = this; // 3D position of points being projected
		//Vector c = eye;  // Camera position
		
		double sinTX = sin(thetaX);
		double sinTY = sin(thetaY);
		double sinTZ = sin(thetaZ);
		
		double cosTX = cos(thetaX);
		double cosTY = cos(thetaY);
		double cosTZ = cos(thetaZ);
		
		/*
		double dX = cosTY * (sinTZ * (a.y - c.y) + cosTZ * (a.x - c.x)) - sinTY * (a.z - c.z);
		double dY = sinTX * (cosTY * (a.z - c.z) + sinTY * (sinTZ * (a.y - c.y) + cosTZ * (a.x - c.x))) + cosTX * (cosTZ * (a.y - c.y) - sinTZ * (a.x - c.x));
		double dZ = cosTX * (cosTY * (a.z - c.z) + sinTY * (sinTZ * (a.y - c.y) + cosTZ * (a.x - c.x))) - sinTX * (cosTZ * (a.y - c.y) - sinTZ * (a.x - c.x));
		*/
		double dX = cosTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0])) - sinTY * (a[2] - c[2]);
		double dY = sinTX * (cosTY * (a[2] - c[2]) + sinTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0]))) + cosTX * (cosTZ * (a[1] - c[1]) - sinTZ * (a[0] - c[0]));
		double dZ = cosTX * (cosTY * (a[2] - c[2]) + sinTY * (sinTZ * (a[1] - c[1]) + cosTZ * (a[0] - c[0]))) - sinTX * (cosTZ * (a[1] - c[1]) - sinTZ * (a[0] - c[0]));
		
		/*
		double bX = (dX - e.x) * (e.z / dZ);
		double bY = (dY - e.y) * (e.z / dZ);
		*/
		double bX = (dX - e[0]) * (e[2] / dZ);
		double bY = (dY - e[1]) * (e[2] / dZ);
		double bZ = a[2];
		
		vector[0] = bX;
		vector[1] = bY;
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
	
	
	public DataPackage getDataPackage()
	{
		return dataPackage;
	}

	public void setDataPackage(DataPackage dataPackage)
	{
		this.dataPackage = dataPackage;
	}

	public ModelOptions getModelOptions()
	{
		return modelOptions;
	}

	public void setModelOptions(ModelOptions modelOptions)
	{
		this.modelOptions = modelOptions;
	}


	private void getPoint(int row, int column, DemPoint point)
	{
		getPoint(row, column, 1, point);
	}
	
	private void getPoint(int row, int column, int gridSize, DemPoint point)
	{
		//DemPoint point = new DemPoint();


		if (dataPackage == null) {
			point.setCondition(DemConstants.STAT_NO_DATA_PACKAGE);
			return;
			//return point;
		}

		float elevMax = dataPackage.getMaxElevation();
		float elevMin = dataPackage.getMinElevation();
		float nodata = dataPackage.getNoData();

		float elevation = dataPackage.getElevation(row, column);


		if (elevation == DemConstants.ELEV_NO_DATA) {
			point.setCondition(DemConstants.STAT_INVALID_ELEVATION);
			return;
			//return point;
		}
		
		point.setBackLeftElevation(elevation);


		elevation = dataPackage.getElevation(row, column + gridSize);
		if (elevation >= elevMin && elevation <= elevMax && elevation != DemConstants.ELEV_NO_DATA && elevation != nodata) {
			point.setBackRightElevation(elevation);
		} else {
			point.setBackRightElevation(point.getBackLeftElevation());
		}

		elevation = dataPackage.getElevation(row + gridSize, column);
		if (elevation >= elevMin && elevation <= elevMax && elevation != DemConstants.ELEV_NO_DATA && elevation != nodata) {
			point.setFrontLeftElevation(elevation);
		} else {
			point.setFrontLeftElevation(point.getBackLeftElevation());
		}

		//this->data_package->get(row + 1, column + 1, &elevation);
		elevation = dataPackage.getElevation(row + gridSize, column + gridSize);
		if (elevation >= elevMin && elevation <= elevMax && elevation != DemConstants.ELEV_NO_DATA && elevation != nodata) {
			point.setFrontRightElevation(elevation);
		} else {
			point.setFrontRightElevation(point.getBackLeftElevation());
		}
		
		
		if (point.getBackLeftElevation() == 0 
			&& point.getBackRightElevation() == 0 
			&& point.getFrontLeftElevation() == 0 
			&& point.getFrontRightElevation() == 0) {
			point.setCondition(DemConstants.STAT_FLAT_SEA_LEVEL);
			return;
			//return point;
		}
		

		elevation = (point.getBackLeftElevation() + point.getBackRightElevation() + point.getFrontLeftElevation() + point.getFrontRightElevation()) / 4.0f;
		point.setMiddleElevation(elevation);
		
		point.setCondition(DemConstants.STAT_SUCCESSFUL);
		//return point;
		return;
	}
	
	protected static double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected static double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}
	
	
	
}
