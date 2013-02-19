package us.wthr.jdem846ui.editors.renderedmodel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

import us.wthr.jdem846.model.ElevationHistogramModel;

public class ElevationHistogram extends Composite
{
	
	private ElevationHistogramModel elevationHistogramModel = null;
	private Chart chart = null;
	
	public ElevationHistogram(Composite parent, int style)
	{
		super(parent, style);
		
		this.setLayout(new FillLayout());
	}
	
	
	
	protected void createHistogram()
	{
		if (chart != null) {
			chart.dispose();
			chart = null;
		}
		
		if (elevationHistogramModel == null) {
			
			return;
		}
		
		// See http://www.swtchart.org/doc/index.html
		chart = new Chart(this, SWT.NONE);
		chart.getTitle().setText("Elevation Histogram");
		
		chart.getAxisSet().getYAxis(0).getTitle().setText("");
		chart.getAxisSet().getXAxis(0).getTitle().setText("");
		
		int[] distribution = elevationHistogramModel.getDistribution();
		double[] ySeries = new double[distribution.length];
		for (int i = 0; i < distribution.length; i++) {
			ySeries[i] = distribution[i];
		}
		// create line series
		ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE, "Frequency");
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineWidth(2);
		lineSeries.setAntialias(SWT.ON);
		lineSeries.setYSeries(ySeries);

		// adjust the axis range
		chart.getAxisSet().adjustRange();
		chart.getLegend().setVisible(false);
		
	}



	public ElevationHistogramModel getElevationHistogramModel()
	{
		return elevationHistogramModel;
	}



	public void setElevationHistogramModel(ElevationHistogramModel elevationHistogramModel)
	{
		this.elevationHistogramModel = elevationHistogramModel;
		createHistogram();
	}
	
	
	
	
}	
