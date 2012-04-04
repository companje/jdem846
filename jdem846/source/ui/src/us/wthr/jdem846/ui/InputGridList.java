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

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.ui.base.Table;

@SuppressWarnings("serial")
@Deprecated
public class InputGridList extends Table {

	private InputTableModel tableModel;
	private DefaultListSelectionModel selectionModel;
	
	public InputGridList()
	{
		// Create models
		tableModel = new InputTableModel();
		selectionModel = new DefaultListSelectionModel();
		
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				
			}
		});
		
		// Set properties
		setModel(tableModel);
		setSelectionModel(selectionModel);
		setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
		
		setColumnWidth(0, 40);
		setColumnWidth(1, 200);
	}
	
	
	public void setColumnWidth(int index, int width)
	{
		TableColumn col = getColumnModel().getColumn(index);
		col.setPreferredWidth(width);
	}
	
	public void clearInputData()
	{
		tableModel.clear();
		selectionModel.clearSelection();
	}
	
	public void addInputData(DataSource dataSource)
	{
		tableModel.addInputData(dataSource);
	}
	
	public boolean removeInputData(DataSource dataSource)
	{
		return tableModel.removeInputData(dataSource);
	}
	
	public DataSource removeInputData(int index)
	{
		return tableModel.removeInputData(index);
	}
	
	public void addListSelectionListener(ListSelectionListener listener)
	{
		selectionModel.addListSelectionListener(listener);
	}
	
	public void removeListSelectionListener(ListSelectionListener listener)
	{
		selectionModel.removeListSelectionListener(listener);
	}
	
}
