package us.wthr.jdem846.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.geom.Geometric;
import us.wthr.jdem846.geom.Polygon;
import us.wthr.jdem846.geom.Triangle;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.EquirectangularProjection;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.render.render2d.ScanlinePath;
import us.wthr.jdem846.util.ColorSerializationUtil;

public class ModelCanvas
{
	private static Log log = Logging.getLog(ModelCanvas.class);
	
	private int width;
	private int height;
	
	//private MapProjection mapProjection;
	private ModelContext modelContext;
	private Color backgroundColor;
	private ModelDimensions2D modelDimensions;
	private CanvasProjection canvasProjection;
	
	//private BufferedImage image;
	//private WritableRaster raster;
	
	//private Graphics2D graphics;
	
	private boolean isDisposed = false;
	
	//private Path2D.Double pathBuffer = new Path2D.Double();
	//private Rectangle2D.Double rectangle = new Rectangle2D.Double();
	//private Quadrangle3d quad = new Quadrangle3d();
	
	private boolean isAntiAliased = false;
	
	private MapPoint mapPoint = new MapPoint();
	
	private Canvas3d canvas;
	
	//private int[][] zBuffer; // TODO: Implement this for crying out loud!
	//private ZBuffer zBuffer;
	
	public ModelCanvas(ModelContext modelContext)
	{
		this(modelContext, null);
	}
	
	public ModelCanvas(ModelContext modelContext, BufferedImage masterImage)
	{
		this.modelContext = modelContext;
		backgroundColor = ColorSerializationUtil.stringToColor(modelContext.getModelOptions().getBackgroundColor());
		modelDimensions = modelContext.getModelDimensions();
		
		width = modelContext.getModelDimensions().getOutputWidth();
		height = modelContext.getModelDimensions().getOutputHeight();

		isAntiAliased = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.ANTIALIASED);
		
		canvas = new Canvas3d(width, height);
		
		/*
		if (masterImage != null) {
			image = masterImage;
		} else {
			GraphicsConfiguration gc = getGraphicsConfiguration();
			int transparencyType = (isAntiAliased) ? Transparency.TRANSLUCENT : Transparency.BITMASK;
			image = gc.createCompatibleImage(getWidth()-1, getHeight()-1, transparencyType);
		}
		
		raster = image.getRaster();
		
		
		graphics = (Graphics2D) image.createGraphics();
		graphics.setComposite(AlphaComposite.SrcOver);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		
		if (isAntiAliased) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		graphics.setColor(new Color(0, 0, 0, 0));
		graphics.fillRect(0, 0, getWidth(), getHeight());
		*/
		
		/*
		if (modelContext.getMapProjection() != null) {
			mapProjection = modelContext.getMapProjection();
		} else {
			mapProjection = new EquirectangularProjection();
			mapProjection.setUp(modelContext);
		}
		*/
		
		canvasProjection = CanvasProjectionFactory.create(modelContext);
		
