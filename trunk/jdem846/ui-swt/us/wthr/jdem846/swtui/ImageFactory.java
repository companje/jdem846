package us.wthr.jdem846.swtui;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class ImageFactory
{
	private static ImageLoader loader = new ImageLoader();
	
	public static Image loadImageResource(String filename)
	{
		InputStream in = ImageFactory.class.getResourceAsStream(filename);
		ImageData[] data = loader.load(in);
		Image image = new Image(JDemShell.getDisplayInstance(), data[0]);
		return image;
	}
	
	public static Image loadImage(String filename)
	{
		ImageData[] data = loader.load(filename);
		Image image = new Image(JDemShell.getDisplayInstance(), data[0]);
		return image;
	}
	
	
}
