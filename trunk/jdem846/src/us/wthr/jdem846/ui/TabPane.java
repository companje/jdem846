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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


/** Implements a tabbed pane with the first tab being the project form and cannot be closed. The
 * other tabs are output images within a OutputImageViewer component panel.
 * 
 * @author Gill Family
 *
 */
@SuppressWarnings("serial")
public class TabPane extends JTabbedPane
{
	private static Log log = Logging.getLog(TabPane.class);
	
	public TabPane()
	{

	}
	
	public void addTab(String title, Component component)
	{
		addTab(title, component, false);
	}
	
	public void addTab(String title, Component component, boolean closable)
	{
		super.addTab(title, component);
		ClosableTab tab = new ClosableTab(title, closable);

		tab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component comp = (Component) e.getSource();
				int index = indexOfTabComponent(comp);
				if (index >= 0) {
					
					Component tabComponent = getComponentAt(index);
					if (tabComponent instanceof JdemPanel) {
						JdemPanel panel = (JdemPanel) tabComponent;
						panel.cleanUp();
					}
					removeTabAt(index);
				} else {
					log.warn("Invalid tab pane index: " + index);
				}
			}
		});
		
		this.setTabComponentAt(this.getTabCount() - 1, tab);

	}
	
	
	@Override
	public void setTitleAt(int index, String title)
	{
		ClosableTab tab = (ClosableTab) this.getTabComponentAt(index);
		tab.setTitle(title);
	}
	

	
	
}
