package us.wthr.jdem846.json;

import java.io.InputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import junit.framework.TestCase;

public class GradientJSONTest extends TestCase
{
	
	private static Log log = Logging.getLog(GradientJSONTest.class);
	
	public void testLoadJSONGradient()
	{
		String jsonFile = "/color/gradient/hypsometric.json";
		
		log.info("Loading JSON file " + jsonFile);
		
		InputStream is = GradientJSONTest.class.getResourceAsStream( jsonFile);
		assertNotNull(is);
		
		String jsonTxt = null;
		
		try {
			jsonTxt = IOUtils.toString( is );
		} catch (Exception ex) {
			assertTrue(false);
		}
		
		assertNotNull(jsonTxt);
		
        JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );   
        
        
        String name = json.getString("name");
        String identifier = json.getString("identifier");
        boolean needsMinMaxElevation = json.getBoolean("needsMinMaxElevation");
        String units = json.getString("units");
        JSONArray gradient = json.getJSONArray("gradient");
        
        
        assertEquals("hypsometric-tint", identifier);
        assertTrue(needsMinMaxElevation);
        assertEquals("percent", units);
        assertEquals(8, gradient.size());
        assertEquals("us.wthr.jdem846.color.hypsometricTint.name", name);
        
        
        for (int i = 0; i < gradient.size(); i++) {
        	JSONObject gradientStop = gradient.getJSONObject(i);
        	double stop = gradientStop.getDouble("stop");
        	double red = gradientStop.getDouble("red");
        	double green = gradientStop.getDouble("green");
        	double blue = gradientStop.getDouble("blue");
        	
        	if (red > 1.0) {
        		red = red / 255;
        	}
        	
        	if (green > 1.0) {
        		green = green / 255;
        	}
        	
        	if (blue > 1.0) {
        		blue = blue / 255;
        	}
        	
        	assertTrue(stop >= 0.0 && stop <= 1.0);
        	assertTrue(red >= 0.0 && red <= 1.0);
        	assertTrue(green >= 0.0 && green <= 1.0);
        	assertTrue(blue >= 0.0 && blue <= 1.0);
        }
		
	}
	
}
