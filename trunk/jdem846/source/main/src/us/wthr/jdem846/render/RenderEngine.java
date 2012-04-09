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

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.ElevationDataLoaderInstance;
import us.wthr.jdem846.input.ElevationDataLoaderRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.shapedata.ShapeDataContext;

public abstract class RenderEngine extends InterruptibleProcess implements ImageObserver
{
	private static Log log = Logging.getLog(RenderEngine.class);
	
	private DemEngine annotation = null;
	private String needsOutputFileOfTypeIdentifier = null;
	
	protected List<TileCompletionListener> tileCompletionListeners = new LinkedList<TileCompletionListener>();

	private ModelContext modelContext;
	
	public RenderEngine()
	{
		
	}
	
	public void initialize(ModelContext modelContext)
	{
		this.modelContext = modelContext;
		
		annotation = (DemEngine) this.getClass().getAnnotation(DemEngine.class);
		if (annotation != null) {
			

			log.info("Loading Dem Engine with properties:");
			log.info("Name: " + annotation.name());
			log.info("Identifier: " + annotation.identifier());
			/*
			log.info("Enabled: " + annotation.enabled());
			log.info("Uses Width: " + annotation.usesWidth());
			log.info("Uses Height: " + annotation.usesHeight());
			log.info("Uses Background Color: " + annotation.usesBackgroundColor());
			log.info("Uses Coloring: " + annotation.usesColoring());
			log.info("Uses Hillshading: " + annotation.usesHillshading());
			log.info("Uses Light Multiple: " + annotation.usesLightMultiple());
			log.info("Uses Tile Size: " + annotation.usesTileSize());
			log.info("Generates Image: " + annotation.generatesImage());
			*/
			
			Class<?> clazz = annotation.needsOutputFileOfType();
			if (clazz != null && clazz != Object.class) {
				ElevationDataLoader needsOutputFileOfTypeAnnotation = (ElevationDataLoader) clazz.getAnnotation(ElevationDataLoader.class);
				log.info("Needs Output File of Type: " + needsOutputFileOfTypeAnnotation.name());
				needsOutputFileOfTypeIdentifier = needsOutputFileOfTypeAnnotation.identifier();
			}
			
			
		}
	}
	

	@SuppressWarnings("unchecked")
	public abstract OutputProduct generate() throws RenderEngineException;
	
	@SuppressWarnings("unchecked")
	public abstract OutputProduct generate(boolean skipRasterData, boolean skipShapes) throws RenderEngineException;
	
	public RasterDataContext getRasterDataContext()
	{
		return modelContext.getRasterDataContext();
	}
	public ShapeDataContext getShapeDataContext()
	{
		return modelContext.getShapeDataContext();
	}
	
	//public abstract void setDataPackage(DataPackage dataPackage);

	public ModelOptions getModelOptions()
	{
		return modelContext.getModelOptions();
	}
	//public abstract void setModelOptions(ModelOptions modelOptions);
	
	public ScriptProxy getScriptProxy()
	{
		return modelContext.getScriptProxy();
	}
	
	public boolean enabled() { return annotation.enabled(); }
	public boolean usesWidth() { return annotation.usesWidth(); }
	public boolean usesHeight() { return annotation.usesHeight(); } 
	public boolean usesBackgroundColor() { return annotation.usesBackgroundColor(); }
	public boolean usesColoring() { return annotation.usesColoring(); } 
	public boolean usesHillshading() { return annotation.usesHillshading(); }
	public boolean usesLightMultiple() { return annotation.usesLightMultiple(); }
	public boolean usesTileSize() { return annotation.usesTileSize(); }
	public boolean generatesImage() { return annotation.generatesImage(); }
	
	public ElevationDataLoaderInstance needsOutputFileOfType()
	{
		if (needsOutputFileOfTypeIdentifier == null) {
			return null;
		} else {
			return ElevationDataLoaderRegistry.getInstance(needsOutputFileOfTypeIdentifier);
		}
	}
	
	
	
	public ModelContext getModelContext()
	{
		return modelContext;
	}

	public void addTileCompletionListener(TileCompletionListener listener)
	{
		tileCompletionListeners.add(listener);
	}
	
	public void removeTileCompletionListener(TileCompletionListener listener)
	{
		tileCompletionListeners.remove(listener);
	}
	
	protected void fireTileCompletionListeners(ModelCanvas modelCanvas, double pctComplete)
	{
		for (TileCompletionListener listener : tileCompletionListeners) {
			listener.onTileCompleted(modelCanvas, pctComplete);
		}
	}
	
	public interface TileCompletionListener
	{
		public void onTileCompleted(ModelCanvas modelCanvas, double pctComplete);
	}
	
	
	
	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y,
			int width, int height) {
		return true;
	}
}
