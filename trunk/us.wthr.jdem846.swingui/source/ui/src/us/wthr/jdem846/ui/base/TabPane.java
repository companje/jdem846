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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.ClosableTab;
import us.wthr.jdem846.ui.Disposable;


/** Implements a tabbed pane with the first tab being the project form and cannot be closed. The
 * other tabs are output images within a OutputImageViewer component panel.
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class TabPane extends JTabbedPane implements Disposable
{
	private static Log log = Logging.getLog(TabPane.class);
	
	private boolean disposed = false;
	
	private HiddenTabPaneUI hiddenTabUI;
	
	public TabPane()
	{
		
		hiddenTabUI = new HiddenTabPaneUI();
	//	this.setUI(hiddenTabUI);
 
		
		
		addContainerListener(new ContainerListener() {
			public void componentAdded(ContainerEvent arg0)
			{
				checkTabVisibilityState();
			}
			public void componentRemoved(ContainerEvent arg0)
			{
				checkTabVisibilityState();
			}
		});
	}
	
	@Override
	public void addTab(String title, Component component)
	{
		addTab(title, null, component, false);
	}
	
	public void addTab(String title, String iconUrl, Component component)
	{
		addTab(title, iconUrl, component, false);
	}
	
	public void addTab(String title, Component component, boolean closable)
	{
		addTab(title, null, component, closable);
	}
	
	public void addTab(String title, String iconUrl, Component component, boolean closable)
	{
		super.addTab(title, component);
		
		if (closable) {
			ClosableTab tab = new ClosableTab(title, iconUrl, closable);
	
			tab.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					Component comp = (Component) e.getSource();
					int index = indexOfTabComponent(comp);
					removeTabAt(index);
					
					
				}
			});
			
			this.setTabComponentAt(this.getTabCount() - 1, tab);
		}
		
		checkTabVisibilityState();
	}
	
	
	
	
	@Override
	public void removeTabAt(int index)
	{
		if (index >= 0) {
			Component tabComponent = getComponentAt(index);
			
			log.info("Closing tab of type " + tabComponent.getClass().getCanonicalName());
			try {
				Frame.dispose(tabComponent);
			} catch (ComponentException ex) {
				log.error("Failed to dispose of component: " + ex.getMessage(), ex);
				ex.printStackTrace();
			}
		}
		super.removeTabAt(index);
	}

	@Override
	public void setTitleAt(int index, String title)
	{
		ClosableTab tab = (ClosableTab) this.getTabComponentAt(index);
		tab.setTitle(title);
	}

	@Override
	public void dispose() throws ComponentException
	{
		log.info("TabPane.dispose()");

		disposed = true;
	}
	
	
	public boolean isDisposed()
	{
		return disposed;
	}
	
	public void setTabsVisible(boolean v)
	{
		//hiddenTabUI.setTabsVisible(v);
		
	}
	
	public boolean areTabsVisible()
	{
		return hiddenTabUI.areTabsVisible();
	}

	public void checkTabVisibilityState()
	{

		if (getTabCount() == 1 ) {
		//	hiddenTabUI.setTabsVisible(false);
		} else {
		//	hiddenTabUI.setTabsVisible(true);
		}
		
	}
	
	
	class HiddenTabPaneUI extends BasicTabbedPaneUI
	{

		private boolean tabsVisible = true;
		
		public HiddenTabPaneUI()
		{
			
		}

		
		@Override
		protected int calculateTabHeight(int tabPlacement, int tabIndex,
				int fontHeight)
		{
			if (tabsVisible) {
				return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
			} else {
				return 0;
			}
		}


		@Override
		protected int calculateMaxTabHeight(int tabPlacement)
		{
			if (tabsVisible) {
				return super.calculateMaxTabHeight(tabPlacement);
			} else {
				return 0;
			}
		}


		@Override
		protected int calculateTabAreaHeight(int tabPlacement,
				int horizRunCount, int maxTabHeight)
		{
			if (tabsVisible) {
				return super.calculateTabAreaHeight(tabPlacement, horizRunCount, maxTabHeight);
			} else {
				return 0;
			}
		}

		
		@Override
		protected void paintTab(Graphics g, int tabPlacement,
				Rectangle[] rects, int tabIndex, Rectangle iconRect,
				Rectangle textRect)
		{
			if (tabsVisible) {
				super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
			}
		}


		@Override
		protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex)
		{
			if (tabsVisible) {
				super.paintTabArea(g, tabPlacement, selectedIndex);
			}
		}
		
		@Override
		protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
				int selectedIndex, int x, int y, int w, int h)
		{
			if (tabsVisible) {
				super.paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
			}
		}
		
		public void setTabsVisible(boolean v)
		{
			this.tabsVisible = v;
		}
		
		public boolean areTabsVisible()
		{
			return tabsVisible;
		}
	}
}
