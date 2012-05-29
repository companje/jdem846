package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsOptionModel;
import us.wthr.jdem846.scripting.ScriptProxy;


@GridProcessing(id="us.wthr.jdem846.model.processing.coloring.HypsometricColorProcessor",
					name="Hypsometric Color Process",
					type=GridProcessingTypesEnum.COLORING,
					optionModel=HypsometricColorOptionModel.class,
					enabled=true
					)
public class HypsometricColorProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(HypsometricColorProcessor.class);
	
	private ModelColoring modelColoring;
	//private ElevationScaler elevationScaler;
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	
	protected double maximumElevationTrue;
	protected double minimumElevation;
	protected double maximumElevation;
	
	private int[] rgbaBufferA = new int[4];
	private int[] rgbaBufferB = new int[4];
	
	private boolean useScripting = true;
	
	public HypsometricColorProcessor()
	{
		
	}
	
	public HypsometricColorProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}

	@Override
	public void prepare() throws RenderEngineException
	{
		HypsometricColorOptionModel optionModel = (HypsometricColorOptionModel) this.getProcessOptionModel();
		
		
		
		useScripting = getGlobalOptionModel().getUseScripting();
		
		
		north = getGlobalOptionModel().getNorthLimit();
		south = getGlobalOptionModel().getSouthLimit();
		east = getGlobalOptionModel().getEastLimit();
		west = getGlobalOptionModel().getWestLimit();
		
		
		
		minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		maximumElevationTrue = modelContext.getRasterDataContext().getDataMaximumValueTrue();
		
		
		modelColoring = ColoringRegistry.getInstance(optionModel.getColorTint()).getImpl();
		modelColoring.setElevationScaler(modelContext.getRasterDataContext().getElevationScaler());
		
		
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
		
		//ModelPoint modelPoint = modelGrid.get(latitude, longitude);
		
		double elevation = modelGrid.getElevation(latitude, longitude);
		try {
			getPointColor(latitude, longitude, elevation, rgbaBufferA);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error getting point color: " + ex.getMessage(), ex);
		}
		
		modelGrid.setRgba(latitude, longitude, rgbaBufferA);
		//modelPoint.setRgba(rgbaBufferA);

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
		
		
		boolean imageOverlayed = false;
		if (modelContext.getImageDataContext() != null
				&& modelContext.getImageDataContext().getColor(latitude, longitude, rgba)) {
			imageOverlayed = true;
		} 
		
		if (imageOverlayed && rgba[3] < 0xFF) {
			modelColoring.getGradientColor(elevation, minimumElevation, maximumElevation, rgbaBufferB);
			double r = ((double)rgba[3] / 255.0);
			ColorAdjustments.interpolateColor(rgbaBufferB, rgba, rgba, r);
		}
		
		if (!imageOverlayed) {
			modelColoring.getGradientColor(elevation, minimumElevation, maximumElevation, rgba);
		}
		
		if (useScripting) {
			onGetPointColor(latitude, longitude, elevation, minimumElevation, maximumElevation, rgba);
		}
	}
	
	protected void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			if (scriptProxy != null) {
				scriptProxy.onGetPointColor(modelContext, latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

	}

	public boolean useScripting()
	{
		return useScripting;
	}

	public void setUseScripting(boolean useScripting)
	{
		this.useScripting = useScripting;
	}
	
	
	
}
