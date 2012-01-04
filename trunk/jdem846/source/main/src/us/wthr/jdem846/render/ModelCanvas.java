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
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.EquirectangularProjection;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.util.ColorSerializationUtil;

public class ModelCanvas
{
	private static Log log = Logging.getLog(ModelCanvas.class);
	
	private int width;
	private int height;
	
	private MapProjection mapProjection;
	private ModelContext modelContext;
	private Color backgroundColor;
	private ModelDimensions2D modelDimensions;
	
	private BufferedImage image;
	private WritableRaster raster;
	
	private Graphics2D graphics;
	
	private boolean isDisposed = false;
	
	private Path2D.Double pathBuffer = new Path2D.Double();
	private Rectangle2D.Double rectangle = new Rectangle2D.Double();
	
	private boolean isAntiAliased = false;
	
	private MapPoint mapPoint = new MapPoint();
	
	public ModelCanvas(ModelContext modelContext)
	{
		this.modelContext = modelContext;
		backgroundColor = ColorSerializationUtil.stringToColor(modelContext.getModelOptions().getBackgroundColor());
		modelDimensions = modelContext.getModelDimensions();
		
		width = modelContext.getModelDimensions().getOutputWidth();
		height = modelContext.getModelDimensions().getOutputHeight();
		//width = modelContext.getModelOptions().getWidth();
		//height = modelContext.getModelOptions().getHeight();
		//width = (int) modelContext.getModelDimensions().getTileOutputWidth();
		//height = (int) modelContext.getModelDimensions().getTileOutputHeight();
		
		//modelDimensions = ModelDimensions2D.getModelDimensions(modelContext);
		
		isAntiAliased = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.ANTIALIASED);
		
		GraphicsConfiguration gc = getGraphicsConfiguration();
		
		
		
		int transparencyType = (isAntiAliased) ? Transparency.TRANSLUCENT : Transparency.BITMASK;
		
		image = gc.createCompatibleImage(getWidth()-1, getHeight()-1, transparencyType);
		raster = image.getRaster();
		
