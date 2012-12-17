package us.wthr.jdem846ui.views.raster;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.generic.GenericRasterDataProvider;
import us.wthr.jdem846ui.project.ProjectChangeAdapter;
import us.wthr.jdem846ui.project.ProjectContext;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;

public class RasterPropertiesView extends ViewPart
{
	private static Log log = Logging.getLog(RasterPropertiesView.class);
	public static final String ID = "jdem846ui.rasterPropertiesView";

	private RasterPropertiesContainer rasterPropertiesContainer;

	@Override
	public void createPartControl(Composite parent)
	{

		/*
		 * TableWrapLayout layout = new TableWrapLayout(); layout.numColumns =
		 * 1; layout.bottomMargin = 0; layout.topMargin = 0; layout.leftMargin =
		 * 0; layout.rightMargin = 0; layout.numColumns = 1;
		 * parent.setLayout(layout);
		 */
		parent.setLayout(new FillLayout());

		rasterPropertiesContainer = new RasterPropertiesContainer(parent, SWT.NONE);
		// TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		// ///td.grabVertical = true;
		// rasterPropertiesContainer.setLayoutData(td);

		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onSourceDataSelectionChanged(InputSourceData selectedData)
			{
				if (selectedData != null && selectedData instanceof GenericRasterDataProvider) {
					showView();
					GenericRasterDataProvider provider = (GenericRasterDataProvider) selectedData;
					rasterPropertiesContainer.setRasterDefinition(provider.getRasterDefinition());
				} else {
					rasterPropertiesContainer.setRasterDefinition(null);
				}
			}
		});

		ProjectContext.getInstance().addProjectChangeListener(new ProjectChangeAdapter()
		{
			public void onProjectLoaded()
			{
				rasterPropertiesContainer.setRasterDefinition(null);
			}
		});

	}

	@Override
	public void setFocus()
	{
		rasterPropertiesContainer.setFocus();
	}

	public void showView()
	{
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RasterPropertiesView.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
