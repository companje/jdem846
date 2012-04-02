package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.CanvasProjection;
import us.wthr.jdem846.render.ModelCanvas;

public class ModelRenderer
{
	private static Log log = Logging.getLog(ModelRenderer.class);
	
	private ModelContext modelContext;
	private ModelGrid modelGrid;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	protected CanvasProjection projection;
	protected MapPoint point = new MapPoint();
	
	private int[] rgbaBuffer = new int[4];
	
	public ModelRenderer(ModelContext modelContext, ModelGrid modelGrid)
	{
		this.modelContext = modelContext;
		this.modelGrid = modelGrid;
		
		latitudeResolution = modelContext.getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = modelContext.getModelDimensions().getOutputLongitudeResolution();
		
		
		projection = modelContext.getModelCanvas().getCanvasProjection();
	}
	
	
	
	public void process()
	{
		ModelPointCycler pointCycler = new ModelPointCycler(modelContext);
		
		final ModelCanvas canvas = modelContext.getModelCanvas();
		
		
		pointCycler.forEachModelPoint(false, new ModelPointHandler() {
			
			TriangleStrip strip = null;
			double lastElevation = 0;
			
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
		double nwElev = 0;
		if (nwPoint != null) {
			nwElev = nwPoint.getElevation();
			nwPoint.getRgba(rgbaBuffer);
		} else {
			nwElev = lastElevation;
		}
		Vertex nwVtx = createVertex(latitude, longitude, nwElev, rgbaBuffer);
		
		
		ModelPoint swPoint = modelGrid.get(latitude - latitudeResolution, longitude);
		double swElev = 0;
		if (swPoint != null) {
			swElev = swPoint.getElevation();
			swPoint.getRgba(rgbaBuffer);
		} else {
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
