package us.wthr.jdem846.render.mapprojection;

import us.wthr.jdem846.exception.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class MapProjectionProviderFactory
{
	private static Log log = Logging.getLog(MapProjectionProviderFactory.class);
	
	protected MapProjectionProviderFactory()
	{
		
	}
	
	public static MapProjection getMapProjection(String identifier, double north, double south, double east, double west, double width, double height) throws MapProjectionException
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
	
	
	public static MapProjection getMapProjection(MapProjectionEnum projectionEnum, double north, double south, double east, double west, double width, double height) throws MapProjectionException
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
		
		mapProjectionInstance.setUp(north, south, east, west, width, height);
		
		return mapProjectionInstance;
	}
}
