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
import javax.swing.JMenu;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class Menu extends JMenu
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(Menu.class);

	public Menu()
	{
		super();
	}

	public Menu(Action a)
	{
		super(a);
	}

	public Menu(String s, boolean b)
	{
		super(s, b);
	}

	public Menu(String s)
	{
		super(s);
	}
	
	
}
