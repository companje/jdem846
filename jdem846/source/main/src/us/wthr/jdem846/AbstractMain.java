package us.wthr.jdem846;

import java.io.File;

import us.wthr.jdem846.ServiceKernel.ServiceThreadListener;
import us.wthr.jdem846.exception.ArgumentException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.InstanceIdentifier;

public abstract class AbstractMain
{
	private static Log log = null;
	private Boolean lock = false;
	
	
	public abstract void beforeInit() throws Exception;
	public abstract void afterCoreInit() throws Exception;
	public abstract void afterInit() throws Exception;
	
	
	public Boolean isLocked()
	{
		synchronized(this.lock) {
			return lock;
		}
	}


	protected void setLocked(Boolean lock)
	{
		synchronized(this.lock) {
			this.lock = lock;
		}
	
	}
	
	public void initialization(String[] args) throws Exception
	{
		initialization(args, true, true, true);
	}
	
	public void initialization(String[] args, boolean runComponentDiscovery, boolean loadRegistries, boolean loadServices) throws Exception
	{
		beforeInit();
		
		bootstrapSystemProperties();
		
		try {
			checkCommandLineOptions(args);
		} catch (ArgumentException ex) {
			System.out.println("Invalid parameters: " + ex.getMessage());
			return;
		}
		
		JDem846Properties.initializeApplicationProperties();
		JDem846Properties.initializeUserProperties();
		
		log = Logging.getLog(AbstractMain.class);
		
		String instanceId = InstanceIdentifier.getInstanceId();
		log.info("Instance ID: " + instanceId);
		
		
		String loadLanguage = System.getProperty("us.wthr.jdem846.ui.i18n.load");
		if (loadLanguage != null && loadLanguage.length() > 0) {
			try {
				I18N.loadLanguage(loadLanguage);
			} catch (Exception ex) {
				System.out.println("Language Error: Invalid language or failure to load language file");
				ex.printStackTrace();
				return;
			}
		}
		
		
		afterCoreInit();
		
		if (runComponentDiscovery) {
			//DiscoverableAnnotationIndexer.addClassLoaders(this.getClass().getClassLoader());
			DiscoverableAnnotationIndexer.createIndex();
		}
		
		if (loadRegistries) {
			log.info("Loading registries...");
			try {
				RegistryKernel regKernel = new RegistryKernel();
				regKernel.init();
			} catch (RegistryException ex) {
				log.error("Registry initialization error: " + ex.getMessage(), ex);
			}
		}
		
		if (loadServices) {
			log.info("Loading services...");
			try {
				ServiceKernel serviceKernel = new ServiceKernel();
				serviceKernel.addServiceThreadListener(new ServiceThreadListener() {
					public void onServiceThreadExited()
					{
						log.info("Service Thread has exited, now shutting down the VM");
						System.exit(0);
					}
				});
				
				
				serviceKernel.initializeServices();
				serviceKernel.start();
				
				
	
			} catch (Exception ex) {
				log.error("Service initialization error: " + ex.getMessage(), ex);
	
			}
		}

		afterInit();
	}
	
	
	/** Scans command line options and places appropriate values into system properties map.
	 * 
	 * @param args Command line options array.
	 */
	protected static void checkCommandLineOptions(String[] args) throws ArgumentException
	{
		//for (String arg : args) {
		
		String openFiles = "";
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (fileExistsAndIsProject(arg)) {
				openFiles += arg + ";";
			}
			
			if (arg.equals("-no-splash-screen"))
				System.setProperty("us.wthr.jdem846.ui.displaySplash", "false");
			else if (arg.equals("-show-splash-screen"))
				System.setProperty("us.wthr.jdem846.ui.displaySplash", "true");
			
			else if (arg.equals("-debug"))
				System.setProperty("us.wthr.jdem846.ui.displayLogViewPanel", "true");
			else if (arg.equals("-no-debug"))
				System.setProperty("us.wthr.jdem846.ui.displayLogViewPanel", "false");
			
			else if (arg.equals("-show-toolbar-text"))
				System.setProperty("us.wthr.jdem846.ui.mainToolBar.displayText", "true");
			else if (arg.equals("-no-toolbar-text"))
				System.setProperty("us.wthr.jdem846.ui.mainToolBar.displayText", "false");
			
			else if (arg.equals("-language") && args.length >= i+2) {
				System.setProperty("us.wthr.jdem846.ui.i18n.load", args[i+1]);
			} else if (arg.equals("-language") && args.length < i+2) {
				throw new ArgumentException("Missing parameter for '-language'");
			}
		}
		
		if (openFiles.length() > 0) {
			System.setProperty("us.wthr.jdem846.ui.openFiles", openFiles);
		}
		
	}
	
	
	
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
	
	
	

	/** Determines if a path is a project file, exists, and can be read.
	 * 
	 * @param path Path to a (hopefully) project file
	 * @return True if the target file exists, is a jDem project file, and can be read
	 */
	protected static boolean fileExistsAndIsProject(String path)
	{
		if (path == null)
			return false;
		
		String pathLower = path.toLowerCase();
		if (!(pathLower.endsWith(".jdemprj") || pathLower.endsWith(".jdemimg"))) {
			return false;
		}
		
		File file = JDemResourceLoader.getAsFile(path);
		if (file.exists() && file.canRead()) {
			return true;
		} else {
			return false;
		}
		
		
	}
	
}
