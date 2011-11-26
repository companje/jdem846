package us.wthr.jdem846;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class AbstractTestMain
{
	/** Sets base system property values
	 * 
	 */
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
		
	}
	
	
	protected static void initialize(boolean initRegistry) throws Exception
	{
		bootstrapSystemProperties();
		
		
		Log log = Logging.getLog(AbstractTestMain.class);

		if (initRegistry) {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		}

	}
	
}
