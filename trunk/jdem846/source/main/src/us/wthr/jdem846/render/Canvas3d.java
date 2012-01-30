package us.wthr.jdem846.render;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.geom.Bounds;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Geometric;
import us.wthr.jdem846.geom.Polygon;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.render.gfx.NumberUtil;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.render2d.ScanlinePath;
import us.wthr.jdem846.shapefile.ShapePath;

public class Canvas3d
{
	private static Log log = Logging.getLog(Canvas3d.class);
	
	private static final double Z_VALUE_NOT_SET = Double.NaN;
	
	private int height;
	private int width;
	
	private MatrixBuffer<Integer> pixelBuffer;
	private MatrixBuffer<Double> zBuffer;
	
	private RenderPipeline pipeline;
	
	public Canvas3d(int width, int height)
	{
		this(width, height, null);
	}
	
	public Canvas3d(int width, int height, RenderPipeline pipeline)
	{
		this.pipeline = pipeline;
		this.width = width;
		this.height = height;
		
		pixelBuffer = new MatrixBuffer<Integer>(width, height);
		zBuffer = new MatrixBuffer<Double>(width, height);
		
		pixelBuffer.fill(0x0);
		zBuffer.fill(Canvas3d.Z_VALUE_NOT_SET);

	}
	
	public void draw(Geometric shape, int[] rgba)
	{
		draw(shape, rgbaToInt(rgba));
	}
	
	public void draw(Geometric shape, int rgba)
	{
		
		Edge[] edgeList = shape.getEdges();
		
		for (Edge edge : edgeList) {
			draw(edge, rgba);
		}
		
	}
	
	
	public void drawLine(int x0, int y0, double z0, int x1, int y1, double z1, int[] rgba)
	{
		drawLine(x0, y0, z0, x1, y1, z1, rgbaToInt(rgba));
	}
	
	public void drawLine(int x0, int y0, double z0, int x1, int y1, double z1, int rgba)
	{
		Edge edge = new Edge(x0, y0, z0, x1, y1, z1);
		draw(edge, rgba);
	}
	
	public void draw(Edge edge, int rgba)
	{

		double x0 = edge.p0.x;
		double y0 = edge.p0.y;
		
		double x1 = edge.p1.x;
		double y1 = edge.p1.y;
		
		if (edge.p0.compareTo(edge.p1) < 0) {
			x0 = edge.p1.x;
			y0 = edge.p1.y;
			
			x1 = edge.p0.x;
			y1 = edge.p0.y;
			
		} else {
			x0 = edge.p0.x;
			y0 = edge.p0.y;
			
			x1 = edge.p1.x;
			y1 = edge.p1.y;
		}
		

		int xMn = (int)MathExt.min(x0, x1);
		int xMx = (int)MathExt.max(x0, x1);
		
		int yMn = (int)MathExt.min(y0, y1);
		int yMx = (int)MathExt.max(y0, y1);

		int mxX = (int) x1;
		int mxY = (int) y1;

		int mnX = (int) x0;
		int mnY = (int) y0;
		
		double s = edge.m;//(double)(mnY - mxY) / (double)(mnX - mxX);
		boolean isValidSlope = NumberUtil.isValidNumber(s);
		
		if (Math.abs(x1 - x0) >= Math.abs(y1 - y0)) {
			// Long
			for (int x = xMn; x <= xMx; x++) {
				int y = (int) ((isValidSlope) ? ((s * (x - mxX)) + mxY) + 1 : mxY);
				double f = (double)(x - xMn) / (double)(xMx - xMn);
				double z = edge.getInterpolatedZ(f);
				set(x, y, z, rgba);
			}
			
		} else {
			// Tall
			for (int y = yMn; y <= yMx; y++) {
				int x =  (int) ((isValidSlope) ? (((y - mxY) / s) + mxX) : mxX);
				double f = ((yMx - yMn) != 0) ? (y - yMn) / (yMx - yMn) : 0;
				double z = edge.getInterpolatedZ(f);
				set(x, y+1, z, rgba);
			}
			
		}
	}
	
	
	public void draw(Shape shape, int[] rgba)
	{
		draw(shape, rgbaToInt(rgba));
	}
	
