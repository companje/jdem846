package us.wthr.jdem846ui.views.raster;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.generic.GenericRasterDataProvider;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionListener;

public class RasterPropertiesView extends ViewPart
{
	private static Log log = Logging.getLog(RasterPropertiesView.class);
	public static final String ID = "jdem846ui.rasterPropertiesView";
	
	private RasterPropertiesContainer rasterPropertiesContainer;
	
	@Override
	public void createPartControl(Composite parent) {
		
		
		TableWrapLayout layout = new TableWrapLayout();
		parent.setLayout(layout);
		layout.numColumns = 1;
		
		rasterPropertiesContainer = new RasterPropertiesContainer(parent, SWT.NONE);
		
		DataView.addTreeSelectionListener(new TreeSelectionListener() {
			public void onSelectionChanged(InputSourceData selectedData) {
				if (selectedData != null && selectedData instanceof GenericRasterDataProvider) {
					GenericRasterDataProvider provider = (GenericRasterDataProvider) selectedData;
					rasterPropertiesContainer.setRasterDefinition(provider.getRasterDefinition());
				} else {
					rasterPropertiesContainer.setRasterDefinition(null);
				}
			}
		});
		
	}

	@Override
	public void setFocus() {
		rasterPropertiesContainer.setFocus();
	}
	
	
}
