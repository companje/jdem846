package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.ModelDimensions2D;
import us.wthr.jdem846.render.ProcessInterruptListener;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.util.ColorSerializationUtil;

public class ThreadedModelRenderer extends ModelRenderer
{
	private static Log log = Logging.getLog(ThreadedModelRenderer.class);
	
	public ThreadedModelRenderer(ModelContext modelContext, List<TileCompletionListener> tileCompletionListeners)
	{
		super(modelContext, tileCompletionListeners);
	}
	
	
	
	public ModelCanvas renderModel() throws RenderEngineException
	{
		//ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(getDataPackage(), getModelOptions());
		ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(modelContext);
		//getDataPackage().setAvgXDim(modelDimensions.getxDim());
		//getDataPackage().setAvgYDim(modelDimensions.getyDim());
		
		Color backgroundColor = ColorSerializationUtil.stringToColor(getModelOptions().getBackgroundColor());
		
		boolean fullCaching = getModelOptions().getPrecacheStrategy().equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_FULL);
		
		//int gridSize = getModelOptions().getGridSize();
		//int tileSizeAdjusted = (int)Math.round((double)modelDimensions.getTileSize() / (double) gridSize);
		//DemCanvas tileCanvas = new DemCanvas(backgroundColor, tileSizeAdjusted, tileSizeAdjusted);
		//DemCanvas outputCanvas = new DemCanvas(backgroundColor, (int)modelDimensions.getOutputWidth(), (int)modelDimensions.getOutputHeight());
		
		
		
		
		//int tileRow = 0;
		//int tileCol = 0;
		//int tileNum = 0;
		//int dataRows = modelDimensions.getDataRows();
		//int dataCols = modelDimensions.getDataColumns();
		int tileNumber = 0;
		int tileRow = 0;
		int tileColumn = 0;
		int tileSize = modelDimensions.getTileSize();
		long tileCount = modelDimensions.getTileCount();
		
		
		//int tileOutputWidth = (int) modelDimensions.getTileOutputWidth();
		//int tileOutputHeight = (int) modelDimensions.getTileOutputHeight();
		
		//double scaledWidthPercent = (double) modelDimensions.getOutputWidth() / (double) dataCols;
		//double scaledHeightPercent = (double) modelDimensions.getOutputHeight() / (double) dataRows;
		double northLimit = getRasterDataContext().getNorth();
		double southLimit = getRasterDataContext().getSouth();
		double eastLimit = getRasterDataContext().getEast();
		double westLimit = getRasterDataContext().getWest();
		
		double latitudeResolution = getRasterDataContext().getLatitudeResolution();
		double longitudeResolution = getRasterDataContext().getLongitudeResolution();
		
		//double tileSize = modelContext.getModelOptions().getTileSize();
		
		double tileLatitudeHeight = latitudeResolution * tileSize - latitudeResolution;
		double tileLongitudeWidth = longitudeResolution * tileSize - longitudeResolution;
		
		log.info("Tile Size: " + tileSize);
		log.info("Tile Latitude Height: " + tileLatitudeHeight);
		log.info("Tile Longitude Width: " + tileLongitudeWidth);
		
		ModelColoring modelColoring = ColoringRegistry.getInstance(getModelOptions().getColoringType()).getImpl();
		
		
		/*
		ModelCanvas modelCanvas = new ModelCanvas(modelContext);
		MapProjection mapProjection = null;
		try {
			
			mapProjection = MapProjectionProviderFactory.getMapProjection(
									getModelOptions().getMapProjection(),
									northLimit, 
									southLimit, 
									eastLimit, 
									westLimit, 
									modelDimensions.getOutputWidth(), 
									modelDimensions.getOutputHeight());
			
		} catch (MapProjectionException ex) {
			throw new RenderEngineException("Error loading map projection algorithm: " + ex.getMessage(), ex);
		}
		modelCanvas.setMapProjection(mapProjection);
		*/
		//ModelCanvas modelCanvas = modelContext.createModelCanvas();
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
			
			LinkedList<TileRenderRunnable> renderQueue = new LinkedList<TileRenderRunnable>();
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
					
					//tileRenderer.renderTile(tileNorth, tileSouth, tileEast, tileWest);
					
					
					TileRenderRunnable tileRenderRunnable = null;
					try {
						ModelContext tileContext = modelContext.copy();
						tileContext.setNorthLimit(tileNorth);
						tileContext.setSouthLimit(tileSouth);
						tileContext.setEastLimit(tileEast);
						tileContext.setWestLimit(tileWest);
						tileRenderRunnable = new TileRenderRunnable(tileContext, tileNorth, tileSouth, tileEast, tileWest, (tileColumn + 1), (tileRow + 1));
					} catch (DataSourceException ex) {
						throw new RenderEngineException("Failed to create tile render runnable: " + ex.getMessage(), ex);
					}
					
					renderQueue.add(tileRenderRunnable);
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
			
			
			try {
				ExecutorService exec = Executors.newFixedThreadPool(getModelOptions().getConcurrentRenderPoolSize());
				List<Future<RenderedTile>> futureRenderedTiles = exec.invokeAll(renderQueue);
				
				List<Future<RenderedTile>> processedTiles = new LinkedList<Future<RenderedTile>>();
				
				boolean allComplete = false;
				while(!allComplete && futureRenderedTiles.size() > 0) {
					
					allComplete = true;
					processedTiles.clear();
					
					for (Future<RenderedTile> futureRenderedTile : futureRenderedTiles) {
						if (!futureRenderedTile.isDone()) {
							allComplete = false;
							continue;
						}
						
						
						RenderedTile renderedTile = futureRenderedTile.get();
						
						//ImageWriter.saveImage((BufferedImage)renderedTile.getModelCanvas().getFinalizedImage(), "C:/srv/elevation/DataRaster-Testing/output/tile-" + renderedTile.getTileRow() + "-" + renderedTile.getTileColumn() + ".png");
						ModelCanvas tileCanvas = renderedTile.getModelCanvas();
						BufferedImage subImage = (BufferedImage) tileCanvas.getSubImage(renderedTile.getNorthLimit(),
								renderedTile.getSouthLimit(), 
								renderedTile.getEastLimit(), 
								renderedTile.getWestLimit());
						

						modelCanvas.drawImage(subImage, 
								renderedTile.getNorthLimit(), 
								renderedTile.getSouthLimit(), 
								renderedTile.getEastLimit(), 
								renderedTile.getWestLimit());
						
						tileCanvas.dispose();
						subImage = null;
						
						
						processedTiles.add(futureRenderedTile);
						Thread.yield();
					}
					
					futureRenderedTiles.removeAll(processedTiles);
					Thread.yield();
				}
				
				exec.shutdown();
				
				log.info("All Done.");
			} catch (Exception ex) {
				ex.printStackTrace();
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

}
