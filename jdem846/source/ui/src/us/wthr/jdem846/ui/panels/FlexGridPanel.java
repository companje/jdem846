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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class FlexGridPanel extends Panel
{
	private static Log log = Logging.getLog(FlexGridPanel.class);
	
	private int columns = 1;
	private int addColumn = 0;
	private GridBagLayout gridbag;
	
	public FlexGridPanel(int columns)
	{
		this.columns = columns;
		
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		
	}
	
	@Override
	public void dispose() throws ComponentException
	{
		super.dispose();
	}
	
	@Override
	public Component add(Component component)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		addColumn++;
		
		if (addColumn >= columns) {
			constraints.gridwidth  = GridBagConstraints.REMAINDER;
			addColumn = 0;	
		} else {
			constraints.gridwidth  = 1;
		}
		gridbag.setConstraints(component, constraints);
		return super.add(component);
	}
}
