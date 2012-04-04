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

import javax.swing.Icon;
import javax.swing.JLabel;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class Label extends JLabel
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(Label.class);

	public Label()
	{
		super();
	}

	public Label(Icon image, int horizontalAlignment)
	{
		super(image, horizontalAlignment);
	}

	public Label(Icon image)
	{
		super(image);
	}

	public Label(String text, Icon icon, int horizontalAlignment)
	{
		super(text, icon, horizontalAlignment);
	}

	public Label(String text, int horizontalAlignment)
	{
		super(text, horizontalAlignment);
	}

	public Label(String text)
	{
		super(text);
	}
	
	
	
}
