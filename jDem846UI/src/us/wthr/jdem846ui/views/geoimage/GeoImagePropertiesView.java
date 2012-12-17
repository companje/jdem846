package us.wthr.jdem846ui.views.geoimage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.image.ISimpleGeoImageDefinition;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class GeoImagePropertiesView extends ViewPart
{
	private static Log log = Logging.getLog(GeoImagePropertiesView.class);
	public static final String ID = "jdem846ui.geoImagePropertiesView";
	
	private GeoImagePropertiesContainer geoImagePropertiesContainer;
	
	@Override
	public void createPartControl(Composite parent)
	{

		parent.setLayout(new FillLayout());

		geoImagePropertiesContainer = new GeoImagePropertiesContainer(parent, SWT.NONE);

		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onSourceDataSelectionChanged(InputSourceData selectedData)
			{
				if (selectedData != null && selectedData instanceof ISimpleGeoImageDefinition) {
					showView();
					ISimpleGeoImageDefinition definition = (ISimpleGeoImageDefinition) selectedData;
					geoImagePropertiesContainer.setImageDefinition(definition);
				} else {
					geoImagePropertiesContainer.setImageDefinition(null);
				}
			}
		});

		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter()
		{
			public void onProjectLoaded()
			{
				geoImagePropertiesContainer.setImageDefinition(null);
			}
		});

	}

	@Override
	public void setFocus()
	{
		geoImagePropertiesContainer.setFocus();
	}

	public void showView()
	{
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(GeoImagePropertiesView.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
}
