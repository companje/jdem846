package us.wthr.jdem846ui.views.geoimage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.image.ISimpleGeoImageDefinition;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class GeoImagePropertiesView extends Composite
{
	
	private static Log log = Logging.getLog(GeoImagePropertiesView.class);
	private GeoImagePropertiesContainer geoImagePropertiesContainer;
	
	public GeoImagePropertiesView(Composite parent, int style)
	{
		super(parent, style);
		
		setLayout(new FillLayout());

		geoImagePropertiesContainer = new GeoImagePropertiesContainer(this, SWT.NONE);

		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onSourceDataSelectionChanged(InputSourceData selectedData)
			{
				if (selectedData != null && selectedData instanceof ISimpleGeoImageDefinition) {
					ISimpleGeoImageDefinition definition = (ISimpleGeoImageDefinition) selectedData;
					geoImagePropertiesContainer.setImageDefinition(definition);
				} else {
					geoImagePropertiesContainer.setImageDefinition(null);
				}
			}
		});

		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter()
		{
			public void onProjectLoaded(String filePath)
			{
				geoImagePropertiesContainer.setImageDefinition(null);
			}
		});

	}



}
