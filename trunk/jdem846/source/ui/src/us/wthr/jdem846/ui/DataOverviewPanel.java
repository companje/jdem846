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

@SuppressWarnings("serial")
public class DataOverviewPanel extends Panel
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
	
	public void setMaxLatitude(double maxLatitude)
	{
		tableModel.north = maxLatitude;
	}
	
	public void setMinLatitude(double minLatitude)
	{
		tableModel.south = minLatitude;
	}
	
	public void setMaxLongitude(double maxLongitude)
	{
		tableModel.east = maxLongitude;
	}
	
	public void setMinLongitude(double minLongitude)
	{
		tableModel.west = minLongitude;
	}
	

	
	class OverviewTableModel extends DefaultTableModel {
		
		public int rows;
		public int columns;
		
		public double north;
		public double south;
		public double east;
		public double west;
		
		
		@Override
		public int getRowCount()
		{
			return 6;
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
			default:
				return "";
			}
		}
		
		
	}
	
}
