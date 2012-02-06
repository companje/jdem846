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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

/** Main JDem846 user interface entry point.
 * 
 * @author Kevin M. Gill
 *
 */
public class JDemUiMain extends BaseUIMain
{
	
	private static Log log = null;

	private JdemFrame frame;
	
	
	@Override
	public void beforeInit() throws Exception
	{
		
	}

	@Override
	public void afterInit() throws Exception
	{
		log = Logging.getLog(JDemUiMain.class);
		frame = JdemFrame.getInstance();
		
		
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
		
	}


	public static void main(String[] args)
	{

		
		
		JDemUiMain main = new JDemUiMain();
		
		try {
			main.initialization(args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		
	}

	
}
