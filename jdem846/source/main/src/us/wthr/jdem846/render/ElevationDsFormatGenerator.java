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

import java.awt.Rectangle;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.edef.ElevationDatasetExchange;
import us.wthr.jdem846.input.edef.ElevationDatasetExchangeHeader;
import us.wthr.jdem846.input.edef.ElevationDatasetExchangeWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataLatLongBox;
import us.wthr.jdem846.rasterdata.RasterDataRowColumnBox;
import us.wthr.jdem846.scaling.FloatRaster;
import us.wthr.jdem846.scaling.RasterScale;
import us.wthr.jdem846.scaling.ResizeDimensions;


@DemEngine(name="us.wthr.jdem846.render.elevationDsFormatGenerator.name", 
			identifier="edef-gen", 
			usesWidth=true,
			usesHeight=true,
			usesBackgroundColor=false, 
			usesColoring=false,
			usesHillshading=false,
			usesLightMultiple=false,
			usesSpotExponent=false,
			usesElevationMultiple=false,
			usesLightDirection=false,
			usesAntialiasing=false,
			usesPrecacheStrategy=true,
			generatesImage=false,
			usesRelativeLightMultiple=false,
			usesRelativeDarkMultiple=false,
			enabled=false,
			usesMapProjection=false,
			needsOutputFileOfType=ElevationDatasetExchange.class)
public class ElevationDsFormatGenerator extends RenderEngine
{
	
	private static Log log = Logging.getLog(ElevationDsFormatGenerator.class);
	
	//private DataPackage dataPackage;
	//private ModelOptions modelOptions;
	
	
	public ElevationDsFormatGenerator(ModelContext modelContext)
	{
		super.initialize(modelContext);
	}
	
	//public ElevationDsFormatGenerator(DataPackage dataPackage, ModelOptions modelOptions)
	//{
	//	this.dataPackage = dataPackage;
	//	this.modelOptions = modelOptions;
	//}

	
	
	
	@SuppressWarnings("unchecked")
	public OutputProduct generate() throws RenderEngineException
	{
		return generate(false, false);
	}
	
	@SuppressWarnings("unchecked")
	public OutputProduct generate(boolean skipElevation, boolean skipShapes) throws RenderEngineException
	{
		ModelOptions modelOptions = getModelOptions();
		RasterDataContext rasterDataContext = getRasterDataContext();
		//DataPackage dataPackage = getModelContext().getDataPackage();
		
		
		String writeTo = modelOptions.getWriteTo();
		if (writeTo == null) {
			writeTo = System.getProperty("user.home") + "/" + "output.edef";
		} else {
			if (!writeTo.toLowerCase().endsWith(".edef")) {
				writeTo = writeTo + ".edef";
    		}		
			
		}
		

		int dataCols = rasterDataContext.getDataColumns();
		int dataRows = rasterDataContext.getDataRows();
		
		int tileSize = modelOptions.getTileSize();
		if (tileSize > dataRows && dataRows > dataCols)
			tileSize = dataRows;
		
		if (tileSize > dataCols && dataCols > dataRows)
			tileSize = dataCols;
		
		int outputWidth = modelOptions.getWidth();
		int outputHeight = modelOptions.getHeight();
		
		Rectangle dimensions = ResizeDimensions.resize(dataCols, dataRows, outputWidth, outputHeight);
		
		outputWidth = dimensions.width;
		outputHeight = dimensions.height;
		
		log.info("Generating output of width/height: " + outputWidth + "/" + outputHeight);
		float cellSizeRatio = (float)outputWidth / (float)dataCols;
		
		
		float numTilesHoriz = ((float)dataCols) / ((float)tileSize);
		float numTilesVert = ((float)dataRows) / ((float)tileSize);
		
		long tileOutputWidth = Math.round(((float)outputWidth) / numTilesHoriz);
		long tileOutputHeight = Math.round(((float)outputHeight) / numTilesVert);
		
		
		int tileRow = 0;
		int tileCol = 0;
		int tileNum = 0;
		
		int numTiles = (int) (Math.ceil(((double)dataRows / (double)tileSize)) * Math.ceil(((double)dataCols / (double)tileSize)));
		
		FloatRaster tileRaster = new FloatRaster((int)tileSize, (int)tileSize);
		FloatRaster outputRaster = new FloatRaster(outputWidth, outputHeight);
		
		for (int fromRow = 0; fromRow <= dataRows; fromRow+=tileSize) {
			int toRow = fromRow + tileSize - 1;
			if (toRow > dataRows)
				toRow = dataRows;
		
			tileCol = 0;
			for (int fromCol = 0; fromCol <= dataCols; fromCol+=tileSize) {
				int toCol = fromCol + tileSize - 1;
				if (toCol > dataCols)
					toCol = dataCols;
				
				RasterDataRowColumnBox tileBounds = new RasterDataRowColumnBox((int) fromCol, (int) fromRow, (int) tileSize, (int) tileSize);
				if (rasterDataContext.dataOverlaps(tileBounds)) {
					log.info("Running tile #" + (tileNum + 1));
					tileRaster.reset();
					generate(fromRow, toRow, fromCol, toCol, tileRaster);
					
					FloatRaster scaled = RasterScale.scale(tileRaster, (int)tileOutputWidth, (int) tileOutputHeight);
					
					outputRaster.overlay(scaled, (int)Math.floor(tileCol * tileOutputWidth), (int)Math.floor(tileRow * tileOutputHeight), scaled.getWidth(), scaled.getHeight());
					
					tileNum++;
					
					fireTileCompletionListeners(null, ((double)tileNum) / ((double)numTiles));
				}
				tileCol++;	
			}
			tileRow++;	
			
		}
		
		writeOutput(writeTo, outputRaster, cellSizeRatio);
		return null;
		
	}
	
