package us.wthr.jdem846ui.observers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.editors.ElevationModelEditorInput;
import us.wthr.jdem846ui.editors.renderedmodel.RenderedModelEditor;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;


public class RenderedModelSelectionObserver
{
	private static Log log = Logging.getLog(RenderedModelSelectionObserver.class);
	
	private static RenderedModelSelectionObserver INSTANCE;
	
	private static List<RenderedModelSelectionListener> selectionListeners = new LinkedList<RenderedModelSelectionListener>();
	
	static {
		RenderedModelSelectionObserver.INSTANCE = new RenderedModelSelectionObserver();
	}
	
	
	public RenderedModelSelectionObserver()
	{
		
		
		
		
		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				
			}
		});
		
	}
	
	public void addRenderedModelSelectionListener(RenderedModelSelectionListener l)
	{
		RenderedModelSelectionObserver.selectionListeners.add(l);
	}
	
	public boolean removeRenderedModelSelectionListener(RenderedModelSelectionListener l)
	{
		return RenderedModelSelectionObserver.selectionListeners.remove(l);
	}
	
	public void fireRenderedModelSelected(ElevationModel elevationModel)
	{
		for (RenderedModelSelectionListener listener : RenderedModelSelectionObserver.selectionListeners) {
			listener.onRenderedModelSelected(elevationModel);
		}
	}
	
	
	public void openElevationModel(ElevationModel elevationModel)
	{
		if (elevationModel != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				if (isEditorOpen(elevationModel)) {
					page.activate(getEditorReferenceForElevationModel(elevationModel).getEditor(false));
				} else {
					log.info("Rendered Model Selected");
					page.openEditor(new ElevationModelEditorInput(elevationModel), RenderedModelEditor.ID);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	
	protected IEditorReference getEditorReferenceForElevationModel(ElevationModel elevationModel) throws Exception
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		for (IEditorReference editorReference : page.getEditorReferences()) {
			
			IEditorInput editorInput = editorReference.getEditorInput();
			if (editorInput instanceof ElevationModelEditorInput) {
				ElevationModelEditorInput elevationModelEditorInput = (ElevationModelEditorInput) editorInput;
				
				if (elevationModel.equals(elevationModelEditorInput.getElevationModel())) {
					return editorReference;
				}
				
			}
			
		}
		return null;
	}
	
	protected boolean isEditorOpen(ElevationModel elevationModel) throws Exception
	{
		return getEditorReferenceForElevationModel(elevationModel) != null;
		
	}
	
	public static RenderedModelSelectionObserver getInstance()
	{
		return RenderedModelSelectionObserver.INSTANCE;
	}
	
	
	
	public interface RenderedModelSelectionListener {
		public void onRenderedModelSelected(ElevationModel elevationModel);
	}
}
