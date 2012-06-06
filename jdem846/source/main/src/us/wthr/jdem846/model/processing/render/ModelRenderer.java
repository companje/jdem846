package us.wthr.jdem846.model.processing.render;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.Projection;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.GeoTriangleStrip;
import us.wthr.jdem846.geom.GeoVertex;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.gis.projections.MapProjectionProviderFactory;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
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
	
	private ModelRenderOptionModel optionModel;
	
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
		optionModel = (ModelRenderOptionModel) this.getProcessOptionModel();
		GlobalOptionModel globalOptionModel = modelContext.getModelProcessManifest().getGlobalOptionModel();
		
		latitudeResolution = getModelDimensions().getOutputLatitudeResolution();
		
		MapProjection mapProjection = null;
		
		try {
			mapProjection = MapProjectionProviderFactory.getMapProjection(optionModel.getMapProjection(), 
					getGlobalOptionModel().getNorthLimit(), 
					getGlobalOptionModel().getSouthLimit(), 
					getGlobalOptionModel().getEastLimit(), 
					getGlobalOptionModel().getWestLimit(), 
					getGlobalOptionModel().getWidth(), 
					getGlobalOptionModel().getHeight());
		} catch (MapProjectionException ex) {
			log.warn("Error creating map projection: " + ex.getMessage(), ex);
		}

		projection = CanvasProjectionFactory.create( 
				CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(globalOptionModel.getRenderProjection()),
				mapProjection,
				getGlobalOptionModel().getNorthLimit(),
				getGlobalOptionModel().getSouthLimit(),
				getGlobalOptionModel().getEastLimit(),
				getGlobalOptionModel().getWestLimit(),
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
		strip = new GeoTriangleStrip();
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
	{
		
		try {
			double elev = createPointVertexes(strip, latitude, longitude, lastElevation);
			
			if (elev != DemConstants.ELEV_NO_DATA) {
				lastElevation = elev;
			} else {
				canvas.fillShape(strip);
				strip.reset();
			}
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
		
		double nwElev = createPointVertex(strip, latitude, longitude, lastElevation);
		double swElev = createPointVertex(strip, latitude - latitudeResolution, longitude, nwElev);
		
		return (nwElev != DemConstants.ELEV_NO_DATA) ? nwElev : swElev;
	}
	
	
	protected double createPointVertex(TriangleStrip strip, double latitude, double longitude, double lastElevation) throws Exception
	{

		double elev = modelGrid.getElevation(latitude, longitude);
		
		if (elev == DemConstants.ELEV_NO_DATA) {
			return DemConstants.ELEV_NO_DATA;
		}
		
		modelGrid.getRgba(latitude, longitude, rgbaBuffer);
		
		if (rgbaBuffer[3] > optionModel.getForceAlpha()) {
			rgbaBuffer[3] = optionModel.getForceAlpha();
		}
		
		Vertex nwVtx = createVertex(latitude, longitude, elev, rgbaBuffer);
		
		strip.addVertex(nwVtx);
		
		return elev;
	}
	
	
	protected Vertex createVertex(double lat, double lon, double elev, int[] rgba) throws MapProjectionException
	{
    	projection.getPoint(lat, lon, elev, point);
    	
    	double x = point.column;
    	double y = point.row;
    	double z = point.z;
		
    	Vertex v = new GeoVertex(x, y, z, rgba, lat, lon, elev);
    	return v;
	}



	




	
	
}