	public FloatRaster generate(int fromRow, int toRow, int fromColumn, int toColumn, FloatRaster raster) throws RenderEngineException
	{
		//DataPackage dataPackage = getModelContext().getDataPackage();
		
		RasterDataContext rasterDataContext = getRasterDataContext();
		
		int numRows = (toRow - fromRow) + 1;
		int numCols = (toColumn - fromColumn) + 1;
		int dataRow = -1;
		int dataCol = -1;

		if (raster == null) {
			raster = new FloatRaster((int)numCols, (int)numRows);
		}
		
		double elevation = 0;
		
		// TODO: Rewrite for RasterData API
		/*
		try {
			for (int row = fromRow; row <= toRow; row++) {
				dataRow++;
				dataCol = -1;
				
				for (int column = fromColumn; column <= toColumn; column++)  {
					dataCol++;
					
					
					elevation = dataPackage.getElevation(row, column);
					raster.set(dataCol, dataRow, (float)elevation);
	
				}
				
			}
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Failed to read source data: " + ex.getMessage(), ex);
		}
		*/

		return raster;
	}
	
	public void writeOutput(String writeTo, FloatRaster raster, float cellSizeRatio)
	{
		// TODO: Rewrite for raster data api
		log.warn("No implemented");
		/*
		DataPackage dataPackage = getModelContext().getDataPackage();
		ElevationDatasetExchangeHeader header = new ElevationDatasetExchangeHeader();
		header.setCellSize((float)dataPackage.getAvgXDim());
		header.setColumns(raster.getWidth());
		header.setRows(raster.getHeight());
		header.setMaxElevation((float)dataPackage.getMaxElevation());
		header.setMinElevation((float)dataPackage.getMinElevation());
		header.setxCellSize((float)dataPackage.getAvgXDim() / cellSizeRatio);
		header.setyCellSize((float)dataPackage.getAvgYDim() / cellSizeRatio);
		header.setxLowerLeft((float)dataPackage.getMinLongitude());
		header.setyLowerLeft((float)dataPackage.getMinLatitude());
		
		log.info("Writing EDEF to " + writeTo);
		ElevationDatasetExchangeWriter writer = new ElevationDatasetExchangeWriter(writeTo, header);
		
		try {
			writer.open();
			writer.writeHeader();
			
			for (int y = 0; y < raster.getHeight(); y++) {
				for (int x = 0; x < raster.getWidth(); x++) {
					writer.write(raster.get(x, y));
				}
			}
			
			
			writer.flush();
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log.info("Done");
		*/
		
	}
	
	
	
	//public DataPackage getDataPackage()
	//{
	//	return dataPackage;
	//}

	//public void setDataPackage(DataPackage dataPackage) 
	//{
	//	this.dataPackage = dataPackage;
	//}

	//public ModelOptions getModelOptions() 
	//{
	//	return modelOptions;
	//}

	//public void setModelOptions(ModelOptions modelOptions)
	//{
	//	this.modelOptions = modelOptions;
	//}
	
	
	
	
	
}