	public void draw(Shape shape, int rgba)
	{
		Edge[] edgeArray = getEdges(shape, true);
		
		Polygon poly = new Polygon();
		for (Edge edge : edgeArray) {
			poly.addEdge(edge);
		}
		
		draw(poly, rgba);
	}
	
	
	
	public void fill(Quadrangle3d quad, int[] rgba)
	{
		fill(quad, rgbaToInt(rgba));

	}
	
	public void fill(Quadrangle3d quad, int rgba)
	{
		
		double[] point = new double[3];

		Polygon poly = new Polygon();
		
		quad.get(0, point);
		double x0 = point[0];
		double y0 = point[1];
		double z0 = point[2];
		
		quad.get(1, point);
		double x1 = point[0];
		double y1 = point[1];
		double z1 = point[2];
		
		quad.get(2, point);
		double x2 = point[0];
		double y2 = point[1];
		double z2 = point[2];
		
		quad.get(3, point);
		double x3 = point[0];
		double y3 = point[1];
		double z3 = point[2];

		poly.addEdge((int)x0, (int)y0, (int)z0, (int)x1, (int)y1, (int)z1);
		poly.addEdge((int)x1, (int)y1, (int)z1, (int)x2, (int)y2, (int)z2);
		poly.addEdge((int)x2, (int)y2, (int)z2, (int)x3, (int)y3, (int)z3);

		fill(poly, rgba);
	
	}
	
	public void fill(Geometric shape, int[] rgba)
	{
		fill(shape,  rgbaToInt(rgba));
	}
	
	public void fill(Shape shape, int[] rgba)
	{
		fill(shape, rgbaToInt(rgba));
		
	}
	
	
	
	public void fill(Geometric shape, int rgba)
	{
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		Edge[] edgeArray = shape.getEdges(true);
		
		for (int i = 0; i < edgeArray.length; i++) {
			if (maxY < (int)edgeArray[i].p1.y)
				maxY = (int)edgeArray[i].p1.y;
		}
		minY = (int)edgeArray[0].p0.y;
			
			
		if (minY >= getHeight() || maxY < 0 || minY == maxY)
			return;
			
		if (minY < 0)
			minY = 0;
		if (maxY >= getHeight())
			maxY = getHeight() - 1;
		
		if (maxY == minY) {
			maxY = minY + 1;
		}
		
		List<Integer> xList = new ArrayList<Integer>();
		List<Integer> zList = new ArrayList<Integer>();
		
		for (int y = minY; y <= maxY; y++) {
			xList.clear();
			zList.clear();
			
			for (int i = 0; i < edgeArray.length; i++) {
				

				int curX = (int) edgeArray[i].curX;
				int curZ = (int) edgeArray[i].curZ;
				
				if (y == edgeArray[i].p0.y) 
                {
                    if (y == edgeArray[i].p1.y)
                    {
                        // the current edge is horizontal, so we add both vertices
                    	edgeArray[i].deactivate();
                    	xList.add(curX);
                    	zList.add(curZ);
                    } else {
                    	edgeArray[i].activate();
                        // we don't insert it in the list cause this vertice is also
                        // the (bigger) vertice of another edge and already handled
                    }
                }
                
                // here the scanline intersects the bigger vertice
                if (y == edgeArray[i].p1.y) {
                	edgeArray[i].deactivate();
                	xList.add(curX);
                	zList.add(curZ);
                }
                
                // here the scanline intersects the edge, so calc intersection point
                if (y > edgeArray[i].p0.y && y < edgeArray[i].p1.y) {
                	edgeArray[i].update();
                	xList.add(curX);
                	zList.add(curZ);
                }
			}
			
			if (xList.size() < 2 || xList.size() % 2 != 0) 
            {
               //log.warn("This should never happen! (list size: " + list.size() + ", Edge Count: " + edgeArray.length + ")");
                continue;
            } 
			
			int xSwap;
			int zSwap;
            for (int i = 0; i < xList.size(); i++) {
                for (int j = 0; j < xList.size() - 1; j++) {
                    if (xList.get(j) > xList.get(j+1)) {
                    	xSwap = xList.get(j);
                        xList.set(j, xList.get(j+1));
                        xList.set(j+1, xSwap);
                        
                        zSwap = zList.get(j);
                        zList.set(j, zList.get(j+1));
                        zList.set(j+1, zSwap);
                    }
                
                }
            }
            
            
             
            // so draw all line segments on current scanline
            for (int i = 0; i < xList.size(); i+=2)
            {
            	if (i+1 < xList.size()) {
            		int leftX = xList.get(i);
            		int rightX = xList.get(i+1);
            		int leftZ = zList.get(i);
            		int rightZ = zList.get(i+1);
            		//_fillScanLine(leftX, rightX, y, leftZ, rightZ, rgba);
            		Edge edge = new Edge(leftX, y-1, leftZ, rightX, y-1, rightZ);
            		draw(edge, rgba);

            	}
            }
		
		}
	}
	
