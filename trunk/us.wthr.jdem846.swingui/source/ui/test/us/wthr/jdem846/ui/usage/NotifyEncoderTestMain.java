package us.wthr.jdem846.ui.usage;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class NotifyEncoderTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(NotifyEncoderTestMain.class);
		
		try {
			NotifyEncoderTestMain testMain = new NotifyEncoderTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	
	public void doTesting() throws Exception
	{
		
		SingleRunNotify singleRunNotify = SingleRunNotifyFactory.create();
		
		String encoded = NotifyEncoder.encode(singleRunNotify);
		
		log.info(encoded);
		
	}
}