		//zBuffer = new ZBuffer(getWidth(), getHeight());
	}

	public void setRenderPipeline(RenderPipeline pipeline)
	{
		this.canvas.setRenderPipeline(pipeline);
	}
	
	public ModelCanvas getDependentHandle() throws CanvasException
	{
		//ModelCanvas other = new ModelCanvas(modelContext, image);
		return null;//other;
	}
	
	
	public ModelCanvas getCopy(boolean overlayImage) throws CanvasException
	{
		
		ModelCanvas copy = null;
		
		try {
			copy = new ModelCanvas(modelContext.copy());
			///if (overlayImage) {
			//	copy.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
			//}
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

	public void fillScanLine(ScanlinePath scanline)
	{
		this.canvas.fillScanLine(scanline);
	}
	
	public void fillScanLine(int leftX, int rightX, int y, double z, int[] rgba)
	{
		this.canvas.fillScanLine(leftX, rightX, y, z, rgba);
	}
	
	public void fillScanLine(int leftX, int rightX, int y, double z, int rgba)
	{
		this.canvas.fillScanLine(leftX, rightX, y, z, rgba);
	}
	
	public void fillScanLine(double leftX, double rightX, double y, double z, int[] rgba)
	{
		this.canvas.fillScanLine(leftX, rightX, y, z, rgba);
	}
	
	
	public void fillScanLine(double leftX, double rightX, double y, double z, int rgba)
	{
		this.canvas.fillScanLine(leftX, rightX, y, z, rgba);
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
		

		if (isAntiAliased) {
			drawRectangleSimpleAntialiased(color, row0, column0, row1, column1, fill);
		} else {
			drawRectangleSimpleStandard(color, row0, column0, row1, column1, fill);
		}

		
	}
	
	
	protected void drawRectangleSimpleStandard(int[] color, 
												double row0, double column0,
												double row1, double column1,
												boolean fill)
	{
		
		int _column0 = (int) column0;
		int _column1 = (int) Math.ceil(column1);
		if (_column1 <= _column0)
			_column1 = _column0 + 1;
		
		int _row0 = (int) row0;
		int _row1 = (int) Math.ceil(row1);
		if (_row1 <= _row0)
			_row1 = _row0 + 1;
		
		if (_row1 - _row0 <= 0 || _column1 - _column0 <= 0) {
			int i = 0;
		}

		int maxRow = canvas.getHeight() - 1;
		int maxCol = canvas.getWidth() - 1;
		
		for (int row = _row0; row <= _row1 && row < maxRow; row++) {
			for (int col = _column0; col <= _column1 && col < maxCol; col++) {
				canvas.set(col, row, 0.0, color);
			}
		}
		
	}
	
	protected void drawRectangleSimpleAntialiased(int[] color, 
												double row0, double column0,
												double row1, double column1,
												boolean filled)
	{

		Polygon poly = new Polygon();
		poly.addEdge((int)column0, (int)row0, (int)column0, (int)row1);
		poly.addEdge((int)column0, (int)row1, (int)column1, (int)row1);
		poly.addEdge((int)column1, (int)row1, (int)column1, (int)row0);

		if (filled) {
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
		//Color fillColor = new Color(color[0], color[1], color[2], alpha);

		//graphics.setColor(fillColor);
		//graphics.drawLine((int)Math.round(column0), (int)Math.round(row0), (int)Math.round(column1), (int)Math.round(row1));
		
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
	
	public void fillShape(Triangle tri)
	{
		canvas.fill(tri);
	}
	
	public void fillShape(Geometric poly, int[] color)
	{
		canvas.fill(poly, color);
	}
	
	public void fillShape(Quadrangle3d quad, int[] color)
	{
		canvas.fill(quad, color);
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
		
		// TODO: Restore drawText
		//graphics.setColor(textColor);
		//graphics.drawString(text, x, y);
		
	}
	
	public void drawImage(Image image, double north, double south, double east, double west) throws CanvasException
	{
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		
		try {
			canvasProjection.getPoint(north, west, 0, mapPoint);
			y = (int) Math.floor(mapPoint.row);
			x = (int) Math.floor(mapPoint.column);
			
			width = image.getWidth(null);
			height = image.getHeight(null);
			
			canvasProjection.getPoint(south, east, 0, mapPoint);
			
		} catch (Exception ex) {
			throw new CanvasException("Failed to project coordinates: " + ex.getMessage(), ex);
		}
		
		drawImage(image, x, y, width, height);
		
	}
	
	public void drawImage(Image image, int x, int y, int width, int height)
	{
		// TODO: Restore drawImage
		//graphics.drawImage(image, x, y, width, height, null);
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
		//return modelDimensions.getOutputWidth();
	}
	
	public int getHeight()
	{
		return height;
		//return modelDimensions.getOutputHeight();
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	
	public Image getImage()
	{
		//BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		//Graphics2D g2d = (Graphics2D) newImage.createGraphics();
		//g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		//g2d.dispose();
		//return newImage;
		return canvas.getImage();
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
		
		if (isAntiAliased) {
			stripAlphaChannel(subImage);
		}
		
		graphics.dispose();
		
		return subImage;
	}
	
	public Image getFinalizedImage()
	{
		return getFinalizedImage(true);
	}
	
	public Image getFinalizedImage(boolean applyBackground)
	{
		BufferedImage finalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) finalImage.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		if (modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.ANTIALIASED)) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		graphics.drawImage(canvas.getImage(), 0, 0, getWidth(), getHeight(), null);
		
		
		//BufferedImage finalImage = ImageUtilities.getScaledInstance(image, getWidth(), getHeight(), RenderingHints.VALUE_INTERPOLATION_BILINEAR, false);
		if (isAntiAliased) {
			stripAlphaChannel(finalImage);
		}
		
		if (applyBackground) {
			applyBackgroundColor(finalImage);
		}
		graphics.dispose();
		return finalImage;
		
	}
	
	protected void applyBackgroundColor(BufferedImage image)
	{
		WritableRaster raster = image.getRaster();
		int[] rasterPixel = new int[4];
		
		for (int row = 0; row < raster.getHeight(); row++) {
			for (int column = 0; column < raster.getWidth(); column++) {
				raster.getPixel(column, row, rasterPixel);
				
				if (rasterPixel[0] == 0 && rasterPixel[1] == 0 && rasterPixel[2] == 0 && rasterPixel[3] == 0) {
					// Apply background color
					
					rasterPixel[0] = backgroundColor.getRed();
					rasterPixel[1] = backgroundColor.getGreen();
					rasterPixel[2] = backgroundColor.getBlue();
					rasterPixel[3] = backgroundColor.getAlpha();
					
					raster.setPixel(column, row, rasterPixel);
				} 
			}
			
		}
	}
	
	protected void stripAlphaChannel(BufferedImage image)
	{
		WritableRaster raster = image.getRaster();
		int[] rasterPixel = new int[4];
		
		for (int row = 0; row < raster.getHeight(); row++) {
			for (int column = 0; column < raster.getWidth(); column++) {
				raster.getPixel(column, row, rasterPixel);
				
				if (!(rasterPixel[0] == 0 && rasterPixel[1] == 0 && rasterPixel[2] == 0 && rasterPixel[3] == 0)) {
					// Remove alpha channel
					
					rasterPixel[3] = 255;
					raster.setPixel(column, row, rasterPixel);
				}
			}
			
		}
	}
	
	
	public void save(String saveTo) throws CanvasException
	{
		try {
			ImageWriter.saveImage((BufferedImage)getFinalizedImage(), saveTo);
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
