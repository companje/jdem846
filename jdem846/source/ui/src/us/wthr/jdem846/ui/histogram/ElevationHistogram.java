package us.wthr.jdem846.ui.histogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.ui.base.Panel;

import org.jfree.chart.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;

@SuppressWarnings("serial")
public class ElevationHistogram extends Panel
{
	
	
	private ElevationHistogramModel elevationHistogramModel;
	private BufferedImage histogram;
	
	public ElevationHistogram()
	{
		this(null);
	}
	
	public ElevationHistogram(ElevationHistogramModel elevationHistogramModel)
	{
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		this.setElevationHistogramModel(elevationHistogramModel);
		
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				repaint();
			}
			
		});
	}
	
	public void setElevationHistogramModel(ElevationHistogramModel elevationHistogramModel)
	{
		this.elevationHistogramModel = elevationHistogramModel;
		createHistogram();
	}
	
	public void createHistogram()
	{
		if (elevationHistogramModel == null)
			return;
		

		int[] ivalue = elevationHistogramModel.getDistribution();
		double[][] dvalue = new double[1][ivalue.length];
		for (int i = 0; i < ivalue.length; i++) {
			dvalue[0][i] = ivalue[i];
		}

		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
	            "", "", dvalue
	    );
		
		String plotTitle = null;
		String xaxis = null;//"Elevation (m)";
		String yaxis = null;//"Freq.";
		
		PlotOrientation orientation = PlotOrientation.VERTICAL; 
		boolean legend = false; 
		boolean toolTips = true;
		boolean urls = false; 
		JFreeChart chart = ChartFactory.createAreaChart(
				plotTitle,             	// chart title
				xaxis,               	// domain axis label
				yaxis,                  // range axis label
	            dataset,                // data
	            orientation, 			// orientation
	            legend,                 // include legend
	            toolTips,               // tooltips
	            urls                    // urls
	        );
		
		CategoryPlot plot = chart.getCategoryPlot();
		plot.getDomainAxis().setTickMarksVisible(false);
		plot.getDomainAxis().setMinorTickMarksVisible(false);
		plot.getDomainAxis().setTickLabelsVisible(false);
		plot.getDomainAxis().setMinorTickMarksVisible(false);
		plot.getRangeAxis().setMinorTickMarksVisible(false);
		plot.getRangeAxis().setTickLabelsVisible(false);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		add(chartPanel, BorderLayout.CENTER);
		

	}
	

	
}