		graphics = (Graphics2D) image.getGraphics();
		graphics.setComposite(AlphaComposite.SrcOver);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		
		if (isAntiAliased) {
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		graphics.setColor(new Color(0, 0, 0, 0));
		graphics.fillRect(0, 0, getWidth(), getHeight());
		
		
		if (modelContext.getMapProjection() != null) {
			mapProjection = modelContext.getMapProjection();
		} else {
			mapProjection = new EquirectangularProjection();
			mapProjection.setUp(modelContext);
		}
	}

	
	public ModelCanvas getCopy(boolean overlayImage) throws CanvasException
	{
		ModelCanvas copy = null;
		
		try {
			copy = new ModelCanvas(modelContext.copy());
			if (overlayImage) {
				copy.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
			}
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
	
	public MapProjection getMapProjection()
	{
		return mapProjection;
	}

	public void setMapProjection(MapProjection mapProjection)
	{
		this.mapProjection = mapProjection;
	}

	public void fillTriangle(int[] color, 
								double latitude0, double longitude0, double elevation0,
								double latitude1, double longitude1, double elevation1,
								double latitude2, double longitude2, double elevation2) throws CanvasException
	{
		pathBuffer.reset();
		
		int alpha = 0xFF;
		if (color.length >= 4) {
			alpha = color[3];
		}

		Color fillColor = new Color(color[0], color[1], color[2], alpha);
		
		double row0, row1, row2;
		double column0, column1, column2;
		
		try {
			mapProjection.getPoint(latitude0, longitude0, elevation0, mapPoint);
			row0 = mapPoint.row;
			column0 = mapPoint.column;
			
			mapProjection.getPoint(latitude1, longitude1, elevation1, mapPoint);
			row1 = mapPoint.row;
			column1 = mapPoint.column;
			
			mapProjection.getPoint(latitude2, longitude2, elevation2, mapPoint);
			row2 = mapPoint.row;
			column2 = mapPoint.column;
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordinates to canvas: " + ex.getMessage(), ex);
		}

		pathBuffer.moveTo(column0, row0);
		pathBuffer.lineTo(column1, row1);
		pathBuffer.lineTo(column2, row2);
		pathBuffer.closePath();
		
		fillShape(fillColor, null, pathBuffer);
		//graphics.setColor(fillColor);
		//graphics.fill(pathBuffer);
		
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
			mapProjection.getPoint(latitude, longitude, elevation, mapPoint);
			row0 = mapPoint.row;
			column0 = mapPoint.column;
	
			
			mapProjection.getPoint(latitude-height, longitude+width, elevation, mapPoint);
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
		//color[3] = 0xFF;
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
		
		
		
		int maxRow = raster.getHeight() - 1;
		int maxCol = raster.getWidth() - 1;
		
		for (int row = _row0; row <= _row1 && row < maxRow; row++) {
			for (int col = _column0; col <= _column1 && col < maxCol; col++) {
				raster.setPixel(col, row, color);
			}
		}
		
	}
	
	protected void drawRectangleSimpleAntialiased(int[] color, 
												double row0, double column0,
												double row1, double column1,
												boolean filled)
	{
		int alpha = 255;
		if (color.length >= 4) {
			alpha = color[3];
		}

		Color fillColor = new Color(color[0], color[1], color[2], alpha);
		rectangle.x = column0;
		rectangle.y = row0;
		rectangle.width = column1 - column0;
		rectangle.height = row1 - row0;

		if (filled) {
			fillShape(fillColor, null, rectangle);
		} else {
			drawShape(fillColor, null, rectangle);
		}
	}
	
	public void drawLine(int[] color, 
			double lat0, double lon0, double elev0,
			double lat1, double lon1, double elev1) throws CanvasException
	{
		double row0, row1;
		double column0, column1;
		

		try {
			mapProjection.getPoint(lat0, lon0, elev0, mapPoint);
			row0 = mapPoint.row;
			column0 = mapPoint.column;
			
			mapProjection.getPoint(lat1, lon1, elev1, mapPoint);
			row1 = mapPoint.row;
			column1 = mapPoint.column;
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordates to canvas: " + ex.getMessage(), ex);
		}
		
		
		int alpha = 255;
		if (color.length >= 4) {
			alpha = color[3];
		}
		
		
		Color fillColor = new Color(color[0], color[1], color[2], alpha);

		graphics.setColor(fillColor);
		graphics.drawLine((int)Math.round(column0), (int)Math.round(row0), (int)Math.round(column1), (int)Math.round(row1));
		
	}
	
	public void fillRectangle(int[] color, 
			double lat0, double lon0, double elev0,
			double lat1, double lon1, double elev1,
			double lat2, double lon2, double elev2,
			double lat3, double lon3, double elev3) throws CanvasException
	{
		pathBuffer.reset();
		

		double row0, row1, row2, row3;
		double column0, column1, column2, column3;
		
		try {
			mapProjection.getPoint(lat0, lon0, elev0, mapPoint);
			row0 = mapPoint.row;
			column0 = mapPoint.column;
			
			mapProjection.getPoint(lat1, lon1, elev1, mapPoint);
			row1 = mapPoint.row;
			column1 = mapPoint.column;
			
			mapProjection.getPoint(lat2, lon2, elev2, mapPoint);
			row2 = mapPoint.row;
			column2 = mapPoint.column;
			
			mapProjection.getPoint(lat3, lon3, elev3, mapPoint);
			row3 = mapPoint.row;
			column3 = mapPoint.column;
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordates to canvas: " + ex.getMessage(), ex);
		}
		
		//if (row1 < row0 || row2 < row3)
		//	return;
		
		pathBuffer.moveTo(column0, row0);
		pathBuffer.lineTo(column1, row1);
		pathBuffer.lineTo(column2, row2);
		pathBuffer.lineTo(column3, row3);
		pathBuffer.closePath();
		
		int alpha = 255;
		if (color.length >= 4) {
			alpha = color[3];
		}
		
		
		Color fillColor = new Color(color[0], color[1], color[2], alpha);
		
		fillShape(fillColor, null, pathBuffer);
		
		/*
		
		*/
		
	}
	
	public void fillShape(Color color, Stroke stroke, Shape shape)
	{
		if (color != null) {
			graphics.setColor(color);
		}
			
		Stroke origStroke = graphics.getStroke();
			
		if (stroke != null) {
			graphics.setStroke(stroke);
		}
			
		graphics.fill(shape);
		graphics.setStroke(origStroke);
	}
	
	public void drawShape(Color color, Stroke stroke, Shape shape)
	{
		if (color != null) {
			graphics.setColor(color);
		}
			
		Stroke origStroke = graphics.getStroke();
			
		if (stroke != null) {
			graphics.setStroke(stroke);
		}
			
		graphics.draw(shape);
		graphics.setStroke(origStroke);
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
			mapProjection.getPoint(latitude, longitude, elevation, mapPoint);
			row = mapPoint.row;
			column = mapPoint.column;
		} catch (MapProjectionException ex) {
			throw new CanvasException("Failed to project coordinates to canvas: " + ex.getMessage(), ex);
		}
		
		int y = (int) Math.round(row - (radiusPixels / 2.0));
		int x = (int) Math.round(column - (radiusPixels / 2.0));
		
		int radius = (int) Math.round(radiusPixels);
		
		
		if (color != null) {
			graphics.setColor(color);
		}
		graphics.fillOval(x, y, radius, radius);
		
		
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
			mapProjection.getPoint(latitude, longitude, 0, mapPoint);
			y = (int) Math.round(mapPoint.row);
			x = (int) Math.round(mapPoint.column);
		} catch (Exception ex) {
			throw new CanvasException("Failed to project coordinates: " + ex.getMessage(), ex);
		}
		

		graphics.setColor(textColor);
		graphics.drawString(text, x, y);
		
	}
	
