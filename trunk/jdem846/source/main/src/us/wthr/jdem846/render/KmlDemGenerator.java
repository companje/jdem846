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

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.kml.KmlDocument;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.kml.GriddedModel;
import us.wthr.jdem846.render.kml.GriddedModelGenerator;
import us.wthr.jdem846.render.kml.KmlModelGenerator;
import us.wthr.jdem846.render.kml.Tile;

@DemEngine(name="us.wthr.jdem846.render.demEngineKml.name", identifier="dem-kml-gen", generatesImage=false, enabled=false)
public class KmlDemGenerator extends BasicRenderEngine
{
	private static Log log = Logging.getLog(KmlDemGenerator.class);
	
	private GriddedModel griddedModel;
	
	// TODO: Get these values from model options rather then getters/setters
	private String outputPath;
	private String tempPath;
	private int overlayTileSize;
	private int layerMultiplier;
	private ImageTypeEnum imageType;
	
	public KmlDemGenerator(ModelContext modelContext)
	{
		super(modelContext);
	}
	
	//public KmlDemGenerator(DataPackage dataPackage, ModelOptions modelOptions)
	//{
	//	super(dataPackage, modelOptions);
	//}
	
	@Override
	public OutputProduct<KmlDocument> generate() throws RenderEngineException
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
	public OutputProduct<KmlDocument> generate(boolean skipElevation, boolean skipShapes) throws RenderEngineException
	{
		
		// TODO: tile completion listeners
		try {
			
			griddedModel = GriddedModelGenerator.generate(getModelContext(), tempPath, imageType, tileCompletionListeners);
		} catch (Exception ex) {
			throw new RenderEngineException("Failed to generate 2D DEM: " + ex.getMessage(), ex);
		} 
		
		log.info("Total Tiles Generated: " + griddedModel.getTiles().size());
		
		try {
			KmlDocument kml = KmlModelGenerator.generate(getModelContext(), griddedModel, outputPath, overlayTileSize, layerMultiplier, "jDem846 Model", null, imageType, true);
			
			//cleanUpTemporaryFiles();
			
			return new OutputProduct<KmlDocument>(OutputProduct.EXPORT_SPEC, kml);
		} catch (RenderEngineException ex) {
			throw new RenderEngineException("Failed to create KML model: " + ex.getMessage(), ex);
		}

	}

	protected void cleanUpTemporaryFiles()
	{
		for (Tile tile : griddedModel.getTiles()) {
			tile.deleteImageFile();
		}
	}
	
	public String getOutputPath()
	{
		return outputPath;
	}

	public void setOutputPath(String outputPath)
	{
		this.outputPath = outputPath;
	}

	public String getTempPath()
	{
		return tempPath;
	}

	public void setTempPath(String tempPath)
	{
		this.tempPath = tempPath;
	}

	public int getOverlayTileSize()
	{
		return overlayTileSize;
	}

	public void setOverlayTileSize(int overlayTileSize)
	{
		this.overlayTileSize = overlayTileSize;
	}

	public int getLayerMultiplier()
	{
		return layerMultiplier;
	}

	public void setLayerMultiplier(int layerMultiplier)
	{
		this.layerMultiplier = layerMultiplier;
	}

	public ImageTypeEnum getImageType()
	{
		return imageType;
	}

	public void setImageType(ImageTypeEnum imageType)
	{
		this.imageType = imageType;
	}
	
	
	
}