	public void fill(Shape shape, int rgba)
	{
		
		
		Edge[] edgeArray = getEdges(shape, true);
		
		Polygon poly = new Polygon();
		for (Edge edge : edgeArray) {
			poly.addEdge(edge);
		}
		
		fill(poly, rgba);
		
		/*
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (int i = 0; i < edgeArray.length; i++) {
			if (maxY < edgeArray[i].p1.y)
				maxY = (int)edgeArray[i].p1.y;
		}
		minY = (int)edgeArray[0].p0.y;
			
			
		if (minY >= getHeight() || maxY < 0 || minY == maxY)
			return;
			
		if (minY < 0)
			minY = 0;
		if (maxY >= getHeight())
			maxY = getHeight() - 1;
		
		List<Integer> list = new ArrayList<Integer>();
		for (int y = minY; y <= maxY; y++) {
			list.clear();
			
			for (int i = 0; i < edgeArray.length; i++) {
				
				//int curX = (int) ((1+(y - minY)) * (1.0 / edgeArray[i].m));
				int curX = (int) edgeArray[i].curX;
				if (y == edgeArray[i].p0.y) 
                {
                    if (y == edgeArray[i].p1.y)
                    {
                        // the current edge is horizontal, so we add both vertices
                    	edgeArray[i].deactivate();
                        list.add(curX);
                    } else {
                    	edgeArray[i].activate();
                        // we don't insert it in the list cause this vertice is also
                        // the (bigger) vertice of another edge and already handled
                    }
                }
                
                // here the scanline intersects the bigger vertice
                if (y == edgeArray[i].p1.y) {
                	edgeArray[i].deactivate();
                    list.add(curX);
                }
                
                // here the scanline intersects the edge, so calc intersection point
                if (y > edgeArray[i].p0.y && y < edgeArray[i].p1.y) {
                	edgeArray[i].update();
                    list.add(curX);
                }
			}
			
			if (list.size() < 2 || list.size() % 2 != 0) 
            {
               //log.warn("This should never happen! (list size: " + list.size() + ", Edge Count: " + edgeArray.length + ")");
                continue;
            } 
			
			int swaptmp;
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.size() - 1; j++) {
                    if (list.get(j) > list.get(j+1)) {
                        swaptmp = list.get(j);
                        list.set(j, list.get(j+1));
                        list.set(j+1, swaptmp);
                    }
                
                }
            }
            
            
             
            // so draw all line segments on current scanline
            for (int i = 0; i < list.size(); i+=2)
            {
            	if (i+1 < list.size()) {
            		int leftX = list.get(i);
            		int rightX = list.get(i+1);
            		//log.info("Left/Right X: " + leftX + "/" + rightX);
            		
            		_fillScanLine(leftX, rightX, y, 0.0, rgba);
            	}
               // g.drawLine(list.get(i), scanline, list.get(i+1), scanline);
            }
		
		}
		*/
		
		//log.info("Completed polygon");
		//log.info("Getting bounds 3D");
		/*
		
		Rectangle2D bounds = shape.getBounds2D();
		
		//int minX = (int) Math.round(bounds.getMinX());
		//int maxX = (int) Math.round(bounds.getMaxX());
		//int minY = (int) Math.round(bounds.getMinY());
		//int maxY = (int) Math.round(bounds.getMaxY());
		
		double minX = Math.round(bounds.getMinX());
		double maxX = Math.round(bounds.getMaxX());
		double minY = Math.round(bounds.getMinY());
		double maxY = Math.round(bounds.getMaxY());
		
		if (maxX < 0 || minX >= getWidth() || maxY < 0 || minY >= getHeight())
			return;
		
		if (minX < 0)
			minX = 0;
		if (maxX >= getWidth())
			maxX = getWidth() - 1;
		if (minY < 0)
			minY = 0;
		if (minY >= getHeight())
			minY = getHeight() - 1;
		
		if (maxX <= minX)
			maxX = minX + 1;
		if (maxY <= minY)
			maxY = minY + 1;
		
		
		boolean inside = false;
		double leftX = 0;
		log.info("Filling polygon min x/y: " + minX + "/" + minY + ", max x/y: " + maxX + "/" + maxY);
		for (double y = minY; y <= maxY; y++) {
			

			for (double x = minX; x <= maxX; x++) {

				
				//if (shape.contains(x, y) && !inside) {
				if (shape.intersects(x, y, 1, 1) && !inside) {
					leftX = x;
					inside = true;
				} else if (inside) {
					fillScanLine(leftX, x, y, 10.0, rgba);
					inside = false;
				}
			}
		}
		log.info("Completed polygon");
		*/
	}
	
	
	public void fillScanLine(double leftX, double rightX, double y, double z, int[] rgba)
	{
		_fillScanLine(leftX, rightX, y, z,  rgbaToInt(rgba));
	}
	
