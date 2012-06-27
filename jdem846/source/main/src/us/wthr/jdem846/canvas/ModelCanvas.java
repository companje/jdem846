package us.wthr.jdem846.canvas;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.geom.Geometric;
import us.wthr.jdem846.geom.Polygon;
import us.wthr.jdem846.geom.Triangle;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.render.RenderPipeline;
import us.wthr.jdem846.render.render2d.ScanlinePath;
import us.wthr.jdem846.util.ColorSerializationUtil;

public class ModelCanvas
{
	private static Log log = Logging.getLog(ModelCanvas.class);
	
	private int width;
	private int height;
	
	
	private ModelContext modelContext;
	private Color backgroundColor;
	private CanvasProjection canvasProjection;

	private boolean isDisposed = false;
	private MapPoint mapPoint = new MapPoint();
	
	private Canvas3d canvas;
	

	public ModelCanvas(ModelContext modelContext) throws CanvasException
	{
		this(modelContext.getModelProcessManifest().getGlobalOptionModel().getWidth(), 
				modelContext.getModelProcessManifest().getGlobalOptionModel().getHeight(), 
				modelContext.getModelProcessManifest().getGlobalOptionModel().getPixelStackDepth(),
				modelContext.getModelProcessManifest().getGlobalOptionModel().getSubpixelGridSize(), 
				modelContext.getModelProcessManifest().getGlobalOptionModel().getBackgroundColor(), 
				CanvasProjectionFactory.create(modelContext));
	}
	
	public ModelCanvas(int width, int height, int pixelStackDepth, int subpixelWidth, String backgroundColor, CanvasProjection canvasProjection)
	{
		this(width, height, pixelStackDepth, subpixelWidth, ColorSerializationUtil.stringToColor(backgroundColor), canvasProjection);
	}
	
	public ModelCanvas(int width, int height, int pixelStackDepth, int subpixelWidth, RgbaColor backgroundColor, CanvasProjection canvasProjection)
	{
		this(width, height, pixelStackDepth, subpixelWidth, backgroundColor.toAwtColor(), canvasProjection);
	}
	
	public ModelCanvas(int width, int height, int pixelStackDepth, int subpixelWidth, Color backgroundColor, CanvasProjection canvasProjection)
	{
		this.backgroundColor = backgroundColor;
		
		this.width = width;
		this.height = height;
		
		// I know this is wrong.
		double clipNearZ = width * 50;
		double clipFarZ = -width * 50;
		

		canvas = new Canvas3d(width, height, clipNearZ, clipFarZ, pixelStackDepth, subpixelWidth, backgroundColor);
		
		if (canvasProjection != null) {
			this.canvasProjection = canvasProjection;
		} else {
			// TODO: Maybe a default canvas projection?
		}
	}

	public void reset()
	{
		canvas.reset();
	}
	
	public void setRenderPipeline(RenderPipeline pipeline)
	{
		this.canvas.setRenderPipeline(pipeline);
	}

	
	
	public ModelCanvas getCopy(boolean overlayImage) throws CanvasException
	{
		
		ModelCanvas copy = null;
		
		try {
			copy = new ModelCanvas(modelContext.copy());
		} catch (Exception ex) {
			throw new CanvasException("Error creating canvas copy: " + ex.getMessage(), ex);
		}
		
		return copy;
		
	}
	
	
	
	protected GraphicsConfiguration getGraphicsConfiguration()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration defaultConfiguration = gd.getDefaultConfiguration();
		log.info("Default graphics configuration is accelerated: " + defaultConfiguration.getImageCapabilities().isAccelerated());
		
		
		for (GraphicsDevice gfxdev : ge.getScreenDevices()) {
			for (GraphicsConfiguration gc : gfxdev.getConfigurations()) {
				if (gc.getImageCapabilities().isAccelerated()) {
					log.info("Found accelerated graphics configuration");
					return gc;
				}
			}
		}
		
