package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class MapProjectionProviderFactory
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(MapProjectionProviderFactory.class);
	
	protected MapProjectionProviderFactory()
	{
		
	}
	

	public static MapProjection getMapProjection(String identifier, double north, double south, double east, double west, double width, double height) throws MapProjectionException
	{
		
		return MapProjectionProviderFactory._getMapProjection(identifier, null, north, south, east, west, width, height);
		
	}
	
	public static MapProjection _getMapProjection(String identifier, ModelContext modelContext, double north, double south, double east, double west, double width, double height) throws MapProjectionException
	{
		MapProjectionEnum useProjectionEnum = null;
		for (MapProjectionEnum projectionEnum : MapProjectionEnum.values()) {
			if (projectionEnum.identifier().equalsIgnoreCase(identifier)) {
				useProjectionEnum = projectionEnum;
				break;
			}
		}
		
		if (useProjectionEnum != null) {
			return MapProjectionProviderFactory.getMapProjection(useProjectionEnum, north, south, east, west, width, height);
		} else {
			throw new MapProjectionException("Invalid map projection identifier '" + identifier + "' specified");
		}
		
	}
	
	public static MapProjection getMapProjection(ModelContext modelContext) throws MapProjectionException
	{
		return MapProjectionProviderFactory._getMapProjection(modelContext.getModelOptions().getMapProjection(), modelContext, 0, 0, 0, 0, 0, 0);
	}
	
	
	public static MapProjection getMapProjection(MapProjectionEnum projectionEnum, double north, double south, double east, double west, double width, double height) throws MapProjectionException
	{
		return MapProjectionProviderFactory._getMapProjection(projectionEnum, null, north, south, east, west, width, height);
	}
	
	protected static MapProjection _getMapProjection(MapProjectionEnum projectionEnum, ModelContext modelContext, double north, double south, double east, double west, double width, double height) throws MapProjectionException
	{
		if (projectionEnum == null) {
			throw new MapProjectionException("Map projection provider is null.");
		}
		
		MapProjection mapProjectionInstance = null;
		
		try {
			mapProjectionInstance = projectionEnum.provider().newInstance();
		} catch (Exception ex) {
			throw new MapProjectionException("Error trying to create new map projection instance: " + ex.getMessage(), ex);
		} 
		
		if (mapProjectionInstance == null) {
			throw new MapProjectionException("Map projection instance is null following dynamic create.");
		}
		
		if (modelContext != null) {
			mapProjectionInstance.setUp(modelContext);
		} else {
			mapProjectionInstance.setUp(north, south, east, west, width, height);
		}
		
		
		return mapProjectionInstance;
	}
}
