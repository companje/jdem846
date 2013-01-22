package us.wthr.jdem846ui.actions;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.export.ExportCompletedListener;
import us.wthr.jdem846.export.ModelImageExporter;
import us.wthr.jdem846ui.ICommandIds;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class ExportModelAction extends BasicAction
{
	
	private ElevationModel selectedElevationModel;
	
	public ExportModelAction(String label, String viewId)
	{
		super(ICommandIds.CMD_EXPORT_MODEL, viewId, label, "/icons/eclipse/export.gif");
		
		
		
		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				selectedElevationModel = elevationModel;
			}
		});
		//ModelImageExporter
	}
	
	
	@Override
	public void run() {
		super.run();
		
		if (selectedElevationModel == null) {
			return;
		}
		
		String exportTo = promptForFilePath(null);
		if (exportTo != null) {
			ModelImageExporter.exportModelImage(selectedElevationModel, exportTo, new ExportCompletedListener() {
				@Override
				public void onSaveSuccessful()
				{	
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Save Successful", "Image was saved successfully");
				}

				@Override
				public void onSaveFailed(Exception ex)
				{
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error on Save", "Error occurred saving image");
				}
			});
		}
		
	}
	
	

	protected String promptForFilePath(String previousFile)
	{
		FileDialog dialog = new FileDialog (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
		String [] filterNames = StandardFileTypes.EXPORT_DATA_FILE_TYPES;
		String [] filterExtensions = StandardFileTypes.EXPORT_DATA_FILE_EXTENSIONS;
		String filterPath = "/";
		String platform = SWT.getPlatform();
		if (platform.equals("win32") || platform.equals("wpf")) {
			filterNames = StandardFileTypes.EXPORT_DATA_FILE_TYPES_WIN;
			filterExtensions = StandardFileTypes.EXPORT_DATA_FILE_EXTENSIONS_WIN;
			filterPath = "c:\\";
		}
		dialog.setFilterNames (filterNames);
		dialog.setFilterExtensions (filterExtensions);
		
		if (previousFile != null) {
			File f = new File(previousFile);
			dialog.setFileName(f.getName());
			dialog.setFilterPath(f.getAbsolutePath());
		} else {
			dialog.setFilterPath(filterPath);
		}
		
		
		return dialog.open();
	}
}
