package us.wthr.jdem846.ui.histogram;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class Histogram extends Panel
{
	private int channels;
	private HistogramModel histogramModel;
	private BufferedImage histogram;
	
	public Histogram()
	{
		this(null, Channels.CHANNEL_1 | Channels.CHANNEL_2 | Channels.CHANNEL_3);
	}
	
	public Histogram(HistogramModel histogramModel, int channels)
	{
		this.channels = channels;
		this.setHistogramModel(histogramModel);
		this.setOpaque(false);
	}
	
	
	public void setHistogramModel(HistogramModel histogramModel)
	{
		this.histogramModel = histogramModel;
		createHistogram();
	}
	
	public void createHistogram()
	{
		histogram = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		paintHistogram(histogram.getGraphics(), 256, 256);
	}
	
	public void paintHistogram(Graphics g, int width, int height)
	{
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
		
		
		for (int i = 0; i <= 255; i++) {
			double c0 = histogramModel.getChannel0().distribution[i];
			double c1 = histogramModel.getChannel1().distribution[i];
			double c2 = histogramModel.getChannel2().distribution[i];
			double c3 = histogramModel.getChannel3().distribution[i];
			
			int h0 = (int) MathExt.round((c0 / max) * (double)height);
			int h1 = (int) MathExt.round((c1 / max) * (double)height);
			int h2 = (int) MathExt.round((c2 / max) * (double)height);
			int h3 = (int) MathExt.round((c3 / max) * (double)height);
			
			
			polygon0.addPoint(i, height - h0);
			polygon1.addPoint(i, height - h1);
			polygon2.addPoint(i, height - h2);
			polygon3.addPoint(i, height - h3);

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
		
	}
	
	
	public void paint(Graphics g)
	{
		if (histogram != null) {
			
			Insets insets = this.getInsets();
			int width = getWidth() - insets.left - insets.right;
			int height = getHeight() - insets.top - insets.bottom;
			g.drawImage(histogram, insets.left, insets.top, width, height, null);
		}
		
		super.paint(g);
	}
	
}
