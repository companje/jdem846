package us.wthr.jdem846.render.render3;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.scaling.ElevationScaler;
import us.wthr.jdem846.render.scaling.ElevationScalerFactory;
import us.wthr.jdem846.scripting.ScriptProxy;

public class GridColorProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	private static Log log = Logging.getLog(GridColorProcessor.class);
	
	private ModelColoring modelColoring;
	private ElevationScaler elevationScaler;
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	
	protected double maximumElevationTrue;
	protected double minimumElevation;
	protected double maximumElevation;
	
	private int[] rgbaBuffer = new int[4];
	
	private boolean useScripting = true;
	
	public GridColorProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}

	@Override
	public void prepare() throws RenderEngineException
	{

		north = modelContext.getNorth();
		south = modelContext.getSouth();
		east = modelContext.getEast();
		west = modelContext.getWest();
		
		
		
		minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		maximumElevationTrue = modelContext.getRasterDataContext().getDataMaximumValueTrue();
		
		
		try {
			elevationScaler = ElevationScalerFactory.createElevationScaler(modelContext.getModelOptions().getElevationScaler(), modelContext.getModelOptions().getElevationMultiple(), minimumElevation, maximumElevationTrue);
		} catch (Exception ex) {
			throw new RenderEngineException("Error creating elevation scaler: " + ex.getMessage(), ex);
		}
		modelContext.getRasterDataContext().setElevationScaler(elevationScaler);
		maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		
		modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		modelColoring.setElevationScaler(elevationScaler);
		
	}

	@Override
	public void process() throws RenderEngineException
	{
		super.process();
	}
	
	
	
	@Override
	public void onCycleStart() throws RenderEngineException
	{
		
	}

	@Override
	public void onModelLatitudeStart(double latitude)
			throws RenderEngineException
	{
		
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		try {
			getPointColor(latitude, longitude, modelPoint.getElevation(), rgbaBuffer);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error getting point color: " + ex.getMessage(), ex);
		}
		
		modelPoint.setRgba(rgbaBuffer);

	}

	@Override
	public void onModelLatitudeEnd(double latitude)
			throws RenderEngineException
	{
		
	}

	@Override
	public void onCycleEnd() throws RenderEngineException
	{
		
	}

	protected void getPointColor(double latitude, double longitude, double elevation, int[] rgba) throws DataSourceException, RenderEngineException
	{
		
		if (modelContext.getImageDataContext() != null
				&& modelContext.getImageDataContext().getColor(latitude, longitude, rgba)) {
			// All right, then
		} else {
			modelColoring.getGradientColor(elevation, minimumElevation, maximumElevation, rgba);
		}
		
		if (useScripting) {
			onGetPointColor(latitude, longitude, elevation, minimumElevation, maximumElevation, rgba);
		}
	}
	
	protected void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onGetPointColor(modelContext, latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

	}
	
}
