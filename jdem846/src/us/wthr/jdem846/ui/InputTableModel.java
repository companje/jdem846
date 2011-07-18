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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import us.wthr.jdem846.input.DataSource;

@SuppressWarnings("serial")
public class InputTableModel extends AbstractTableModel
{
	
	private List<DataSource> inputData = new LinkedList<DataSource>();
	
	public InputTableModel()
	{
		
	}
	
	public void clear()
	{
		inputData.clear();
	}
	
	public void addInputData(DataSource dataSource)
	{
		inputData.add(dataSource);
		this.fireTableDataChanged();
		
	}
	
	public boolean removeInputData(DataSource dataSource)
	{
		boolean result = inputData.remove(dataSource);
		this.fireTableDataChanged();
		return result;
	}
	
	public DataSource removeInputData(int index)
	{
		DataSource dataSource = inputData.remove(index);
		this.fireTableDataChanged();
		return dataSource;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		switch(columnIndex) {
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		case 2:
			return Integer.class;
		case 3:
			return Integer.class;
		case 4:
			return Float.class;
		case 5:
			return Float.class;
		case 6:
			return Long.class;
		default:
			return String.class;
		}
	}

	@Override
	public String getColumnName(int column) 
	{
		switch(column) {
		case 0:
			return "No.";
		case 1:
			return "File";
		case 2:
			return "Rows";
		case 3:
			return "Columns";
		case 4:
			return "Latitude";
		case 5:
			return "Longitude";
		case 6:
			return "Size (KB)";
		default:
			return "";
		}
	}

	@Override
	public int getColumnCount() 
	{
		
		return 7;
	}

	@Override
	public int getRowCount()
	{
		
		return inputData.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		if (rowIndex < 0 || rowIndex >= inputData.size()) 
			return null;
		
		DataSource dataSource = inputData.get(rowIndex);
		
		switch(columnIndex) {
		case 0:
			return (rowIndex + 1);
		case 1:
			File f0 = new File(dataSource.getFilePath());
			return f0.getName();
		case 2:
			return dataSource.getHeader().getRows();
		case 3:
			return dataSource.getHeader().getColumns();
		case 4:
			return dataSource.getHeader().getyLowerLeft();
		case 5:
			return dataSource.getHeader().getxLowerLeft();
		case 6:
			File f1 = new File(dataSource.getFilePath());
			long size = f1.length();
			return (long) Math.ceil((double)size / 1024.0);
		default:
			return null;
		}
	}
	
	
	
	
}
