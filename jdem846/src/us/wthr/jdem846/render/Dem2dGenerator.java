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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;


import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemPoint;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColorRegistry;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.dbase.ClassLoadException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.SubsetDataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.shapefile.PointTranslateHandler;
import us.wthr.jdem846.shapefile.Shape;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.ShapeConstants;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.ShapeLayer;
import us.wthr.jdem846.shapefile.ShapePath;
import us.wthr.jdem846.shapefile.modeling.FeatureTypeStroke;
import us.wthr.jdem846.shapefile.modeling.LineStroke;

@DemEngine(name="us.wthr.jdem846.render.demEngine2D.name", 
	identifier="dem2d-gen",
	usesElevationMultiple=false)
public class Dem2dGenerator extends BasicRenderEngine
{
	private static Log log = Logging.getLog(Dem2dGenerator.class);
	

	public Dem2dGenerator(ModelContext modelContext)
	{
		super(modelContext);
	}
	
	//public Dem2dGenerator(DataPackage dataPackage, ModelOptions modelOptions)
	//{
	//	super(dataPackage, modelOptions);
	//}
	
	public OutputProduct<DemCanvas> generate() throws RenderEngineException
	{
		try {
			DemCanvas canvas = generate(getModelOptions().getWidth(), getModelOptions().getHeight(), getModelOptions().getTileSize(), false);
			applyShapefileLayers(canvas);
			return new OutputProduct<DemCanvas>(OutputProduct.IMAGE, canvas);
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
	}
	
	public OutputProduct<DemCanvas> generate(boolean skipElevation) throws RenderEngineException
	{
		try {
			DemCanvas canvas = generate(getModelOptions().getWidth(), getModelOptions().getHeight(), getModelOptions().getTileSize(), skipElevation);
			applyShapefileLayers(canvas);
			return new OutputProduct<DemCanvas>(OutputProduct.IMAGE, canvas);
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
	}
	
	public DemCanvas generate(int reqdWidth, int reqdHeight, int tileSize, boolean skipElevation) throws RenderEngineException
	{
		ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(getDataPackage(), getModelOptions());
		getDataPackage().setAvgXDim(modelDimensions.getxDim());
		getDataPackage().setAvgYDim(modelDimensions.getyDim());
		
		
		Color background = getDefinedColor(getModelOptions().getBackgroundColor());

		DemCanvas tileCanvas = new DemCanvas(background, (int)modelDimensions.getTileSize(), (int)modelDimensions.getTileSize());
		DemCanvas outputCanvas = new DemCanvas(background, (int)modelDimensions.getOutputWidth(), (int)modelDimensions.getOutputHeight());
		
		//applyTiledBackground(outputCanvas, "/background-tiles/water_3.png");
		
		int tileRow = 0;
		int tileCol = 0;
		int tileNum = 0;
		int dataRows = modelDimensions.getDataRows();
		int dataCols = modelDimensions.getDataColumns();
		tileSize = modelDimensions.getTileSize();
	
		log.info("Processing " + modelDimensions.getTileCount() + " tiles of size: " + tileSize);
		

		boolean tiledPrecaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_TILED);
		if (tiledPrecaching) {
			log.info("Data Precaching Strategy Set to TILED");
		}
		
		if ((!skipElevation) && getDataPackage().getDataSources().size() > 0) {
			for (int fromRow = 0; fromRow < dataRows; fromRow+=tileSize) {
				int toRow = fromRow + tileSize - 1;
				if (toRow > dataRows)
					toRow = dataRows;
			
				tileCol = 0;
				for (int fromCol = 0; fromCol < dataCols; fromCol+=tileSize) {
					int toCol = fromCol + tileSize - 1;
					if (toCol > dataCols)
						toCol = dataCols;
					
					//DataBounds tileBounds = new DataBounds((int) fromCol, (int) fromRow, (int) tileSize, (int) tileSize);
					//if (dataPackage.dataOverlaps(tileBounds)) {
					//SubsetDataPackage dataSubset = dataPackage.getDataSubset(tileBounds);

					loadDataSubset((int) fromCol, (int) fromRow, (int) tileSize, (int) tileSize);
					
					if (dataSubset != null && dataSubset.containsData()) {
						
						if (tiledPrecaching) {
							try {
								dataSubset.precacheData();
							} catch (DataSourceException ex) {
								throw new RenderEngineException("Error with source data: " + ex.getMessage(), ex);
							}
						}
						
						tileCanvas.reset();
						
						//log.info("Rendering tile...");
						generate(fromRow, toRow, fromCol, toCol, tileCanvas);
						
						//log.info("Rescaling tile...");
						DemCanvas scaled = tileCanvas.getScaled((int)modelDimensions.getTileOutputWidth(), (int) modelDimensions.getTileOutputHeight());
						
						//log.info("Overlaying tile to output canvas...");
						outputCanvas.overlay(scaled.getImage(), (int)Math.floor(tileCol * modelDimensions.getTileOutputWidth()), (int)Math.floor(tileRow * modelDimensions.getTileOutputHeight()), scaled.getWidth(), scaled.getHeight());
						
						//log.info("Completed tile.");
						
						if (tiledPrecaching) {
							try {
								dataSubset.unloadData();
							} catch (DataSourceException ex) {
								throw new RenderEngineException("Error with source data: " + ex.getMessage(), ex);
							}
						}
						
						//tileCanvas.save("output-" + tileRow + "-" + tileCol + ".png");
						//scaled.save("scaled-" + tileRow + "-" + tileCol + ".png");
						
						tileNum++;
						
						fireTileCompletionListeners(tileCanvas, outputCanvas, ((double)tileNum) / ((double)modelDimensions.getTileCount()));
					}
					tileCol++;	
					
					if (isCancelled()) {
						log.warn("Render process cancelled, model not complete.");
						break;
					}
				}
				tileRow++;
				if (isCancelled()) {
					log.warn("Render process cancelled, model not complete.");
					break;
				}
			}	
		} else {
			log.info("There was no elevation data to render");
		}
		
		//float tiepointLat = dataPackage.rowToLatitude(0);
		//float tiepointLon = dataPackage.columnToLongitude(0);
		
		
		//SimpleImageViewer imageViewer = new SimpleImageViewer(outputCanvas.getImage());
		//imageViewer.setVisible(true);
		
		//outputCanvas.save(saveTo);
		
		//System.out.println("Done.");
		log.info("Done.");
		return outputCanvas;
	}
	
	public DemCanvas generate(int fromRow, int toRow, int fromColumn, int toColumn) throws RenderEngineException
	{
		return generate(fromRow, toRow, fromColumn, toColumn, null);
	}
	
	public DemCanvas generate(int fromRow, int toRow, int fromColumn, int toColumn, DemCanvas canvas) throws RenderEngineException
	{
		
		
		int numRows = (toRow - fromRow) + 1;
		//int numCols = (toColumn - fromColumn) + 1;
		
		//log.info("Prebuffering tile raster");
		//float[][] buffer = new float[numRows][numCols];
		//dataSubset.fillBuffer(buffer, fromRow, fromColumn, numRows, numCols);
		
		
		double elevationMax = getDataPackage().getMaxElevation();
		double elevationMin = getDataPackage().getMinElevation();
		
		DemPoint point = new DemPoint();
		
		int[] color = {0, 0, 0, 0};
		int[] reliefColor = {0, 0, 0, 0};
		int[] hillshadeColor = {0, 0, 0, 0};
		
		
		//double res1_2 = dataPackage.getAverageResolution() / 2.0;
		//double sunsource[] = {1.0, 1.0, 1.0};	
		double sunsource[] = {0.0, 0.0, 0.0};	
		//double sunsource[] = {res1_2, res1_2, res1_2};
		double normal[] = {0.0, 0.0, 0.0};
		
		Vector sun = new Vector(0.0, 0.0, -1.0);
		//modelOptions.getLightingElevation()
		//-modelOptions.getLightingAzimuth()
		//Vector rotate = new Vector(modelOptions.getLightingElevation(), 0, 0);
		double solarElevation = getModelOptions().getLightingElevation();
		double solarAzimuth = getModelOptions().getLightingAzimuth();
		sun.rotate(solarElevation, Vector.X_AXIS);
		sun.rotate(-solarAzimuth, Vector.Y_AXIS);
		
		//sun.rotate(rotate);
		//sun.rotate(180.0, Vector.Y_AXIS);
		//sun.rotate(-45.0, Vector.X_AXIS);
		sunsource[0] = sun.getX();
		sunsource[1] = sun.getY();
		sunsource[2] = sun.getZ();
		
		
		double backLeftPoints[] = {-1.0, 0.0f, -1.0};
		double backRightPoints[] = {1.0, 0.0f, -1.0};
		double frontLeftPoints[] = {-1.0, 0.0f, 1.0};
		double frontRightPoints[] = {1.0, 0.0f, 1.0};
		
		
		/*
		double halfRes = (dataPackage.getAverageResolution() / 2.0);
		
		double backLeftPoints[] = {-halfRes, 0.0, -halfRes};
		double backRightPoints[] = {halfRes, 0.0, -halfRes};
		double frontLeftPoints[] = {-halfRes, 0.0, halfRes};
		double frontRightPoints[] = {halfRes, 0.0, halfRes};
		*/
		
		int imgRow = -1;
		int imgCol = -1;
		
		double progress = 0;

		float elevationMultiple = 1.0f;
		
		if (canvas == null) {
			//Color background = new Color(0x2C, 0x49, 0x80, 0xFF);
			Color background = getDefinedColor(getModelOptions().getBackgroundColor());
			int width = (int)(toColumn - fromColumn) + 1;
			int height = (int)(toRow - fromRow) + 1;
			//System.out.println("Creating default canvas of width/height: " + width + "/" + height);
			log.info("Creating default canvas of width/height: " + width + "/" + height);
			canvas = new DemCanvas(background, width, height);
		}
		
		ModelColoring modelColoring = ColoringRegistry.getInstance(getModelOptions().getColoringType()).getImpl();

		
		//System.out.println("Percent Complete: 0%");
		
		log.info("Row from/to: " + fromRow + "/" + toRow + ", Column from/to: " + fromColumn + "/" + toColumn);
		log.info("Percent Complete: 0%");
		for (int row = fromRow; row <= toRow; row++) {
			imgRow++;
			imgCol = -1;
			
			for (int column = fromColumn; column <= toColumn; column++)  {
				imgCol++;

				try {
					getPoint(row, column, point);
				} catch (DataSourceException ex) {
					throw new RenderEngineException("Error loading elevation data: " + ex.getMessage(), ex);
				}
				
				if (point.getCondition() == DemConstants.STAT_SUCCESSFUL) {
					/*
					point.setBackLeftElevation(point.getBackLeftElevation() * elevationMultiple);
					point.setBackRightElevation(point.getBackRightElevation() * elevationMultiple);
					point.setFrontLeftElevation(point.getFrontLeftElevation() * elevationMultiple);
					point.setFrontRightElevation(point.getFrontRightElevation() * elevationMultiple);
					*/
					
					backLeftPoints[1] = point.getBackLeftElevation() * elevationMultiple;
					backRightPoints[1] = point.getBackRightElevation() * elevationMultiple;
					frontLeftPoints[1] = point.getFrontLeftElevation() * elevationMultiple;
					//frontRightPoints[1] = point.getFrontRightElevation() * elevationMultiple;
					
					Perspectives.calcNormal(backLeftPoints, frontLeftPoints, backRightPoints, normal);
					
					
					//dot = (dot + 1.0f) / 2.0f;

					// Back Left Color
					modelColoring.getGradientColor((float)backLeftPoints[1], (float)elevationMin, (float)elevationMax, reliefColor);
					//color = modelColoring.getGradientColor(point.getBackLeftElevation(), (float)elevationMin, (float)elevationMax);
					
					////color.darkenColor((1.0 - dot));
					
					
					if (getModelOptions().getHillShadeType() != DemConstants.HILLSHADING_NONE) {
						
						hillshadeColor[0] = reliefColor[0];
						hillshadeColor[1] = reliefColor[1];
						hillshadeColor[2] = reliefColor[2];
						
						double dot = Perspectives.dotProduct(normal, sunsource);
						
						//dot *= modelOptions.getLightingMultiple();
						//System.out.println("Spot Exponent: " + modelOptions.getSpotExponent());
						//dot = Math.pow(dot, 0.4);
						dot = Math.pow(dot, getModelOptions().getSpotExponent());
						
						switch (getModelOptions().getHillShadeType()) {
						case DemConstants.HILLSHADING_LIGHTEN:
							dot = (dot + 1.0) / 2.0;
							ColorAdjustments.adjustBrightness(hillshadeColor, 1.0 - dot);
							break;
						case DemConstants.HILLSHADING_DARKEN:
							dot = Math.abs((dot + 1.0) / 2.0) * -1.0;
							ColorAdjustments.adjustBrightness(hillshadeColor, dot);
							break;
						
						case DemConstants.HILLSHADING_COMBINED:
							
							if (dot > 0) {
								dot *= getModelOptions().getRelativeLightIntensity();
							} else if (dot < 0) {
								dot *= getModelOptions().getRelativeDarkIntensity();
							}
							
							ColorAdjustments.adjustBrightness(hillshadeColor, dot);
							break;
						}
						
						
						ColorAdjustments.interpolateColor(reliefColor, hillshadeColor, color, getModelOptions().getLightingMultiple());
						
						
					} else {
						color[0] = reliefColor[0];
						color[1] = reliefColor[1];
						color[2] = reliefColor[2];
					}

					canvas.setColor(imgCol, imgRow, color);
					
	
				}
				
				if (isCancelled()) {
					log.warn("Render process cancelled, model not complete.");
					break;
				}
			}
			
			
			if (row % 1000 == 0) {
				double pctComplete = ((double)row - fromRow) / ((double)numRows);
				pctComplete = Math.floor(pctComplete * 100);
				if (pctComplete > progress) {
					log.info("Percent Complete: " + pctComplete + "%");
					//System.out.println("Percent Complete: " + (pctComplete) + "%");
				}
				progress = pctComplete;
			}
			
			if (isCancelled()) {
				log.warn("Render process cancelled, model not complete.");
				break;
			}
		}
		
		
		log.info("Percent Complete: 100%");
		
		return canvas;
	}
	
	public void applyTiledBackground(DemCanvas canvas, String path) throws RenderEngineException
	{
		try {
			Image tiledImage = ImageIcons.loadImage(path);
			applyTiledBackground(canvas, tiledImage);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RenderEngineException("Failed to load tiled image @ " + path, ex);
		}
	}
	
	public void applyTiledBackground(DemCanvas canvas, Image tiledImage) throws RenderEngineException
	{
		log.info("Applying tile background image");
		
		int tileWidth = tiledImage.getWidth(this);
		int tileHeight = tiledImage.getHeight(this);
		
		Image demImage = canvas.getImage();
		Graphics2D g2d = (Graphics2D) demImage.getGraphics();
		
		int demWidth = demImage.getWidth(this);
		int demHeight = demImage.getHeight(this);
		
		for (int x = 0; x < demWidth; x += tileWidth) {
			for (int y = 0; y < demHeight; y += tileHeight) {
				g2d.drawImage(tiledImage, x, y, this);
			}
		}
		
		g2d.dispose();
		
	}

	
	public void applyShapefileLayers(DemCanvas canvas) throws RenderEngineException
	{
		if (isCancelled()) {
			log.warn("Render process cancelled, model not complete.");
			return;
		}
		
		int numLayers = getDataPackage().getShapeFiles().size();
		int layerNumber = 0;
		
		for (ShapeFileRequest shapeFilePath : getDataPackage().getShapeFiles()) {
			layerNumber++;
			try {
				log.info("Loading shapefile from " + shapeFilePath.getPath());
				//ShapeBase shapeBase = new ShapeBase(shapeFilePath.getPath(), shapeFilePath.getShapeDataDefinitionId());
				ShapeBase shapeBase = shapeFilePath.open();
				ShapeLayer shapeLayer = new ShapeLayer(shapeBase.getShapeType());
				
				log.info("Loading " + shapeBase.getShapeCount() + " shapes");
				for (int i = 0; i < shapeBase.getShapeCount(); i++) {
					Shape shape = shapeBase.getShape(i);
					shapeLayer.addShape(shape);
				}
				
				shapeLayer.translate(new PointTranslateHandler() {
					public void translatePoint(double[] coords)
					{
						double x = getDataPackage().longitudeToColumn((float) coords[0]);
						double y = getDataPackage().latitudeToRow((float) coords[1]);
						coords[0] = x;
						coords[1] = y;
					}
				}, false);
				
				shapeLayer = shapeLayer.getCombinedPathsByTypes();
				
				Image layerImage = renderLayer(shapeLayer);
				layerImage = layerImage.getScaledInstance(canvas.getWidth(), canvas.getHeight(), Image.SCALE_SMOOTH);
				canvas.overlay(layerImage, 0, 0);
				
				shapeBase.close();
				fireTileCompletionListeners(canvas, canvas, ((double)layerNumber) / ((double)numLayers));
				
			} catch (OutOfMemoryError err) {
				throw err;
			} catch(Exception ex) {
				throw new RenderEngineException("Error occured rendering shape files", ex);
				//ex.printStackTrace();
			}
			
			if (isCancelled()) {
				log.warn("Render process cancelled, model not complete.");
				break;
			}
		}
		
		log.info("Completed shapefile rendering");
		
	}
	

	public Image renderLayer(ShapeLayer shapeLayer)
	{
		//Image image = canvas.getImage();
		BufferedImage image = new BufferedImage((int)getDataPackage().getColumns(), (int)getDataPackage().getRows(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int shapeType = shapeLayer.getType();
		log.info("Shape Type: " + shapeType);
		fillShapes(shapeLayer, g2d);
		//DemCanvas filledCanvas = new DemCanvas(image);
		
		g2d.dispose();
		return image;

	}
	

	public void fillShapes(ShapeLayer shapeLayer, Graphics2D g2d)
	{
		log.info("Creating shape paths");

		log.info("Drawing " + shapeLayer.size() + " polylines");
		int shapeType = shapeLayer.getType();

		for (ShapePath path : shapeLayer.getShapePaths()) {		
			
			FeatureTypeStroke featureStroke = null;//path.getFeatureType().featureStroke();
			
			if (path.getFeatureType() != null) {
				featureStroke = path.getFeatureType().getFeatureTypeStroke();
			}
			
			if (featureStroke == null) {
				featureStroke = FeatureTypeStroke.getDefaultFeatureTypeStroke();
			}
			
			List<LineStroke> lineStrokes = featureStroke.getLineStrokes();

			for (LineStroke lineStroke : lineStrokes) {
			
				g2d.setStroke(lineStroke);
				g2d.setColor(lineStroke.getColor());
				
				if (shapeType == ShapeConstants.TYPE_POLYGON ||
						shapeType == ShapeConstants.TYPE_POLYGONM ||
						shapeType == ShapeConstants.TYPE_POLYGONZ) {
					
					g2d.fill(path);
					
				} else if (shapeType == ShapeConstants.TYPE_POLYLINE ||
							shapeType == ShapeConstants.TYPE_POLYLINEM ||
							shapeType == ShapeConstants.TYPE_POLYLINEZ) {
					
					g2d.draw(path);

				}					
			}	
			if (isCancelled()) {
				log.warn("Render process cancelled, model not complete.");
				break;
			}
		}

	}

	
	

	
	
}