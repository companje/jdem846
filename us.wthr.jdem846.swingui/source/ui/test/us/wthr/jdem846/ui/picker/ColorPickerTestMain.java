package us.wthr.jdem846.ui.picker;

import javax.swing.JFrame;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ColorPickerTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ColorPickerTestMain.class);
		
		try {
			ColorPickerTestMain testMain = new ColorPickerTestMain();
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
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setContentPane(new ColorPickerControl());
		frame.setVisible(true);
		
		
		
	}
}
