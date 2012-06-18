package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.scripting.ScriptProxy;

public class LightingCalculator
{
	private static Log log = Logging.getLog(LightingCalculator.class);
	
	private LightingValues lightingValues;

	private double[] emmisiveColor = {1.0, 1.0, 1.0, 1.0};
	private double[] ambientColor = {1.0, 1.0, 1.0, 1.0};
	private double[] diffuseColor = {1.0, 1.0, 1.0, 1.0};
	private double[] specularColor = {1.0, 1.0, 1.0, 1.0};
	
	private boolean useDistanceAttenuation = true;
	private double attenuationRadius = 2000;
	private double blockShadowIntensity = 0.4;

	//double eye[] = {0.000001,0.000001,0.000001};
	double eye[] = {0.0, 0.0, 0.0};
	
	double[] P = new double[3];
	double[] color = new double[4];
	double[] N = new double[3];
	double[] L =  new double[3];
	double[] V = new double[3];
	double[] H = new double[3];
	
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
		
		//Vectors.rotate(0.0, viewPerspective.getRotateY(), 0.0, eye);
		//Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0.0, eye);
	}

	
	public void calculateColor(double[] normal, double latitude, double longitude, double radius, double shininess, double blockDistance, double[] lightSource, int[] rgba)
	{
		Spheres.getPoint3D(longitude, latitude, radius, P);
		
		//Vectors.rotate(0.0, viewPerspective.getRotateY(), 0.0, P);
		//Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0.0, P);
		
		//modelPoint.getNormal(N);
		
		N[0] = normal[0];
		N[1] = normal[1];
		N[2] = normal[2];
		
		color[0] = (double) rgba[0] / 255.0;
		color[1] = (double) rgba[1] / 255.0;
		color[2] = (double) rgba[2] / 255.0;


		Vectors.subtract(lightSource, P, L);
		Vectors.normalize(L, L);
		lightingValues.diffuseLight = MathExt.max(0, Vectors.dotProduct(N, L));
		
		
		if (blockDistance > 0.0) {
			
			double r = attenuationRadius;
			double d = blockDistance;
			double kC = 1.0;
			double kL = 2.0 / r;
			double kQ = 1.0 / (r * r);
			
			if (useDistanceAttenuation) {
				double attenuation = (blockDistance > 0.0) ? 1.0 : 0.0;
				attenuation = 1.0 / (kC + kL * d + kQ * d * d);
				attenuation *= blockShadowIntensity;
				lightingValues.diffuseLight = lightingValues.diffuseLight - (2 * attenuation * 1.0);
			}
			
			
			if (lightingValues.diffuseLight < -1.0) {
				lightingValues.diffuseLight = -1.0;
			}

		}


		
		Vectors.subtract(eye, P, V);
		Vectors.normalize(V, V);
		Vectors.add(lightSource, V, H);
		Vectors.normalize(H, H);
		
		double effectiveSpecular = (blockDistance > 0.0) ? 0.0 : lightingValues.specularLevel;
		
		lightingValues.specularLight = Vectors.dotProduct(N, H);
		lightingValues.specularLight = MathExt.pow(MathExt.max(0, lightingValues.specularLight), shininess);
		if (lightingValues.diffuseLight <= 0) 
			lightingValues.specularLight = 0;
		
		
		
		onLightLevels(latitude, longitude);
		
		
		lightingValues.emmisiveColor[0] = color[0] * lightingValues.emmisiveLight;
		lightingValues.emmisiveColor[1] = color[1] * lightingValues.emmisiveLight;
		lightingValues.emmisiveColor[2] = color[2] * lightingValues.emmisiveLight;
		

		
		lightingValues.ambientColor[0] = color[0] * lightingValues.ambientLight;
		lightingValues.ambientColor[1] = color[1] * lightingValues.ambientLight;
		lightingValues.ambientColor[2] = color[2] * lightingValues.ambientLight;
		
		lightingValues.diffuseColor[0] = lightingValues.diffuseLevel * color[0] * lightingValues.diffuseLight;
		lightingValues.diffuseColor[1] = lightingValues.diffuseLevel * color[1] * lightingValues.diffuseLight;
		lightingValues.diffuseColor[2] = lightingValues.diffuseLevel * color[2] * lightingValues.diffuseLight;
		
		lightingValues.specularColor[0] = effectiveSpecular * specularColor[0] * lightingValues.specularLight;
		lightingValues.specularColor[1] = effectiveSpecular * specularColor[1] * lightingValues.specularLight;
		lightingValues.specularColor[2] = effectiveSpecular * specularColor[2] * lightingValues.specularLight;


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

	
	protected void onLightLevels(double latitude, double longitude)
	{
		
		if (scriptProxy != null) {
			scriptProxy.onLightLevels(latitude, longitude, lightingValues);
		}
	}
	
	
}	
