package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.ModelDimensions2D;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.gis.projections.MapProjectionProviderFactory;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.util.ColorSerializationUtil;

public class ModelRenderer extends InterruptibleProcess
{
	private static Log log = Logging.getLog(ModelRenderer.class);
	private static Color DEFAULT_BACKGROUND = new Color(0, 0, 0, 0);
	
	protected ModelContext modelContext;
	private List<TileCompletionListener> tileCompletionListeners;
	
	
	public ModelRenderer(ModelContext modelContext, List<TileCompletionListener> tileCompletionListeners)
	{
		this.modelContext = modelContext;
		this.tileCompletionListeners = tileCompletionListeners;
	}
	
	public ModelCanvas renderModel() throws RenderEngineException
	{
		ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(modelContext);

		boolean fullCaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_FULL);
		int tileNumber = 0;
		int tileRow = 0;
		int tileColumn = 0;
		int tileSize = modelDimensions.getTileSize();
		long tileCount = modelDimensions.getTileCount();
		
		
		double northLimit = getRasterDataContext().getNorth();
		double southLimit = getRasterDataContext().getSouth();
		double eastLimit = getRasterDataContext().getEast();
		double westLimit = getRasterDataContext().getWest();
		
		double latitudeResolution = getRasterDataContext().getLatitudeResolution();
		double longitudeResolution = getRasterDataContext().getLongitudeResolution();

		double tileLatitudeHeight = latitudeResolution * tileSize - latitudeResolution;
		double tileLongitudeWidth = longitudeResolution * tileSize - longitudeResolution;
		
		log.info("Tile Size: " + tileSize);
		log.info("Tile Latitude Height: " + tileLatitudeHeight);
		log.info("Tile Longitude Width: " + tileLongitudeWidth);
		
		ModelColoring modelColoring = ColoringRegistry.getInstance(getModelOptions().getColoringType()).getImpl();

		final ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		on2DModelBefore(modelCanvas);
		
		double pctComplete = 0;
		final TileRenderer tileRenderer = new TileRenderer(modelContext, modelColoring, modelCanvas);
		
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
		
		
		if (fullCaching) {
			try {
				getRasterDataContext().fillBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to prebuffer raster data: " + ex.getMessage(), ex);
			}
		}
		
		
		if ( getRasterDataContext().getRasterDataListSize() > 0) {
			
			
			// Latitude
			for (double tileNorth = northLimit; tileNorth > southLimit; tileNorth -= tileLatitudeHeight) {
				double tileSouth = tileNorth - tileLatitudeHeight;
				if (tileSouth <= southLimit) {
					tileSouth = southLimit + latitudeResolution;
				}
				
				tileColumn = 0;
				
				// Longitude
				for (double tileWest = westLimit; tileWest < eastLimit; tileWest += tileLongitudeWidth) {
					double tileEast = tileWest + tileLongitudeWidth;
					
					if (tileEast >= eastLimit) {
						tileEast = eastLimit - longitudeResolution;
					}
					
					
					log.info("Tile #" + (tileNumber + 1) + " of " + tileCount + ", Row #" + (tileRow + 1) + ", Column #" + (tileColumn + 1));
					log.info("    North: " + tileNorth);
					log.info("    South: " + tileSouth);
					log.info("    East: " + tileEast);
					log.info("    West: " + tileWest);	
					
					tileRenderer.renderTile(tileNorth, tileSouth, tileEast, tileWest);

					//tileRenderRunnable.run();
					
					tileColumn++;
					tileNumber++;

					
					
					pctComplete = (double)tileNumber / (double)tileCount;
					
					fireTileCompletionListeners(modelCanvas, pctComplete);
					
					if (isCancelled()) {
						break;
					}	
				}
				
				tileRow++;
				
				if (isCancelled()) {
					break;
				}
				
			}
			
		}
		
		if (fullCaching) {
			try {
				getRasterDataContext().clearBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to prebuffer raster data: " + ex.getMessage(), ex);
			}
		}
		
		
		on2DModelAfter(modelCanvas);
		
		
		
		return modelCanvas;
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
