package us.wthr.jdem846.canvas;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class PixelMatrixTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(PixelMatrixTestMain.class);
		
		try {
			PixelMatrixTestMain testMain = new PixelMatrixTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	
	
	public void doTesting() throws Exception
	{
		PixelMatrix pixelMatrix = new PixelMatrix(100, 100, 1, 1);
		//pixelMatrix.setRenderingHint(CanvasRenderingHints.KEY_PIXEL_DEPTH, CanvasRenderingHints.VALUE_DEPTH_LIMITED);
		int[] rgbaStack = null;
		
		int i = 10;
		
		pixelMatrix.set(i, i, 6, 0x8);
		pixelMatrix.set(i, i, 6, 0x6);
		pixelMatrix.set(i, i, 4, 0x4);
		pixelMatrix.set(i, i, 2, 0x2);
		pixelMatrix.set(i, i, 0, 0x0);

		
		rgbaStack = pixelMatrix.getRgbaStack(i, i);
		for (int rgba : rgbaStack) {
			log.info("RGBA " + i + ": " + rgba);
		}
		
		
		log.info("------------------------------------------");
		i = 20;
		
		pixelMatrix.set(i, i, 0, 0x0);
		pixelMatrix.set(i, i, 2, 0x2);
		pixelMatrix.set(i, i, 4, 0x4);
		pixelMatrix.set(i, i, 6, 0x6);
		pixelMatrix.set(i, i, 8, 0x8);
		rgbaStack = pixelMatrix.getRgbaStack(i, i);
		for (int rgba : rgbaStack) {
			log.info("RGBA " + i + ": " + rgba);
		}
		
		log.info("------------------------------------------");
		i = 40;
		
		pixelMatrix.set(i, i, 8, 0x8);
		pixelMatrix.set(i, i, 2, 0x2);
		pixelMatrix.set(i, i, 0, 0x0);
		pixelMatrix.set(i, i, 6, 0x6);
		pixelMatrix.set(i, i, 4, 0x4);
		rgbaStack = pixelMatrix.getRgbaStack(i, i);
		for (int rgba : rgbaStack) {
			log.info("RGBA " + i + ": " + rgba);
		}
		
	}
}
