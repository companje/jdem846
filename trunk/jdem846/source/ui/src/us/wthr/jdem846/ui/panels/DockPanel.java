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

package us.wthr.jdem846.ui.panels;

import java.awt.Component;

import us.wthr.jdem846.ui.base.ScrollPane;


@SuppressWarnings("serial")
public class DockPanel extends ScrollPane
{
	
	
	public DockPanel(Component child)
	{
		//ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
		//ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
		//ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
		
		//ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
		//ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
		//ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
		
		super(child);//, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	
	
}
