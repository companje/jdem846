package us.wthr.jdem846ui.views.layers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.image.ISimpleGeoImage;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.generic.GenericRasterDataProvider;
import us.wthr.jdem846ui.views.data.DataView;
import us.wthr.jdem846ui.views.data.TreeSelectionAdapter;
import us.wthr.jdem846ui.views.geoimage.GeoImagePropertiesView;
import us.wthr.jdem846ui.views.models.RenderedModelPropertiesView;
import us.wthr.jdem846ui.views.raster.RasterPropertiesView;

public class LayerPropertiesView extends ViewPart
{
	private static Log log = Logging.getLog(LayerPropertiesView.class);
	public static final String ID = "jdem846ui.layerPropertiesView";
	
	private GeoImagePropertiesView geoImagePropertiesView;
	private RasterPropertiesView rasterPropertiesView;
	private RenderedModelPropertiesView renderedModelPropertiesView;
	
	private Composite blankView;
	private Composite activeView = null;
	
	private StackLayout stackLayout;
	
	
	
	@Override
	public void createPartControl(final Composite parent)
	{
		
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);
		
		blankView = new Composite(parent, SWT.NONE);
		geoImagePropertiesView = new GeoImagePropertiesView(parent, SWT.NONE);
		rasterPropertiesView = new RasterPropertiesView(parent, SWT.NONE);
		renderedModelPropertiesView = new RenderedModelPropertiesView(parent, SWT.NONE);
		
		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onSourceDataSelectionChanged(InputSourceData selectedData)
			{
				if (selectedData != null && selectedData instanceof ISimpleGeoImage) {
					activeView = geoImagePropertiesView;
				} else if (selectedData != null && selectedData instanceof GenericRasterDataProvider) { 
					activeView = rasterPropertiesView;
				} else {
					activeView = blankView;
				}
				
				stackLayout.topControl = activeView;
				parent.layout();
			}
			
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{
				if (elevationModel != null) {
					activeView = renderedModelPropertiesView;
				} else {
					activeView = blankView;
				}
				
				stackLayout.topControl = activeView;
				parent.layout();
			}
			
		});
		
		stackLayout.topControl = blankView;
	}
	
	@Override
	public void setFocus()
	{
		if (activeView != null) {
			activeView.setFocus();
		}
	}
}
