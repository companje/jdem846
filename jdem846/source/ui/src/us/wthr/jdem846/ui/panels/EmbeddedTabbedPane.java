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

package us.wthr.jdem846.ui.panels;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.TabPane;

@SuppressWarnings("serial")
public class EmbeddedTabbedPane extends TabPane
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(EmbeddedTabbedPane.class);
	
	public EmbeddedTabbedPane()
	{
		this(TabPane.TOP, TabPane.WRAP_TAB_LAYOUT);
	}
	
	public EmbeddedTabbedPane(int tabPlacement)
	{
		this(tabPlacement, TabPane.WRAP_TAB_LAYOUT);
	}
	
	public EmbeddedTabbedPane(int tabPlacement, int tabLayoutPolicy)
	{	
		super();
		
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.usingJGoodies")) {
			putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);
			putClientProperty("jgoodies.embeddedTabs", Boolean.TRUE);
		} /*else {
			setUI(new EmbeddedTabbedPaneUI());
		}
		*/
		
		this.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0)
			{
				
			}
		});
		
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e)
			{
				log.info("Embedded Tab Pane Hidden!");
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentResized(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				log.info("Embedded Tab Pane Shown!");
			}
			
		});
		

	}
	
	class EmbeddedTabbedPaneUI extends BasicTabbedPaneUI
	{

		public EmbeddedTabbedPaneUI()
		{

		}

		
		@Override
		protected void paintTabArea(Graphics g, int tabPlacement,
				int selectedIndex)
		{
			super.paintTabArea(g, tabPlacement, selectedIndex);
		}


		@Override
		protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
				int selectedIndex, int x, int y, int w, int h)
		{
			super.paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
		}

		@Override
		protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
				int selectedIndex, int x, int y, int w, int h)
		{
		
		}

		@Override
		protected void paintContentBorderBottomEdge(Graphics g,
				int tabPlacement, int selectedIndex, int x, int y, int w, int h)
		{
		
		}

		@Override
		protected void paintContentBorderRightEdge(Graphics g,
				int tabPlacement, int selectedIndex, int x, int y, int w, int h)
		{ 
			
		}
	}
}