		return defaultConfiguration;
	}
	
	public CanvasProjection getCanvasProjection()
	{
		return canvasProjection;
	}
	
	public void setCanvasProjection(CanvasProjection canvasProjection)
	{
		this.canvasProjection = canvasProjection;
	}
	
	public MapProjection getMapProjection()
	{
		return canvasProjection.getMapProjection();
	}

	public void setMapProjection(MapProjection mapProjection)
	{
		canvasProjection.setMapProjection(mapProjection);
	}

	
	public void fillTriangle(int[] color, 
								double latitude0, double longitude0, double elevation0,
								double latitude1, double longitude1, double elevation1,
								double latitude2, double longitude2, double elevation2) throws CanvasException
	{

		double row0, row1, row2;
		double column0, column1, column2;
		
		try {
			canvasProjection.getPoint(latitude0, longitude0, elevation0, mapPoint);
			row0 = mapPoint.row;
			column0 = mapPoint.column;
			
			canvasProjection.getPoint(latitude1, longitude1, elevation1, mapPoint);
			row1 = mapPoint.row;
			column1 = mapPoint.column;
			
			canvasProjection.getPoint(latitude2, longitude2, elevation2, mapPoint);
			row2 = mapPoint.row;
			column2 = mapPoint.column;
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordinates to canvas: " + ex.getMessage(), ex);
		}

		Polygon poly = new Polygon();
		poly.addEdge((int)row0, (int)column0, (int)row1, (int)column1);
		poly.addEdge((int)row1, (int)column1, (int)row2, (int)column2);
		fillShape(poly, color);
		
		
	}
	
	/** Simplified rectangle fill.
	 * 
	 * @param color
	 * @param latitude
	 * @param longitude
	 * @param width
	 * @param height
	 * @param elevation
	 */
	public void fillRectangle(int[] color,
			double latitude, double longitude, 
			double width, double height,
			double elevation) throws CanvasException
	{
		__drawRectangle(color, latitude, longitude, width, height, elevation, true);
	}
	
	
	/** Simplified rectangle fill.
	 * 
	 * @param color
	 * @param latitude
	 * @param longitude
	 * @param width
	 * @param height
	 * @param elevation
	 */
	public void drawRectangle(int[] color,
			double latitude, double longitude, 
			double width, double height,
			double elevation) throws CanvasException
	{
		__drawRectangle(color, latitude, longitude, width, height, elevation, false);
	}

	private void __drawRectangle(int[] color,
			double latitude, double longitude, 
			double width, double height,
			double elevation, 
			boolean fill) throws CanvasException
	{
		double row0, row1;
		double column0, column1;

		try {
			canvasProjection.getPoint(latitude, longitude, elevation, mapPoint);
			row0 = mapPoint.row;
			column0 = mapPoint.column;
	
			
			canvasProjection.getPoint(latitude-height, longitude+width, elevation, mapPoint);
			row1 = mapPoint.row;
			column1 = mapPoint.column;
			
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordinates to canvas: " + ex.getMessage(), ex);
		}
		

		Polygon poly = new Polygon();
		poly.addEdge((int)column0, (int)row0, (int)column0, (int)row1);
		poly.addEdge((int)column0, (int)row1, (int)column1, (int)row1);
		poly.addEdge((int)column1, (int)row1, (int)column1, (int)row0);

		if (fill) {
			fillShape(poly, color);
		} else {
			drawShape(poly, color);
		}
	}
	

	
	public void drawLine(int[] color, 
			double lat0, double lon0, double elev0,
			double lat1, double lon1, double elev1) throws CanvasException
	{
		double row0, row1;
		double column0, column1;
		

		try {
			canvasProjection.getPoint(lat0, lon0, elev0, mapPoint);
			row0 = mapPoint.row;
			column0 = mapPoint.column;
			
			canvasProjection.getPoint(lat1, lon1, elev1, mapPoint);
			row1 = mapPoint.row;
			column1 = mapPoint.column;
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordates to canvas: " + ex.getMessage(), ex);
		}
		
		
		int alpha = 255;
		if (color.length >= 4) {
			alpha = color[3];
		}
		
		canvas.drawLine((int)Math.round(column0), (int)Math.round(row0), 0.0, (int)Math.round(column1), (int)Math.round(row1), 0.0, color);

	}
	
	public void fillRectangle(int[] color, 
			double lat0, double lon0, double elev0,
			double lat1, double lon1, double elev1,
			double lat2, double lon2, double elev2,
			double lat3, double lon3, double elev3) throws CanvasException
	{

		double row0, row1, row2, row3;
		double column0, column1, column2, column3;
		double z0, z1, z2, z3;
		
		try {
			canvasProjection.getPoint(lat0, lon0, elev0, mapPoint);
			row0 =  Math.floor(mapPoint.row);
			column0 = Math.floor(mapPoint.column);
			z0 = mapPoint.z;

			
			canvasProjection.getPoint(lat1, lon1, elev1, mapPoint);
			row1 = Math.floor(mapPoint.row);
			column1 = Math.floor(mapPoint.column);
			z1 = mapPoint.z;			//z += mapPoint.z;

			canvasProjection.getPoint(lat2, lon2, elev2, mapPoint);
			row2 = Math.floor(mapPoint.row);
			column2 =  Math.floor(mapPoint.column);
			z2 = mapPoint.z;

			canvasProjection.getPoint(lat3, lon3, elev3, mapPoint);
			row3 = Math.floor(mapPoint.row);
			column3 = Math.floor(mapPoint.column);
			z3 = mapPoint.z;

		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordates to canvas: " + ex.getMessage(), ex);
		}
		
		Polygon poly = new Polygon();
		poly.addEdge(column0, row0, z0, column1, row1, z1);
		poly.addEdge(column1, row1, z1, column2, row2, z2);
		poly.addEdge(column2, row2, z2, column3, row3, z3);
		fillShape(poly, color);

		
	}
	
	public void fillShape(TriangleStrip strip)
	{
		canvas.fill(strip);
	}
	
	public void fillShape(Triangle tri)
	{
		canvas.fill(tri);
	}
	
	public void fillShape(Geometric poly, int[] color)
	{
		canvas.fill(poly, color);
	}
	
	
	public void fillShape(Shape shape, int[] color)
	{
		canvas.fill(shape, color);
	}

	
	public void drawShape(Geometric poly, int[] color)
	{
		canvas.draw(poly, color);
	}
	
	public void drawShape(Shape shape, int[] color)
	{
		canvas.draw(shape, color);
	}

	
	public void fillCircle(int[] color, double latitude, double longitude, double elevation, double radiusPixels) throws CanvasException
	{
		int alpha = 255;
		if (color.length >= 4) {
			alpha = color[3];
		}
		
		Color fillColor = new Color(color[0], color[1], color[2], alpha);
		fillCircle(fillColor, latitude, longitude, elevation, radiusPixels);
	}
	
	public void fillCircle(Color color, double latitude, double longitude, double elevation, double radiusPixels) throws CanvasException
	{
		
		
		double row, column = 0;
		
		try {
			canvasProjection.getPoint(latitude, longitude, elevation, mapPoint);
			row = mapPoint.row;
			column = mapPoint.column;
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordinates to canvas: " + ex.getMessage(), ex);
		}
		
		int y = (int) Math.round(row - (radiusPixels / 2.0));
		int x = (int) Math.round(column - (radiusPixels / 2.0));
		
		int radius = (int) Math.round(radiusPixels);
		
		// TODO: Restore fillCircle
		
	}
	
	public void drawText(String text, int[] color, double latitude, double longitude, boolean centered) throws CanvasException
	{
		int alpha = 255;
		if (color.length >= 4) {
			alpha = color[3];
		}

		Color textColor = new Color(color[0], color[1], color[2], alpha);
		
		int x = 0;
		int y = 0;
		
		try {
			canvasProjection.getPoint(latitude, longitude, 0, mapPoint);
			y = (int) Math.round(mapPoint.row);
			x = (int) Math.round(mapPoint.column);
		} catch (Exception ex) {
			throw new CanvasException("Failed to project coordinates: " + ex.getMessage(), ex);
		}
		

	}
	

	public int getColor(int x, int y)
	{
		if (x < 0 || x >= canvas.getWidth() || y < 0 || y >= canvas.getHeight())
			return 0;
		
		return canvas.get(x, y);
	}

	
	public double getNorth()
	{
		return modelContext.getNorth();
	}
	
	public double getSouth()
	{
		return modelContext.getSouth();
	}
	
	public double getEast()
	{
		return modelContext.getEast();
	}
	
	public double getWest()
	{
		return modelContext.getWest();
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	
	public JDemElevationModel getJdemElevationModel()
	{
		return canvas.getJdemElevationModel();
	}
	
	public Image getImage()
	{
		return canvas.getImage();
	}
	
	public boolean[][] getModelMask()
	{
		return canvas.getModelMask();
	}
	
	public Image getSubImage(double north, double south, double east, double west) throws CanvasException
	{
		
		int srcX = 0;
		int srcY = 0;
		int width = 0;
		int height = 0;
		
		try {
			canvasProjection.getPoint(north, west, 0, mapPoint);
			srcY = (int) Math.floor(mapPoint.row);
			srcX = (int) Math.floor(mapPoint.column);
			
			canvasProjection.getPoint(south, east, 0, mapPoint);
			int srcY2 = (int) Math.ceil(mapPoint.row);
			int srcX2 = (int) Math.ceil(mapPoint.column);
			
			width = srcX2 - srcX;
			height = srcY2 - srcY;
			
		} catch (Exception ex) {
			throw new CanvasException("Failed to project coordinates: " + ex.getMessage(), ex);
		}
		
		
		BufferedImage subImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) subImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		if (modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.ANTIALIASED)) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		graphics.drawImage(canvas.getImage(),
                0, //int dx1,
                0, //int dy1,
                width, //int dx2,
                height, //int dy2,
                srcX, //int sx1,
                srcY, //int sy1,
                srcX + width, //int sx2,
                srcY + height, //int sy2,
                null);

		
		graphics.dispose();
		
		return subImage;
	}
	

	
	public void save(String saveTo) throws CanvasException
	{
		try {
			ImageWriter.saveImage((BufferedImage)getImage(), saveTo);
		} catch (ImageException ex) {
			throw new CanvasException("Failed to save image to disk: " + ex.getMessage(), ex);
		}

	}
	
	public void dispose()
	{
		if (!isDisposed) {
			//graphics.dispose();
			//graphics = null;
			//image.flush();
			//image = null;
		}
	}
}
