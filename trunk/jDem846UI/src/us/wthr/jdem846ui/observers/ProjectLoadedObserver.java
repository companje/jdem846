package us.wthr.jdem846ui.observers;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.editors.JavascriptEditor;
import us.wthr.jdem846ui.editors.ModelPreviewEditorInput;
import us.wthr.jdem846ui.editors.PreviewEditor;
import us.wthr.jdem846ui.editors.ScriptInput;
import us.wthr.jdem846ui.editors.ScriptStorage;

public class ProjectLoadedObserver extends ProjectChangeObserver
{
	private static Log log = Logging.getLog(ProjectLoadedObserver.class);

	public static ProjectLoadedObserver INSTANCE = null;

	static {
		ProjectLoadedObserver.INSTANCE = new ProjectLoadedObserver();
	}

	public ProjectLoadedObserver()
	{
		super();

	}

	@Override
	public void onBeforeProjectLoaded(String filePathOld, String filePathNew)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeAllEditors(true);
	}
	
	
	@Override
	public void onProjectLoaded(String projectPath)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		
		IEditorPart previewEditor = null;
		try {
			previewEditor = page.openEditor(new ModelPreviewEditorInput(), PreviewEditor.ID);
		} catch (PartInitException ex) {
			ex.printStackTrace();
		}
		
		
		try {
			page.openEditor(new ScriptInput(new ScriptStorage()), JavascriptEditor.ID);
		} catch (PartInitException ex) {
			ex.printStackTrace();
		}
		

		if (previewEditor != null) {
			previewEditor.getEditorSite().getPage().activate(previewEditor);
		}
		
		
	}
	
	
/*	protected IEditorPart openFile(File file)
	{
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editor = IDE.openEditor(page, file.toURI(), JavascriptEditor.ID, true);
			return editor;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}*/
	

	public static ProjectLoadedObserver getInstance()
	{
		return ProjectLoadedObserver.INSTANCE;
	}
}
