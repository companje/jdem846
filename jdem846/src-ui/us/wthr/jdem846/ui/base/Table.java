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

package us.wthr.jdem846.ui.base;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class Table extends JTable
{
	private static Log log = Logging.getLog(Table.class);

	public Table()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Table(int numRows, int numColumns)
	{
		super(numRows, numColumns);
		// TODO Auto-generated constructor stub
	}

	public Table(Object[][] rowData, Object[] columnNames)
	{
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}

	public Table(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
	{
		super(dm, cm, sm);
		// TODO Auto-generated constructor stub
	}

	public Table(TableModel dm, TableColumnModel cm)
	{
		super(dm, cm);
		// TODO Auto-generated constructor stub
	}

	public Table(TableModel dm)
	{
		super(dm);
		// TODO Auto-generated constructor stub
	}

	public Table(Vector rowData, Vector columnNames)
	{
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}
	
	
}
