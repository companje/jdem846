package us.wthr.jdem846.gis.projections;

import junit.framework.Test;
import junit.framework.TestSuite;
import us.wthr.jdem846.JDem846Properties;

public class MapProjectionTestSuite extends TestSuite
{
	
	protected static void bootstrapSystemProperties()
	{
		
		if (System.getProperty("us.wthr.jdem846.installPath") == null) {
			System.setProperty("us.wthr.jdem846.installPath", System.getProperty("user.dir"));
		}
		if (System.getProperty("us.wthr.jdem846.resourcesPath") == null) {
			System.setProperty("us.wthr.jdem846.resourcesPath", System.getProperty("us.wthr.jdem846.installPath"));
		}
		
		if (System.getProperty("us.wthr.jdem846.userSettingsPath") == null) {
			System.setProperty("us.wthr.jdem846.userSettingsPath", System.getProperty("user.home") + "/.jdem846");
		}
		
		if (System.getProperty("us.wthr.jdem846.testOutputPath") == null) {
			System.setProperty("us.wthr.jdem846.testOutputPath", System.getProperty("user.dir") + "/test-output");
		}
		
		
		System.out.println("user.dir: " + System.getProperty("user.dir"));
		System.out.println("us.wthr.jdem846.installPath: " + System.getProperty("us.wthr.jdem846.installPath"));
		System.out.println("us.wthr.jdem846.resourcesPath: " + System.getProperty("us.wthr.jdem846.resourcesPath"));
		System.out.println("us.wthr.jdem846.userSettingsPath: " + System.getProperty("us.wthr.jdem846.userSettingsPath"));
		System.out.println("us.wthr.jdem846.testOutputPath: " + System.getProperty("us.wthr.jdem846.testOutputPath"));
	}
	
	public static Test suite()
	{
		//System.setProperty("us.wthr.jdem846.installPath", "");
		bootstrapSystemProperties();
		JDem846Properties.initializeApplicationProperties();
		
		TestSuite suite = new TestSuite("Test suite for utility classes in us.wthr.jdem846.render.mapprojection");
		//$JUnit-BEGIN$
		
		
		suite.addTestSuite(AitoffProjectionTest.class);
		suite.addTestSuite(EquirectangularProjectionTest.class);
		suite.addTestSuite(RobinsonProjectionTest.class);
		suite.addTestSuite(WinkelTripelProjectionTest.class);
		suite.addTestSuite(MollweideProjectionTest.class);
		suite.addTestSuite(HammerProjectionTest.class);
		suite.addTestSuite(WagnerVIProjectionTest.class);
		
		suite.addTestSuite(MercatorProjectionTest.class);
		suite.addTestSuite(TransverseMercatorProjectionTest.class);
		
		//$JUnit-END$
		return suite;
	}
}
