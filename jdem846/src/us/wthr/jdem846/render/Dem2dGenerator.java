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
import us.wthr.jdem846.annotations.DemEngine;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.render2d.ModelRenderer;
import us.wthr.jdem846.render.shapelayer.ShapeLayerRenderer;
import us.wthr.jdem846.scripting.ScriptProxy;

@DemEngine(name="us.wthr.jdem846.render.demEngine2D.name", 
	identifier="dem2d-gen",
	usesElevationMultiple=false)
public class Dem2dGenerator extends BasicRenderEngine
{
	private static Log log = Logging.getLog(Dem2dGenerator.class);
	
	
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
		return generate(false);
	}
	
	public OutputProduct<ModelCanvas> generate(boolean skipElevation) throws RenderEngineException
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
			
			final ModelRenderer rasterRenderer = new ModelRenderer(getModelContext(), tileCompletionListeners);
			final ShapeLayerRenderer shapeRenderer = new ShapeLayerRenderer(getModelContext(), tileCompletionListeners);
			
			this.setProcessCancelListener(new ProcessCancelListener() {
				public void onProcessCancelled()
				{
					rasterRenderer.cancel();
					shapeRenderer.cancel();
				}
			});
			
			ModelCanvas canvas = rasterRenderer.renderModel(skipElevation);

			if (getModelContext().getShapeDataContext() != null && getModelContext().getShapeDataContext().getShapeDataListSize() > 0) {
				shapeRenderer.renderShapeLayer();
			} else {
				log.info("Shape data context is null, skipping render stage");
			}
			
			product = new OutputProduct<ModelCanvas>(OutputProduct.IMAGE, canvas);
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