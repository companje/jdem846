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

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class RadioButton extends JRadioButton
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(RadioButton.class);

	public RadioButton()
	{
		super();
		// 
	}

	public RadioButton(Action a)
	{
		super(a);
		// 
	}

	public RadioButton(Icon icon, boolean selected)
	{
		super(icon, selected);
		// 
	}

	public RadioButton(Icon icon)
	{
		super(icon);
		// 
	}

	public RadioButton(String text, boolean selected)
	{
		super(text, selected);
		// 
	}

	public RadioButton(String text, Icon icon, boolean selected)
	{
		super(text, icon, selected);
		// 
	}

	public RadioButton(String text, Icon icon)
	{
		super(text, icon);
		// 
	}

	public RadioButton(String text)
	{
		super(text);
		// 
	}
	
	
	
}
