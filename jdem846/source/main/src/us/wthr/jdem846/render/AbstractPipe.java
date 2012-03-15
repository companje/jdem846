package us.wthr.jdem846.render;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public abstract class AbstractPipe extends InterruptibleProcess implements RenderPipe
{
	
	private static Log log = Logging.getLog(AbstractPipe.class);
	
	private int pollDelay = 100;
	
	public AbstractPipe()
	{
		
		pollDelay = JDem846Properties.getIntProperty("us.wthr.jdem846.performance.pipelinePollDelay");
		
		
	}
	
	
	protected void sleep()
	{
		try {
			Thread.sleep(pollDelay);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
}
