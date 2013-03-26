package us.wthr.jdem846ui;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import us.wthr.jdem846.DiscoverableAnnotationIndexer;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.ServiceKernel;
import us.wthr.jdem846.ServiceKernel.ServiceThreadListener;
import us.wthr.jdem846.exception.ArgumentException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinition;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinitionLoader;
import us.wthr.jdem846.util.InstanceIdentifier;
import us.wthr.jdem846ui.daemons.PreviewRenderDaemon;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	
	private static Log log = Logging.getLog(Application.class);
	
	
	public void initialization(IApplicationContext context) throws Exception
	{

		
		bootstrapSystemProperties();
		
		try {
			checkCommandLineOptions();
		} catch (ArgumentException ex) {
			System.out.println("Invalid parameters: " + ex.getMessage());
			return;
		}
		
		JDem846Properties.initializeApplicationProperties();
		JDem846Properties.initializeUserProperties();
		
		log = Logging.getLog(Application.class);
		
		String instanceId = InstanceIdentifier.getInstanceId();
		log.info("Instance ID: " + instanceId);
		

		log.info("Starting...");
		
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
		
		
		
		log.info("Searching for annotated classes...");
		Bundle bundle = Platform.getBundle("us.wthr.jdem846.core");

		log.info("In Development Mode: " + Platform.inDevelopmentMode());
		log.info("In Debug Mode: " + Platform.inDebugMode());
		log.info("Location: " + Platform.getLocation());
		log.info("User Location: " + Platform.getUserLocation().getURL());
		log.info("Install Location: " + Platform.getInstallLocation().getURL());
		log.info("Instance Location: " + Platform.getInstanceLocation().getURL());

		String bundleLocation = bundle.getLocation();
		if (bundleLocation != null) {
			bundleLocation = bundleLocation.replace("initial@reference:file:", "");
			log.info("Adding Bundle Location: " + bundleLocation);
			
			if (Platform.inDevelopmentMode()) {
				DiscoverableAnnotationIndexer.addUrls(new URL("file://" + bundleLocation + "/bin"));
			} else {
				DiscoverableAnnotationIndexer.addUrls(new URL("file://" + Platform.getInstallLocation().getURL() + "/" + bundleLocation));
			}
		}
		
		DiscoverableAnnotationIndexer.createIndex();
		

		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (RegistryException ex) {
			log.error("Registry initialization error: " + ex.getMessage(), ex);
		}
		
		try {

			ServiceKernel serviceKernel = new ServiceKernel();
			serviceKernel.addServiceThreadListener(new ServiceThreadListener() {
				public void onServiceThreadExited()
				{
					log.warn("Service Thread has exited!");
					//System.exit(0);
				}
			});
			
			
			serviceKernel.initializeServices();
			serviceKernel.start();

		} catch (Exception ex) {
			log.error("Service initialization error: " + ex.getMessage(), ex);

		}


	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		

		try {
			this.initialization(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		
		String projectPaths = System.getProperty("us.wthr.jdem846.ui.openFiles");
		String[] projects = (projectPaths != null) ? projectPaths.split(";") : null;
		String loadProject = (projects != null && projects.length > 0) ? projects[0] : null;
		if (loadProject != null && !fileExistsAndIsProject(loadProject)) {
			loadProject = null;
		}

		
		//OptionValidationChangeObserver validationObserver = new OptionValidationChangeObserver();
		//ElevationRangeChangeObserver rangeObserver = new ElevationRangeChangeObserver();
		//ModelPreviewChangeObserver modelPreviewObserver = new ModelPreviewChangeObserver();

		
		try {
			setInstanceLocation();
		} catch (Exception ex) {
			log.error("Error setting user instance location: " + ex.getMessage(), ex);
			return IApplication.EXIT_OK;
		}
		
		PreviewRenderDaemon previewRenderThread = new PreviewRenderDaemon();
		previewRenderThread.start();
		
		ShapeDataDefinitionLoader loader = new ShapeDataDefinitionLoader();
		
		for (ShapeDataDefinition shapeDataDefinition : loader.getShapeDataDefinitions()) {
			log.info("Shape Definition: " + shapeDataDefinition.getName() + " " + shapeDataDefinition.getId());
		}
		
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor(loadProject));
			
			ServiceKernel.initiateApplicationShutdown();
			
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	
	protected void setInstanceLocation() throws Exception
	{
		Location instanceLoc = Platform.getInstanceLocation(); 
		if (instanceLoc.isSet()) {
			log.warn("Cannot set instance location: Already set to '" + instanceLoc.getURL().toString() + "'");
			return;
		}
		
		
		String userDirectory = JDem846Properties.getProperty("us.wthr.jdem846.userSettingsPath");
		
		File f = new File(userDirectory);
		if (!f.exists() && !f.mkdirs()) {
			throw new Exception("Instance location '" + userDirectory + "' does not exists and I am unable to create it");
		}
		
		if (!f.canRead()) {
			throw new Exception("Instance location '" + userDirectory + "' exists but unable to read");
		}
		
		if (!f.canWrite()) {
			throw new Exception("Instance location '" + userDirectory + "' exists but unable to write");
		}
		
		log.info("Setting instance location to '" + userDirectory + "'...");
		
		
		
		
		try {
			instanceLoc.set(new URL("file", null, f.getAbsolutePath()), false);
		} catch (IllegalStateException ex) {
			throw new Exception("Error setting instance location to '" + f.getAbsolutePath() + "'. Currently set to '" + instanceLoc.getURL().toString() + "'", ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		
		ServiceKernel.initiateApplicationShutdown();
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
	
	
	/** Scans command line options and places appropriate values into system properties map.
	 * 
	 * @param args Command line options array.
	 */
	protected static void checkCommandLineOptions() throws ArgumentException
	{
		
		String openFiles = "";
		
		String[] args = Platform.getCommandLineArgs();
		
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
			
		//	else if (arg.equals("-language") && args.length >= i+2) {
		//		System.setProperty("us.wthr.jdem846.ui.i18n.load", args[i+1]);
		//	} else if (arg.equals("-language") && args.length < i+2) {
		//		throw new ArgumentException("Missing parameter for '-language'");
		//	}
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
			System.setProperty("us.wthr.jdem846.installPath", Platform.getInstallLocation().getURL().toString());
		}
		if (System.getProperty("us.wthr.jdem846.resourcesPath") == null) {
			System.setProperty("us.wthr.jdem846.resourcesPath", System.getProperty("us.wthr.jdem846.installPath"));
		}
		
		//if (System.getProperty("us.wthr.jdem846.userSettingsPath") == null) {
		//	System.setProperty("us.wthr.jdem846.userSettingsPath", System.getProperty("user.home") + "/.jdem846");
		//}
		
		
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
