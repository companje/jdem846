/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui;



import java.awt.Font;
import java.io.File;
import java.util.Random;
import java.util.UUID;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;
import com.jgoodies.looks.plastic.theme.DarkStar;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.ServiceKernel;
import us.wthr.jdem846.ServiceKernel.ServiceThreadListener;
import us.wthr.jdem846.exception.ArgumentException;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.util.InstanceIdentifier;

/** Main application entry point. Parses command line options and kicks off service and registry kernels.
 * 
 * @author Kevin M. Gill
 *
 */
public class JDemUiMain 
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
		if (!(pathLower.endsWith(".zdem") || pathLower.endsWith(".jdem") || pathLower.endsWith(".xdem"))) {
			return false;
		}
		
		File file = JDemResourceLoader.getAsFile(path);
		if (file.exists() && file.canRead()) {
			return true;
		} else {
			return false;
		}
		
		
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
	

	
	
	/** Application entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		bootstrapSystemProperties();
		
		try {
			checkCommandLineOptions(args);
		} catch (ArgumentException ex) {
			System.out.println("Invalid parameters: " + ex.getMessage());
			return;
		}
		
		JDem846Properties.initializeApplicationProperties();
		
		log = Logging.getLog(JDemUiMain.class);
		
		String instanceId = InstanceIdentifier.getInstanceId();
		log.info("Instance ID: " + instanceId);
		
		/*
		UIManager.installLookAndFeel("JGoodies Plastic 3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			log.info("LaF: " + laf.getName());
		}
		*/
		
		applyLookAndFeel(false);
		
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
		
		//JDem846Properties jdemProperties = JDem846Properties.getInstance();
		//JDem846Properties uiProperties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
		//JDem846Properties coreProperties = new JDem846Properties(JDem846Properties.CORE_PROPERTIES);
		
		SplashScreen splash = null;
		
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.displaySplash")) {
			splash = new SplashScreen();
			splash.setCopyright(JDem846Properties.getProperty("us.wthr.jdem846.copyRight"));
			splash.setVisible(true);
			
			SplashScreen.addIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.48x48") + "/applications-system.png", I18N.get("us.wthr.jdem846.ui.system"));
		}

		
		
		try {
			SplashScreen.addIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.48x48") + "/applications-utilities.png", I18N.get("us.wthr.jdem846.ui.configuration"));
			
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (RegistryException ex) {
			log.error("Registry initialization error: " + ex.getMessage(), ex);
		}
		
		try {
			
			SplashScreen.addIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.48x48") + "/applications-internet.png", I18N.get("us.wthr.jdem846.ui.service"));
			
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
		

		if (splash != null)
			splash.setVisible(false);
		
	}
	
	

	protected static void applyLookAndFeel(boolean forceConfiguredDefault)
	{
		String lafWindows = JDem846Properties.getProperty("us.wthr.jdem846.ui.swingLaf.windows");
		String lafLinux = JDem846Properties.getProperty("us.wthr.jdem846.ui.swingLaf.windows");
		String lafDefault = JDem846Properties.getProperty("us.wthr.jdem846.ui.swingLaf.windows");
		
		if (lafDefault == null) {
			lafDefault = "Metal";
		}
		
		if (lafWindows == null) {
			lafWindows = lafDefault;
		}
		
		if (lafLinux == null) {
			lafLinux = lafDefault;
		}
		
		String os = JDem846Properties.getProperty("os.name");
		
		String laf = lafDefault;
		if (os.toUpperCase().contains("WINDOWS") && !forceConfiguredDefault) {
			laf = lafWindows;
		} else if (os.toUpperCase().contains("LINUX") && !forceConfiguredDefault) {
			laf = lafLinux;
		}
		
		if (laf != null && laf.startsWith("com.jgoodies.looks")) {
			JDem846Properties.setProperty("us.wthr.jdem846.ui.usingJGoodies", "true");
			setJGoodiesSettings();
		} else {
			JDem846Properties.setProperty("us.wthr.jdem846.ui.usingJGoodies", "false");
		}
		
		// check if laf is "default", if so leave the Look & Feel to whatever
		// the JVM default is and exit.
		if (laf.equalsIgnoreCase("default")) {
			return;
		}
		
		try {
			log.info("Applying Look & Feel: '" + laf + "'");
			if (laf != null) {
				UIManager.setLookAndFeel(laf);
				log.info("Applied Look & Feel: '" + laf + "'");
			    //for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			    //    if (laf.equalsIgnoreCase(info.getName())) {
			   //        UIManager.setLookAndFeel(info.getClassName());
			            log.info("Applied Look & Feel: '" + laf + "'");
			   //         break;
			   //     }
			   // }
			}
		} catch (Exception ex) {
		    // We really don't care if the specified look & feel is not available, but if that's
			// the case, we recall this function and force the configured default. If we're
			// already in the forced config'd default call, then fail and fall back to the
			// JVM default Look & Feel.
			
			if (!forceConfiguredDefault) {
				log.warn("Failed to apply configured look and feel '" + laf + "', reverting to application default.", ex);
				applyLookAndFeel(true);
			} else {
				log.warn("Failed to apply application default look & feel, falling back to JVM default.", ex);
			}
		}
	}

	public static void setJGoodiesSettings()
	{
		log.info("Applying JGoodies Settings");
		
		
		
		LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
		boolean found = false;
		for (int i = 0; i < lnfs.length; i++) {
			if (lnfs[i].getName().equals("JGoodies Plastic 3D")) {
				found = true;
			}
		}
		
		PlasticTheme theme = getConfiguredPlasticTheme();
		if (theme != null) {
			PlasticLookAndFeel.setPlasticTheme(theme);
		}
		
		if (!found) {
			UIManager.installLookAndFeel("JGoodies Plastic 3D",
					"com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		}
		
		String fontName = getConfiguredFontName();
		int fontStyle = JDem846Properties.getIntProperty("us.wthr.jdem846.ui.font.style");
		int fontSize = JDem846Properties.getIntProperty("us.wthr.jdem846.ui.font.size");

		FontSet fontSet = FontSets.createDefaultFontSet(new Font(fontName, fontStyle, fontSize));

		FontPolicy fixedPolicy = FontPolicies.createFixedPolicy(fontSet);
		PlasticLookAndFeel.setFontPolicy(fixedPolicy);

	}
	
	public static String getConfiguredFontName()
	{
		String fontName = null;
		String os = System.getProperty("os.name");
		
		if (os.toUpperCase().contains("WINDOWS")) {
			fontName = JDem846Properties.getProperty("us.wthr.jdem846.ui.font.windows");
		} else if (os.toUpperCase().contains("LINUX")) {
			fontName = JDem846Properties.getProperty("us.wthr.jdem846.ui.font.linux");
		} else {
			fontName = JDem846Properties.getProperty("us.wthr.jdem846.ui.font.default");
		}
		return fontName;
	}
	
	
	public static PlasticTheme getConfiguredPlasticTheme()
	{
		String theme = JDem846Properties.getProperty("us.wthr.jdem846.ui.jgoodies.theme");
		
		if (theme == null || theme.length() == 0) {
			return null;
		}
		
		PlasticTheme plasticTheme = null;
		
		try {
			Class<PlasticTheme> clazz = (Class<PlasticTheme>) Class.forName(theme);
			plasticTheme = clazz.newInstance();
		} catch (Exception ex) {
			log.warn("Failed to load pastic theme '" + theme + "': " + ex.getMessage(), ex);
		}
		
		return plasticTheme;
	}
	
}
