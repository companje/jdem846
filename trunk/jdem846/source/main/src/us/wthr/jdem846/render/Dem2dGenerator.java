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

import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.render.render3.ModelBuilder;
import us.wthr.jdem846.render.render3.ModelGrid;
import us.wthr.jdem846.render.render3.ModelRenderer;
import us.wthr.jdem846.render.shapelayer.ShapeLayerRenderer;
import us.wthr.jdem846.scripting.ScriptProxy;

@DemEngine(name="us.wthr.jdem846.render.demEngine2D.name", 
	identifier="dem2d-gen",
	usesElevationMultiple=true,
	usesProjection=true)
public class Dem2dGenerator extends BasicRenderEngine
{
	private static Log log = Logging.getLog(Dem2dGenerator.class);
	
	//private ModelRenderer rasterRenderer = null;
	
	private ModelBuilder modelBuilder;
	private ModelGrid modelGrid;
	private ModelRenderer modelRenderer;
	
	private ShapeLayerRenderer shapeRenderer = null;
	
	private RenderPipeline renderPipeline = null;
	private RenderPipelineProcessContainer pipelineContainer = null;
	
	public Dem2dGenerator()
	{
		super();
	}
	
	public Dem2dGenerator(ModelContext modelContext)
	{
		super(modelContext);
	}
	
	public OutputProduct<ModelCanvas> generate() throws RenderEngineException
	{
		return generate(false, false);
	}
	
	
	public OutputProduct<ModelCanvas> generate(boolean skipElevation, boolean skipShapes) throws RenderEngineException
	{
		boolean usePipelineRender = JDem846Properties.getBooleanProperty("us.wthr.jdem846.performance.pipelineRender");
		
		fillRasterBuffers();
		fillImageBuffers();
		
		final CancelIndicator cancelIndicator = new CancelIndicator();
		
		this.setProcessInterruptListener(new ProcessInterruptListener() {
			public void onProcessCancelled()
			{
				
				// Expect a NullPointerException or two when these are called. Should probably fix those...
				
				cancelIndicator.cancel();
				
				modelBuilder.cancel();
				modelRenderer.cancel();
				shapeRenderer.cancel();
				
				if (renderPipeline != null) {
					renderPipeline.closeQueues();
				}
				
				if (renderPipeline != null) {
					renderPipeline.flushQueues();
				}
			}
			public void onProcessPaused()
			{
				modelBuilder.cancel();
				modelRenderer.cancel();
				shapeRenderer.pause();
			}
			public void onProcessResumed()
			{
				modelBuilder.cancel();
				modelRenderer.cancel();
				shapeRenderer.resume();
			}
		});
		
		
		try {
			getModelContext().updateContext(true, cancelIndicator);
		} catch (ModelContextException ex) {
			throw new RenderEngineException("Exception updating model context: " + ex.getMessage(), ex);
		}
		
		
		modelGrid = new ModelGrid(getModelContext().getNorth(), 
				getModelContext().getSouth(), 
				getModelContext().getEast(), 
				getModelContext().getWest(), 
				getModelContext().getModelDimensions().getOutputLatitudeResolution(), 
				getModelContext().getModelDimensions().getOutputLongitudeResolution());
		modelBuilder = new ModelBuilder(getModelContext(), modelGrid);
		modelBuilder.prepare();
		
		modelRenderer = new ModelRenderer(getModelContext(), modelGrid);
		modelRenderer.prepare();
		
		
		
		OutputProduct<ModelCanvas> product = null;
		
		if (!cancelIndicator.isCancelled()) {
			if (usePipelineRender) {
				product = generatePipelined(skipElevation, skipShapes);
			} else {
				product = generateStandard(skipElevation, skipShapes);
			}
		}
		
		if (modelBuilder != null) {
			modelBuilder.dispose();
		}
		
		if (modelRenderer != null) {
			modelRenderer.dispose();
		}
		
		if (modelGrid != null) {
			modelGrid.dispose();
		}
		
		clearRasterBuffers();
		clearImageBuffers();
		
		return product;
	}
	
