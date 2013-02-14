package us.wthr.jdem846ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import us.wthr.jdem846.math.MathExt;

public class SwtImageUtil
{
	public static Image getScaledImage(Image image, double scalePercent)
	{
		int width = (int) MathExt.round((double) image.getBounds().width * scalePercent);
		int height = (int) MathExt.round((double) image.getBounds().height * scalePercent);
		return getScaledImage(image, width, height);
	}

	// http://aniszczyk.org/2007/08/09/resizing-images-using-swt/
	public static Image getScaledImage(Image image, int width, int height)
	{
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		return scaled;
	}
}
