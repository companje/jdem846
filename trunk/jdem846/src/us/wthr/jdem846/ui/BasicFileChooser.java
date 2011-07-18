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
import java.io.File;

import javax.swing.JFileChooser;

@SuppressWarnings("serial")
public class BasicFileChooser extends JFileChooser
{
	private static String lastPath;
	
	static {
		lastPath = System.getProperty("user.home");
	}
	
	public BasicFileChooser()
	{
		super();
		
		String path = BasicFileChooser.getLastPath();
		if (path != null) {
			this.setCurrentDirectory(new File(path));
		}
	}
	
	@Override
	public int showOpenDialog(Component parent)
	{
		int result = super.showOpenDialog(parent);
		if(result == JFileChooser.APPROVE_OPTION) {
			BasicFileChooser.setLastPath(this.getSelectedFile().getParentFile().getAbsolutePath());
		}
		return result;
	}
	
	@Override
	public int showSaveDialog(Component parent)
	{
		int result = super.showSaveDialog(parent);
		if(result == JFileChooser.APPROVE_OPTION) {
			BasicFileChooser.setLastPath(this.getSelectedFile().getParentFile().getAbsolutePath());
		}
		return result;
	}
	
	public static void setLastPath(String lastPath)
	{
		BasicFileChooser.lastPath = lastPath;
	}
	
	public static String getLastPath()
	{
		return BasicFileChooser.lastPath;
	}
}
