package us.wthr.jdem846.canvas;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.Projection;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ViewPerspective;

public class CanvasProjectionFactory
{
	
	
	public static CanvasProjection create(ModelContext modelContext) throws CanvasException
	{
		
		GlobalOptionModel globalOptionModel = modelContext.getModelProcessManifest().getGlobalOptionModel();
		
		try {
			return create(CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(globalOptionModel.getRenderProjection()),
					globalOptionModel.getMapProjectionInstance(),
					modelContext.getNorth(),
					modelContext.getSouth(),
					modelContext.getEast(),
					modelContext.getWest(),
					modelContext.getModelDimensions().getOutputWidth(),
					modelContext.getModelDimensions().getOutputHeight(),
					PlanetsRegistry.getPlanet(globalOptionModel.getPlanet()),
					globalOptionModel.getElevationMultiple(),
					modelContext.getRasterDataContext().getDataMinimumValue(),
					modelContext.getRasterDataContext().getDataMaximumValue(),
					modelContext.getModelDimensions(),
					globalOptionModel.getViewAngle());
		} catch (MapProjectionException ex) {
			throw new CanvasException("Error fetching map project instance: " + ex.getMessage(), ex);
		}
		
	}
	
	public static CanvasProjection create(CanvasProjectionTypeEnum projectionType,
										MapProjection mapProjection,
										double north,
										double south,
										double east,
										double west,
										int width,
										int height,
										Planet planet,
										double elevationMultiple,
										double minimumValue,
										double maximumValue,
										ModelDimensions modelDimensions,
										ViewPerspective projection)
	{
		CanvasProjection canvasProjection = null;
		
		
		if (projectionType == CanvasProjectionTypeEnum.PROJECT_FLAT) {
			canvasProjection = new CanvasProjection(mapProjection, north, south, east, west, width, height);
		} else if (projectionType == CanvasProjectionTypeEnum.PROJECT_3D) {
			canvasProjection = new CanvasProjection3d(mapProjection, north, south, east, west, width, height, planet, elevationMultiple, minimumValue, maximumValue, modelDimensions, projection);
		} else if (projectionType == CanvasProjectionTypeEnum.PROJECT_SPHERE) {
			canvasProjection = new CanvasProjectionGlobe(mapProjection, north, south, east, west, width, height, planet, elevationMultiple, minimumValue, maximumValue, modelDimensions, projection);
		}
		
		
		return canvasProjection;
	}
	
	
}
