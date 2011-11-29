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

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import us.wthr.jdem846.AbstractLockableService;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ServiceKernel;
import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


@Service(name="us.wthr.jdem846.ui.jdemuiservice", enabled=true)
public class JDemUiService extends AbstractLockableService
{
	
	private static Log log = Logging.getLog(JDemUiService.class);
	private JdemFrame frame;
	
	
	
	public JDemUiService()
	{
		
	}
	
	@Initialize
	public void initialize()
	{
		//System.out.println("JDemUiService.initialize()");
		log.info("JDemUIService.initialize()");
		
		applyLookAndFeel(false);
		
		frame = JdemFrame.getInstance();
	}
	
	

	@ServiceRuntime
	public void runtime()
	{
		//System.out.println("JDemUiService.runtime()");
		log.info("JDemUiService.runtime()");
		
		
		
		frame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent event) { }
			public void windowClosing(WindowEvent event) 
			{ 
				log.info("JDemUiService.windowClosing()");
				//System.out.println("JDemUiService.windowClosing()");
				//setLocked(false);
				//event.
			}
			public void windowDeactivated(WindowEvent event) { }
			public void windowDeiconified(WindowEvent event) { }
			public void windowIconified(WindowEvent event) { }
			public void windowOpened(WindowEvent event) { }
			
			public void windowClosed(WindowEvent event)
			{ 
				log.info("JDemUiService.windowClosed()");
				//System.out.println("JDemUiService.windowClosed()");
				setLocked(false);
			}
		});
		
		setLocked(true);
		frame.setVisible(true);
		
		while (this.isLocked()) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		log.info("Window was closed & lock released.");
		
		ServiceKernel.initiateApplicationShutdown();
		
		//System.out.println("Window was closed & lock released.");
	}
	
	
	
	protected void applyLookAndFeel(boolean forceConfiguredDefault)
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
	
	@Destroy
	public void destroy()
	{
		log.info("JDemUiService.destroy()");
		//System.out.println("JDemUiService.destroy()");
	}
	
}
