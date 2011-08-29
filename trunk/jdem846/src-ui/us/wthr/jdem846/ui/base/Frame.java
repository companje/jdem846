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

package us.wthr.jdem846.ui.base;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.Disposable;

@SuppressWarnings("serial")
public class Frame extends JFrame
{
	private static Log log = Logging.getLog(Frame.class);
	
	
	public void close()
	{
		try {
			disposeComponents();
		} catch (ComponentException e) {
			log.error("Error when disposing child components: " + e.getMessage(), e);
			e.printStackTrace();
		}
		dispose();
	}
	
	
	public static void dispose(Component component) throws ComponentException
	{
		if (component instanceof Container)  {
			Container container = (Container) component;
			for (Component child : container.getComponents()) {
				Frame.dispose(child);
			}
		}
		
		if (component instanceof Disposable) {
			Disposable disposableComponent = (Disposable) component;
			disposableComponent.dispose();
		}
		
		
		
		
	}
	
	public void disposeComponents() throws ComponentException
	{
		log.info("Frame dispose initiated");
		Frame.dispose(this);
	}
}
