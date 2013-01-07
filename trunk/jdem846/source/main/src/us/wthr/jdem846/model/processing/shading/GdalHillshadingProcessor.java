package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;

/** Implements the GDAL hillshading algorithm from gdaldem.cpp
 * 
 * @author Kevin M. Gill
 *
 */
@GridProcessing(id="us.wthr.jdem846.model.processing.shading.GdalHillshadingProcessor",
				name="GDAL Hillshading Process",
				type=GridProcessingTypesEnum.SHADING,
				optionModel=GdalHillshadingOptionModel.class,
				enabled=true
				)
public class GdalHillshadingProcessor extends GridProcessor
{
	private static Log log = Logging.getLog(GdalHillshadingProcessor.class);
	
	double azimuth;
	double altitude;
	double scale;
	boolean preserveColor;
	
	double lightIntensity;
	double darkIntensity;
	
	double latitudeResolution;
	double longitudeResolution;
	
	private int[] rgba = new int[4];
	
	@Override
	public void prepare() throws RenderEngineException 
	{
		GdalHillshadingOptionModel optionModel = (GdalHillshadingOptionModel) this.getOptionModel();
		
		this.azimuth = optionModel.getAzimuth();
		this.altitude = optionModel.getAltitude();
		this.preserveColor = optionModel.getPreserveColor();
		this.scale = this.getGlobalOptionModel().getElevationMultiple();
		
		this.lightIntensity = optionModel.getLightIntensity();
		this.darkIntensity = optionModel.getDarkIntensity();
		
		this.latitudeResolution = this.getModelDimensions().textureLatitudeResolution;
		this.longitudeResolution = this.getModelDimensions().textureLongitudeResolution;
		
	}

	
	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{
		
		double x, y, slope, aspect, cang;
		
		modelGrid.getElevation(latitude, longitude, false);
		
		// First Slope ...
		x = ((
				this.modelGrid.getElevation(latitude + this.latitudeResolution, longitude - this.longitudeResolution, true)
				+ (this.modelGrid.getElevation(latitude, longitude - this.longitudeResolution, true) * 2)
				+ this.modelGrid.getElevation(latitude - this.latitudeResolution, longitude - this.longitudeResolution, true)
			) - (
				this.modelGrid.getElevation(latitude + this.latitudeResolution, longitude + this.longitudeResolution, true)
				+ (this.modelGrid.getElevation(latitude, longitude + this.longitudeResolution, true) * 2)
				+ this.modelGrid.getElevation(latitude - this.latitudeResolution, longitude + this.longitudeResolution, true)
			)) / (8.0 * this.longitudeResolution * this.scale);

		y = ((
				this.modelGrid.getElevation(latitude - this.latitudeResolution, longitude - this.longitudeResolution, true)
				+ (this.modelGrid.getElevation(latitude - this.latitudeResolution, longitude, true) * 2)
				+ this.modelGrid.getElevation(latitude - this.latitudeResolution, longitude + this.longitudeResolution, true)
			) - (
				this.modelGrid.getElevation(latitude + this.latitudeResolution, longitude - this.longitudeResolution, true)
				+ (this.modelGrid.getElevation(latitude + this.latitudeResolution, longitude, true) * 2)
				+ this.modelGrid.getElevation(latitude + this.latitudeResolution, longitude + this.longitudeResolution, true)
			)) / (8.0 * this.latitudeResolution * this.scale);

		slope = MathExt.HALFPI - MathExt.atan(MathExt.sqrt(x * x + y * y));
		aspect = MathExt.atan2(y, x);

		cang = MathExt.sin(MathExt.radians(this.altitude)) * MathExt.sin(slope) +
				MathExt.cos(MathExt.radians(this.altitude)) * MathExt.cos(slope) *
				MathExt.cos(MathExt.radians(this.azimuth) - MathExt.HALFPI - aspect);

		cang = (cang <= 0) ? 1.0 : (1.0 + (254.0 * cang));

		double f = ((cang / 180.0) * 2) - 1.0;
		
		if (f > 0) {
			f *= lightIntensity;
		} else if (f < 0) {
			f *= darkIntensity;
		}
		
		this.modelGrid.getRgba(latitude, longitude, rgba);


		if (!this.preserveColor) {
			rgba[0] = 0x7F;
			rgba[1] = 0x7F;
			rgba[2] = 0x7F;
		}

		//color::adjustBrightness(c, f);
		ColorAdjustments.adjustBrightness(rgba, f);
		this.modelGrid.setRgba(latitude, longitude, rgba);
		//this->_modelGrid->color(latitude, longitude, c);
	}


	@Override
	public void onLatitudeStart(double latitude) throws RenderEngineException
	{
		
	}


	@Override
	public void onLatitudeEnd(double latitude) throws RenderEngineException
	{
		
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
