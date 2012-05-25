package us.wthr.jdem846.ui.histogram;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);
		
		if (histogramModel == null)
			return;
		
		
		double max = histogramModel.getMax(channels);
		
		Color color0 = new Color(255, 0, 0, 85);
		Color color1 = new Color(0, 255, 0, 85);
		Color color2 = new Color(0, 0, 255, 85);
		Color color3 = new Color(255, 255, 0, 85);
		
		for (int i = 0; i <= 255; i++) {
			double c0 = histogramModel.getChannel0().distribution[i];
			double c1 = histogramModel.getChannel1().distribution[i];
			double c2 = histogramModel.getChannel2().distribution[i];
			double c3 = histogramModel.getChannel3().distribution[i];
			
			int h0 = (int) MathExt.round((c0 / max) * (double)height);
			int h1 = (int) MathExt.round((c1 / max) * (double)height);
			int h2 = (int) MathExt.round((c2 / max) * (double)height);
			int h3 = (int) MathExt.round((c3 / max) * (double)height);
			
			if ((channels & Channels.CHANNEL_1) == Channels.CHANNEL_1) {
				g2d.setColor(color0);
				g2d.drawLine(i, height - h0, i, height);
			}
			
			if ((channels & Channels.CHANNEL_2) == Channels.CHANNEL_2) {
				g2d.setColor(color1);
				g2d.drawLine(i, height - h1, i, height);
			}
			
			if ((channels & Channels.CHANNEL_3) == Channels.CHANNEL_3) {
				g2d.setColor(color2);
				g2d.drawLine(i, height - h2, i, height);
			}
			
			if ((channels & Channels.CHANNEL_4) == Channels.CHANNEL_4) {
				g2d.setColor(color3);
				g2d.drawLine(i, height - h3, i, height);
			}
		}
		
		
	}
	
	
	public void paint(Graphics g)
	{
		if (histogram != null) {
			g.drawImage(histogram, 0, 0, getWidth(), getHeight(), null);
		}
	}
	
}
