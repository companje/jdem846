package us.wthr.jdem846.model.processing.render;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.Projection;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointAdapter;
import us.wthr.jdem846.model.ModelPointCycler;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.CanvasProjectionFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.canvas.ModelCanvas;


@GridProcessing(id="us.wthr.jdem846.model.processing.render.ModelRenderer",
				name="Rendering Process",
				type=GridProcessingTypesEnum.RENDER,
				optionModel=ModelRenderOptionModel.class,
				enabled=true
				)
public class ModelRenderer extends AbstractGridProcessor implements GridProcessor
{
	private static Log log = Logging.getLog(ModelRenderer.class);
	
	private double latitudeResolution;

	
	private double south;
	
	protected CanvasProjection projection;
	protected MapPoint point = new MapPoint();
	
	private int[] rgbaBuffer = new int[4];
	
	TriangleStrip strip = null;
	double lastElevation = 0;
	ModelCanvas canvas;
	
	public ModelRenderer()
	{
		
	}
	
	
	public ModelRenderer(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);

	}
	
	public void dispose()
	{
		
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		ModelRenderOptionModel optionModel = (ModelRenderOptionModel) this.getProcessOptionModel();
		
		latitudeResolution = getModelDimensions().getOutputLatitudeResolution();
		
		projection = CanvasProjectionFactory.create( 
				CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(globalOptionModel.getRenderProjection()),
				modelContext.getMapProjection(),
				modelContext.getNorth(),
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelDimensions.getOutputWidth(),
				modelDimensions.getOutputHeight(),
				PlanetsRegistry.getPlanet(globalOptionModel.getPlanet()),
				getGlobalOptionModel().getElevationMultiple(),
				modelContext.getRasterDataContext().getDataMinimumValue(),
				modelContext.getRasterDataContext().getDataMaximumValue(),
				(ModelDimensions) modelDimensions,
				optionModel.getViewAngle());
		
		modelContext.getModelCanvas().setCanvasProjection(projection);
		//projection = modelContext.getModelCanvas().getCanvasProjection();
		south = getGlobalOptionModel().getSouthLimit();
	}
	
	public void process() throws RenderEngineException
	{
		if (!this.modelContainsData()) {
			log.info("Model contains no data. Skipping render process.");
			return;
		}
		
		canvas = modelContext.getModelCanvas();
		
		super.process();
		
	}
	
		
	@Override
	public void onCycleStart() throws RenderEngineException
	{
		lastElevation = modelContext.getRasterDataContext().getDataMaximumValue();
	}

	@Override
	public void onModelLatitudeStart(double latitude)
	{
		strip = new TriangleStrip();
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
	{
		
		try {
			lastElevation = createPointVertexes(strip, latitude, longitude, lastElevation);
		} catch (Exception ex) {
			log.error("Error creating vertexes: " + ex.getMessage(), ex);
		}

	}

	@Override
	public void onModelLatitudeEnd(double latitude)
	{
		canvas.fillShape(strip);
	}
		

	
	protected double createPointVertexes(TriangleStrip strip, double latitude, double longitude, double lastElevation) throws Exception
	{
		ModelPoint nwPoint = modelGrid.get(latitude, longitude);
		double nwElev = DemConstants.ELEV_NO_DATA;
		if (nwPoint != null) {
			nwElev = nwPoint.getElevation();
			nwPoint.getRgba(rgbaBuffer, true);
		}
		
		if (nwElev == DemConstants.ELEV_NO_DATA){
			nwElev = lastElevation;
		}

		Vertex nwVtx = createVertex(latitude, longitude, nwElev, rgbaBuffer);
		
		
		double swLat = latitude - latitudeResolution;
		if (swLat < south)
			swLat = south;
		
		ModelPoint swPoint = modelGrid.get(swLat, longitude);
		double swElev = DemConstants.ELEV_NO_DATA;
		if (swPoint != null) {
			swElev = swPoint.getElevation();
			swPoint.getRgba(rgbaBuffer, true);
		} 
		
		if (swElev == DemConstants.ELEV_NO_DATA){
			swElev = lastElevation;
		}

		
		Vertex swVtx = createVertex(latitude - latitudeResolution, longitude, swElev, rgbaBuffer);
		
		strip.addVertex(nwVtx);
		strip.addVertex(swVtx);
		
		return nwElev;
	}
	
	
	protected Vertex createVertex(double lat, double lon, double elev, int[] rgba) throws MapProjectionException
	{
    	projection.getPoint(lat, lon, elev, point);
    	
    	double x = point.column;
    	double y = point.row;
    	double z = point.z;
		
    	Vertex v = new Vertex(x, y, z, rgba);
    	return v;
	}



	




	
	
}
