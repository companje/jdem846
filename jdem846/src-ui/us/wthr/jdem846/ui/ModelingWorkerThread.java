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

package us.wthr.jdem846.ui;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.render.RenderEngine;

@Deprecated
public class ModelingWorkerThread extends Thread
{
	private static Log log = Logging.getLog(ModelingWorkerThread.class);
	
	private RenderEngine engine;
	private RasterDataContext rasterDataContext;
	//private DataPackage dataPackage;
	//private ModelOptions modelOptions;
	
	private boolean previewModel = false;
	
	private List<TileCompletionListener> tileCompletionListeners = new LinkedList<TileCompletionListener>();
	private List<ModelCompletionListener> modelCompletionListeners = new LinkedList<ModelCompletionListener>();

	
	public ModelingWorkerThread(RenderEngine engine)
	{
		this.engine = engine;
		this.rasterDataContext = engine.getRasterDataContext();
		//this.dataPackage = engine.getDataPackage();
		//this.modelOptions = engine.getModelOptions();
	}
	
	@SuppressWarnings("unchecked")
	public void run()
	{
		log.info("Creating model within worker thread...");
		
		//if (dataPackage.getDataSourceCount() > 0) {
			
			long start = 0;
			long elapsed = 0;
			
			for (TileCompletionListener listener : tileCompletionListeners) {
				engine.addTileCompletionListener(listener);
			}
			
			start = System.currentTimeMillis();
			
			try {
				rasterDataContext.calculateElevationMinMax(true);
			} catch (Exception ex) {
				fireModelFailedListeners(ex);
				return;
			}
			
			elapsed = (System.currentTimeMillis() - start) / 1000;
			log.info("Completed elevation min/max task in " + elapsed + " seconds");
			
			try {
				start = System.currentTimeMillis();
				OutputProduct<ModelCanvas> product = engine.generate(this.isPreviewModel());
				elapsed = (System.currentTimeMillis() - start) / 1000;
				log.info("Completed render task in " + elapsed + " seconds");
				
				if (product != null)
					fireModelCompletionListeners(product.getProduct());
				else 
					fireModelCompletionListeners(null);
			} catch (RenderEngineException ex) {
				fireModelFailedListeners(ex);
			}
			
			
		//} else {
		//	fireModelCompletionListeners(null);
		//}
		
		
	}
	
	public void cancel()
	{
		if (engine != null) {
			engine.cancel();
		}
	}
	
	public boolean isCancelled()
	{
		if (engine != null) {
			return engine.isCancelled();
		} else {
			return false;
		}
	}
	
	public void setPreviewModel(boolean previewModel)
	{
		this.previewModel = previewModel;
	}
	
	public boolean isPreviewModel()
	{
		return previewModel;
	}
	
	public void addTileCompletionListener(TileCompletionListener listener)
	{
		tileCompletionListeners.add(listener);
	}
	
	public void removeTileCompletionListener(TileCompletionListener listener)
	{
		tileCompletionListeners.remove(listener);
	}
	
	
	public void addModelCompletionListener(ModelCompletionListener listener)
	{
		modelCompletionListeners.add(listener);
	}
	
	public void removeModelCompletionListener(ModelCompletionListener listener)
	{
		modelCompletionListeners.remove(listener);
	}
	
	protected void fireModelCompletionListeners(ModelCanvas modelCanvas)
	{
		for (ModelCompletionListener listener : modelCompletionListeners) {
			listener.onModelComplete(modelCanvas);
		}
	}
	
	protected void fireModelFailedListeners(Exception ex) 
	{
		for (ModelCompletionListener listener : modelCompletionListeners) {
			listener.onModelFailed(ex);
		}
	}
	
	public interface ModelCompletionListener
	{
		public void onModelComplete(ModelCanvas modelCanvas);
		public void onModelFailed(Exception ex);
	}
	
}
