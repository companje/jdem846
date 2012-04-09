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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelDimensions2D;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;

public class GriddedModelGenerator
{
	private static Log log = Logging.getLog(GriddedModelGenerator.class);
	
	private ModelContext modelContext;
	private RasterDataContext rasterDataContext;
	private String tempPath;
	private ImageTypeEnum imageType;
	private List<TileCompletionListener> tileCompletionListeners;
	
	
	protected GriddedModelGenerator(ModelContext modelContext, String tempPath, ImageTypeEnum imageType, List<TileCompletionListener> tileCompletionListeners)
	{
		this.modelContext = modelContext;
		this.rasterDataContext = modelContext.getRasterDataContext();
		this.tempPath = tempPath;
		this.imageType = imageType;
		this.tileCompletionListeners = tileCompletionListeners;
	}
	
	protected GriddedModel generate() throws RenderEngineException, DataSourceException
	{
		ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(modelContext);

		int tileRow = 0;
		int tileCol = 0;
		int tileNum = 0;
		int dataRows = modelDimensions.getDataRows();
		int dataCols = modelDimensions.getDataColumns();
		int tileSize = 0;//modelDimensions.getTileSize();
		// TODO: This is broken...
		
		DemCanvas tileCanvas = new DemCanvas(new Color(0x0, 0x0, 0x0, 0x0), tileSize, tileSize);

		//double latResolution = tileSize * modelDimensions.getyDim();
		//double lonResolution = tileSize * modelDimensions.getxDim();
		
		double latResolution = tileSize * rasterDataContext.getLatitudeResolution();
		double lonResolution = tileSize * rasterDataContext.getLongitudeResolution();
		
		GriddedModel model = new GriddedModel(latResolution, lonResolution);
		model.setNorth(rasterDataContext.getNorth());
		model.setSouth(rasterDataContext.getSouth());
		model.setWest(rasterDataContext.getWest());
		model.setEast(rasterDataContext.getEast());
		
		
		
		if (rasterDataContext.getRasterDataListSize() > 0) {
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
					
					// TODO: Restore render method call with ModelCanvas
					//TileRenderer.render(fromRow, toRow, fromCol, toCol, modelContext, tileCanvas);
					
					BufferedImage tileImage = getCroppedImage(tileCanvas.getImage(), (toCol - fromCol) + 1, (toRow - fromRow) + 1);
					
					File tileFile = null;
					try {
						tileFile = saveTileImage(tileImage, fromRow, fromCol, toRow, toCol, tempPath);
					} catch (ImageException ex) {
						throw new RenderEngineException("Failed to save tile image to disk: " + ex.getMessage(), ex);
					}
					
					double west = rasterDataContext.columnToLongitude(fromCol - 1);
					double east = rasterDataContext.columnToLongitude(toCol);
					
					double north = rasterDataContext.rowToLatitude(fromRow - 1);
					double south = rasterDataContext.rowToLatitude(toRow);
					
					Tile tile = new Tile(tileFile, fromRow, fromCol, toRow, toCol, north, south, east, west);
					model.addTile(tile);

					//dem2d.unloadData();
					
					tileCol++;
					
					// TODO: Restore correct canvas type
					//fireTileCompletionListeners(modelCanvas,((double)tileNum) / ((double)modelDimensions.getTileCount()));
				}
				
				tileRow++;
			}
		}
		
		return model;
	}
	
	
	protected BufferedImage getCroppedImage(Image original, int width, int height)
	{
		log.info("Cropping image of width/height: " + width + "/" + height);
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D) image.createGraphics();
		g2d.drawImage(original, 0, 0, null);
		g2d.dispose();
		
		
		return image;
	}
	
	protected File saveTileImage(BufferedImage image, int fromRow, int fromCol, int toRow, int toCol, String outputPath) throws ImageException
	{
		String fileName = "tile-" + fromRow + "-" + toRow + "-" + fromCol + "-" + toCol + "." + imageType.extension();
		
		String path = outputPath + "/" + fileName;
		log.info("Writing image to " + path);
		

		ImageWriter.saveImage(image, path, imageType);

		//canvas.save(path);
		
		return (new File(path));
	}
	
	
	protected void fireTileCompletionListeners(ModelCanvas modelCanvas, double pctComplete)
	{
		for (TileCompletionListener listener : tileCompletionListeners) {
			listener.onTileCompleted(modelCanvas, pctComplete);
		}
	}
	
	public static GriddedModel generate(ModelContext modelContext, String tempPath, ImageTypeEnum imageType, List<TileCompletionListener> tileCompletionListeners) throws RenderEngineException, DataSourceException
	{
		GriddedModelGenerator generator = new GriddedModelGenerator(modelContext, tempPath, imageType, tileCompletionListeners);
		
		//Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
		GriddedModel model = generator.generate();
		
		return model;
	}
	
	
	
}
