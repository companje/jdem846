package us.wthr.jdem846.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.geom.Bounds;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Geometric;
import us.wthr.jdem846.geom.Polygon;
import us.wthr.jdem846.geom.Triangle;
import us.wthr.jdem846.geom.TriangleStrip;
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
	
	private static final double Z_VALUE_NOT_SET = DemConstants.ELEV_NO_DATA;
	
	private int height;
	private int width;
	private double clipNearZ;
	private double clipFarZ;
	
	private MatrixBuffer<Integer> pixelBuffer;
	private MatrixBuffer<Double> zBuffer;
	
	private RenderPipeline pipeline;
	
	public Canvas3d(int width, int height, double clipNearZ, double clipFarZ)
	{
		this(width, height, clipNearZ, clipFarZ, null);
	}
	
	public Canvas3d(int width, int height, double clipNearZ, double clipFarZ, RenderPipeline pipeline)
	{
		this.pipeline = pipeline;
		this.width = width;
		this.height = height;
		this.clipNearZ = clipNearZ;
		this.clipFarZ = clipFarZ;
		
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
	
	public void draw(Edge edge)
	{
		draw(edge, -1);
	}
	
	public void draw(Edge edge, int rgba)
	{
		int[] rgba0 = new int[4];
		int[] rgba1 = new int[4];
		int[] pixel = new int[4];
		
		if (rgba != -1) {
			rgba0 = new int[4];
			rgba1 = new int[4];
			
			intToRGBA(rgba, rgba0);
			intToRGBA(rgba, rgba1);
			
		} else {
			//rgba0 = edge.p0.rgba;
			//rgba1 = edge.p1.rgba;
			//int i = 0;
			rgba0[0] = edge.p0.rgba[0];
			rgba0[1] = edge.p0.rgba[1];
			rgba0[2] = edge.p0.rgba[2];
			rgba0[3] = edge.p0.rgba[3];
			
			rgba1[0] = edge.p1.rgba[0];
			rgba1[1] = edge.p1.rgba[1];
			rgba1[2] = edge.p1.rgba[2];
			rgba1[3] = edge.p1.rgba[3];
			
		}
		
		
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
		
		if (xMx < 0 || xMn >= getWidth() || yMx < 0 || yMn >= getHeight()) {
			return;
		}
		
		double s = edge.m;//(double)(mnY - mxY) / (double)(mnX - mxX);
		boolean isValidSlope = NumberUtil.isValidNumber(s);
		
		if (Math.abs(x1 - x0) >= Math.abs(y1 - y0)) {
			// Long
			for (int x = xMn; x <= xMx; x++) {
				int y = (int) ((isValidSlope) ? ((s * (x - mxX)) + mxY) + 1 : mxY);
				double f = (double)(x - xMn) / (double)(xMx - xMn);
				double z = edge.getInterpolatedZ(x, y);
				
				ColorAdjustments.interpolateColor(rgba0, rgba1, pixel, f);
				
				set(x, y, z, pixel);
			}
			
		} else {
			// Tall
			for (int y = yMn; y <= yMx; y++) {
				int x =  (int) ((isValidSlope) ? (((y - mxY) / s) + mxX) : mxX);
				double f = ((yMx - yMn) != 0) ? (y - yMn) / (yMx - yMn) : 0;
				//double z = edge.getInterpolatedZ( f);
				double z = edge.getInterpolatedZ(x, y);
				ColorAdjustments.interpolateColor(rgba0, rgba1, pixel, f);
				
				set(x, y, z, pixel);
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

		x0 = Math.round(x0);
		y0 = Math.round(y0);
		z0 = Math.round(z0);
		
		x1 = Math.round(x1);
		y1 = Math.round(y1);
		z1 = Math.round(z1);
		
		x2 = Math.round(x2);
		y2 = Math.round(y2);
		z2 = Math.round(z2);
		
		x3 = Math.round(x3);
		y3 = Math.round(y3);
		z3 = Math.round(z3);
		
		
		
		poly.addEdge(x0, y0, z0, x1, y1, z1);
		poly.addEdge(x1, y1, z1, x2, y2, z2);
		poly.addEdge(x2, y2, z2, x3, y3, z3);

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
	
	public void fill(Geometric shape)
	{
		fill(shape, -1);
	}
	
	public void fill(TriangleStrip strip)
	{
		
		int count = strip.getTriangleCount();
		for (int i = 0; i < count; i++) {
			Triangle tri = strip.getTriangle(i);
			
			if (tri == null) {
				break;
			}
			
			fill(tri);
		}
	}
	
	public void fill(Triangle tri)
	{
		
		if (!isValidZCoordinate(tri.p0.z) && !isValidZCoordinate(tri.p1.z) && !isValidZCoordinate(tri.p2.z)) {
			return;
		}
		
		
		//Bounds bounds = tri.getBounds();
		
		double maxX = MathExt.ceil(MathExt.max(tri.p0.x, tri.p1.x, tri.p2.x));
		double minX = MathExt.floor(MathExt.min(tri.p0.x, tri.p1.x, tri.p2.x));
		double maxY = MathExt.ceil(MathExt.max(tri.p0.y, tri.p1.y, tri.p2.y));
		double minY = MathExt.floor(MathExt.min(tri.p0.y, tri.p1.y, tri.p2.y));

		
		if (maxX < 0 || minX >= getWidth() || maxY < 0 || minY >= getHeight()) {
			return;
		}
		
		if (minX < 0)
			minX = 0;
		if (maxX >= getWidth()) 
			maxX = getWidth() - 1;
		
		if (minY < 0)
			minY = 0;
		if (maxY >= getHeight())
			maxY = getHeight() - 1;
		
		int[] rgba = {0, 0, 0, 255};
		
		for (double y = minY; y <= maxY; y++) {
			for (double x = minX; x <= maxX; x++) {
				
				if (tri.contains(x, y)) {
					
					double z = tri.getInterpolatedZ(x, y);
					tri.getInterpolatedColor(x, y, rgba);
					
					if (y < 1) {
						int i = 0;
					}
					
					//rgba[0] = 255;
					//rgba[1] = 0;
				//	rgba[2] = 0;
					//rgba[3] = 255;
					
					set(x, y, z, rgba);
					
				}
				
			}
		}
		
		
	}
	
	
	public void fill(Geometric shape, int rgba)
	{
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		boolean useEdgeColors = (rgba == -1);
		
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
		List<Rgba> rgbaList = new ArrayList<Rgba>();
		
		for (int y = minY; y <= maxY; y++) {
			xList.clear();
			zList.clear();
			
			for (int i = 0; i < edgeArray.length; i++) {

				if (y == edgeArray[i].p0.y) {
                    if (y == edgeArray[i].p1.y) {
                        // the current edge is horizontal, so we add both vertices
                    	edgeArray[i].deactivate(y);
                    	xList.add((int) edgeArray[i].curX);
                    	zList.add((int) edgeArray[i].curZ);
                    	if (useEdgeColors)
                    		rgbaList.add(new Rgba(edgeArray[i].curRgba));
                    } else {
                    	edgeArray[i].activate(y);
                        // we don't insert it in the list cause this vertice is also
                        // the (bigger) vertice of another edge and already handled
                    }
                }
                
                // here the scanline intersects the bigger vertice
                if (y == edgeArray[i].p1.y) {
                	edgeArray[i].deactivate(y);
                	xList.add((int) edgeArray[i].curX);
                	zList.add((int) edgeArray[i].curZ);
                	if (useEdgeColors)
                		rgbaList.add(new Rgba(edgeArray[i].curRgba));
                }
                
                // here the scanline intersects the edge, so calc intersection point
                if (y > edgeArray[i].p0.y && y < edgeArray[i].p1.y) {
                	edgeArray[i].update(y);
                	xList.add((int) edgeArray[i].curX);
                	zList.add((int) edgeArray[i].curZ);
                	if (useEdgeColors)
                		rgbaList.add(new Rgba(edgeArray[i].curRgba));
                }
			}
			
			if (xList.size() < 2 || xList.size() % 2 != 0) 
            {
               //log.warn("This should never happen! (list size: " + xList.size() + ", Edge Count: " + edgeArray.length + ")");
                //continue;
            } 
			
			int xSwap;
			int zSwap;
			Rgba rSwap;
            for (int i = 0; i < xList.size(); i++) {
                for (int j = 0; j < xList.size() - 1; j++) {
                    if (xList.get(j) > xList.get(j+1)) {
                    	xSwap = xList.get(j);
                        xList.set(j, xList.get(j+1));
                        xList.set(j+1, xSwap);
                        
                        zSwap = zList.get(j);
                        zList.set(j, zList.get(j+1));
                        zList.set(j+1, zSwap);
                        
                        if (useEdgeColors) {
	                        rSwap = rgbaList.get(j);
	                        rgbaList.set(j, rgbaList.get(j+1));
	                        rgbaList.set(j+1, rSwap);
                        }
                    }
                
                }
            }
            
            
            //int pointsDrawn = 0;
            // so draw all line segments on current scanline
            for (int i = 0; i < xList.size(); i+=2)
            {
            	if (i+1 < xList.size()) {
            		int leftX = xList.get(i);
            		int rightX = xList.get(i+1);
            		int leftZ = zList.get(i);
            		int rightZ = zList.get(i+1);
            		if (rightX == leftX) {
            			rightX++;
            		}
            		
            		Edge edge = null;
            		if (useEdgeColors) {
	            		Rgba leftRgba = rgbaList.get(i);
	            		Rgba rightRgba = rgbaList.get(i+1);
	            		
	            		
	            		//_fillScanLine(leftX, rightX, y, leftZ, rightZ, rgba);
	            		edge = new Edge(leftX, y-1, leftZ, leftRgba.rgba, rightX, y-1, rightZ, rightRgba.rgba);
	            		draw(edge);
            		} else {
            			edge = new Edge(leftX, y-1, leftZ, rightX, y-1, rightZ);
            			draw(edge, rgba);
            		}
	            	
            		//pointsDrawn += 1;
            	}
            }
            //if (pointsDrawn == 0) {
            	//log.info("Points Drawn: " + pointsDrawn + ", "+ xList.size());
            //}
            
             
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
		
		if (!isValidZCoordinate(z)) {
			return;
		}

		double _z = zBuffer.get(x, y);
		
		if (_z == DemConstants.ELEV_NO_DATA || _z < z) {
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
		
		int _x = (int) MathExt.floor(x);
		int _y = (int) MathExt.floor(y);
		
		if (!isValidZCoordinate(z))
			return;

		
		if (_x < 0 || _x >= getWidth() || _y < 0 || _y >= getHeight()) {
			return;
		}
		
		double _z = zBuffer.get(_x, _y);
		if (_z == DemConstants.ELEV_NO_DATA || z >= _z) {
			set(_x, _y, z, rgba);
		} /*else {
			int[] pixel = new int[4];
		
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
		}*/
		
	}
	
	private boolean isValidZCoordinate(double z)
	{
		if (z < clipFarZ || z > clipNearZ) {
			return false;
		} else {
			return true;
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

		int[] rgba = new int[4];
		rgba[3] = 0xFF;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				get(x, y, rgba);
				raster.setPixel(x, y, rgba);
			}
		}
		
		
		/*
		 * 
		Graphics2D gfx = (Graphics2D) image.getGraphics();
		gfx.setComposite(AlphaComposite.SrcOver);
		gfx.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Path2D.Double path = new Path2D.Double();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				get(x, y, rgba);
				//Color color = new Color(rgbaToInt(rgba));
				Color color = new Color(rgba[0], rgba[1], rgba[2]);
				gfx.setColor(color);
				path.reset();
				
				path.moveTo(x-0.25, y-0.25);
				path.lineTo(x-0.25, y+1.25);
				path.lineTo(x+1.25, y+1.25);
				path.lineTo(x+1.25, y-0.25);
				path.closePath();
				gfx.fill(path);
				
				//gfx.fillRect(x, y, 1, 1);
				//log.info("RGBA: " + rgba[0] + "/" + rgba[1] + "/" + rgba[2] + "/" + rgba[3]);
				//raster.setPixel(x, y, rgba);
			}
		}
		*/
		
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
	
	class Rgba
	{
		public int[] rgba;
		
		public Rgba(int[] rgba)
		{
			this.rgba = new int[4];
			this.rgba[0] = rgba[0];
			this.rgba[1] = rgba[1];
			this.rgba[2] = rgba[2];
			this.rgba[3] = rgba[3];
		}
	}
}
