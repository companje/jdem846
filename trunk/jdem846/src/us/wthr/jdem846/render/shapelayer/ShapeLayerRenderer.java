package us.wthr.jdem846.render.shapelayer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.PointTranslateHandler;
import us.wthr.jdem846.shapefile.Shape;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.ShapeConstants;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.ShapeLayer;
import us.wthr.jdem846.shapefile.ShapePath;
import us.wthr.jdem846.shapefile.modeling.FeatureTypeStroke;
import us.wthr.jdem846.shapefile.modeling.LineStroke;

public class ShapeLayerRenderer
{
	private static Log log = Logging.getLog(ShapeLayerRenderer.class);
	
	private ModelContext modelContext;
	private List<TileCompletionListener> tileCompletionListeners;
	private DemCanvas canvas;
	private boolean cancel = false;
	
	
	public ShapeLayerRenderer(ModelContext modelContext, DemCanvas canvas, List<TileCompletionListener> tileCompletionListeners)
	{
		this.modelContext = modelContext;
		this.canvas = canvas;
		this.tileCompletionListeners = tileCompletionListeners;
	}
	
	public void renderShapeLayer() throws RenderEngineException
	{
		if (isCancelled()) {
			log.warn("Render process cancelled, model not complete.");
			return;
		}
		
		//int numLayers = getDataPackage().getShapeFiles().size();
		int numLayers = getShapeDataContext().getShapeDataListSize();
		int layerNumber = 0;
		
		for (ShapeFileRequest shapeFilePath : getShapeDataContext().getShapeFiles()) {
			layerNumber++;
			try {
				log.info("Loading shapefile from " + shapeFilePath.getPath());
				//ShapeBase shapeBase = new ShapeBase(shapeFilePath.getPath(), shapeFilePath.getShapeDataDefinitionId());
				ShapeBase shapeBase = shapeFilePath.open();
				ShapeLayer shapeLayer = new ShapeLayer(shapeBase.getShapeType());
				
				log.info("Loading " + shapeBase.getShapeCount() + " shapes");
				for (int i = 0; i < shapeBase.getShapeCount(); i++) {
					Shape shape = shapeBase.getShape(i);
					shapeLayer.addShape(shape);
				}
				
				shapeLayer.translate(new PointTranslateHandler() {
					public void translatePoint(double[] coords)
					{
						double x = getRasterDataContext().longitudeToColumn((float) coords[0]);
						double y = getRasterDataContext().latitudeToRow((float) coords[1]);
						coords[0] = x;
						coords[1] = y;
					}
				}, false);
				
				shapeLayer = shapeLayer.getCombinedPathsByTypes();
				
				Image layerImage = renderLayer(shapeLayer);
				layerImage = layerImage.getScaledInstance(canvas.getWidth(), canvas.getHeight(), Image.SCALE_SMOOTH);
				canvas.overlay(layerImage, 0, 0);
				
				shapeBase.close();
				fireTileCompletionListeners(canvas, canvas, ((double)layerNumber) / ((double)numLayers));
				
			} catch (OutOfMemoryError err) {
				throw err;
			} catch(Exception ex) {
				throw new RenderEngineException("Error occured rendering shape files", ex);
				//ex.printStackTrace();
			}
			
			if (isCancelled()) {
				log.warn("Render process cancelled, model not complete.");
				break;
			}
		}
		
		log.info("Completed shapefile rendering");
		
	}

	public Image renderLayer(ShapeLayer shapeLayer) throws RenderEngineException
	{
		BufferedImage image = new BufferedImage((int)getRasterDataContext().getDataColumns(), (int)getRasterDataContext().getDataRows(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int shapeType = shapeLayer.getType();
		log.info("Shape Type: " + shapeType);
		fillShapes(shapeLayer, g2d);

		g2d.dispose();
		return image;

	}

	protected void fillShapes(ShapeLayer shapeLayer, Graphics2D g2d) throws RenderEngineException
	{
		log.info("Creating shape paths");

		log.info("Drawing " + shapeLayer.size() + " polylines");
		int shapeType = shapeLayer.getType();

		for (ShapePath path : shapeLayer.getShapePaths()) {		
			
			FeatureTypeStroke featureStroke = null;
			
			if (path.getFeatureType() != null) {
				featureStroke = path.getFeatureType().getFeatureTypeStroke();
			}
			
			if (featureStroke == null) {
				featureStroke = FeatureTypeStroke.getDefaultFeatureTypeStroke();
			}
			
			List<LineStroke> lineStrokes = featureStroke.getLineStrokes();

			for (LineStroke lineStroke : lineStrokes) {
			
				g2d.setStroke(lineStroke);
				g2d.setColor(lineStroke.getColor());
				
				if (shapeType == ShapeConstants.TYPE_POLYGON ||
						shapeType == ShapeConstants.TYPE_POLYGONM ||
						shapeType == ShapeConstants.TYPE_POLYGONZ) {
					
					g2d.fill(path);
					
				} else if (shapeType == ShapeConstants.TYPE_POLYLINE ||
							shapeType == ShapeConstants.TYPE_POLYLINEM ||
							shapeType == ShapeConstants.TYPE_POLYLINEZ) {
					
					g2d.draw(path);

				}					
			}	
			if (isCancelled()) {
				log.warn("Render process cancelled, model not complete.");
				break;
			}
		}

	}
	
	protected RasterDataContext getRasterDataContext()
	{
		return modelContext.getRasterDataContext();
	}
	
	protected ShapeDataContext getShapeDataContext()
	{
		return modelContext.getShapeDataContext();
	}
	
	protected ModelOptions getModelOptions()
	{
		return modelContext.getModelOptions();
	}
	
	
	/** Requests that a rendering process is stopped.
	 * 
	 */
	public void cancel()
	{
		this.cancel = true;
	}
	
	/** Determines whether the rendering process has been requested to stop. This does not necessarily mean
	 * that the process <i>has</i> stopped as engine implementations need not check this value that often or
	 * at all.
	 * 
	 * @return Whether the rendering process has been requested to stop.
	 */
	public boolean isCancelled()
	{
		return cancel;
	}
	
	protected void fireTileCompletionListeners(DemCanvas tileCanvas, DemCanvas outputCanvas, double pctComplete)
	{
		if (tileCompletionListeners != null) {
			for (TileCompletionListener listener : tileCompletionListeners) {
				listener.onTileCompleted(tileCanvas, outputCanvas, pctComplete);
			}
		}
	}
	
	
	public static void render(ModelContext modelContext, DemCanvas canvas) throws RenderEngineException
	{
		ShapeLayerRenderer.render(modelContext, canvas, null);
	}
	
	public static void render(ModelContext modelContext, DemCanvas canvas, List<TileCompletionListener> tileCompletionListeners) throws RenderEngineException
	{
		ShapeLayerRenderer renderer = new ShapeLayerRenderer(modelContext, canvas, tileCompletionListeners);
		renderer.renderShapeLayer();
	}
}
