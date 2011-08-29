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

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import us.wthr.jdem846.ui.base.MenuBar;

@SuppressWarnings("serial")
public class MainMenuBar extends MenuBar
{
	
	private static MainMenuBar instance = null;
	private static int insertIndex = -1;
	
	protected MainMenuBar()
	{
		
	}
	
	
	public static void insertMenu(JMenu menu)
	{
		MainMenuBar.getInstance().add(menu, MainMenuBar.getInsertIndex());
		MainMenuBar.getInstance().repaint();
	}
	
	public static void removeMenu(JMenu menu)
	{
		MainMenuBar.getInstance().remove(menu);
		MainMenuBar.getInstance().repaint();
	}
	
	public static MainMenuBar getInstance()
	{
		if (instance == null) {
			instance = new MainMenuBar();
		}
		return instance;
	}
	
	public static void setInsertIndex(int insertIndex)
	{
		MainMenuBar.insertIndex = insertIndex;
	}
	
	public static int getInsertIndex()
	{
		return MainMenuBar.insertIndex;
	}
	
}
