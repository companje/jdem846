package us.wthr.jdem846.render.render2d;

import java.util.List;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.render.RenderPipeline;
import us.wthr.jdem846.scripting.ScriptProxy;

@Deprecated
public class ModelRenderer extends InterruptibleProcess
{
	private static Log log = Logging.getLog(ModelRenderer.class);

	protected ModelContext modelContext;
	private List<TileCompletionListener> tileCompletionListeners;
	
	private TileRenderer tileRenderer = null;
	private RenderPipeline renderPipeline;
	
	public ModelRenderer(ModelContext modelContext, List<TileCompletionListener> tileCompletionListeners)
	{
		this(modelContext, null, tileCompletionListeners);
	}
	
	public ModelRenderer(ModelContext modelContext, RenderPipeline renderPipeline, List<TileCompletionListener> tileCompletionListeners)
	{
		this.modelContext = modelContext;
		this.tileCompletionListeners = tileCompletionListeners;

		this.renderPipeline = renderPipeline;

	}
	
	public ModelCanvas renderModel() throws RenderEngineException
	{

		
		//ModelDimensions2D modelDimensions = modelContext.getModelDimensions();//ModelDimensions2D.getModelDimensions(modelContext);

		//int tileRow = 0;
		//int tileColumn = 0;
		//int tileSize = modelDimensions.getEffectiveTileSize();

		double northLimit = modelContext.getNorth();
		double southLimit = modelContext.getSouth();
		double eastLimit = modelContext.getEast();
		double westLimit = modelContext.getWest();

		log.info("Model North Limit: " + northLimit);
		log.info("Model South Limit: " + southLimit);
		log.info("Model East Limit: " + eastLimit);
		log.info("Model West Limit: " + westLimit);
		
		//double latitudeResolution = modelDimensions.getOutputLatitudeResolution();
		//double longitudeResolution = modelDimensions.getOutputLongitudeResolution();

		
		//double latitudeResolution = getRasterDataContext().getEffectiveLatitudeResolution();
		//double longitudeResolution = getRasterDataContext().getEffectiveLongitudeResolution();

		//double tileLatitudeHeight = latitudeResolution * tileSize - latitudeResolution;
		//double tileLongitudeWidth = longitudeResolution * tileSize - longitudeResolution;

		//log.info("Tile Size: " + tileSize);
		//log.info("Tile Latitude Height: " + tileLatitudeHeight);
		//log.info("Tile Longitude Width: " + tileLongitudeWidth);
		
		ModelColoring modelColoring = ColoringRegistry.getInstance(getModelOptions().getColoringType()).getImpl();

		final ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		
		on2DModelBefore(modelCanvas);
		
		
		
		tileRenderer = new TileRenderer(modelContext, modelColoring, modelCanvas, renderPipeline);
		
		this.setProcessInterruptListener(new ProcessInterruptListener() {
			public void onProcessCancelled()
			{
				tileRenderer.cancel();
			}
			public void onProcessPaused()
			{
				fireTileCompletionListeners(modelCanvas, 0);
				tileRenderer.pause();
			}
			public void onProcessResumed()
			{
				tileRenderer.resume();
			}
		});
		

		if ( getRasterDataContext().getRasterDataListSize() > 0 || (getImageDataContext() != null && getImageDataContext().getImageListSize() > 0)) {
			TileRenderContainer tileRenderContainer = null;
			tileRenderContainer = new TileRenderContainer(modelContext, tileRenderer, northLimit, southLimit, eastLimit, westLimit, 0, 0);

			if (renderPipeline != null) {
				renderPipeline.submit(tileRenderContainer);
			} else {
				tileRenderContainer.render(null);
			}
			
			
		}
		
	
		
		on2DModelAfter(modelCanvas);
		
		
		
		return modelCanvas;
	}
	
	public void dispose()
	{
		log.info("Disposing model renderer");
		
		if (tileRenderer != null) {
			tileRenderer.dispose();
			tileRenderer = null;
		}
	}
	
	protected void fireTileCompletionListeners(ModelCanvas modelCanvas, double pctComplete)
	{
		if (tileCompletionListeners != null) {
			for (TileCompletionListener listener : tileCompletionListeners) {
				listener.onTileCompleted(modelCanvas, pctComplete);
			}
		}
	}
	
	protected RasterDataContext getRasterDataContext()
	{
		return modelContext.getRasterDataContext();
	}

	
	protected ImageDataContext getImageDataContext()
	{
		return modelContext.getImageDataContext();
	}
	
	protected ModelOptions getModelOptions()
	{
		return modelContext.getModelOptions();
	}
	
	
	protected void on2DModelBefore(ModelCanvas modelCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.on2DModelBefore(modelContext, modelCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void on2DModelAfter(ModelCanvas modelCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.on2DModelAfter(modelContext, modelCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
	}

	
	
	
	
	public static ModelCanvas render(ModelContext modelContext) throws RenderEngineException
	{
		ModelRenderer renderer = new ModelRenderer(modelContext, null);
		ModelCanvas canvas = renderer.renderModel();
		return canvas;
	}
	
	public static ModelCanvas render(ModelContext modelContext, List<TileCompletionListener> tileCompletionListeners) throws RenderEngineException
	{
		ModelRenderer renderer = new ModelRenderer(modelContext, tileCompletionListeners);
		ModelCanvas canvas = renderer.renderModel();
		return canvas;
	}
}
