package us.wthr.jdem846;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class JDemResourceLoader
{
	
	protected JDemResourceLoader()
	{
		
	}
	
	
	public static InputStream getAsInputStream(String url) throws FileNotFoundException
	{
		String schema = url.substring(0, url.indexOf("://"));
		String path = url.substring(url.indexOf("://") + 3);
		
		if (schema.equalsIgnoreCase("resources")) {
			return getResourceAsInputStream(path);
		}
		
		return null;
	}
	
	protected static InputStream getResourceAsInputStream(String url) throws FileNotFoundException
	{
		File file = getResourceAsFile(url);
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		return in;
	}
	
	
	
	public static File getAsFile(String url)
	{
		String schema = url.substring(0, url.indexOf("://"));
		String path = url.substring(url.indexOf("://") + 3);
		
		if (schema.equalsIgnoreCase("resources")) {
			return getResourceAsFile(path);
		}
		
		return null;
		
	}
	
	protected static File getResourceAsFile(String url)
	{
		String resourcesPath = JDem846Properties.getProperty("us.wthr.jdem846.resourcesPath");
		String searchPath = resourcesPath + "/" + url;
		
		return new File(searchPath);
	}
	
}
