package us.wthr.jdem846ui.controls;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class FilePicker extends Composite
{
	
	private Text txtPath;
	private Button btnBrowse;
	
	public FilePicker(Composite parent, int style)
	{
		super(parent, style);
		
		TableWrapLayout layout = new TableWrapLayout();
		this.setLayout(layout);
		layout.numColumns = 2;
		
		txtPath = new Text(this, SWT.BORDER | SWT.SINGLE);
		txtPath.setEditable(true);
		
		btnBrowse = new Button(this, SWT.PUSH);
		btnBrowse.setText("Browse...");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				setFilePath(promptForFilePath(getFilePath()));
			}
		});
		
	

		txtPath.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE));
		btnBrowse.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		
	}
	
	public void addModifyListener(ModifyListener listener)
	{
		txtPath.addModifyListener(listener);
	}
	
	public void addSelectionListener(SelectionListener listener)
	{
		txtPath.addSelectionListener(listener);
	}
	
	public String getFilePath()
	{
		return txtPath.getText();
	}
	
	public void setFilePath(String path)
	{
		if (!txtPath.isDisposed()) {
			txtPath.setText(path);
		}
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		if (!txtPath.isDisposed()) {
			txtPath.setEnabled(enabled);
		}
		if (!btnBrowse.isDisposed()) {
			btnBrowse.setEnabled(enabled);
		}
	}
	

	
	
	protected String promptForFilePath(String previousFile)
	{

		FileDialog dialog = new FileDialog (getShell(), SWT.SAVE);

		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterPath = "c:\\";
		}

		if (previousFile != null && previousFile.length() > 0) {
			File f = new File(previousFile);
			dialog.setFileName(f.getName());
			dialog.setFilterPath(f.getAbsolutePath());
		} else {
			dialog.setFilterPath(filterPath);
		}
		
		
		return dialog.open();
	}
}
