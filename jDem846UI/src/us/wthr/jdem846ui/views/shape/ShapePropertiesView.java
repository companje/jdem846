package us.wthr.jdem846ui.views.shape;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.context.ProjectChangeAdapter;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;
import us.wthr.jdem846ui.views.geoimage.GeoImagePropertiesView;

public class ShapePropertiesView extends Composite
{
	private static Log log = Logging.getLog(GeoImagePropertiesView.class);
	private ShapePropertiesContainer shapePropertiesContainer;
	
	public ShapePropertiesView(Composite parent, int style)
	{
		super(parent, style);
		
		setLayout(new FillLayout());

		shapePropertiesContainer = new ShapePropertiesContainer(this, SWT.NONE);

		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onSourceDataSelectionChanged(InputSourceData selectedData)
			{
				if (selectedData != null && selectedData instanceof ShapeFileRequest) {
					ShapeFileRequest definition = (ShapeFileRequest) selectedData;
					shapePropertiesContainer.setShapeDefinition(definition);
				} else {
					shapePropertiesContainer.setShapeDefinition(null);
				}
			}
		});

		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter()
		{
			public void onProjectLoaded(String filePath)
			{
				shapePropertiesContainer.setShapeDefinition(null);
			}
		});

	}

}
