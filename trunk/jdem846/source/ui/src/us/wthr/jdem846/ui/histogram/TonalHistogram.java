package us.wthr.jdem846.ui.histogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.ui.base.Panel;

import org.jfree.chart.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.AreaRenderer;

@SuppressWarnings("serial")
public class TonalHistogram extends Panel
{
	private int channels;
	private TonalHistogramModel histogramModel;
	private BufferedImage histogram;
	
	public TonalHistogram()
	{
		this(null, Channels.CHANNEL_1 | Channels.CHANNEL_2 | Channels.CHANNEL_3);
	}
	
	public TonalHistogram(TonalHistogramModel histogramModel, int channels)
	{
		this.channels = channels;
		this.setHistogramModel(histogramModel);
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				//createHistogram();
				repaint();
			}
			
		});
	}
	
	
	public void setHistogramModel(TonalHistogramModel histogramModel)
	{
		this.histogramModel = histogramModel;
		createHistogram();
	}
	
	public void createHistogram()
	{
		
		if (histogramModel == null) {
			return;
		}
		
		
		Color[] colors = {new Color(255, 0, 0, 85),
						new Color(0, 255, 0, 85),
						new Color(0, 0, 255, 85)
		};

		class CustomRenderer extends AreaRenderer {
			private Paint[] colors;
			
			public CustomRenderer() 
			{
				colors = new Paint[] {new Color(255, 0, 0, 85),
						new Color(0, 255, 0, 85),
						new Color(0, 0, 255, 85)
				};
			}
			
			public Paint getItemPaint(final int row, final int column) 
			{ 
				return (this.colors[row % this.colors.length]); 
			} 
		};
		
		
		double[][] data = new double[3][256];
		for (int i = 0; i <= 255; i++) {
			int c0 = histogramModel.getChannel0().distribution[i];
			int c1 = histogramModel.getChannel1().distribution[i];
			int c2 = histogramModel.getChannel2().distribution[i];
			
			data[0][i] = c0;
			data[1][i] = c1;
			data[2][i] = c2;
			
		}
		
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
	            "", "", data
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
		
		CustomRenderer renderer = new CustomRenderer();
		plot.setRenderer(renderer);
        //plot.setForegroundAlpha(0.33f);
		
		ChartPanel chartPanel = new ChartPanel(chart);
		add(chartPanel, BorderLayout.CENTER);
		
		/*
		if (histogramModel != null && getWidth() > 0 && getHeight() > 0) {
			histogram = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			paintHistogram(histogram.getGraphics(), getWidth(), getHeight());
		}
		*/
	}
	
	public void paintHistogram(Graphics g, int width, int height)
	{
		
		/*
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color background = new Color(0, 0, 0, 0);
		
		g2d.setColor(background);
		g2d.fillRect(0, 0, width, height);
		
		if (histogramModel == null)
			return;
		
		
		double max = histogramModel.getMax(channels);
		
		Color color0 = new Color(255, 0, 0, 85);
		Color color1 = new Color(0, 255, 0, 85);
		Color color2 = new Color(0, 0, 255, 85);
		Color color3 = new Color(255, 255, 0, 85);
		
		Polygon polygon0 = new Polygon();
		Polygon polygon1 = new Polygon();
		Polygon polygon2 = new Polygon();
		Polygon polygon3 = new Polygon();
		
		polygon0.addPoint(0, height);
		polygon1.addPoint(0, height);
		polygon2.addPoint(0, height);
		polygon3.addPoint(0, height);
		
		int x = 0;
		
		for (int i = 0; i <= 255; i++) {
			double c0 = histogramModel.getChannel0().distribution[i];
			double c1 = histogramModel.getChannel1().distribution[i];
			double c2 = histogramModel.getChannel2().distribution[i];
			double c3 = histogramModel.getChannel3().distribution[i];
			
			int h0 = (int) MathExt.round((c0 / max) * (double)height);
			int h1 = (int) MathExt.round((c1 / max) * (double)height);
			int h2 = (int) MathExt.round((c2 / max) * (double)height);
			int h3 = (int) MathExt.round((c3 / max) * (double)height);
			
			
			x = (int) MathExt.round(((double)i / 255.0) * (double) width);
			
			polygon0.addPoint(x, height - h0);
			polygon1.addPoint(x, height - h1);
			polygon2.addPoint(x, height - h2);
			polygon3.addPoint(x, height - h3);

		}
		
		polygon0.addPoint(width, height);
		polygon1.addPoint(width, height);
		polygon2.addPoint(width, height);
		polygon3.addPoint(width, height);
		
		
		if ((channels & Channels.CHANNEL_1) == Channels.CHANNEL_1) {
			g2d.setColor(color0);
			g2d.fill(polygon0);
		}
		
		if ((channels & Channels.CHANNEL_2) == Channels.CHANNEL_2) {
			g2d.setColor(color1);
			g2d.fill(polygon1);
		}
		
		if ((channels & Channels.CHANNEL_3) == Channels.CHANNEL_3) {
			g2d.setColor(color2);
			g2d.fill(polygon2);
		}
		
		if ((channels & Channels.CHANNEL_4) == Channels.CHANNEL_4) {
			g2d.setColor(color3);
			g2d.fill(polygon3);
		}
		*/
	}
	
	/*
	public void paint(Graphics g)
	{
		if (histogramModel != null && histogram != null && histogram.getWidth() != getWidth() || histogram.getHeight() != getHeight()) {
			createHistogram();
		}
		
		if (histogram != null) {
			
			
			
			Insets insets = this.getInsets();
			int width = getWidth() - insets.left - insets.right;
			int height = getHeight() - insets.top - insets.bottom;
			g.drawImage(histogram, insets.left, insets.top, width, height, null);
		}
		
		super.paint(g);
	}
	*/
	
}
