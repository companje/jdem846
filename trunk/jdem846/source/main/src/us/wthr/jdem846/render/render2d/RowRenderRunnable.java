package us.wthr.jdem846.render.render2d;

import java.util.concurrent.Callable;

import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Deprecated
public class RowRenderRunnable implements Runnable, Callable<Boolean> 
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(RowRenderRunnable.class);
	
	private RowRenderer renderer;
	private double latitude;
	private double eastLimit;
	private double westLimit;
	
	
	public RowRenderRunnable(RowRenderer renderer, double latitude, double eastLimit, double westLimit)
	{
		this.renderer = renderer;
		this.latitude = latitude;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
		
	}
	
	public void run()
	{
		__run();
	}
	
	public Boolean call()
	{
		__run();
		return true;
	}

	public void __run() 
	{
		//log.info("Latitude: " + latitude);
		try {
			renderer.renderRow(latitude, eastLimit, westLimit);
		} catch (RenderEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
