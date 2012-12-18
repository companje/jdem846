package us.wthr.jdem846ui.views.raster;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.generic.GenericRasterDataProvider;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class RasterPropertiesView extends Composite
{
	private static Log log = Logging.getLog(RasterPropertiesView.class);

	private RasterPropertiesContainer rasterPropertiesContainer;

	
	
	public RasterPropertiesView(Composite parent, int style)
	{
		super(parent, style);
		

		setLayout(new FillLayout());

		rasterPropertiesContainer = new RasterPropertiesContainer(this, SWT.NONE);

		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onSourceDataSelectionChanged(InputSourceData selectedData)
			{
				if (selectedData != null && selectedData instanceof GenericRasterDataProvider) {
					GenericRasterDataProvider provider = (GenericRasterDataProvider) selectedData;
					rasterPropertiesContainer.setRasterDefinition(provider.getRasterDefinition());
				} else {
					rasterPropertiesContainer.setRasterDefinition(null);
				}
			}
		});

		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter()
		{
			public void onProjectLoaded(String filePath)
			{
				rasterPropertiesContainer.setRasterDefinition(null);
			}
		});
		
	}
	

}
