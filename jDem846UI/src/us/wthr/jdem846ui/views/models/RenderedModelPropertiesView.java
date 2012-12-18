package us.wthr.jdem846ui.views.models;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class RenderedModelPropertiesView extends Composite
{
	
	private RenderedModelPropertiesContainer renderedModelPropertiesContainer;
	
	public RenderedModelPropertiesView(Composite parent, int style)
	{
		super(parent, style);
		
		setLayout(new FillLayout());
		
		this.renderedModelPropertiesContainer = new RenderedModelPropertiesContainer(this, SWT.NONE);
		
		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter() 
		{
			public void onProjectLoaded(String filePath) {
				renderedModelPropertiesContainer.updateValuesFromModel();
			}
		});
		
		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				renderedModelPropertiesContainer.setElevationModel(elevationModel);
			}
		});
		
		renderedModelPropertiesContainer.updateValuesFromModel();
		
	}
	

}
