package us.wthr.jdem846ui.views.models;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.tree.TreeObject;
import us.wthr.jdem846ui.views.tree.ViewContentProvider;

public class RenderedModelListView extends ViewPart
{
	public static final String ID = "jdem846ui.renderedModelListView";
	private static Log log = Logging.getLog(RenderedModelListView.class);
	
	
	private TreeViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		
		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter() {

			@Override
			public void onElevationModelAdded(ElevationModel elevationModel) {
				log.info("Model added: Rebuilding rendered model list");
				resetAndUpdateModelAsync(elevationModel);
			}

			@Override
			public void onElevationModelRemoved(ElevationModel elevationModel) {
				log.info("Model removed: Rebuilding rendered model list");
				resetAndUpdateModelAsync(null);
			}
			
			@Override
			public void onProjectLoaded(String projectPath) {
				resetAndUpdateModelAsync(null);
			}
		});
		
		
		
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		//viewer.setLabelProvider(new ViewLabelProvider());
		
		
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ElevationModel selectedElevationModel = null;

				if (!event.getSelection().isEmpty()) {
					
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					TreeObject<ElevationModel> treeObject = (TreeObject<ElevationModel>) selection.getFirstElement();
					
					if (treeObject instanceof ModelTreeObject) {
						ModelTreeObject modelTreeObject = (ModelTreeObject)treeObject;
						selectedElevationModel = modelTreeObject.getElevationModel();
					}
				}
				
				activateTreeObject(selectedElevationModel);
				
			}
		});
		

		resetAndUpdateModelAsync(null);
		
	}
	
	
	protected void activateTreeObject(ElevationModel elevationModel)
	{
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RenderedModelPropertiesView.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RenderedModelDisplayView.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RenderedModelPropertiesView propertiesView = RenderedModelPropertiesView.getInstance();
		if (propertiesView != null) {
			propertiesView.setElevationModel(elevationModel);
		}
		
		RenderedModelDisplayView displayView = RenderedModelDisplayView.getInstance();
		if (displayView != null) {
			displayView.setElevationModel(elevationModel);
		}
	}
	
	protected Object createTreeModel()
	{
		ModelTreeParent topNode = new ModelTreeParent("");
		
		
		
		for (ElevationModel elevationModel : ProjectContext.getInstance().getElevationModelList()) {
			String modelSubject = elevationModel.getProperty("subject");
			String renderDate = elevationModel.getProperty("render-date");
			
			ModelTreeObject treeObject = new ModelTreeObject(modelSubject + " - " + renderDate, elevationModel);
			topNode.addChild(treeObject);
		}
		
		return topNode;
	}
	
	
	protected void resetAndUpdateModelAsync(final ElevationModel selectElevationModel)
	{
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				resetAndUpdateModel(selectElevationModel);
			}
			
		});
	}
	
	protected void resetAndUpdateModel(ElevationModel selectElevationModel)
	{
		viewer.setInput(createTreeModel());
		viewer.expandAll();
		
		
	}
	
	

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
