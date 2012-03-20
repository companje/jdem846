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
import java.io.File;

import javax.swing.JFileChooser;

import us.wthr.jdem846.JDem846Properties;

@SuppressWarnings("serial")
public class FileChooser extends JFileChooser
{
	private static String UI_STATE_PATH_PROPERTY = "us.wthr.jdem846.state.ui.fileChooser.path";
	
	private static String lastPath;
	
	static {
		
		String uiStateProp = JDem846Properties.getProperty(UI_STATE_PATH_PROPERTY);
		uiStateProp = uiStateProp.replace("{user.home}", System.getProperty("user.home"));
		lastPath = uiStateProp;
	}
	
	public FileChooser()
	{
		super();
		
		String path = FileChooser.getLastPath();
		if (path != null) {
			this.setCurrentDirectory(new File(path));
		}
	}
	
	@Override
	public int showOpenDialog(Component parent)
	{
		int result = super.showOpenDialog(parent);
		if(result == JFileChooser.APPROVE_OPTION) {
			FileChooser.setLastPath(this.getSelectedFile().getParentFile().getAbsolutePath());
		}
		return result;
	}
	
	@Override
	public int showSaveDialog(Component parent)
	{
		int result = super.showSaveDialog(parent);
		if(result == JFileChooser.APPROVE_OPTION) {
			FileChooser.setLastPath(this.getSelectedFile().getParentFile().getAbsolutePath());
		}
		return result;
	}
	
	public static void setLastPath(String lastPath)
	{
		FileChooser.lastPath = lastPath;
		JDem846Properties.setProperty(UI_STATE_PATH_PROPERTY, lastPath);
	}
	
	public static String getLastPath()
	{
		return FileChooser.lastPath;
	}
}
