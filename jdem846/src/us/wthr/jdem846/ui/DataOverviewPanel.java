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

@SuppressWarnings("serial")
public class DataOverviewPanel extends JPanel
{
	
	private JLabel jlblRows = new JLabel("");
	private JLabel jlblColumns = new JLabel("");
	private JLabel jlblMaxLatitude = new JLabel("");
	private JLabel jlblMinLatitude = new JLabel("");
	private JLabel jlblMaxLongitude = new JLabel("");
	private JLabel jlblMinLongitude = new JLabel("");
	
	private JLabel jlblMaxElevation = new JLabel("");
	private JLabel jlblMinElevation = new JLabel("");
	
	public DataOverviewPanel()
	{
		GridLayout layout = new GridLayout(2, 6);
		layout.setVgap(5);
		layout.setHgap(5);
		setLayout(layout);
		
		JLabel label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.rows") + ":");
		add(label);
		add(jlblRows);
		
		label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.northLatitude") + ":");
		add(label);
		add(jlblMaxLatitude);
		
		label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.eastLongitude") + ":");
		add(label);
		add(jlblMaxLongitude);
		
		label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.columns") + ":");
		add(label);
		add(jlblColumns);
		
		label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.southLatitude") + ":");
		add(label);
		add(jlblMinLatitude);
		
		label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.westLongitude") + ":");
		add(label);
		add(jlblMinLongitude);
		
		//label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.maxElevation") + ":");
		//add(label);
		//add(jlblMaxElevation);
		
		//label = new JLabel(I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.minElevation") + ":");
		//add(label);
		//add(jlblMinElevation);
		
		
	}
	
	public void setValuesVisible(boolean visible)
	{
		jlblRows.setVisible(visible);
		jlblColumns.setVisible(visible);
		jlblMaxLatitude.setVisible(visible);
		jlblMinLatitude.setVisible(visible);
		jlblMaxLongitude.setVisible(visible);
		jlblMinLongitude.setVisible(visible);
		jlblMaxElevation.setVisible(visible);
		jlblMinElevation.setVisible(visible);
	}
	
	public void setRows(int rows)
	{
		jlblRows.setText(""+rows);
	}
	
	public void setColumns(int columns)
	{
		jlblColumns.setText(""+columns);
	}
	
	public void setMaxLatitude(float maxLatitude)
	{
		jlblMaxLatitude.setText(""+maxLatitude);
	}
	
	public void setMinLatitude(float minLatitude)
	{
		jlblMinLatitude.setText(""+minLatitude);
	}
	
	public void setMaxLongitude(float maxLongitude)
	{
		jlblMaxLongitude.setText(""+maxLongitude);
	}
	
	public void setMinLongitude(float minLongitude)
	{
		jlblMinLongitude.setText(""+minLongitude);
	}
	
	public void setMaxElevation(float maxElevation)
	{
		jlblMaxElevation.setText(""+maxElevation);
	}
	
	public void setMinElevation(float minElevation)
	{
		jlblMinElevation.setText(""+minElevation);
	}
	
	
}
