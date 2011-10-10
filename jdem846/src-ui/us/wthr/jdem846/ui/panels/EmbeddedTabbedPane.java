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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import us.wthr.jdem846.ui.base.TabPane;

@SuppressWarnings("serial")
public class EmbeddedTabbedPane extends TabPane
{
	
	
	public EmbeddedTabbedPane()
	{	
		setUI(new EmbeddedTabbedPaneUI());
	}
	
	
	class EmbeddedTabbedPaneUI extends BasicTabbedPaneUI
	{

		public EmbeddedTabbedPaneUI()
		{

		}
		
		@Override
		protected void paintTabBorder(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected)
		{
			Rectangle bounds = getTabBounds(this.tabPane, tabIndex);
			
			int left = bounds.x;
			int top = bounds.y;
			
			g.setColor(Color.GRAY);
			g.drawRoundRect(left, top, w, maxTabHeight + 2, 4, 4);

			if (isSelected) {
				g.setColor(Color.WHITE);
				g.drawRoundRect(left+1, top+1, w-2, maxTabHeight + 2, 4, 4);
			}
			
			g.setColor(tabPane.getBackground());
			g.fillRect(left + 1, h, w - 2, h + 2);
		}

		@Override
		protected void paintTabBackground(Graphics g, int tabPlacement,
				int tabIndex, int x, int y, int w, int h, boolean isSelected)
		{

			Rectangle bounds = getTabBounds(this.tabPane, tabIndex);
			int left = bounds.x;
			int top = bounds.y;
			
			g.setColor(tabPane.getBackground());
			g.fillRect(left, top, w, h);
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
