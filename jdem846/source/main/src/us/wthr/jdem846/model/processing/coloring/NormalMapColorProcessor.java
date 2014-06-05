package us.wthr.jdem846.model.processing.coloring;

import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.graphics.FlatNormalsCalculator;
import us.wthr.jdem846.graphics.ScaledElevationFetchCallback;
import us.wthr.jdem846.graphics.SphericalNormalsCalculator;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.GridFilter;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.scaling.ElevationScaler;
import us.wthr.jdem846.scaling.ElevationScalerEnum;
import us.wthr.jdem846.scaling.ElevationScalerFactory;

@GridProcessing(id="us.wthr.jdem846.model.processing.coloring.NormalMapColorProcessor",
				name="Normal Map Coloring",
				type=GridProcessingTypesEnum.COLORING,
				optionModel=NormalMapColorOptionModel.class,
				enabled=true,
				isFilter=true
				)
public class NormalMapColorProcessor extends GridFilter
{
	private static Log log = Logging.getLog(NormalMapColorProcessor.class);

	
	protected SphericalNormalsCalculator normals = null;
	
	private Vector n = new Vector();
	private int[] rgba = new int[4];
	
	@Override
	public void prepare() throws RenderEngineException
	{
		
		NormalMapColorOptionModel normalsOptionModel = (NormalMapColorOptionModel) this.optionModel;
		
		Planet planet = PlanetsRegistry.getPlanet(globalOptionModel.getPlanet());
		if (planet == null) {
			planet = PlanetsRegistry.getPlanet("earth");
		}
		
		double minimumElevation = modelContext.getRasterDataContext().getDataMinimumValue();
		double maximumElevation = modelContext.getRasterDataContext().getDataMaximumValue();
		
		ElevationScaler elevationScaler = null;
		ElevationScalerEnum elevationScalerEnum = ElevationScalerEnum.getElevationScalerEnumFromIdentifier(globalOptionModel.getElevationScale());
		try {
			elevationScaler = ElevationScalerFactory.createElevationScaler(elevationScalerEnum, globalOptionModel.getElevationMultiple(), minimumElevation, maximumElevation);
		} catch (Exception ex) {
			throw new RenderEngineException("Error creating elevation scaler: " + ex.getMessage(), ex);
		}
		
		
		if (normalsOptionModel.getUseFlatSurface()) {
			normals = new FlatNormalsCalculator(planet
					, modelDimensions.getModelLatitudeResolution()
					, modelDimensions.getModelLongitudeResolution()
					, new ScaledElevationFetchCallback(modelGrid, elevationScaler));/*
					, new ElevationFetchCallback() {
						@Override
						public double getElevation(double latitude, double longitude)
						{
							return modelGrid.getElevation(latitude, longitude, true);
						}
				
			});*/
		} else {
		
			normals = new SphericalNormalsCalculator(planet
					, modelDimensions.getModelLatitudeResolution()
					, modelDimensions.getModelLongitudeResolution()
					, new ScaledElevationFetchCallback(modelGrid, elevationScaler));/*
					, new ElevationFetchCallback() {
						@Override
						public double getElevation(double latitude, double longitude)
						{
							return modelGrid.getElevation(latitude, longitude, true);
						}
				
			});*/
		}
		
	}

	@Override
	public void onProcessBefore() throws RenderEngineException
	{
		
	}

	@Override
	public void onModelPoint(double latitude, double longitude) throws RenderEngineException
	{
		normals.calculateNormal(latitude, longitude, n);
		n.normalize();
		
		rgba[3] = 0xFF;
		
		rgba[0] = (int) MathExt.round((1.0 - ((n.x + 1.0) / 2.0)) * 255.0);
		rgba[1] = (int) MathExt.round((1.0 - ((n.y + 1.0) / 2.0)) * 255.0);
		rgba[2] = (int) MathExt.round((1.0 - ((n.z + 1.0) / 2.0)) * 255.0);
		
		modelGrid.setRgba(latitude, longitude, rgba);
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
