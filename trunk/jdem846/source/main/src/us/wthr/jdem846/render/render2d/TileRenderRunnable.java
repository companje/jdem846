package us.wthr.jdem846.render.render2d;

import java.util.concurrent.Callable;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Deprecated
public class TileRenderRunnable implements  Callable<RenderedTile> 
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(TileRenderRunnable.class);
	
	private TileRenderContainer tileRenderContainer;
	
	public TileRenderRunnable(ModelContext modelContext, double northLimit, double southLimit, double eastLimit, double westLimit, int tileColumn, int tileRow)
	{
		this.tileRenderContainer = new TileRenderContainer(modelContext, northLimit, southLimit, eastLimit, westLimit, tileColumn, tileRow);
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
		//log.info("Starting tile row #" + tileRow + ", column #" + tileColumn);
		
		RenderedTile renderedTile = null;
		
		try {
			renderedTile = tileRenderContainer.render(null);
		} catch (RenderEngineException ex) {
			ex.printStackTrace();
		}
		return renderedTile;
		
		
	}
}
