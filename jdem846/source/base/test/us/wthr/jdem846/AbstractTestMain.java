package us.wthr.jdem846;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class AbstractTestMain
{
	protected static void bootstrapSystemProperties()
	{
		
		if (System.getProperty("us.wthr.jdem846.installPath") == null) {
			System.setProperty("us.wthr.jdem846.installPath", System.getProperty("user.dir"));
		}
		if (System.getProperty("us.wthr.jdem846.resourcesPath") == null) {
			System.setProperty("us.wthr.jdem846.resourcesPath", System.getProperty("us.wthr.jdem846.installPath"));
		}
		
		if (System.getProperty("us.wthr.jdem846.userSettingsPath") == null) {
			System.setProperty("us.wthr.jdem846.userSettingsPath", System.getProperty("user.home") + "/.jdem846");
		}
		
		if (System.getProperty("us.wthr.jdem846.testOutputPath") == null) {
			System.setProperty("us.wthr.jdem846.testOutputPath", System.getProperty("user.dir") + "/test-output");
		}
	}
	
	
	public static void initialize(boolean initRegistry) throws Exception
	{
		bootstrapSystemProperties();
		
		JDem846Properties.initializeApplicationProperties();
		
		@SuppressWarnings("unused")
		Log log = Logging.getLog(AbstractTestMain.class);

		if (initRegistry) {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		}

	}
	
}
