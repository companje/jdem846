package us.wthr.jdem846.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ResourceLoadingTesting extends TestCase
{

	private static Log log = Logging.getLog(ResourceLoadingTesting.class);
	
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		bootstrapSystemProperties();
	}

	
	public void testInstallPathSetAndExists()
	{
		assertNotNull(System.getProperty("us.wthr.jdem846.installPath"));
		
		File file = new File(System.getProperty("us.wthr.jdem846.installPath"));
		assertTrue(file.exists());
	}
	
	
	
	
	public void testResourcePathSetAndExists()
	{
		assertNotNull(System.getProperty("us.wthr.jdem846.resourcesPath"));
		
		File file = new File(System.getProperty("us.wthr.jdem846.resourcesPath"));
		assertTrue(file.exists());
	}
	
	public void testJDemPropertiesLoaded()
	{
		try {
			assertNotNull("Property was null indicating property file load problem", JDem846Properties.getProperty("us.wthr.jdem846.applicationName"));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("jDem properties failed to load: " + ex.getMessage());
		}
	}
	
	public void testLoggingInitialized()
	{
		try {
			log.info("Install Path: " + System.getProperty("us.wthr.jdem846.installPath"));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Logging initialization failed: " + ex.getMessage());
		}
	}
	
	/*  Testing resources://
	 * 
	 */
	
	public void testLoadResourceFile()
	{
		__testLoadFile(JDem846Properties.getProperty("us.wthr.jdem846.icon"));
	}
	
	public void testLoadResourceURL()
	{
		__testLoadURL(JDem846Properties.getProperty("us.wthr.jdem846.icon"));
	}
	
	public void testLoadResourceInputStream()
	{
		__testLoadInputStream(JDem846Properties.getProperty("us.wthr.jdem846.icon"));
	}
	
	
	/*  Testing file://
	 * 
	 */
	
	public void testLoadFileFile()
	{
		__testLoadFile("file://" + System.getProperty("us.wthr.jdem846.installPath") + "\\jdem846-icon.png");
	}
	
	public void testLoadFileURL()
	{
		__testLoadURL("file://" + System.getProperty("us.wthr.jdem846.installPath") + "\\jdem846-icon.png");
	}
	
	public void testLoadFileInputStream()
	{
		__testLoadInputStream("file://" + System.getProperty("us.wthr.jdem846.installPath") + "\\jdem846-icon.png");
	}
	
	/* Testing default path
	 * 
	 */
	
	
	public void testLoadDefaultPathFile()
	{
		__testLoadFile(System.getProperty("us.wthr.jdem846.installPath") + "\\jdem846-icon.png");
	}
	
	
	public void testLoadDefaultPathURL()
	{
		__testLoadURL(System.getProperty("us.wthr.jdem846.installPath") + "\\jdem846-icon.png");
	}
	
	public void testLoadDefaultPathInputStream()
	{
		__testLoadInputStream(System.getProperty("us.wthr.jdem846.installPath") + "\\jdem846-icon.png");
	}
	
	
	
	
	public void __testLoadFile(String url)
	{
		File file = JDemResourceLoader.getAsFile(url);
	
		assertNotNull("File '" + url + "' is null", file);
		assertTrue("File '" + url + "' does not exist", file.exists());
	}
	
	public void __testLoadURL(String path)
	{
		URL url = JDemResourceLoader.getAsURL(path);
	
		assertNotNull("URL '" + url + "' is null", url);
	}
	
	public void __testLoadInputStream(String url)
	{
		InputStream in = null;
		
		try {
			in = JDemResourceLoader.getAsInputStream(url);
		} catch (FileNotFoundException ex) {
			fail(ex.getMessage());
		}
		
		assertNotNull("Input stream for '" + url + "' is null", in);
		
		byte[] bytes = new byte[1024];
		int len = 0;
		int bytesRead = 0;
		try {
			while((len = in.read(bytes)) > 0) {
				bytesRead += len;
			}
		} catch (IOException ex) {
			fail("Read bytes from '" + url + "' failed: " + ex.getMessage());
		}
		
		try {
			in.close();
		} catch (IOException ex) {
			fail("Closing input stream '" + url + "' failed: " + ex.getMessage());
		}
		
		assertTrue("No bytes were read from '" + url + "'", bytesRead > 0);
	}
	
	
	
	
	
	protected void bootstrapSystemProperties()
	{
		
		if (System.getProperty("us.wthr.jdem846.installPath") == null) {
			System.setProperty("us.wthr.jdem846.installPath", System.getProperty("user.dir"));
		}
		if (System.getProperty("us.wthr.jdem846.resourcesPath") == null) {
			System.setProperty("us.wthr.jdem846.resourcesPath", System.getProperty("us.wthr.jdem846.installPath"));
		}
		
	}
	
	
	
	
}
