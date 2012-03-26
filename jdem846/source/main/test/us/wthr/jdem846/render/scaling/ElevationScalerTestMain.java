package us.wthr.jdem846.render.scaling;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ElevationScalerTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ElevationScalerTestMain.class);
		
		try {
			ElevationScalerTestMain testMain = new ElevationScalerTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	public void doTesting() throws Exception
	{
		double min = 415.7161560058594;
		double max = 1916.0155029296875;
		
		double range = max - min;
		double step = range / 20;
		
		ElevationScaler elevationScaler = ElevationScalerFactory.createElevationScaler(ElevationScalerEnum.CUBIC, 1.0, min, max);
		
		for (double elev = min; elev <= max; elev += step) {
			double scaled = elevationScaler.scale(elev);
			
			log.info("" + min + ", " + max + ", " + elev + ", " + scaled);
			
		}
	}
	
}
