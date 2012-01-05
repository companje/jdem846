package us.wthr.jdem846.render.render2d;

import java.util.concurrent.Callable;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.ProcessInterruptListener;

public class TileRenderRunnable implements  Callable<RenderedTile> 
{
	private static Log log = Logging.getLog(TileRenderRunnable.class);
	
	private ModelContext modelContext;
	private double northLimit; 
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	private int tileColumn = 0;
	private int tileRow = 0;
	
	private TileRenderer tileRenderer = null;
	
	public TileRenderRunnable(ModelContext modelContext, double northLimit, double southLimit, double eastLimit, double westLimit, int tileColumn, int tileRow)
	{
		this.modelContext = modelContext;
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
		this.tileColumn = tileColumn;
		this.tileRow = tileRow;
		
		
		
	}
	
	
	
	public void run()
	{
		__run();
	}
	
	public RenderedTile call()
	{
		return __run();
	}

	public RenderedTile __run() 
	{
		//ModelCanvas modelCanvas = modelContext.getModelCanvas();
		log.info("Starting tile row #" + tileRow + ", column #" + tileColumn);
		TileRenderer tileRenderer = new TileRenderer(modelContext);
		
		try {
			tileRenderer.renderTile(northLimit, southLimit, eastLimit, westLimit);
		} catch (RenderEngineException ex) {
			ex.printStackTrace();
		}
		
		
		tileRenderer = null;
		ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		
		try {
			modelContext.getRasterDataContext().dispose();
		} catch (DataSourceException ex) {
			log.error("Error disposing of tile raster data context: " + ex.getMessage(), ex);
		}
		modelContext = null;
		
		log.info("Completed tile row #" + tileRow + ", column #" + tileColumn);
		return new RenderedTile(modelCanvas, northLimit, southLimit, eastLimit, westLimit, tileColumn, tileRow);
		
	}
}
