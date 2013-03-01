package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.graphics.Colors;
import us.wthr.jdem846.graphics.IColor;
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

	protected double north;
	protected double south;
	protected double east;
	protected double west;

	protected double minimumElevation;
	protected double maximumElevation;

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


		try {
			modelColoring = ColoringRegistry.getInstance(optionModel.getColorTint()).getImpl().copy();
		} catch (Exception ex) {
			throw new RenderEngineException("Failed to create coloring instance copy: " + ex.getMessage(), ex);
		}

		nearestNeighbor = getGlobalOptionModel().getStandardResolutionElevation();

	}

	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
	{

		double elevation = modelGrid.getElevation(latitude, longitude, true);
		
		IColor color = null;
		try {
			color = getPointColor(latitude, longitude, elevation);
		} catch (DataSourceException ex) {
			throw new RenderEngineException("Error getting point color: " + ex.getMessage(), ex);
		}

		modelGrid.setRgba(latitude, longitude, color);

	}

	protected IColor getPointColor(double latitude, double longitude, double elevation) throws DataSourceException, RenderEngineException
	{

		boolean imageOverlayed = false;
		
		IColor color = Colors.TRANSPARENT;
		
		if (modelContext.getImageDataContext() != null
				&& (color = modelContext.getImageDataContext().getColor(latitude, longitude, modelDimensions.textureLatitudeResolution, modelDimensions.textureLongitudeResolution, nearestNeighbor)) != null) {
			imageOverlayed = true;
		}

		if (imageOverlayed && color.getAlpha() < 0xFF) {
			IColor color1 = modelColoring.getGradientColor(elevation, minimumElevation, maximumElevation);
			double r = ((double) color.getAlpha() / 255.0);
			color = ColorUtil.interpolateColor(color1, color, r);
		}

		if (!imageOverlayed) {
			color = modelColoring.getGradientColor(elevation, minimumElevation, maximumElevation);
		}

		if (useScripting) {
			color = onGetPointColor(latitude, longitude, elevation, minimumElevation, maximumElevation, color);
		}
		
		return color;

	}

	protected IColor onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, IColor color) throws RenderEngineException
	{
		try {
			ScriptProxy scriptProxy = modelContext.getScriptingContext().getScriptProxy();
			IColor result = null;
			if (scriptProxy != null) {
				result = scriptProxy.onGetPointColor(latitude, longitude, elevation, elevationMinimum, elevationMaximum, color);
			}
			
			return (result != null) ? result : color;
			
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
