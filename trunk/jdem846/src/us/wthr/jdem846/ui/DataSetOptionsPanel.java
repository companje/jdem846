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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.ui.datasetoptions.ElevationDataSetOptions;
import us.wthr.jdem846.ui.datasetoptions.ShapeDataSetOptions;

@SuppressWarnings("serial")
public class DataSetOptionsPanel extends JPanel
{
	private DataSource dataSource;
	private ShapeFileRequest shapeFileRequest;
	private JButton jbtnUpdatePreview;
	private Component currentConfigPanel = null;
	
	private List<ActionListener> updateListeners = new LinkedList<ActionListener>();
	
	public DataSetOptionsPanel()
	{
		// Create components
		jbtnUpdatePreview = new JButton("Update Preview");
		
		// Add listeners
		jbtnUpdatePreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				fireActionListeners();
			}
		});
		
		// Set layout
		this.setLayout(new BorderLayout());
		this.add(jbtnUpdatePreview, BorderLayout.SOUTH);
		
		// Set default state
		clear();
	}
	
	/** Removes config panel (if present) and hides the update model button.
	 * 
	 */
	public void clear()
	{
		if (currentConfigPanel != null) {
			this.remove(currentConfigPanel);
			currentConfigPanel = null;
		}
		jbtnUpdatePreview.setVisible(false);
	}
	
	public void setElevationDataSet(DataSource dataSource)
	{
		ElevationDataSetOptions panel = new ElevationDataSetOptions(dataSource);
		if (currentConfigPanel != null) {
			this.remove(currentConfigPanel);
		}
		currentConfigPanel = panel;
		this.add(panel, BorderLayout.CENTER);
		jbtnUpdatePreview.setVisible(true);
	}
	
	public void setShapeDataSet(ShapeFileRequest shapeFileRequest)
	{
		ShapeDataSetOptions panel = new ShapeDataSetOptions(shapeFileRequest);
		if (currentConfigPanel != null) {
			this.remove(currentConfigPanel);
		}
		currentConfigPanel = panel;
		this.add(panel, BorderLayout.CENTER);
		jbtnUpdatePreview.setVisible(true);
	}
	
	
	public void addActionListener(ActionListener listener)
	{
		updateListeners.add(listener);
	}
	
	public boolean removeActionListener(ActionListener listener)
	{
		return updateListeners.remove(listener);
	}
	
	public void fireActionListeners()
	{
		ActionEvent event = new ActionEvent(this, 0, "update");
		for (ActionListener listener : updateListeners) {
			listener.actionPerformed(event);
		}
	}
}
