package us.wthr.jdem846.model.processing.shapes;

import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.CanvasProjectionFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.gis.projections.MapProjectionProviderFactory;
import us.wthr.jdem846.graphics.FlatView;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.graphics.ImmutableColor;
import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridFilter;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.PointTranslateHandler;
import us.wthr.jdem846.shapefile.Shape;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.ShapeConstants;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.ShapeLayer;
import us.wthr.jdem846.shapefile.ShapePath;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.shapefile.modeling.FeatureTypeStroke;
import us.wthr.jdem846.shapefile.modeling.LineStroke;
import us.wthr.jdem846.util.ColorIntFormatEnum;
import us.wthr.jdem846.util.ColorUtil;

@GridProcessing(id = "us.wthr.jdem846.model.processing.shapes.ShapeProcessor"
			, name = "Shape Rendering Process"
			, type = GridProcessingTypesEnum.SHAPES
			, optionModel = ShapeOptionModel.class
			, isFilter = true
			, enabled = true)
public class ShapeProcessor extends GridFilter
{
	private static Log log = Logging.getLog(ShapeProcessor.class);

	private MapProjection mapProjection;
	private CanvasProjection canvasProjection;

	private ShapeRenderCanvas canvas;

	private View canvasView;
	
	private BufferedImage shapeImage;
	
	public ShapeProcessor()
	{

	}

