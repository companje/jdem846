package us.wthr.jdem846ui.views.models;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.editors.renderedmodel.ElevationHistogram;
import us.wthr.jdem846ui.editors.renderedmodel.TonalHistogram;
import us.wthr.jdem846ui.observers.RenderedModelSelectionObserver;
import us.wthr.jdem846ui.observers.RenderedModelSelectionObserver.RenderedModelSelectionListener;

public class ModelStatisticsView extends ViewPart
{
	private static Log log = Logging.getLog(ModelStatisticsView.class);
	
	public static final String ID = "jdem846ui.modelStatisticsView";
	
	private ElevationModel elevationModel;
	
	private ElevationHistogram elevationHistogramDisplay;
	private TonalHistogram tonalHistogramDisplay;
	
	private Composite parent;
	
	@Override
	public void createPartControl(final Composite parent) 
	{
		this.parent = parent;
		/*
		parent.setLayout(new FillLayout());
		Composite chartContainer = new Composite(parent, SWT.SINGLE);
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		rowLayout.pack = false;
		rowLayout.justify = true;
		rowLayout.marginLeft = 5;
		rowLayout.marginTop = 5;
		rowLayout.marginRight = 5;
		rowLayout.marginBottom = 5;
		rowLayout.spacing = 10;
		chartContainer.setLayout(rowLayout);
		*/
		
		elevationHistogramDisplay = new ElevationHistogram(parent, SWT.NONE);
		tonalHistogramDisplay = new TonalHistogram(parent, SWT.NONE);
		
		
		RenderedModelSelectionObserver.getInstance().addRenderedModelSelectionListener(new RenderedModelSelectionListener() {
			@Override
			public void onRenderedModelSelected(ElevationModel elevationModel)
			{
				setElevationModel(elevationModel);
			}
		});
		
		
	}
	
	
	protected void setElevationModel(ElevationModel elevationModel)
	{
		this.elevationModel = elevationModel;
		elevationHistogramDisplay.setElevationHistogramModel(elevationModel.getElevationHistogramModel());
		tonalHistogramDisplay.setElevationModel(elevationModel);
		
		parent.layout();
	}
	
	@Override
	public void setFocus() 
	{
		
	}
	
	
}
