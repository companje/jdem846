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

import java.awt.FlowLayout;

import javax.swing.BorderFactory;

import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ToolBar;
import us.wthr.jdem846.ui.layouts.ModifiedFlowLayout;

@SuppressWarnings("serial")
public class MainButtonBar extends Panel
{
	
	private static MainButtonBar instance = null;
	
	protected MainButtonBar()
	{
		/*
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		layout.setHgap(0);
		layout.setVgap(0);
		setLayout(layout);
		*/
		ModifiedFlowLayout mFlowLayout = new ModifiedFlowLayout(FlowLayout.LEFT, 1, 1);
		setLayout(mFlowLayout);
	}
	
	
	public static void addToolBar(ToolBar toolbar)
	{
		MainButtonBar.getInstance().add(toolbar);
		MainButtonBar.getInstance().repaint();
	}
	
	public static void removeToolBar(ToolBar toolbar)
	{
		MainButtonBar.getInstance().remove(toolbar);
		MainButtonBar.getInstance().repaint();
	}
	
	public static MainButtonBar getInstance()
	{
		if (instance == null) {
			instance = new MainButtonBar();
		}
		
		return instance;
	}
	
}