	public void drawImage(Image image, double north, double south, double east, double west) throws CanvasException
	{
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		
		try {
			mapProjection.getPoint(north, west, 0, mapPoint);
			y = (int) Math.floor(mapPoint.row);
			x = (int) Math.floor(mapPoint.column);
			
			width = image.getWidth(null);
			height = image.getHeight(null);
			
			mapProjection.getPoint(south, east, 0, mapPoint);
			//height = (int) Math.ceil(mapPoint.row - y);
			//width = (int) Math.ceil(mapPoint.column - x);
			
			//int y2 = (int) Math.ceil(mapPoint.row);
			//int x2 = (int) Math.ceil(mapPoint.column);
			
			//width = x2 - x;
			//height = y2 - y;
			
		} catch (Exception ex) {
			throw new CanvasException("Failed to project coordinates: " + ex.getMessage(), ex);
		}
		
		//BufferedImage scaled = ImageUtilities.getScaledInstance((BufferedImage)image, width, height, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
		drawImage(image, x, y, width, height);
		
	}
	
	public void drawImage(Image image, int x, int y, int width, int height)
	{
		graphics.drawImage(image, x, y, width, height, null);
	}
	
	public int getColor(int x, int y)
	{
		if (x < 0 || x >= image.getWidth(null) || y < 0 || y >= image.getHeight(null))
			return 0;
		
		// TODO: Restore pixel color fetch
		return 0;
		//return image.getRGB(x, y);
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
		BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) newImage.createGraphics();
		g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g2d.dispose();
		return newImage;
	}
	
	public Image getSubImage(double north, double south, double east, double west) throws CanvasException
	{
		
		int srcX = 0;
		int srcY = 0;
		int width = 0;
		int height = 0;
		
		try {
			mapProjection.getPoint(north, west, 0, mapPoint);
			srcY = (int) Math.floor(mapPoint.row);
			srcX = (int) Math.floor(mapPoint.column);
			
			mapProjection.getPoint(south, east, 0, mapPoint);
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
		graphics.drawImage(image,
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
		graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		
		
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
			graphics.dispose();
			graphics = null;
			image = null;
		}
	}
}
