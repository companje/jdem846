package us.wthr.jdem846.ui.histogram;

import javax.swing.JFrame;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class HistogramTestMain extends AbstractTestMain
{
private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(HistogramTestMain.class);
		
		try {
			HistogramTestMain testMain = new HistogramTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	
	public void doTesting() throws Exception
	{
		JFrame frame = new JFrame();
		frame.setTitle("Color Picker");
		frame.setLocationRelativeTo(null);
		frame.setSize(256, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/*
		BufferedImage spectrumImage = (BufferedImage) ImageIcons.loadImage("resources://jdem846-splash.png");
		
		TonalHistogramModel histogramModel = DistributionGenerator.generateHistogramModelFromImage(spectrumImage);
		
		for (int i = 0; i < 256; i++) {
			
			int c0 = histogramModel.getChannel0().distribution[i];
			int c1 = histogramModel.getChannel1().distribution[i];
			int c2 = histogramModel.getChannel2().distribution[i];
			int c3 = histogramModel.getChannel3().distribution[i];

			System.out.printf("%4d: %3d %3d %3d %3d\n", i , c0, c1, c2, c3);
			
		}
		
		int max = histogramModel.getMax(Channels.CHANNEL_1 | Channels.CHANNEL_2 | Channels.CHANNEL_3);
		System.out.println("Max: " + max);
		
		
		TonalHistogram histogram = new TonalHistogram(histogramModel, Channels.CHANNEL_1 | Channels.CHANNEL_2 | Channels.CHANNEL_3);
		frame.setContentPane(histogram);
		frame.setVisible(true);
		
		*/
		
	}
}
