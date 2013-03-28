package us.wthr.jdem846.ui;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Table;

@SuppressWarnings("serial")
public class ModelPropertiesTable extends Table
{
	
	private static Log log = Logging.getLog(ModelPropertiesTable.class);
	
	private ModelPropertiesTableModel tableModel;
	
	private ElevationModel jdemElevationModel;
	
	
	public ModelPropertiesTable()
	{
		this(null);
	}
	
	public ModelPropertiesTable(JDemElevationModel jdemElevationModel)
	{
		
		tableModel = new ModelPropertiesTableModel();
		this.setModel(tableModel);
		setTableHeader(null);
	}
	
	
	
	
	public ElevationModel getJdemElevationModel()
	{
		return jdemElevationModel;
	}

	
	public void setJdemElevationModel(ElevationModel jdemElevationModel2)
	{
		this.jdemElevationModel = jdemElevationModel2;
		updateData();
	}
	
	public void updateData()
	{
		tableModel.updateData();
		
	}
	
	
	public void addTableModelListener(TableModelListener listener)
	{
		tableModel.addTableModelListener(listener);
	}
	
	public void removeTableModelListener(TableModelListener listener)
	{
		tableModel.removeTableModelListener(listener);
	}
	
	class ModelPropertiesTableModel extends DefaultTableModel
	{
		
		private String[][] data;
		
		@Override
		public int getRowCount()
		{
			if (data != null) {
				return data.length;
			} else {
				return 0;
			}
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return (column == 1);
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			if (data == null) {
				return null;
			}
			
			if (row < 0 || row >= data.length) {
				return null;
			}
			
			if (column < 0 || column > 1) {
				return null;
			}
			
			return I18N.get(data[row][column], data[row][column]);

		}

		
		@Override
		public void setValueAt(Object aValue, int row, int column)
		{
			if (data == null) {
				return;
			}
			
			if (row < 0 || row >= data.length) {
				return;
			}
			
			if (column != 1) {
				return;
			}
			
			data[row][column] = aValue.toString();
			
			jdemElevationModel.setProperty(data[row][0], data[row][1]);
			
			this.fireTableDataChanged();
			
		}

		public void updateData()
		{
			if (jdemElevationModel == null) {
				data = null;
				return;
			}
			
			data = new String[jdemElevationModel.getProperties().size()][2];
			
			int i = 0;
			for (String key : jdemElevationModel.getProperties().keySet()) {
				data[i][0] = key;
				data[i][1] = jdemElevationModel.getProperty(key);
				i++;
			}
			
			repaint();
			
			
		}

		
		
	}
}
