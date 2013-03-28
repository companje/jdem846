package us.wthr.jdem846.ui;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import us.wthr.jdem846.AbstractMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;


/** Main application entry point. Parses command line options and kicks off service and registry kernels.
 * 
 * @author Kevin M. Gill
 *
 */
public abstract class BaseUIMain extends AbstractMain
{
	private static Log log = null;

	private SplashScreen splash = null;
	
	
	public void afterCoreInit() throws Exception
	{
		log = Logging.getLog(BaseUIMain.class);
		
		applyLookAndFeel(false);
		
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.displaySplash")) {
			splash = new SplashScreen();
			splash.setCopyright(JDem846Properties.getProperty("us.wthr.jdem846.copyRight"));
			splash.setVisible(true);
			
			SplashScreen.addIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.48x48") + "/applications-system.png", I18N.get("us.wthr.jdem846.ui.system"));
		}
	}
	
	public void afterInit() throws Exception
	{
		if (splash != null) {
			splash.setVisible(false);
		}
		
	}
	
	
	
	


	protected static void applyLookAndFeel(boolean forceConfiguredDefault)
	{
		String lafWindows = JDem846Properties.getProperty("us.wthr.jdem846.ui.swingLaf.windows");
		String lafLinux = JDem846Properties.getProperty("us.wthr.jdem846.ui.swingLaf.windows");
		String lafDefault = JDem846Properties.getProperty("us.wthr.jdem846.ui.swingLaf.windows");
		
		if (lafDefault == null) {
			lafDefault = "Metal";
		}
		
		if (lafWindows == null) {
			lafWindows = lafDefault;
		}
		
		if (lafLinux == null) {
			lafLinux = lafDefault;
		}
		
		String os = JDem846Properties.getProperty("os.name");
		
		String laf = lafDefault;
		if (os.toUpperCase().contains("WINDOWS") && !forceConfiguredDefault) {
			laf = lafWindows;
		} else if (os.toUpperCase().contains("LINUX") && !forceConfiguredDefault) {
			laf = lafLinux;
		}
		
		if (laf != null && laf.startsWith("com.jgoodies.looks")) {
			JDem846Properties.setProperty("us.wthr.jdem846.ui.usingJGoodies", "true");
			setJGoodiesSettings();
		} else {
			JDem846Properties.setProperty("us.wthr.jdem846.ui.usingJGoodies", "false");
		}
		
		// check if laf is "default", if so leave the Look & Feel to whatever
		// the JVM default is and exit.
		if (laf.equalsIgnoreCase("default")) {
			return;
		}
		
		try {
			log.info("Applying Look & Feel: '" + laf + "'");
			if (laf != null) {
				UIManager.setLookAndFeel(laf);
				log.info("Applied Look & Feel: '" + laf + "'");
			    //for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			    //    if (laf.equalsIgnoreCase(info.getName())) {
			   //        UIManager.setLookAndFeel(info.getClassName());
			            log.info("Applied Look & Feel: '" + laf + "'");
			   //         break;
			   //     }
			   // }
			}
		} catch (Exception ex) {
		    // We really don't care if the specified look & feel is not available, but if that's
			// the case, we recall this function and force the configured default. If we're
			// already in the forced config'd default call, then fail and fall back to the
			// JVM default Look & Feel.
			
			if (!forceConfiguredDefault) {
				log.warn("Failed to apply configured look and feel '" + laf + "', reverting to application default.", ex);
				applyLookAndFeel(true);
			} else {
				log.warn("Failed to apply application default look & feel, falling back to JVM default.", ex);
			}
		}
	}

	public static void setJGoodiesSettings()
	{
		log.info("Applying JGoodies Settings");
		
		
		
		LookAndFeelInfo[] lnfs = UIManager.getInstalledLookAndFeels();
		boolean found = false;
		for (int i = 0; i < lnfs.length; i++) {
			if (lnfs[i].getName().equals("JGoodies Plastic 3D")) {
				found = true;
			}
		}
		
		PlasticTheme theme = getConfiguredPlasticTheme();
		if (theme != null) {
			PlasticLookAndFeel.setPlasticTheme(theme);
		}
		
		if (!found) {
			UIManager.installLookAndFeel("JGoodies Plastic 3D",
					"com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		}
		
		String fontName = getConfiguredFontName();
		int fontStyle = JDem846Properties.getIntProperty("us.wthr.jdem846.ui.font.style");
		int fontSize = JDem846Properties.getIntProperty("us.wthr.jdem846.ui.font.size");

		FontSet fontSet = FontSets.createDefaultFontSet(new Font(fontName, fontStyle, fontSize));

		FontPolicy fixedPolicy = FontPolicies.createFixedPolicy(fontSet);
		PlasticLookAndFeel.setFontPolicy(fixedPolicy);

	}
	
	public static String getConfiguredFontName()
	{
		String fontName = null;
		String os = System.getProperty("os.name");
		
		if (os.toUpperCase().contains("WINDOWS")) {
			fontName = JDem846Properties.getProperty("us.wthr.jdem846.ui.font.windows");
		} else if (os.toUpperCase().contains("LINUX")) {
			fontName = JDem846Properties.getProperty("us.wthr.jdem846.ui.font.linux");
		} else {
			fontName = JDem846Properties.getProperty("us.wthr.jdem846.ui.font.default");
		}
		return fontName;
	}
	
	
	public static PlasticTheme getConfiguredPlasticTheme()
	{
		String theme = JDem846Properties.getProperty("us.wthr.jdem846.general.ui.jgoodies.theme");
		
		if (theme == null || theme.length() == 0) {
			return null;
		}
		
		PlasticTheme plasticTheme = null;
		
		try {
			Class<?> clazz =  Class.forName(theme);
			plasticTheme = (PlasticTheme) clazz.newInstance();
		} catch (Exception ex) {
			log.warn("Failed to load pastic theme '" + theme + "': " + ex.getMessage(), ex);
		}
		
		return plasticTheme;
	}
	
	
	
	
}
