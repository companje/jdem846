package us.wthr.jdem846.render.render2d;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
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

@Deprecated
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
		
		boolean fullCaching = JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy").equalsIgnoreCase(DemConstants.PRECACHE_STRATEGY_FULL);
		
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
		//int tileSize = modelDimensions.getTileSize();
		//long tileCount = modelDimensions.getTileCount();
		
		
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
		
		//double tileLatitudeHeight = latitudeResolution * tileSize - latitudeResolution;
	//double tileLongitudeWidth = longitudeResolution * tileSize - longitudeResolution;
		
		//log.info("Tile Size: " + tileSize);
		//log.info("Tile Latitude Height: " + tileLatitudeHeight);
		//log.info("Tile Longitude Width: " + tileLongitudeWidth);
		
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
		/*
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
		*/
		
		if (fullCaching) {
			try {
				getRasterDataContext().fillBuffers();
			} catch (DataSourceException ex) {
				throw new RenderEngineException("Failed to prebuffer raster data: " + ex.getMessage(), ex);
			}
		}
		
		/*
		if ( getRasterDataContext().getRasterDataListSize() > 0) {
			
			ExecutorService exec = Executors.newFixedThreadPool(getModelOptions().getConcurrentRenderPoolSize());
			LinkedList<Future<RenderedTile>> futureRenderedTiles = new LinkedList<Future<RenderedTile>>();
			//LinkedList<TileRenderRunnable> renderQueue = new LinkedList<TileRenderRunnable>();
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
						ModelContext tileContext = modelContext.copy(true);
						tileContext.setNorthLimit(tileNorth);
						tileContext.setSouthLimit(tileSouth);
						tileContext.setEastLimit(tileEast);
						tileContext.setWestLimit(tileWest);
						tileContext.setRasterDataContext(modelContext.getRasterDataContext().getSubSet(tileNorth, tileSouth, tileEast, tileWest).copy());
						tileRenderRunnable = new TileRenderRunnable(tileContext, tileNorth, tileSouth, tileEast, tileWest, (tileColumn + 1), (tileRow + 1));
					} catch (DataSourceException ex) {
						throw new RenderEngineException("Failed to create tile render runnable: " + ex.getMessage(), ex);
					}
					
					futureRenderedTiles.add(exec.submit(tileRenderRunnable));
					//renderQueue.add(tileRenderRunnable);
					//tileRenderRunnable.run();
					
					tileColumn++;
					tileNumber++;

					
					
					//pctComplete = (double)tileNumber / (double)tileCount;
					
					//fireTileCompletionListeners(modelCanvas, pctComplete);
					
					if (isCancelled()) {
						break;
					}	
				}
				
				tileRow++;
				
				if (isCancelled()) {
					break;
				}
				
			}
			*/
			
			/*
			try {
				log.info("Sleeping, so connect the JConsole!");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
		/*
			try {
				log.info("Beginning Threaded Rendering...");
				
				
				//List<Future<RenderedTile>> futureRenderedTiles = exec.invokeAll(renderQueue);
				//renderQueue.clear();
				
				//System.gc();
				
				//List<Future<RenderedTile>> futureRenderedTiles = new LinkedList<Future<RenderedTile>>();
				
				
				List<Future<RenderedTile>> processedTiles = new LinkedList<Future<RenderedTile>>();
				
				log.info(" Future tile list size: " + futureRenderedTiles.size());

				
				tileNumber = 1;
				while(futureRenderedTiles.size() > 0) {
					processedTiles.clear();
					
					for (Future<RenderedTile> futureRenderedTile : futureRenderedTiles) {
						if (!futureRenderedTile.isDone()) {
							continue;
						}
						

						tileNumber++;
						pctComplete = (double)tileNumber / (double)tileCount;
						log.info("Percent Complete: " + (pctComplete * 100));
						fireTileCompletionListeners(modelCanvas, pctComplete);
						
						processedTiles.add(futureRenderedTile);
						
						//System.gc();
						Thread.yield();
						//Thread.sleep(200);
					}
					
					futureRenderedTiles.removeAll(processedTiles);
					Thread.yield();
					//Thread.sleep(500);
				}
				
				exec.shutdown();
				
				log.info("All Done.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		*/
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
