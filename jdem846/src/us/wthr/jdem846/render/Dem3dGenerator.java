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
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.gfx.Line;
import us.wthr.jdem846.render.gfx.Renderable;
import us.wthr.jdem846.render.gfx.Square;
import us.wthr.jdem846.render.gfx.StaticPolygonList;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.gfx.ViewportBuffer;

@DemEngine(name="DEM-3D Generator", identifier="dem3d-gen")
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
	public OutputProduct<DemCanvas> generate()
	{
		return generate(false);
	}
	
	@Override
	public OutputProduct<DemCanvas> generate(boolean skipElevation)
	{
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

		}
		
		log.info("Transferring pixel data to image");

		buffer.paint(image, 0);
		buffer.dispose();

		
		g2d.dispose();
		
		DemCanvas canvas = new DemCanvas(image);//background, modelOptions.getWidth(), modelOptions.getHeight());
		return new OutputProduct<DemCanvas>(OutputProduct.IMAGE, canvas);
	}
	
	protected void renderTile(ViewportBuffer buffer, int startZ, int endZ)
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
		
		
		Vector eye = new Vector(0, 0, (int) dataPackage.getRows());
		
		Vector near = new Vector(0, 0, (int) Math.round((dataPackage.getRows()/2.0f)));
		
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
					
					square.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
					square.prepareForRender(sunsource, 1.0);
	
					square.render(buffer, width, height);
	
					polyCount++;
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
		}
	}
	
	
	
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
	
	
	
	
	
}
