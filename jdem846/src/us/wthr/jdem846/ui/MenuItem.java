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

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ImageIcons;

@SuppressWarnings("serial")
public class MenuItem extends JMenuItem
{
	private static Log log = Logging.getLog(MenuItem.class);
	
	
	public MenuItem(String text, String iconPath, int mnemonic, ActionListener actionListener)
	{
		super(text, mnemonic);
		try {
			setIcon(ImageIcons.loadImageIcon(iconPath));
		} catch (IOException ex) {
			log.warn("Failed to load icon: " + ex.getMessage(), ex);
		}
		this.addActionListener(actionListener);
		setPreferredSize();
	}
	
	public MenuItem(String text, String iconPath, int mnemonic, ActionListener actionListener, KeyStroke keyStroke)
	{
		super(text, mnemonic);
		try {
			setIcon(ImageIcons.loadImageIcon(iconPath));
		} catch (IOException ex) {
			log.warn("Failed to load icon: " + ex.getMessage(), ex);
		}
		this.addActionListener(actionListener);
		this.setAccelerator(keyStroke);
		setPreferredSize();
	}
	
	public MenuItem(String text, int mnemonic, ActionListener actionListener)
	{
		super(text, mnemonic);
		this.addActionListener(actionListener);
		setPreferredSize();
	}
	
	public MenuItem(String text, int mnemonic, ActionListener actionListener, KeyStroke keyStroke)
	{
		super(text, mnemonic);
		this.addActionListener(actionListener);
		this.setAccelerator(keyStroke);
		setPreferredSize();
	}
	
	
	protected void setPreferredSize()
	{
		int height = this.getPreferredSize().height;
		this.setMinimumSize(new Dimension(200, height));
		this.setPreferredSize(new Dimension(200, height));
	}
	
}