	public void fillScanLine(double leftX, double rightX, double y, double z, int rgba)
	{
		if (pipeline == null) {
			_fillScanLine(leftX, rightX, y, z, rgba);
		} else {
			pipeline.submit(new ScanlinePath(leftX, rightX, y, z, rgba));
		}
	}
	
	public void fillScanLine(int leftX, int rightX, int y, double z, int[] rgba)
	{
		fillScanLine(leftX, rightX, y, z, rgbaToInt(rgba));
	}
	
	public void fillScanLine(int leftX, int rightX, int y, double z, int rgba)
	{
		if (pipeline == null) {
			_fillScanLine(leftX, rightX, y, z, rgba);
		} else {
			pipeline.submit(new ScanlinePath(leftX, rightX, y, z, rgba));
		}
	}
	
	public void fillScanLine(ScanlinePath scanline)
	{
		_fillScanLine(scanline.getLeftX(),
				scanline.getRightX(),
				scanline.getY(),
				scanline.getZ(),
				scanline.getRgba());
	}
	
	
	protected void _fillScanLine(int leftX, int rightX, int y, double z, int rgba)
	{

		for (int x = leftX; x <= rightX; x++) {
			set(x, y, z, rgba);
			
		}
	}
	
	protected void _fillScanLine(double leftX, double rightX, double y, double z, int rgba)
	{
		_fillScanLine(leftX, rightX, y, z, z, rgba);
	}
	
	protected void _fillScanLine(double leftX, double rightX, double y, double leftZ, double rightZ, int rgba)
	{

		for (double x = leftX; x <= rightX; x++) {
			double frac = (x - leftX) / (rightX - leftX);
			double z = interpolate(leftZ, rightZ, frac);
			set((int)x, (int)y, z, rgba);
			
		}
	}
	
	public void set(int x, int y, double z, int[] rgb)
	{
		set(x, y, z, rgbaToInt(rgb));
	}
	
	public void set(int x, int y, double z, int r, int g, int b)
	{
		set(x, y, z, rgbaToInt(r, g, b));
	}
	
	public void set(int x, int y, double z, int rgb)
	{
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
			return;
		}
		
		double _z = zBuffer.get(x, y);
		
