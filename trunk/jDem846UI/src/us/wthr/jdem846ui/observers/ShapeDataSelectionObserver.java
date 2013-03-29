package us.wthr.jdem846ui.observers;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846ui.editors.DBaseEditor;
import us.wthr.jdem846ui.editors.ShapeBaseEditorInput;

public class ShapeDataSelectionObserver
{
	private static Log log = Logging.getLog(ShapeDataSelectionObserver.class);
	
	private static ShapeDataSelectionObserver INSTANCE;
	
	static {
		ShapeDataSelectionObserver.INSTANCE = new ShapeDataSelectionObserver();
	}
	
	public ShapeDataSelectionObserver()
	{
		
	}
	
	
	public void openShapeData(ShapeBase shapeBase)
	{
		if (shapeBase != null) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				if (isEditorOpen(shapeBase)) {
					page.activate(getEditorReferenceForShapeBase(shapeBase).getEditor(false));
				} else {
					log.info("Shape Database Selected");
					page.openEditor(new ShapeBaseEditorInput(shapeBase), DBaseEditor.ID);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	protected IEditorReference getEditorReferenceForShapeBase(ShapeBase shapeBase) throws Exception
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		for (IEditorReference editorReference : page.getEditorReferences()) {
			
			IEditorInput editorInput = editorReference.getEditorInput();
			if (editorInput instanceof ShapeBaseEditorInput) {
				ShapeBaseEditorInput shapeBaseEditorInput = (ShapeBaseEditorInput) editorInput;
				
				if (shapeBase.equals(shapeBaseEditorInput.getShapeBase())) {
					return editorReference;
				}
				
			}
			
		}
		return null;
	}
	
	protected boolean isEditorOpen(ShapeBase shapeBase) throws Exception
	{
		return getEditorReferenceForShapeBase(shapeBase) != null;
		
	}
	
	public static ShapeDataSelectionObserver getInstance()
	{
		return ShapeDataSelectionObserver.INSTANCE;
	}
	
}
