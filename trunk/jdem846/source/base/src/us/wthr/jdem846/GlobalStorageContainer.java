package us.wthr.jdem846;

import java.util.HashMap;
import java.util.Map;

/** A very simple class used for persisting objects across renderings. Mainly to be utilized by user
 * scripting to allow loading resources once and reusing them on subsequent runs.
 * 
 * @author Kevin M. Gill
 *
 */
public class GlobalStorageContainer
{
	
	private static Map<String, Object> resourceMap = new HashMap<String, Object>();
	
	
	protected GlobalStorageContainer()
	{
		
	}
	
	
	public static boolean hasResource(String key)
	{
		return (get(key) != null);
	}
	
	public static void put(String key, Object resource)
	{
		synchronized(resourceMap) {
			resourceMap.put(key, resource);
		}
	}
	
	public static Object get(String key)
	{
		synchronized(resourceMap) {
			return resourceMap.get(key);
		}
	}
	
	
}
