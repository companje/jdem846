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

package us.wthr.jdem846.ui.border;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.border.EtchedBorder;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class ButtonBorder extends EtchedBorder
{
	private static Log log = Logging.getLog(ButtonBorder.class);
	
	private int borderPadding = 5;
	
	public ButtonBorder()
	{
		
	}
	
	@Override
	public Insets getBorderInsets(Component c)
	{
		Insets borderInserts = super.getBorderInsets(c);
		
		borderInserts.top = 
				borderInserts.left = 
				borderInserts.right =
				borderInserts.bottom = borderPadding;

		return borderInserts;
	}
	
	@Override
	public Insets getBorderInsets(Component c, Insets insets)
	{
		Insets borderInserts = super.getBorderInsets(c, insets);
		
		borderInserts.top = 
				borderInserts.left = 
				borderInserts.right =
				borderInserts.bottom = borderPadding;
		
		return borderInserts;
	}

	public int getBorderPadding()
	{
		return borderPadding;
	}

	public void setBorderPadding(int borderPadding)
	{
		this.borderPadding = borderPadding;
	}
	
	
	
	
}
