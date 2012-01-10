package us.wthr.jdem846.render;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class Canvas3d
{
	private static Log log = Logging.getLog(Canvas3d.class);
	
	private static final double Z_VALUE_NOT_SET = Double.NaN;
	
	private int height;
	private int width;
	
	private MatrixBuffer<Integer> pixelBuffer;
	private MatrixBuffer<Double> zBuffer;
	
	public Canvas3d(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		pixelBuffer = new MatrixBuffer<Integer>(width, height);
		zBuffer = new MatrixBuffer<Double>(width, height);
		
		pixelBuffer.fill(0x0);
		zBuffer.fill(Canvas3d.Z_VALUE_NOT_SET);
	}
	
	public void draw(Shape shape, int[] rgb)
	{
		draw(shape, rgbToInt(rgb));
	}
	
	public void draw(Shape shape, int rgb)
	{
		AffineTransform at = new AffineTransform();
		PathIterator path = shape.getPathIterator(at);
		
		double[] prev = new double[2];
		double[] next = new double[2];
		while(!path.isDone()) {
			path.next();
			int moveType = path.currentSegment(next);
			
			// TODO: ...
		}
		
		
	}
	
	public void drawLine(int x0, int y0, double z0, int x1, int y1, double z1, int[] rgb)
	{
		drawLine(x0, y0, z0, x1, y1, z1, rgbToInt(rgb));
	}
	
	public void drawLine(int x0, int y0, double z0, int x1, int y1, double z1, int rgb)
	{
		// TODO: ...
	}
	
	public void fill(Quadrangle3d quad, int[] rgb)
	{
		fill(quad, rgbToInt(rgb));
	}
	
	public void fill(Quadrangle3d quad, int rgb)
	{
		Rectangle2D bounds = quad.getBounds2D();
		
		for (double y = bounds.getMinY(); y <= bounds.getMaxY(); y++) {
			for (double x = bounds.getMinX(); x <= bounds.getMaxX(); x++) {
				//if (quad.contains(x, y)) {
				if (quad.intersects(x, y, 1, 1)) {
					int _x = (int) Math.round(x);
					int _y = (int) Math.round(y);
					
					double xFrac = (x - bounds.getMinX()) / (bounds.getMaxX() - bounds.getMinX());
					double yFrac = (y - bounds.getMinY()) / (bounds.getMaxY() - bounds.getMinY());
					double z = quad.interpolateZ(xFrac, yFrac);
					
					set(_x, _y, z, rgb);
				}
			}
		}
	}
	
	public void fill(Shape shape, int[] rgb)
	{
		fill(shape, rgbToInt(rgb));
	}
	
	public void fill(Shape shape, int rgb)
	{
		Rectangle2D bounds = shape.getBounds2D();
		
		for (double y = bounds.getMinY(); y <= bounds.getMaxY(); y++) {
			for (double x = bounds.getMinX(); x <= bounds.getMaxX(); x++) {
				if (shape.contains(x, y)) {
				//if (shape.intersects(x, y, 1, 1)) {
					int _x = (int) Math.round(x);
					int _y = (int) Math.round(y);
					set(_x, _y, 0.0, rgb);
				}
			}
		}
		
	}
	
	public void set(int x, int y, double z, int[] rgb)
	{
		set(x, y, z, rgbToInt(rgb[0], rgb[1], rgb[2]));
	}
	
	public void set(int x, int y, double z, int r, int g, int b)
	{
		set(x, y, z, rgbToInt(r, g, b));
	}
	
	public void set(int x, int y, double z, int rgb)
	{
		double _z = zBuffer.get(x, y);
		
		if (Double.isNaN(_z) || _z < z) {
			pixelBuffer.set(x, y, rgb);
			zBuffer.set(x, y, z);
		}
		
	}
	
	public int get(int x, int y)
	{
		return pixelBuffer.get(x, y);
	}
	
	public void get(int x, int y, int[] rgb)
	{
		int c = get(x, y);
		intToRGB(c, rgb);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	
	public BufferedImage getImage()
	{
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = image.getRaster();
		
		int[] rgba = new int[4];
		rgba[3] = 0xFF;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				get(x, y, rgba);
				//log.info("RGBA: " + rgba[0] + "/" + rgba[1] + "/" + rgba[2] + "/" + rgba[3]);
				raster.setPixel(x, y, rgba);
			}
		}
		
		return image;
	}
	
	
	public void dispose()
	{
		pixelBuffer.dispose();
		zBuffer.dispose();
	}
	
	protected static int rgbToInt(int[] rgb)
	{
		int r = rgb[0];
		int g = rgb[1];
		int b = rgb[2];
		return rgbToInt(r, g, b);
	}
	
	protected static int rgbToInt(int r, int g, int b)
	{	
		int v = (0xFF << 24) |
				((r & 0xff) << 16) |
				((g & 0xff) << 8) |
				(b & 0xff);
		return v;
	}
	
	protected static void intToRGB(int c, int[] rgb)
	{
		rgb[0] = 0xFF & (c >>> 16);
		rgb[1] = 0xFF & (c >>> 8);
		rgb[2] = 0xFF & c;
		if (rgb.length >= 4) {
			rgb[3] = 0xFF;
		}
	}
	
	
	
}
