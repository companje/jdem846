package us.wthr.jdem846;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import us.wthr.jdem846.exception.ResourceLoaderException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class Resource
{
	private static Log log = Logging.getLog(Resource.class);
	
	private String scheme;
	private ResourceTypeEnum schemeType;
	private String uri;
	
	public Resource(String url) throws ResourceLoaderException
	{
		if (url.indexOf("file://") == 0) {
			scheme = "file://";
			schemeType = ResourceTypeEnum.FILE;
			uri = url.substring(7);
		} else if (url.indexOf("jar://") == 0) {
			scheme = "jar://";
			schemeType = ResourceTypeEnum.JAR;
			uri = url.substring(6);
		} else if (url.indexOf("resources://") == 0) {
			scheme = "resources://";
			schemeType = ResourceTypeEnum.RESOURCE;
			uri = url.substring(12);
		} else if (url.indexOf("user://") == 0) {
			scheme = "user://";
			schemeType = ResourceTypeEnum.USER;
			uri = url.substring(7);
		} else {
			scheme = "file://";
			schemeType = ResourceTypeEnum.FILE;
			uri = url;
		}
		
		log.debug("Resouce: " + scheme + ", " + uri);
		
	}

	public String getScheme()
	{
		return scheme;
	}

	public ResourceTypeEnum getSchemeType()
	{
		return schemeType;
	}

	public String getUri()
	{
		return uri;
	}
	
	public InputStream getAsInputStream()  throws Exception
	{
		if (schemeType == ResourceTypeEnum.FILE) {
			return getFileAsInputStream();
		} else if (schemeType == ResourceTypeEnum.JAR) {
			return getJarFileAsInputStream();
		} else if (schemeType == ResourceTypeEnum.RESOURCE) {
			return getResourceAsInputStream();
		} else if (schemeType == ResourceTypeEnum.USER) {
			return getUserAsInputStream();
		} else {
			return null;
		}
	}
	
	public OutputStream getAsOutputStream() throws Exception
	{
		if (schemeType == ResourceTypeEnum.FILE) {
			return getFileAsOutputStream();
		} else if (schemeType == ResourceTypeEnum.JAR) {
			return getJarFileAsOutputStream();
		} else if (schemeType == ResourceTypeEnum.RESOURCE) {
			return getResourceAsOutputStream();
		} else if (schemeType == ResourceTypeEnum.USER) {
			return getUserAsOutputStream();
		} else {
			return null;
		}
	}
	
	public File getAsFile() throws Exception
	{
		if (schemeType == ResourceTypeEnum.FILE) {
			return getFileAsFile();
		} else if (schemeType == ResourceTypeEnum.JAR) {
			return getJarFileAsFile();
		} else if (schemeType == ResourceTypeEnum.RESOURCE) {
			return getResourceAsFile();
		} else if (schemeType == ResourceTypeEnum.USER) {
			return getUserAsFile();
		} else {
			return null;
		}
	}
	

	public URL getAsURL() throws Exception
	{
		
		if (schemeType == ResourceTypeEnum.FILE) {
			return getFileAsURL();
		} else if (schemeType == ResourceTypeEnum.JAR) {
			return getJarFileAsURL();
		} else if (schemeType == ResourceTypeEnum.RESOURCE) {
			return getResourceAsURL();
		} else if (schemeType == ResourceTypeEnum.USER) {
			return getUserAsURL();
		} else {
			return null;
		}
		

	}
	
	
	
	

	protected OutputStream getJarFileAsOutputStream()
	{
		return null;
	}
	
	protected OutputStream getResourceAsOutputStream() throws Exception
	{
		File file = getResourceAsFile();
		return new BufferedOutputStream(new FileOutputStream(file));
	}
	
	protected OutputStream getUserAsOutputStream() throws Exception
	{
		File file = getUserAsFile();
		return new BufferedOutputStream(new FileOutputStream(file));
	}
	
	protected OutputStream getFileAsOutputStream() throws Exception
	{
		File file = getFileAsFile();
		return new BufferedOutputStream(new FileOutputStream(file));
	}
	
	
	
	
	
	protected InputStream getJarFileAsInputStream() throws Exception
	{
		return Resource.class.getResourceAsStream(uri);
	}
	
	protected InputStream getResourceAsInputStream() throws Exception
	{
		File file = getResourceAsFile();
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	protected InputStream getUserAsInputStream() throws Exception
	{
		File file = getUserAsFile();
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	protected InputStream getFileAsInputStream() throws Exception
	{
		File file = getFileAsFile();
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	
	/** Don't expect this to work that often
	 * 
	 * @return
	 */
	protected File getJarFileAsFile() throws Exception
	{
		URL url = getJarFileAsURL();
		return new File(url.toURI().toString());
	}
	
	protected File getFileAsFile() throws Exception
	{
		return new File(uri);
	}
	
	protected File getResourceAsFile() throws Exception
	{
		String resourcesPath = JDem846Properties.getProperty("us.wthr.jdem846.resourcesPath");
		String searchPath = resourcesPath + "/" + uri;
		
		return new File(searchPath);
	}
	
	protected File getUserAsFile() throws Exception
	{
		String resourcesPath = JDem846Properties.getProperty("user.home");
		String searchPath = resourcesPath + "/" + uri;
		
		return new File(searchPath);
	}
	
	
	
	
	
	
	
	
	

	protected URL getJarFileAsURL() throws Exception
	{
		return Resource.class.getResource(uri);
	}
	
	protected URL getFileAsURL() throws Exception
	{
		return new URL("file:/" + uri);
	}
	
	protected URL getResourceAsURL() throws Exception
	{
		String resourcesPath = JDem846Properties.getProperty("us.wthr.jdem846.resourcesPath");
		String searchPath = resourcesPath + "/" + uri;
		
		return new URL("file:/" + searchPath);

	}
	
	protected URL getUserAsURL() throws Exception
	{
		String resourcesPath = JDem846Properties.getProperty("user.home");
		String searchPath = resourcesPath + "/" + uri;
		
		return new URL("file:/" + searchPath);

	}
	
}
