package us.wthr.jdem846ui.editors.renderedmodel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

import us.wthr.jdem846.ElevationModel;

public class TonalHistogram extends Composite
{
	private int channels;
	private TonalHistogramModel histogramModel;
	
	private Chart chart = null;
	
	public TonalHistogram(Composite parent, int style)
	{
		this(parent, style, Channels.CHANNEL_1 | Channels.CHANNEL_2 | Channels.CHANNEL_3);
	}

	
	public TonalHistogram(Composite parent, int style, int channels)
	{
		super(parent, style);
		
		this.channels = channels;
		
		this.setLayout(new FillLayout());
	}
	
	
	protected void createHistogram()
	{
		if (chart != null) {
			chart.dispose();
			chart = null;
		}
		
		if (histogramModel == null) {
			return;
		}
		
		double[][] data = new double[3][255];
		
		// Eliminate the two extremes
		for (int i = 1; i < 255; i++) {
			int c0 = histogramModel.getChannel0().distribution[i];
			int c1 = histogramModel.getChannel1().distribution[i];
			int c2 = histogramModel.getChannel2().distribution[i];
			
			data[0][i] = c0;
			data[1][i] = c1;
			data[2][i] = c2;
			
		}
		
		
		chart = new Chart(this, SWT.NONE);
		chart.getTitle().setText("Tonal Histogram");
		
		chart.getAxisSet().getYAxis(0).getTitle().setText("");
		chart.getAxisSet().getXAxis(0).getTitle().setText("");
		
		createLineSeries(chart, data[0], new Color(getDisplay(), 255, 0, 0), "Red");
		createLineSeries(chart, data[1], new Color(getDisplay(), 0, 255, 0), "Green");
		createLineSeries(chart, data[2], new Color(getDisplay(), 0, 0, 255), "Blue");
		
		chart.getAxisSet().adjustRange();
		chart.getLegend().setVisible(false);
	}
	
	
	protected void createLineSeries(Chart chart, double[] seriesData, Color color, String title)
	{
		ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet().createSeries(SeriesType.LINE, title);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineWidth(1);
		lineSeries.setAntialias(SWT.ON);
		lineSeries.setLineColor(color);
		lineSeries.enableArea(true);
		lineSeries.setYSeries(seriesData);
	}
	
	
	public void setElevationModel(ElevationModel elevationModel)
	{
		setHistogramModel(DistributionGenerator.generateHistogramModelFromImage(elevationModel));
	}
	
	public void setHistogramModel(TonalHistogramModel histogramModel)
	{
		this.histogramModel = histogramModel;
		createHistogram();
	}
	
}
