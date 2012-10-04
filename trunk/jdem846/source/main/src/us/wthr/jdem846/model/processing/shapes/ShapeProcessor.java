package us.wthr.jdem846.model.processing.shapes;

import java.util.List;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.CanvasProjectionFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.gis.projections.MapProjectionProviderFactory;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.InterruptibleProcess;
import us.wthr.jdem846.render.shapelayer.ShapeFill;
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


@GridProcessing(id="us.wthr.jdem846.model.processing.shapes.ShapeProcessor",
				name="Shape Rendering Process",
				type=GridProcessingTypesEnum.SHAPES,
				optionModel=ShapeOptionModel.class,
				enabled=true
)
public class ShapeProcessor extends GridProcessor
{
	private static Log log = Logging.getLog(ShapeProcessor.class);
	
	private ShapeOptionModel optionModel;


	private MapProjection mapProjection;
	private CanvasProjection canvasProjection;
	
	private boolean processing = false;
	
	public ShapeProcessor()
	{
		
	}
	







	@Override
	public void prepare() throws RenderEngineException
	{
		log.info("Preparing Shape Rendering Process");
		
		
		try {
			mapProjection = MapProjectionProviderFactory.getMapProjection("us.wthr.jdem846.render.mapProjection.equirectangularProjection.name", 
					globalOptionModel.getNorthLimit(), 
					globalOptionModel.getSouthLimit(), 
					globalOptionModel.getEastLimit(), 
					globalOptionModel.getWestLimit(), 
					globalOptionModel.getWidth(), 
					globalOptionModel.getHeight());
		} catch (MapProjectionException ex) {
			log.warn("Error creating map projection: " + ex.getMessage(), ex);
		}
		
		
		canvasProjection = CanvasProjectionFactory.create( 
				CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(globalOptionModel.getRenderProjection()),
				mapProjection,
				globalOptionModel.getNorthLimit(),
				globalOptionModel.getSouthLimit(),
				globalOptionModel.getEastLimit(),
				globalOptionModel.getWestLimit(),
				modelDimensions.getOutputWidth(),
				modelDimensions.getOutputHeight(),
				PlanetsRegistry.getPlanet(globalOptionModel.getPlanet()),
				globalOptionModel.getElevationMultiple(),
				modelContext.getRasterDataContext().getDataMinimumValue(),
				modelContext.getRasterDataContext().getDataMaximumValue(),
				(ModelDimensions) modelDimensions,
				globalOptionModel.getViewAngle());
	}


	
	public void process() throws RenderEngineException
	{
		log.info("Running Shape Rendering Process");
		//if (isCancelled()) {
		//	log.warn("Render process cancelled, model not complete.");
		//	return;
		//}
		
		
		
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
			} catch(Exception ex) {
				throw new RenderEngineException("Error occured rendering shape files", ex);
			}
			
			//checkPause();
			//if (isCancelled()) {
			//	log.warn("Render process cancelled, model not complete.");
			//	break;
			//}
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
		shapeLayer.translate(new PointTranslateHandler() {
			public void translatePoint(double[] coords)
			{
				try {
					ModelPoint modelPoint = modelGrid.get(coords[1], coords[0]);
					double elevation = 0;
					if (modelPoint != null) {
						elevation = modelPoint.getElevation();
					}
					
					canvasProjection.getPoint(coords[1], coords[0], elevation, mapPoint);
					coords[0] = (int)mapPoint.column;
					coords[1] = (int)mapPoint.row;
					coords[2] = (int)mapPoint.z + 10.0;
					
				} catch (MapProjectionException ex) {
					log.warn("Error projecting coordinates to map: " + ex.getMessage(), ex);
				} 
			}
		}, false);
		
		log.info("Combining shapes...");
		
		//shapeLayer = shapeLayer.getCombinedPathsByTypes();
		return shapeLayer;
	}
	

	
	public void renderLayer(ShapeLayer shapeLayer) throws RenderEngineException
	{
		int shapeType = shapeLayer.getType();
		log.info("Shape Type: " + shapeType);

		for (ShapePath path : shapeLayer.getShapePaths()) {		
			renderPath(shapeType, path);
			
			//checkPause();
			//if (isCancelled()) {
			//	log.warn("Render process cancelled, model not complete.");
			//	break;
			//}
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
			
			lineStroke.getColor(color);
			color[3] = 255;
			
			boolean fill = (shapeType == ShapeConstants.TYPE_POLYGON ||
					shapeType == ShapeConstants.TYPE_POLYGONM ||
					shapeType == ShapeConstants.TYPE_POLYGONZ);
			
			ShapeFill shapeFill = new ShapeFill(color, shapeType, path, lineStroke, fill);
			try {
				shapeFill.fill(modelContext.getModelCanvas());
			} catch (CanvasException ex) {
				throw new RenderEngineException("Shape fill error: " + shapeType);
			} catch (ModelContextException ex) {
				throw new RenderEngineException("Error fetching model canvas: " + ex.getMessage(), ex);
			}

		}	
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
	public void onLatitudeStart(double latitude) throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLatitudeEnd(double latitude) throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProcessBefore() throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
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
