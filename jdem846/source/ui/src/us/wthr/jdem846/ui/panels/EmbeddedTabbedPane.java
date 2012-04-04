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

import javax.swing.plaf.basic.BasicTabbedPaneUI;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ui.base.TabPane;

@SuppressWarnings("serial")
public class EmbeddedTabbedPane extends TabPane
{
	
	
	public EmbeddedTabbedPane()
	{	
		super();
		
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.usingJGoodies")) {
			putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);
			putClientProperty("jgoodies.embeddedTabs", Boolean.TRUE);
		} /*else {
			setUI(new EmbeddedTabbedPaneUI());
		}
		*/
		
		
		

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
