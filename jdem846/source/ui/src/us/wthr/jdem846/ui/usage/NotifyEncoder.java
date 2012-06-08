package us.wthr.jdem846.ui.usage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class NotifyEncoder
{
	private static Log log = Logging.getLog(NotifyEncoder.class);
	
	
	public static String encode(Object notify) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		
		String query = "";
		
		for (Method method : notify.getClass().getMethods()) {
			
			String name = method.getName();
			
			if (isNotifyProperty(name) && !name.equals("getClass")) {
				String propertyName = getPropertyName(name);
				Object value = method.invoke(notify);
				
				query += propertyName + "=" + encodeForUrl(value.toString()) + "&";
				
			}
			
		}
		

		return query;
	}
	
	protected static String encodeForUrl(String s)
	{
		//s = s.replaceAll("\\+", "%2B");
		//s = s.replaceAll(" ", "+");
		
		
		String encodedUrl = s;
		try {
			encodedUrl = URLEncoder.encode(s,"UTF-8");
		} catch (UnsupportedEncodingException ex) {
			log.warn("Error encoding string: " + s, ex);
		} 
		return encodedUrl;
	}
	
	protected static boolean isNotifyProperty(String methodName)
	{
		if (methodName.startsWith("get") || methodName.startsWith("is")) {
			return true;
		} else {
			return false;
		}
	}
	
	protected static String getPropertyName(String methodName)
	{
		String propertyName = "";
		
		if (methodName.startsWith("get")) {
			propertyName = methodName.substring(3);
		} else if (methodName.startsWith("is")) {
			propertyName = methodName.substring(2);
		} else {
			propertyName = methodName;
		}
		
		return propertyName.toLowerCase();
	}
	
}
