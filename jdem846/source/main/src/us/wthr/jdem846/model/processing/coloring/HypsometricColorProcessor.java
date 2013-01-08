package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridFilter;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.util.ColorUtil;

@GridProcessing(id = "us.wthr.jdem846.model.processing.coloring.HypsometricColorProcessor", name = "Hypsometric Color Process", type = GridProcessingTypesEnum.COLORING, optionModel = HypsometricColorOptionModel.class, enabled = true, isFilter = true)
public class HypsometricColorProcessor extends GridFilter
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(HypsometricColorProcessor.class);

	private ModelColoring modelColoring;
	// private ElevationScaler elevationScaler;

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

	protected boolean nearestNeighbor = false;

	public HypsometricColorProcessor()
	{

	}

	@Override
	public void prepare() throws RenderEngineException
	{
		HypsometricColorOptionModel optionModel = (HypsometricColorOptionModel) this.getOptionModel();

		useScripting = getGlobalOptionModel().getUseScripting();

		north = getGlobalOptionModel().getNorthLimit();
		south = getGlobalOptionModel().getSouthLimit();
		east = getGlobalOptionModel().getEastLimit();
		west = getGlobalOptionModel().getWestLimit();

		minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		maximumElevationTrue = modelContext.getRasterDataContext().getDataMaximumValueTrue();

		try {
			modelColoring = ColoringRegistry.getInstance(optionModel.getColorTint()).getImpl().copy();
		} catch (Exception ex) {
			throw new RenderEngineException("Failed to create coloring instance copy: " + ex.getMessage(), ex);
		}
		modelColoring.setElevationScaler(modelContext.getRasterDataContext().getElevationScaler());

		nearestNeighbor = getGlobalOptionModel().getStandardResolutionElevation();

	}

	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
	{

		double elevation = modelGrid.getElevation(latitude, longitude, true);
		try {
			getPointColor(latitude, longitude, elevation, rgbaBufferA);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error getting point color: " + ex.getMessage(), ex);
		}

		modelGrid.setRgba(latitude, longitude, rgbaBufferA);

	}

	protected void getPointColor(double latitude, double longitude, double elevation, int[] rgba) throws DataSourceException, RenderEngineException
	{

		boolean imageOverlayed = false;
		if (modelContext.getImageDataContext() != null
				&& modelContext.getImageDataContext().getColor(latitude, longitude, modelDimensions.textureLatitudeResolution, modelDimensions.textureLongitudeResolution, rgba, nearestNeighbor)) {
			imageOverlayed = true;
		}

		if (imageOverlayed && rgba[3] < 0xFF) {
			modelColoring.getGradientColor(elevation, minimumElevation, maximumElevation, rgbaBufferB);
			double r = ((double) rgba[3] / 255.0);
			ColorUtil.interpolateColor(rgbaBufferB, rgba, rgba, r);
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
				scriptProxy.onGetPointColor(latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

	}

	@Override
	public void onProcessBefore() throws RenderEngineException
	{

	}

	@Override
	public void onProcessAfter() throws RenderEngineException
	{

	}

	@Override
	public void dispose() throws RenderEngineException
	{

	}

}
