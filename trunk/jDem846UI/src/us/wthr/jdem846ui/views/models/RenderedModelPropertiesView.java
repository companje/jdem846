package us.wthr.jdem846ui.views.models;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846ui.controls.LabeledText;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class RenderedModelPropertiesView extends ViewPart
{
	public static final String ID = "jdem846ui.renderedModelPropertiesView";
	
	private static RenderedModelPropertiesView INSTANCE;
	
	private LabeledText txtSubject;
	private LabeledText txtDescription;
	private LabeledText txtAuthor;
	private LabeledText txtDateRendered;
	
	private ElevationModel elevationModel = null;
	
	public RenderedModelPropertiesView()
	{
		super();
		RenderedModelPropertiesView.INSTANCE = this;
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		TableWrapLayout layout = new TableWrapLayout();
		parent.setLayout(layout);
		layout.numColumns = 2;
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 2;
		
		txtSubject = LabeledText.create(parent, "Subject:");
		txtDescription = LabeledText.create(parent, "Description:");
		txtAuthor = LabeledText.create(parent, "Author:");
		txtDateRendered = LabeledText.create(parent, "Date Rendered:");
		
		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter() 
		{
			public void onProjectLoaded() {
				update();
			}
		});
		
		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				setElevationModel(elevationModel);
			}
		});
		
		update();
	}

	@Override
	public void setFocus() {
		txtSubject.getControl().setFocus();
	}
	
	public void updateAsync()
	{
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				update();
			}
			
		});
	}
	
	protected void update()
	{
		String subject = (elevationModel != null) ? elevationModel.getProperty("subject") : "";
		String description = (elevationModel != null) ? elevationModel.getProperty("description") : "";
		String author = (elevationModel != null) ? elevationModel.getProperty("author") : "";
		String renderDate = (elevationModel != null) ? elevationModel.getProperty("render-date") : "";
		
		txtSubject.getControl().setText(subject);
		txtDescription.getControl().setText(description);
		txtAuthor.getControl().setText(author);
		txtDateRendered.getControl().setText(renderDate);
		
		
	}
	
	public void setElevationModel(ElevationModel elevationModel)
	{
		this.elevationModel = elevationModel;
		updateAsync();
	}
	
	
	public static RenderedModelPropertiesView getInstance()
	{
		// TODO: Create the view if it hasn't been yet
		return RenderedModelPropertiesView.INSTANCE;
	}
	
}