		if (Double.isNaN(_z) || _z < z) {
			pixelBuffer.set(x, y, rgb);
			zBuffer.set(x, y, z);
		}
		
	}
	
	public void set(double x, double y, double z, int rgba)
	{
		int[] _rgba = new int[4];
		intToRGBA(rgba, _rgba);
		set(x, y, z, _rgba);
	}
	
	public void set(double x, double y, double z, int[] rgba)
	{
		
		int _x = (int) x;
		int _y = (int) y;
		
		int[] pixel = new int[4];
		
		if (_x < 0 || _x >= getWidth() || _y < 0 || _y >= getHeight()) {
			return;
		}
		
		double _z = zBuffer.get(_x, _y);
		if (Double.isNaN(_z)) {
			set(_x, _y, z, rgba);
			return;
		} else {
			get(_x, _y, pixel);
			
			double xFrac = x - _x;
			double yFrac = y - _y;
			
			if (xFrac == 0) {
				xFrac = 1.0;
			}
			
			if (yFrac == 0) {
				yFrac = 1.0;
			}
			
			double pct = xFrac * yFrac;
			
			ColorAdjustments.interpolateColor(pixel, rgba, pixel, pct);
			
			set(_x, _y, z, pixel);
		}
		
	}
	
	
	public int get(int x, int y)
	{
		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
			return pixelBuffer.get(x, y);
		} else {
			return 0x0;
		}
	}
	
	public void get(int x, int y, int[] rgb)
	{
		int c = get(x, y);
		intToRGBA(c, rgb);
	}
	
	
	protected double interpolate(double z0, double z1, double frac)
    {
    	//double z0 = p0.z;
		//double z1 = p1.z;
		
    	double value = 0;
		
		if (!NumberUtil.isValidNumber(frac)) {
			value = z0;
		} else {
			value = (z1 - z0)*frac + z0;
		}

		return value;
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
		
		//Graphics gfx = image.getGraphics();
		//log.info("*****************************");
		//log.info("Class: " + gfx.getClass().getName());
		//log.info("Class: " + ((sun.java2d.SunGraphics2D)gfx).shapepipe.getClass().getName());
		//log.info("*****************************");;
		//gfx.dispose();
		
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
	
	public void setRenderPipeline(RenderPipeline pipeline)
	{
		this.pipeline = pipeline;
	}
	
	protected static int rgbaToInt(int[] rgb)
	{
		int r = rgb[0];
		int g = rgb[1];
		int b = rgb[2];
		int a = 0xFF;
		if (rgb.length >= 4) {
			a = rgb[3];
		}
		return rgbaToInt(r, g, b, a);
	}
	
	protected static int rgbaToInt(int r, int g, int b)
	{	
		return rgbaToInt(r, g, b, 0xFF);
	}
	
	protected static int rgbaToInt(int r, int g, int b, int a)
	{	
		int v = (a << 24) |
				((r & 0xff) << 16) |
				((g & 0xff) << 8) |
				(b & 0xff);
		return v;
	}
	
	protected static void intToRGBA(int c, int[] rgba)
	{
		rgba[0] = 0xFF & (c >>> 16);
		rgba[1] = 0xFF & (c >>> 8);
		rgba[2] = 0xFF & c;
		if (rgba.length >= 4) {
			rgba[3] = 0xFF & (c >>> 24);
		}
	}
	
	
	protected Edge[] getEdges(Shape path, boolean sort)
	{
		List<Edge> edgeList = new ArrayList<Edge>();
		double[] coords = new double[2];
		PathIterator iter = path.getPathIterator(null);
		
		int lastX = 0;
		int lastY = 0;
		
		int i = 0;
		while(!iter.isDone()) {
			iter.currentSegment(coords);
			
			int x = (int)coords[0];
			int y = (int)coords[1];
			
			if (i == 0) {
				
			} else {
				
				if (lastY < y) {
					edgeList.add(new Edge(lastX, lastY, x, y));
				} else {
					edgeList.add(new Edge(x, y, lastX, lastY));
				}
			}
			
			lastX = x;
			lastY = y;

			iter.next();

            i++;
		} 
        
		Edge[] edgeArray = new Edge[edgeList.size()];
		edgeList.toArray(edgeArray);
		
		
		if (sort) {
			Arrays.sort(edgeArray, new Comparator<Edge>() {
				public int compare(Edge e0, Edge e1)
				{
					return e0.compareTo(e1);
					/*
					if (e1.p0.y > e0.p0.y)
						return -1;
					else if (e1.p0.y == e0.p0.y) 
						return 0;
					else
						return 1;
					*/
				}
			});
		}
		
		return edgeArray;
		
	}
}
