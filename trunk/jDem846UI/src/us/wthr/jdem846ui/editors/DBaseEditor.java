package us.wthr.jdem846ui.editors;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;

import us.wthr.jdem846.dbase.DBaseFile;
import us.wthr.jdem846.dbase.DBaseRecord;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinition;

public class DBaseEditor extends EditorPart
{
	private static Log log = Logging.getLog(DBaseEditor.class);

	public static final String ID = "us.wthr.jdem846ui.editors.DBaseEditor";

	private Table table;

	public DBaseEditor()
	{

	}

	@Override
	public void createPartControl(Composite parent)
	{
		
		table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);

		ShapeBaseEditorInput editorInput = (ShapeBaseEditorInput) this.getEditorInput();
		ShapeBase shapeBase = editorInput.getShapeBase();
		ShapeDataDefinition shapeDataDef = shapeBase.getShapeDataDefinition();
		Map<String, String> columnNameMap = shapeDataDef.getColumnNameMap();
		this.setPartName((new File(shapeBase.getShapeFileReference().getPath())).getName());
		DBaseFile dbase = shapeBase.getDBaseFile();
		populateTable(dbase, columnNameMap);
		
	}

	
	
	protected void populateTable(DBaseFile dbase, Map<String, String> columnNameMap)
	{
		table.removeAll();
		
		for (String columnName : columnNameMap.keySet()) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnName);
		}
		
		try {

			for (int i = 0; i < dbase.getNumRecords(); i++) {
				DBaseRecord record = dbase.getRecord(i);
				TableItem item = new TableItem (table, SWT.NONE);

				int c = 0;
				for (String columnName : columnNameMap.keySet()) {
					String value = record.getString(columnNameMap.get(columnName));
					if (value != null) {
						item.setText(c, value);
					}
					c++;
				}
			}
		} catch(Exception ex) {
			// TODO Handle this a little better...
			ex.printStackTrace();
		}
		
		for (int i = 0; i < columnNameMap.size(); i++) {
			table.getColumn(i).pack();
		}
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
	{
		setSite(site);
		setInput(input);
	}

	@Override
	public void setFocus()
	{

	}

	@Override
	public void doSave(IProgressMonitor arg0)
	{

	}

	@Override
	public void doSaveAs()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
