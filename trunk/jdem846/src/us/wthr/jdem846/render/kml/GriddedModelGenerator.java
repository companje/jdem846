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

package us.wthr.jdem846.render.kml;

import java.awt.Color;
import java.io.File;
import java.util.List;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelDimensions2D;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;

public class GriddedModelGenerator
{
	private static Log log = Logging.getLog(GriddedModelGenerator.class);
	
	private DataPackage dataPackage;
	private ModelOptions modelOptions;
	private String tempPath;
	private List<TileCompletionListener> tileCompletionListeners;
	
	protected GriddedModelGenerator(DataPackage dataPackage, ModelOptions modelOptions, String tempPath, List<TileCompletionListener> tileCompletionListeners)
	{
		this.dataPackage = dataPackage;
		this.modelOptions = modelOptions;
		this.tempPath = tempPath;
		this.tileCompletionListeners = tileCompletionListeners;
	}
	
	protected GriddedModel generate(Dem2dGenerator dem2d) throws RenderEngineException, DataSourceException
	{
		ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(dataPackage, modelOptions);

		int tileRow = 0;
		int tileCol = 0;
		int tileNum = 0;
		int dataRows = modelDimensions.getDataRows();
		int dataCols = modelDimensions.getDataColumns();
		int tileSize = modelDimensions.getTileSize();
		
		DemCanvas tileCanvas = new DemCanvas(new Color(0x0, 0x0, 0x0, 0x0), (int)modelDimensions.getTileSize(), (int)modelDimensions.getTileSize());

		//double latResolution = tileSize * modelDimensions.getyDim();
		//double lonResolution = tileSize * modelDimensions.getxDim();
		
		double latResolution = tileSize * dataPackage.getAvgYDim();
		double lonResolution = tileSize * dataPackage.getAvgXDim();
		
		GriddedModel model = new GriddedModel(latResolution, lonResolution);
		model.setNorth(dataPackage.getMaxLatitude());
		model.setSouth(dataPackage.getMinLatitude());
		model.setWest(dataPackage.getMinLongitude());
		model.setEast(dataPackage.getMaxLongitude());
		
		
		
		if (dataPackage.getDataSources().size() > 0) {
			for (int fromRow = 0; fromRow < dataRows; fromRow+=tileSize) {
				int toRow = fromRow + tileSize - 1;
				if (toRow > dataRows)
					toRow = dataRows;
			
				tileCol = 0;
				for (int fromCol = 0; fromCol < dataCols; fromCol+=tileSize) {
					int toCol = fromCol + tileSize - 1;
					if (toCol > dataCols)
						toCol = dataCols;
					
					//dem2d.loadDataSubset((int) fromCol, (int) fromRow, (int) tileSize, (int) tileSize);
					//dem2d.precacheData();
					
					tileCanvas.reset();
					
					//dem2d.generate(fromRow, toRow, fromCol, toCol, tileCanvas);
					
					//saveTileImage(DemCanvas canvas, int fromRow, int fromCol, int toRow, int toCol, String outputPath)
					File tileFile = saveTileImage(tileCanvas, fromRow, fromCol, toRow, toCol, tempPath);
					
					double west = dataPackage.columnToLongitude(fromCol - 1);
					double east = dataPackage.columnToLongitude(toCol);
					
					double north = dataPackage.rowToLatitude(fromRow - 1);
					double south = dataPackage.rowToLatitude(toRow);
					
					Tile tile = new Tile(tileFile, fromRow, fromCol, toRow, toCol, north, south, east, west);
					model.addTile(tile);

					//dem2d.unloadData();
					
					tileCol++;
					
					fireTileCompletionListeners(tileCanvas,((double)tileNum) / ((double)modelDimensions.getTileCount()));
				}
				
				tileRow++;
			}
		}
		
		return model;
	}
	
	
	
	protected File saveTileImage(DemCanvas canvas, int fromRow, int fromCol, int toRow, int toCol, String outputPath)
	{
		String fileName = "tile-" + fromRow + "-" + toRow + "-" + fromCol + "-" + toCol + ".png";
		
		String path = outputPath + "/" + fileName;
		log.info("Writing image to " + path);

		//canvas.save(path);
		
		return (new File(path));
	}
	
	
	protected void fireTileCompletionListeners(DemCanvas tileCanvas, double pctComplete)
	{
		for (TileCompletionListener listener : tileCompletionListeners) {
			listener.onTileCompleted(tileCanvas, tileCanvas, pctComplete);
		}
	}
	
	public static GriddedModel generate(DataPackage dataPackage, ModelOptions modelOptions, String tempPath, List<TileCompletionListener> tileCompletionListeners) throws RenderEngineException, DataSourceException
	{
		GriddedModelGenerator generator = new GriddedModelGenerator(dataPackage, modelOptions, tempPath, tileCompletionListeners);
		
		Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
		GriddedModel model = generator.generate(dem2d);
		
		return model;
	}
	
	
	
}
