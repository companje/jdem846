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

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class DataOverviewPanel extends Panel
{
	
	private Label lblRows = new Label("");
	private Label lblColumns = new Label("");
	private Label lblMaxLatitude = new Label("");
	private Label lblMinLatitude = new Label("");
	private Label lblMaxLongitude = new Label("");
	private Label lblMinLongitude = new Label("");
	
	private Label lblMaxElevation = new Label("");
	private Label lblMinElevation = new Label("");
	
	public DataOverviewPanel()
	{
		GridLayout layout = new GridLayout(3, 4);
		layout.setVgap(5);
		layout.setHgap(5);
		setLayout(layout);
		
		Label label = new Label(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.columns") + ":");
		add(label);
		add(lblColumns);
		
		label = new Label(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.rows") + ":");
		add(label);
		add(lblRows);
		
		
		label = new Label(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.southLatitude") + ":");
		add(label);
		add(lblMinLatitude);
		
		label = new Label(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.northLatitude") + ":");
		add(label);
		add(lblMaxLatitude);
		

		label = new Label(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.westLongitude") + ":");
		add(label);
		add(lblMinLongitude);
		
		label = new Label(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.eastLongitude") + ":");
		add(label);
		add(lblMaxLongitude);
		
		
		
		
		
		
		//label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.maxElevation") + ":");
		//add(label);
		//add(jlblMaxElevation);
		
		//label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.minElevation") + ":");
		//add(label);
		//add(jlblMinElevation);
		
		
	}
	
	public void setValuesVisible(boolean visible)
	{
		lblRows.setVisible(visible);
		lblColumns.setVisible(visible);
		lblMaxLatitude.setVisible(visible);
		lblMinLatitude.setVisible(visible);
		lblMaxLongitude.setVisible(visible);
		lblMinLongitude.setVisible(visible);
		lblMaxElevation.setVisible(visible);
		lblMinElevation.setVisible(visible);
	}
	
	public void setRows(int rows)
	{
		lblRows.setText(""+rows);
	}
	
	public void setColumns(int columns)
	{
		lblColumns.setText(""+columns);
	}
	
	public void setMaxLatitude(float maxLatitude)
	{
		lblMaxLatitude.setText(""+maxLatitude);
	}
	
	public void setMinLatitude(float minLatitude)
	{
		lblMinLatitude.setText(""+minLatitude);
	}
	
	public void setMaxLongitude(float maxLongitude)
	{
		lblMaxLongitude.setText(""+maxLongitude);
	}
	
	public void setMinLongitude(float minLongitude)
	{
		lblMinLongitude.setText(""+minLongitude);
	}
	
	public void setMaxElevation(float maxElevation)
	{
		lblMaxElevation.setText(""+maxElevation);
	}
	
	public void setMinElevation(float minElevation)
	{
		lblMinElevation.setText(""+minElevation);
	}
	
	
}
