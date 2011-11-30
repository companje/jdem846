package us.wthr.jdem846.gis;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ProjectionInformationFileLoaderTest extends AbstractTestMain
{
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		log = Logging.getLog(ProjectionInformationFileLoaderTest.class);
		
		ProjectionInformationFileLoaderTest tester = new ProjectionInformationFileLoaderTest();
		tester.toTesting();
	}
	
	
	public void toTesting()
	{
		log.info("Starting test...");
		
		String testPrjFile = "C:/srv/elevation/jDem_Orthoimagery_Testing/71860114.prj";
		
		
		try {
			ProjectionInformationFileLoader.load(testPrjFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
}
