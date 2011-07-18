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

package us.wthr.jdem846;



import us.wthr.jdem846.ServiceKernel.ServiceThreadListener;
import us.wthr.jdem846.exception.RegistryException;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.ui.SplashScreen;


public class JDemMain 
{
	
	private static Log log = Logging.getLog(JDemMain.class);
	

	public static void checkCommandLineOptions(String[] args)
	{
		for (String arg : args) {
			
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
			
			
		}
	}
	
	public static void main(String[] args)
	{
		checkCommandLineOptions(args);

		log.info("Starting...");
		
		JDem846Properties uiProperties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
		JDem846Properties coreProperties = new JDem846Properties(JDem846Properties.CORE_PROPERTIES);
		
		SplashScreen splash = null;
		
		if (uiProperties.getBooleanProperty("us.wthr.jdem846.ui.displaySplash")) {
			splash = new SplashScreen();
			splash.setCopyright(coreProperties.getProperty("us.wthr.jdem846.copyRight"));
			splash.setVisible(true);
			
			SplashScreen.addIcon("/us/wthr/jdem846/ui/icons/dim48x48/applications-system.png", "System");
		}

		
		try {
			SplashScreen.addIcon("/us/wthr/jdem846/ui/icons/dim48x48/applications-utilities.png", "Configuration");
			
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (RegistryException ex) {
			log.error("Registry initialization error: " + ex.getMessage(), ex);
		}
		
		try {
			
			SplashScreen.addIcon("/us/wthr/jdem846/ui/icons/dim48x48/applications-internet.png", "Services");
			
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

}