	@Override
	public void prepare() throws RenderEngineException
	{
		log.info("Preparing Shape Rendering Process");

		try {
			mapProjection = MapProjectionProviderFactory.getMapProjection("us.wthr.jdem846.render.mapProjection.equirectangularProjection.name", globalOptionModel.getNorthLimit(), globalOptionModel.getSouthLimit(), globalOptionModel.getEastLimit(),
					globalOptionModel.getWestLimit(), globalOptionModel.getWidth(), globalOptionModel.getHeight());
		} catch (MapProjectionException ex) {
			log.warn("Error creating map projection: " + ex.getMessage(), ex);
		}

		
		canvasView = new FlatView();
		canvasView.setModelContext(modelContext);
		canvasView.setGlobalOptionModel(globalOptionModel);
		canvasView.setModelDimensions(modelDimensions);
		canvasView.setMapProjection(mapProjection);
		canvasView.setModelGrid(modelGrid);

		canvasProjection = CanvasProjectionFactory.create(CanvasProjectionTypeEnum.PROJECT_FLAT, mapProjection, globalOptionModel.getNorthLimit(), globalOptionModel.getSouthLimit(), globalOptionModel.getEastLimit(), globalOptionModel.getWestLimit(),
				modelDimensions.getOutputWidth(), modelDimensions.getOutputHeight(), PlanetsRegistry.getPlanet(globalOptionModel.getPlanet()), globalOptionModel.getElevationMultiple(), modelContext.getRasterDataContext().getDataMinimumValue(),
				modelContext.getRasterDataContext().getDataMaximumValue(), (ModelDimensions) modelDimensions, globalOptionModel.getViewAngle());

		
		BufferedImage canvasBufferImage = new BufferedImage(globalOptionModel.getWidth(), globalOptionModel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		canvas = new ShapeRenderCanvas(modelContext, canvasBufferImage);
		
		process();
		
		shapeImage = (BufferedImage) canvas.getFinalizedImage();
		try {
			ImageWriter.saveImage(shapeImage, "C:/jdem/temp/shapecanvas.png");
		} catch (ImageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void process() throws RenderEngineException
	{
		log.info("Running Shape Rendering Process");
		// if (isCancelled()) {
		// log.warn("Render process cancelled, model not complete.");
		// return;
		// }

		int numLayers = getShapeDataContext().getShapeDataListSize();
		int layerNumber = 0;

		for (ShapeFileRequest shapeFilePath : getShapeDataContext().getShapeFiles()) {
			layerNumber++;
			try {
				log.info("Loading shapefile from " + shapeFilePath.getPath());

				ShapeBase shapeBase = shapeFilePath.open();
				ShapeLayer shapeLayer = buildShapeLayer(shapeBase);
				shapeBase.close();

				renderLayer(shapeLayer);

			} catch (OutOfMemoryError err) {
				throw err;
			} catch (Exception ex) {
				throw new RenderEngineException("Error occured rendering shape files", ex);
			}

			// checkPause();
			// if (isCancelled()) {
			// log.warn("Render process cancelled, model not complete.");
			// break;
			// }
		}

		log.info("Completed shapefile rendering");

	}

	public ShapeLayer buildShapeLayer(ShapeBase shapeBase) throws RenderEngineException, ShapeFileException
	{
		ShapeLayer shapeLayer = new ShapeLayer(shapeBase.getShapeType());

		log.info("Loading " + shapeBase.getShapeCount() + " shapes");
		for (int i = 0; i < shapeBase.getShapeCount(); i++) {
			Shape shape = shapeBase.getShape(i);
			shapeLayer.addShape(shape);
		}

		log.info("Translating coordinates...");
		final MapPoint mapPoint = new MapPoint();
		shapeLayer.translate(new PointTranslateHandler()
		{
			private Vector vector = new Vector();
			
			public void translatePoint(double[] coords)
			{
				//try {

					double elevation = 0;//modelGrid.getElevation(coords[1], coords[0], true);
					
					try {
						canvasProjection.getPoint(coords[1], coords[0], elevation, mapPoint);
					} catch (MapProjectionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					canvasView.project(coords[1], coords[0], elevation, vector);
					if (elevation != DemConstants.ELEV_NO_DATA) {
						int i = 0;
					}
					//coords[0] = vector.x;
					//coords[1] = vector.z;
					//coords[2] = vector.y;
                    coords[0] = mapPoint.column;
                    coords[1] = mapPoint.row;
				//} catch (MapProjectionException ex) {
				//	log.warn("Error projecting coordinates to map: " + ex.getMessage(), ex);
				//}
			}
		}, false);

		log.info("Combining shapes...");

		// shapeLayer = shapeLayer.getCombinedPathsByTypes();
		return shapeLayer;
	}

	public void renderLayer(ShapeLayer shapeLayer) throws RenderEngineException
	{
		int shapeType = shapeLayer.getType();
		log.info("Shape Type: " + shapeType);

		for (ShapePath path : shapeLayer.getShapePaths()) {
			renderPath(shapeType, path);

			// checkPause();
			// if (isCancelled()) {
			// log.warn("Render process cancelled, model not complete.");
			// break;
			// }
		}
	}

	protected void renderPath(int shapeType, ShapePath path) throws RenderEngineException
	{

		FeatureTypeStroke featureStroke = null;

		if (path.getFeatureType() != null) {
			featureStroke = path.getFeatureType().getFeatureTypeStroke();
		}

		if (featureStroke == null) {
			featureStroke = FeatureTypeStroke.getDefaultFeatureTypeStroke();
		}

		List<LineStroke> lineStrokes = featureStroke.getLineStrokes();

		int[] color = new int[4];

		for (LineStroke lineStroke : lineStrokes) {
			
			
			if (shapeType == ShapeConstants.TYPE_POLYGON ||
                    shapeType == ShapeConstants.TYPE_POLYGONM ||
                    shapeType == ShapeConstants.TYPE_POLYGONZ) {
                      
				canvas.fillShape(lineStroke.getColor(), (Stroke) lineStroke, shapePathToAwtShape(path, true));
                   //canvas.fillShape(Color.RED, (Stroke) lineStroke, shapePathToAwtShape(path));

            } else if (shapeType == ShapeConstants.TYPE_POLYLINE ||
                                    shapeType == ShapeConstants.TYPE_POLYLINEM ||
                                    shapeType == ShapeConstants.TYPE_POLYLINEZ) {
                    canvas.drawShape(lineStroke.getColor(),  (Stroke) lineStroke, shapePathToAwtShape(path, false));
            	//canvas.drawShape(Color.RED, (Stroke) lineStroke, shapePathToAwtShape(path));

            } else {
                    throw new RenderEngineException("Unsupported shape type: " + shapeType);
            }
			
//			lineStroke.getColor(color);
//			color[3] = 255;
//
//			boolean fill = (shapeType == ShapeConstants.TYPE_POLYGON || shapeType == ShapeConstants.TYPE_POLYGONM || shapeType == ShapeConstants.TYPE_POLYGONZ);
//			
//			canvas.fillShape(color, stroke, shape)
			//ShapeFill shapeFill = new ShapeFill(color, shapeType, path, lineStroke, fill);
			//try {
			//	shapeFill.fill(canvas);
			//} catch (CanvasException ex) {
			//	throw new RenderEngineException("Shape fill error: " + shapeType);
			//} catch (ModelContextException ex) {
			//	throw new RenderEngineException("Error fetching model canvas: " + ex.getMessage(), ex);
			//}

		}
	}
	
	protected java.awt.Shape shapePathToAwtShape(ShapePath shapePath, boolean closePath)
	{
		java.awt.geom.Path2D shape = new java.awt.geom.Path2D.Double();
		int i = 0;
		for (Edge edge : shapePath.getEdges()) {
			if (i == 0) {
				shape.moveTo(edge.p0.x(), edge.p0.y());
			}
			shape.lineTo(edge.p1.x(), edge.p1.y());
			i++;
		}
		
		if (closePath) {
			shape.closePath();
		}
		return shape;
	}

	protected MapProjection getMapProjection()
	{
		return mapProjection;
	}

	protected RasterDataContext getRasterDataContext()
	{
		return modelContext.getRasterDataContext();
	}

	protected ShapeDataContext getShapeDataContext()
	{
		return modelContext.getShapeDataContext();
	}



	@Override
	public void onProcessBefore() throws RenderEngineException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
	{
		
		IColor color = getCanvasColor(latitude, longitude);
		if (color != null) {
			
			IColor gridColor = modelGrid.getRgba(latitude, longitude);
			
			color = ColorUtil.overlayColor(color, gridColor);
			
			modelGrid.setRgba(latitude, longitude, color);
		}
		


	}
	
	
	protected IColor getCanvasColor(double latitude, double longitude) throws RenderEngineException
	{
		double elevation = modelGrid.getElevation(latitude, longitude, true);
		
		MapPoint mapPoint = new MapPoint();
		try {
			canvasProjection.getPoint(latitude, longitude, elevation, mapPoint);
		} catch (MapProjectionException ex) {
			throw new RenderEngineException("Error projecting point: " + ex.getMessage(), ex);
		}
		
		if (shapeImage != null && mapPoint.row >= 0 && mapPoint.row < globalOptionModel.getHeight() && mapPoint.column >= 0 && mapPoint.column < globalOptionModel.getWidth()) {
			int c = shapeImage.getRGB((int)MathExt.round(mapPoint.column), (int)MathExt.round(mapPoint.row));
			
			int[] rgba = new int[4];
			ColorUtil.intToRGBA(c, rgba, ColorIntFormatEnum.ARGB);
			
			if (c != 0x0) {
				return new ImmutableColor(rgba[0], rgba[1], rgba[2], rgba[3]);
			}
		}
		
		return null;
	}

	@Override
	public void onProcessAfter() throws RenderEngineException
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() throws RenderEngineException
	{
		// TODO Auto-generated method stub

	}
}
