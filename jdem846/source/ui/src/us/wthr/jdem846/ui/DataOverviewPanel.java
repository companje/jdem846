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
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.Table;
import us.wthr.jdem846.ui.panels.RoundedPanel;

@SuppressWarnings("serial")
public class DataOverviewPanel extends RoundedPanel
{
	

	private OverviewTableModel tableModel;
	
	public DataOverviewPanel()
	{
		
		tableModel = new OverviewTableModel();
		Table table = new Table(tableModel);
		table.setTableHeader(null);
		ScrollPane scroll = new ScrollPane(table);
		scroll.setColumnHeaderView(null);
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(scroll, BorderLayout.CENTER);

	}
	
	public void setValuesVisible(boolean visible)
	{
		
	}
	
	public void setRows(int rows)
	{
		tableModel.rows = rows;
	}
	
	public void setColumns(int columns)
	{
		tableModel.columns = columns;
	}
	
	public void setNorth(double north)
	{
		tableModel.north = north;
	}
	
	public void setSouth(double south)
	{
		tableModel.south = south;
	}
	
	public void setEast(double east)
	{
		tableModel.east = east;
	}
	
	public void setWest(double west)
	{
		tableModel.west = west;
	}
	
	public void setLatitudeResolution(double latitudeResolution)
	{
		tableModel.latitudeResolution = latitudeResolution;
	}

	public void setLongitudeResolution(double longitudeResolution)
	{
		tableModel.longitudeResolution = longitudeResolution;
	}
	

	
	class OverviewTableModel extends DefaultTableModel {
		
		public int rows;
		public int columns;
		
		public double north;
		public double south;
		public double east;
		public double west;
		
		public double latitudeResolution;
		public double longitudeResolution;
		
		@Override
		public int getRowCount()
		{
			return 8;
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			switch (column) {
			case 0:
				return getRowTitle(row);
			case 1:
				return getRowValue(row);
			default:
				return "";
			}
		}
		
		public String getRowValue(int row)
		{
			switch(row) {
			case 0:
				return ""+rows;
			case 1:
				return ""+columns;
			case 2:
				return ""+north;
			case 3:
				return ""+south;
			case 4:
				return ""+east;
			case 5:
				return ""+west;
			case 6:
				return ""+latitudeResolution;
			case 7:
				return ""+longitudeResolution;
			default:
				return "";
			}
		}
		
		public String getRowTitle(int row)
		{
			switch (row) {
			case 0:
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.rows") + ":";
			case 1:
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.columns") + ":";
			case 2: 
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.northLatitude") + ":";
			case 3:
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.southLatitude") + ":";
			case 4:
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.eastLongitude") + ":";
			case 5:
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.westLongitude") + ":";
			case 6:
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.latitudeResolution") + ":";
			case 7:
				return I18N.get("us.wthr.jdem846.ui.dataOverviewPanel.longitudeResolution") + ":";
			default:
				return "";
			}
		}
		
		
	}
	
}
