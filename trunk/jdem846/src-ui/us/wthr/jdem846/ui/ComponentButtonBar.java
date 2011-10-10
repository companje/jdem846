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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import us.wthr.jdem846.ui.base.ToolBar;

@SuppressWarnings("serial")
public class ComponentButtonBar extends ToolBar
{
	
	public ComponentButtonBar(Component owner)
	{
		owner.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e)
			{
				onOwnerHidden();
			}
			public void componentShown(ComponentEvent e)
			{
				onOwnerShown();
			}
		});
	}
	
	protected void onOwnerShown()
	{
		setVisible(true);
	}
	
	protected void onOwnerHidden()
	{
		setVisible(false);
	}
}
