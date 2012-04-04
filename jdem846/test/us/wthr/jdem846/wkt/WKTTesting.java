package us.wthr.jdem846.wkt;

import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.InstanceIdentifier;

/** Influenced by JTS Topology Suite
 * 
 * @author Kevin M. Gill
 *
 */
public class WKTTesting
{
	private static Log log = null;
	
	
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
		
		
		System.out.println("us.wthr.jdem846.installPath: " + System.getProperty("us.wthr.jdem846.installPath"));
		System.out.println("us.wthr.jdem846.resourcesPath: " + System.getProperty("us.wthr.jdem846.resourcesPath"));
		System.out.println("us.wthr.jdem846.userSettingsPath: " + System.getProperty("us.wthr.jdem846.userSettingsPath"));
	}
	
	public static void main(String[] args)
	{
		bootstrapSystemProperties();
		
		log = Logging.getLog(WKTTesting.class);
		
		String instanceId = InstanceIdentifier.getInstanceId();
		log.info("Instance ID: " + instanceId);
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (RegistryException ex) {
			log.error("Registry initialization error: " + ex.getMessage(), ex);
			return;
		}
		
		String wktFile = "C:\\srv\\elevation\\jDem_Orthoimagery_Testing\\71860114.prj";

		try {
			WKTReader reader = WKTReader.load(wktFile);
			reader.close();
		} catch (Exception ex) {
			log.error("Failed to read WKT file: " + ex.getMessage(), ex);
		}
	}
	
	
	
	
}
