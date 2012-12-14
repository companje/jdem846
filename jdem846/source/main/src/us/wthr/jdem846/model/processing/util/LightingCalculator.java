package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.scripting.ScriptProxy;

public class LightingCalculator
{
	private static Log log = Logging.getLog(LightingCalculator.class);
	
	private LightingValues lightingValues;

	//private double[] emmisiveColor = {1.0, 1.0, 1.0, 1.0};
	//private double[] ambientColor = {1.0, 1.0, 1.0, 1.0};
	//private double[] diffuseColor = {1.0, 1.0, 1.0, 1.0};
	//private double[] specularColor = {1.0, 1.0, 1.0, 1.0};
	private double[] specularColor = {0.8, 0.8, 0.8, 0.8};
	
	private boolean useDistanceAttenuation = true;
	private double attenuationRadius = 2000;
	private double blockShadowIntensity = 0.4;
	
	double[] color = new double[4];
	
	Vector eye = new Vector(1.0, 0.0, 1.0);
	
	Vector P = new Vector();
	Vector N = new Vector();
	Vector L = new Vector();
	Vector V = new Vector();
	Vector H = new Vector();

	protected ViewPerspective viewPerspective;
	
	protected ScriptProxy scriptProxy;
	
	public LightingCalculator()
	{
		
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ModelContext modelContext)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, null, modelContext.getScriptingContext().getScriptProxy());
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ViewPerspective viewPerspective, ModelContext modelContext)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, viewPerspective, modelContext.getScriptingContext().getScriptProxy());
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, null, (ScriptProxy) null);
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ViewPerspective viewPerspective)
	{
		this(emmisive, ambient, diffuse, specular, blockShadowIntensity, viewPerspective, (ScriptProxy) null);
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity, ViewPerspective viewPerspective, ScriptProxy scriptProxy)
	{
		lightingValues = new LightingValues(emmisive, ambient, diffuse, specular);
		lightingValues.emmisiveLight = lightingValues.emmisiveLevel;
		lightingValues.ambientLight = lightingValues.ambientLevel;
		
		this.scriptProxy = scriptProxy;
		
		setBlockShadowIntensity(blockShadowIntensity);
		this.viewPerspective = viewPerspective;

	}

	
	public void calculateColor(Vector normal, double latitude, double longitude, double elevation, double radius, double shininess, double blockDistance, Vector lightSource, int[] rgba)
	{
		Spheres.getPoint3D(longitude, latitude, radius, P);
		
		normal.copyTo(N);

		color[0] = (double) rgba[0] / 255.0;
		color[1] = (double) rgba[1] / 255.0;
		color[2] = (double) rgba[2] / 255.0;

		
		lightingValues.emmisiveLight = lightingValues.emmisiveLevel;
		lightingValues.ambientLight = lightingValues.ambientLevel;

		lightSource.copyTo(L);
		Vectors.inverse(L);
		Vectors.subtract(L, P, L);
		Vectors.normalize(L);

		double dot = Vectors.dotProduct(N, L);
		double dp = Vectors.dotProduct(L, N) * 2.0;
		N.x *= dp;
		N.y *= dp;
		N.z *= dp;

		lightingValues.diffuseLight = dot;

		
		
		lightingValues.specularLight = 0;
		if (lightingValues.diffuseLight > 0) {
			
			Vectors.subtract(N, L, H);
			Vectors.normalize(H);
			
			double specDot = Vectors.dotProduct(H, eye);
			if (shininess != 1.0) {
				specDot = MathExt.pow(specDot, shininess);
			}
			
			lightingValues.specularLight = specDot;
			if (lightingValues.specularLight < 0) {
		//		lightingValues.specularLight = 0;
			}
		}
		
		
		onLightLevels(latitude, longitude, elevation);
		
		
		lightingValues.emmisiveColor[0] = color[0] * lightingValues.emmisiveLight;
		lightingValues.emmisiveColor[1] = color[1] * lightingValues.emmisiveLight;
		lightingValues.emmisiveColor[2] = color[2] * lightingValues.emmisiveLight;
		

		
		lightingValues.ambientColor[0] = color[0] * lightingValues.ambientLight;
		lightingValues.ambientColor[1] = color[1] * lightingValues.ambientLight;
		lightingValues.ambientColor[2] = color[2] * lightingValues.ambientLight;
		
		lightingValues.diffuseColor[0] = lightingValues.diffuseLevel * color[0] * lightingValues.diffuseLight;
		lightingValues.diffuseColor[1] = lightingValues.diffuseLevel * color[1] * lightingValues.diffuseLight;
		lightingValues.diffuseColor[2] = lightingValues.diffuseLevel * color[2] * lightingValues.diffuseLight;
		
		lightingValues.specularColor[0] = lightingValues.specularLevel * specularColor[0] * lightingValues.specularLight;
		lightingValues.specularColor[1] = lightingValues.specularLevel * specularColor[1] * lightingValues.specularLight;
		lightingValues.specularColor[2] = lightingValues.specularLevel * specularColor[2] * lightingValues.specularLight;


		// Add and clamp color channels
		rgba[0] = (int) clamp(255.0 * (lightingValues.emmisiveColor[0] + lightingValues.ambientColor[0] + lightingValues.diffuseColor[0] + lightingValues.specularColor[0]));
		rgba[1] = (int) clamp(255.0 * (lightingValues.emmisiveColor[1] + lightingValues.ambientColor[1] + lightingValues.diffuseColor[1] + lightingValues.specularColor[1]));
		rgba[2] = (int) clamp(255.0 * (lightingValues.emmisiveColor[2] + lightingValues.ambientColor[2] + lightingValues.diffuseColor[2] + lightingValues.specularColor[2]));

	}
	
	
	protected double clamp(double c)
	{
		if (c > 255)
			c = 255;
		if (c < 0)
			c = 0;
		return c;
	}
	
	
	public void setEye(Vector eye)
	{
		eye.copyTo(this.eye);
	}
	
	public void setEye(double[] eye)
	{
		this.eye.x = eye[0];
		this.eye.y = eye[1];
		this.eye.z = eye[2];
		
	}
	
	public double getEmmisive()
	{
		return lightingValues.emmisiveLevel;
	}

	public void setEmmisive(double emmisive)
	{
		lightingValues.emmisiveLevel = emmisive;
		lightingValues.emmisiveLight = lightingValues.emmisiveLevel;
	}

	public double getAmbient()
	{
		return lightingValues.ambientLevel;
	}

	public void setAmbient(double ambient)
	{
		this.lightingValues.ambientLevel = ambient;
		lightingValues.ambientLight = lightingValues.ambientLevel;
	}

	public double getDiffuse()
	{
		return lightingValues.diffuseLevel;
	}

	public void setDiffuse(double diffuse)
	{
		lightingValues.diffuseLevel = diffuse;
	}

	public double getSpecular()
	{
		return lightingValues.specularLevel;
	}

	public void setSpecular(double specular)
	{
		lightingValues.specularLevel = specular;
	}

	public double getBlockShadowIntensity()
	{
		return blockShadowIntensity;
	}

	public void setBlockShadowIntensity(double blockShadowIntensity)
	{
		this.blockShadowIntensity = blockShadowIntensity;
	}

	public boolean getUseDistanceAttenuation()
	{
		return useDistanceAttenuation;
	}

	public void setUseDistanceAttenuation(boolean useDistanceAttenuation)
	{
		this.useDistanceAttenuation = useDistanceAttenuation;
	}

	public double getAttenuationRadius()
	{
		return attenuationRadius;
	}

	public void setAttenuationRadius(double attenuationRadius)
	{
		this.attenuationRadius = attenuationRadius;
	}

	
	protected void onLightLevels(double latitude, double longitude, double elevation)
	{
		
		if (scriptProxy != null) {
			try {
				scriptProxy.onLightLevels(latitude, longitude, elevation, lightingValues);
			} catch (ScriptingException ex) {
				log.error("Error running light levels callback: " + ex.getMessage(), ex);
			}
		}
	}
	
	
	public void setSpecularColor(double[] specularColor)
	{
		this.specularColor = specularColor;
	}
	
	public double[] getSpecularColor()
	{
		return this.specularColor;
	}
	
}	
