package us.wthr.jdem846.render.render2d;

import java.util.concurrent.Callable;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.ProcessInterruptListener;

public class TileRenderRunnable implements Runnable, Callable<RenderedTile> 
{
	private static Log log = Logging.getLog(TileRenderRunnable.class);
	private ModelContext modelContext;
	private double northLimit; 
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	
	public TileRenderRunnable(ModelContext modelContext, double northLimit, double southLimit, double eastLimit, double westLimit)
	{
		this.modelContext = modelContext;
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
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
		ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		ModelColoring modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		TileRenderer tileRenderer = new TileRenderer(modelContext, modelColoring, modelCanvas);
		
		try {
			tileRenderer.renderTile(northLimit, southLimit, eastLimit, westLimit);
		} catch (RenderEngineException ex) {
			ex.printStackTrace();
		}
		
		return new RenderedTile(modelCanvas, northLimit, southLimit, eastLimit, westLimit);
		
	}
}
