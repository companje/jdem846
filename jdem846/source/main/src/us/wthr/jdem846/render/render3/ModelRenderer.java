package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.canvas.CanvasProjection;
import us.wthr.jdem846.canvas.ModelCanvas;

public class ModelRenderer extends AbstractGridProcessor implements GridProcessor
{
	private static Log log = Logging.getLog(ModelRenderer.class);
	
	private double latitudeResolution;

	
	private double south;
	
	protected CanvasProjection projection;
	protected MapPoint point = new MapPoint();
	protected ModelCanvas canvas;
	
	private int[] rgbaBuffer = new int[4];
	
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
		latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();

		try {
			projection = modelContext.getModelCanvas().getCanvasProjection();
		} catch (ModelContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		south = modelContext.getSouth();
	}
	
	public void process() throws RenderEngineException
	{
		if (!this.modelContainsData()) {
			log.info("Model contains no data. Skipping render process.");
			return;
		}
		
		log.info("Starting model render process");
		
		ModelPointCycler pointCycler = new ModelPointCycler(modelContext);
		
		try {
			canvas = modelContext.getModelCanvas();
		} catch (ModelContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		pointCycler.forEachModelPoint(new ModelPointAdapter() {
			
			TriangleStrip strip = null;
			double lastElevation = 0;
			
			
			
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
			
		});
		
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