	public OutputProduct<ModelCanvas> generateStandard(boolean skipElevation, boolean skipShapes) throws RenderEngineException
	{
		OutputProduct<ModelCanvas> product = null;
		
		try {
			ScriptProxy scriptProxy = getModelContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.initialize(getModelContext());
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
		
		
		try {
			
			//rasterRenderer = getModelRenderer(getModelContext(), renderPipeline, tileCompletionListeners);
			
			
			
			shapeRenderer = new ShapeLayerRenderer(getModelContext(), renderPipeline, tileCompletionListeners);
			
			
			updateCoordinateLimits();
			ModelCanvas canvas = getModelContext().getModelCanvas(true);
			
			
			
			
			if (!skipElevation) {
				modelBuilder.process();
				modelRenderer.process();
			}
			
			
			if (!skipShapes && !isCancelled() && getModelContext().getShapeDataContext() != null && getModelContext().getShapeDataContext().getShapeDataListSize() > 0) {
				shapeRenderer.render();
			} else {
				log.info("Shape data context is null, skipping render stage");
			}
			//Thread.sleep(3000);
			//startPipelineProcesses(pipelineContainer, true);
			//waitForPipelineProcessCompletion(pipelineContainer);
			
			
			product = new OutputProduct<ModelCanvas>(OutputProduct.IMAGE, getModelContext().getModelCanvas());
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
		
		try {
			ScriptProxy scriptProxy = getModelContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.destroy(getModelContext());
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
		return product;
	}
	
	
	public OutputProduct<ModelCanvas> generatePipelined(boolean skipElevation, boolean skipShapes) throws RenderEngineException
	{
		OutputProduct<ModelCanvas> product = null;
		
		try {
			ScriptProxy scriptProxy = getModelContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.initialize(getModelContext());
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
		
		final RenderPipeline renderPipeline = new RenderPipeline(getModelContext());
		final RenderPipelineProcessContainer pipelineContainer = new RenderPipelineProcessContainer(renderPipeline, getModelContext());
		
		//ModelRenderer rasterRenderer = null;
		//ShapeLayerRenderer shapeRenderer = null;
		
		
		try {
			
			//rasterRenderer = getModelRenderer(getModelContext(), renderPipeline, tileCompletionListeners);
			shapeRenderer = new ShapeLayerRenderer(getModelContext(), renderPipeline, tileCompletionListeners);
			
			
			
			
			updateCoordinateLimits();
			
			//ModelCanvas canvas = null;
			ModelCanvas canvas = getModelContext().getModelCanvas(true);
			
			
			
			
			if (!skipElevation) {
				modelBuilder.process();
				modelRenderer.process();
				//rasterRenderer.renderModel();
			}
			
			
			if (!skipShapes && !isCancelled() && getModelContext().getShapeDataContext() != null && getModelContext().getShapeDataContext().getShapeDataListSize() > 0) {
				shapeRenderer.render();
			} else {
				log.info("Shape data context is null, skipping render stage");
			}
			//Thread.sleep(3000);
			startPipelineProcesses(pipelineContainer, true);
			//waitForPipelineProcessCompletion(pipelineContainer);
			
			
			product = new OutputProduct<ModelCanvas>(OutputProduct.IMAGE, getModelContext().getModelCanvas());
		} catch (OutOfMemoryError err) {
			log.error("Out of memory error when generating model", err);
			throw new RenderEngineException("Out of memory error when generating model", err);
		} catch (Exception ex) {
			log.error("Error occured generating model", ex);
			throw new RenderEngineException("Error occured generating model", ex);
		}
		
		try {
			ScriptProxy scriptProxy = getModelContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.destroy(getModelContext());
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		

		
		
		if (shapeRenderer != null) {
			//shapeRenderer.dispose();
		}
		
		
		return product;
	}
	
	
	protected void fillImageBuffers() throws RenderEngineException
	{
		if (getModelContext().getImageDataContext() != null) {
			log.info("Loading image context data");
			
			try {
				getModelContext().getImageDataContext().loadImageData();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to load image data: " + ex.getMessage(), ex);
			}
		}
	}
	
	protected void clearImageBuffers() throws RenderEngineException
	{
		if (getModelContext().getImageDataContext() != null) {
			try {
				getModelContext().getImageDataContext().unloadImageData();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to unload image data: " + ex.getMessage(), ex);
			}
		}
	}
	
	protected void fillRasterBuffers() throws RenderEngineException
	{
		boolean fullCaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_FULL);
		
		if (fullCaching) {
			log.info("Loading Raster Buffers");
			try {
				getRasterDataContext().fillBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to prebuffer raster data: " + ex.getMessage(), ex);
			}
			log.info("Loaded Raster Buffers");
		}
	}
	
	protected void clearRasterBuffers() throws RenderEngineException
	{
		boolean fullCaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_FULL);
		
		if (fullCaching) {
			log.info("Clearing Raster Buffers");
			try {
				getRasterDataContext().clearBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to prebuffer raster data: " + ex.getMessage(), ex);
			}
			log.info("Raster Buffered Cleared");
		}
	}
	
	protected void updateCoordinateLimits() throws RenderEngineException
	{
		if (getModelOptions().getBooleanOption(ModelOptionNamesEnum.LIMIT_COORDINATES)) {
			
			double optNorthLimit = getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_NORTH);
			double optSouthLimit = getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_SOUTH);
			double optEastLimit = getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_EAST);
			double optWestLimit = getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_WEST);
			
			if (optNorthLimit != DemConstants.ELEV_NO_DATA)
				getModelContext().setNorthLimit(optNorthLimit);
			if (optSouthLimit != DemConstants.ELEV_NO_DATA)
				getModelContext().setSouthLimit(optSouthLimit);
			if (optEastLimit != DemConstants.ELEV_NO_DATA)
				getModelContext().setEastLimit(optEastLimit);
			if (optWestLimit != DemConstants.ELEV_NO_DATA)
				getModelContext().setWestLimit(optWestLimit);
		}
		try {
			getModelContext().updateContext();
		} catch (ModelContextException ex) {
			throw new RenderEngineException("Error updating model context: " + ex.getMessage(), ex);
		}
	}
	
	protected void startPipelineProcesses(RenderPipelineProcessContainer pipelineContainer, boolean waitForCompletion)
	{
		
		log.info("Starting pipeline container...");
		pipelineContainer.start();
		
		if (waitForCompletion) {
			waitForPipelineProcessCompletion(pipelineContainer);
		}
	}
	
	protected void waitForPipelineProcessCompletion(RenderPipelineProcessContainer pipelineContainer)
	{

		while(true) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				log.warn("Pipeline wait loop delay interrupted: " + ex.getMessage(), ex);
			}
			
			if (pipelineContainer.isTileProcessPipeCompleted() && pipelineContainer.areQueuesEmpty()) {
				log.info("Pipeline queues emptied. Stopping pipeline threads");
				pipelineContainer.stop(true);
				log.info("Pipeline threads exited. Cleaning up model render");
				break;
			}
			
			
			
			fireTileCompletionListeners();
			Thread.yield();
			if (isCancelled()) {
				break;
			}
		}
	}
	
	

	
	protected void fireTileCompletionListeners()
	{
		for (TileCompletionListener listener : tileCompletionListeners) {
			listener.onTileCompleted(getModelContext().getModelCanvas(), 0);
		}
	}
	
	/*
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
	*/

	
	
	

	
	
}