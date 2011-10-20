package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelDimensions2D;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.util.ColorSerializationUtil;

public class ModelRenderer
{
	private static Log log = Logging.getLog(ModelRenderer.class);
	private static Color DEFAULT_BACKGROUND = new Color(0, 0, 0, 0);
	
	private ModelContext modelContext;
	private List<TileCompletionListener> tileCompletionListeners;
	private boolean cancel = false;
	
	
	public ModelRenderer(ModelContext modelContext, List<TileCompletionListener> tileCompletionListeners)
	{
		this.modelContext = modelContext;
		this.tileCompletionListeners = tileCompletionListeners;
	}
	
	public DemCanvas renderModel(boolean skipElevation) throws RenderEngineException
	{
		ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(getDataPackage(), getModelOptions());
		getDataPackage().setAvgXDim(modelDimensions.getxDim());
		getDataPackage().setAvgYDim(modelDimensions.getyDim());
		
		Color backgroundColor = ColorSerializationUtil.stringToColor(getModelOptions().getBackgroundColor());
		
		int gridSize = getModelOptions().getGridSize();
		int tileSizeAdjusted = (int)Math.round((double)modelDimensions.getTileSize() / (double) gridSize);
		DemCanvas tileCanvas = new DemCanvas(backgroundColor, tileSizeAdjusted, tileSizeAdjusted);
		DemCanvas outputCanvas = new DemCanvas(backgroundColor, (int)modelDimensions.getOutputWidth(), (int)modelDimensions.getOutputHeight());
		
		
		
		int tileRow = 0;
		int tileCol = 0;
		int tileNum = 0;
		int dataRows = modelDimensions.getDataRows();
		int dataCols = modelDimensions.getDataColumns();
		int tileSize = modelDimensions.getTileSize();
		long tileCount = modelDimensions.getTileCount();
		
		int tileOutputWidth = (int) modelDimensions.getTileOutputWidth();
		int tileOutputHeight = (int) modelDimensions.getTileOutputHeight();
		
		//double scaledWidthPercent = (double) modelDimensions.getOutputWidth() / (double) dataCols;
		//double scaledHeightPercent = (double) modelDimensions.getOutputHeight() / (double) dataRows;
		
		
		ModelColoring modelColoring = ColoringRegistry.getInstance(getModelOptions().getColoringType()).getImpl();
		
		on2DModelBefore(outputCanvas);
		
		
		TileRenderer tileRenderer = new TileRenderer(modelContext, modelColoring, tileCanvas);
		
		
		
		if ((!skipElevation) && getDataPackage().getDataSources().size() > 0) {
			for (int fromRow = 0; fromRow < dataRows; fromRow+=tileSize) {
				int toRow = fromRow + tileSize - 1;
				if (toRow > dataRows)
					toRow = dataRows;
			
				tileCol = 0;
				for (int fromCol = 0; fromCol < dataCols; fromCol+=tileSize) {
					int toCol = fromCol + tileSize - 1;
					if (toCol > dataCols)
						toCol = dataCols;

					tileCanvas.reset();
						
					tileRenderer.renderTile(fromRow, toRow, fromCol, toCol);//, scaledWidthPercent, scaledHeightPercent);

					DemCanvas scaled = tileCanvas.getScaled(tileOutputWidth, tileOutputHeight);
					int overlayX = (int)Math.floor(tileCol * modelDimensions.getTileOutputWidth());
					int overlayY = (int)Math.floor(tileRow * modelDimensions.getTileOutputHeight());
					int scaledWidth = scaled.getWidth();
					int scaledHeight = scaled.getHeight();
					
					outputCanvas.overlay(scaled.getImage(), overlayX, overlayY, scaledWidth, scaledHeight);

					tileNum++;
					tileCol++;	
					
					double pctComplete = (double)tileNum / (double)tileCount;
					fireTileCompletionListeners(tileCanvas, outputCanvas, pctComplete);
					
					if (isCancelled()) {
						log.warn("Render process cancelled, model not complete.");
						break;
					}	
				}
				
				tileRow++;
				if (isCancelled()) {
					log.warn("Render process cancelled, model not complete.");
					break;
				}
			}
		}
		
		on2DModelAfter(outputCanvas);
		
		return outputCanvas;
	}
	
	protected void fireTileCompletionListeners(DemCanvas tileCanvas, DemCanvas outputCanvas, double pctComplete)
	{
		if (tileCompletionListeners != null) {
			for (TileCompletionListener listener : tileCompletionListeners) {
				listener.onTileCompleted(tileCanvas, outputCanvas, pctComplete);
			}
		}
	}
	
	protected DataPackage getDataPackage()
	{
		return modelContext.getDataPackage();
	}
	
	protected ModelOptions getModelOptions()
	{
		return modelContext.getModelOptions();
	}
	
	/** Requests that a rendering process is stopped.
	 * 
	 */
	public void cancel()
	{
		this.cancel = true;
	}
	
	/** Determines whether the rendering process has been requested to stop. This does not necessarily mean
	 * that the process <i>has</i> stopped as engine implementations need not check this value that often or
	 * at all.
	 * 
	 * @return Whether the rendering process has been requested to stop.
	 */
	public boolean isCancelled()
	{
		return cancel;
	}
	
	protected void on2DModelBefore(DemCanvas outputCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.on2DModelBefore(modelContext, outputCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
		
	}
	
	protected void on2DModelAfter(DemCanvas outputCanvas) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.on2DModelAfter(modelContext, outputCanvas);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}
	}

	
	
	
	public static DemCanvas render(ModelContext modelContext) throws RenderEngineException
	{
		return ModelRenderer.render(modelContext, false, null);
	}
	
	public static DemCanvas render(ModelContext modelContext, List<TileCompletionListener> tileCompletionListeners) throws RenderEngineException
	{
		return ModelRenderer.render(modelContext, false, tileCompletionListeners);
	}
	
	public static DemCanvas render(ModelContext modelContext, boolean skipElevation) throws RenderEngineException
	{
		ModelRenderer renderer = new ModelRenderer(modelContext, null);
		DemCanvas canvas = renderer.renderModel(skipElevation);
		return canvas;
	}
	
	public static DemCanvas render(ModelContext modelContext, boolean skipElevation, List<TileCompletionListener> tileCompletionListeners) throws RenderEngineException
	{
		ModelRenderer renderer = new ModelRenderer(modelContext, tileCompletionListeners);
		DemCanvas canvas = renderer.renderModel(skipElevation);
		return canvas;
	}
}
