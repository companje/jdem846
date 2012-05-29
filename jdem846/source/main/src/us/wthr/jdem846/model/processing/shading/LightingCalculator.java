package us.wthr.jdem846.model.processing.shading;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.model.ModelPoint;

public class LightingCalculator
{
	private static Log log = Logging.getLog(LightingCalculator.class);
	
	
	private double emmisive = 0;
	private double ambient = 0;
	private double diffuse = 0;
	private double specular = 0;
	
	private boolean useDistanceAttenuation = true;
	private double attenuationRadius = 2000;
	private double blockShadowIntensity = 0.4;
	
	private double[] emmisiveColor = new double[4];
	private double[] ambientColor = new double[4];
	private double[] diffuseColor = new double[4];
	private double[] specularColor = new double[4];
	
	double eye[] = {0.000001,0.000001,0.000001};
	
	double[] P = new double[3];
	double[] color = new double[4];
	double[] N = new double[3];
	double[] L =  new double[3];
	double[] V = new double[3];
	double[] H = new double[3];
	
	protected Perspectives perspectives = new Perspectives();
	
	public LightingCalculator()
	{
		
	}
	
	public LightingCalculator(double emmisive, double ambient, double diffuse, double specular, double blockShadowIntensity)
	{
		setEmmisive(emmisive);
		setAmbient(ambient);
		setDiffuse(diffuse);
		setSpecular(specular);
		setBlockShadowIntensity(blockShadowIntensity);
	}

	
	public void calculateColor(double[] normal, double latitude, double longitude, double radius, double shininess, double blockDistance, double[] lightSource, int[] rgba)
	{
		Spheres.getPoint3D(longitude+180, latitude, radius, P);
		//modelPoint.getNormal(N);
		
		N[0] = normal[0];
		N[1] = normal[1];
		N[2] = normal[2];
		
		color[0] = (double) rgba[0] / 255.0;
		color[1] = (double) rgba[1] / 255.0;
		color[2] = (double) rgba[2] / 255.0;

		
		emmisiveColor[0] = color[0] * emmisive;
		emmisiveColor[1] = color[1] * emmisive;
		emmisiveColor[2] = color[2] * emmisive;

		
		ambientColor[0] = color[0] * ambient;
		ambientColor[1] = color[1] * ambient;
		ambientColor[2] = color[2] * ambient;


		perspectives.subtract(lightSource, P, L);
		perspectives.normalize(L, L);
		double diffuseLight = perspectives.dotProduct(N, L);
		
		
		if (blockDistance > 0.0) {
			
			double r = attenuationRadius;
			double d = blockDistance;
			double kC = 1.0;
			double kL = 2.0 / r;
			double kQ = 1.0 / (r * r);
			
			
			double attenuation = (blockDistance > 0.0) ? 1.0 : 0.0;
			
			if (useDistanceAttenuation) {
				attenuation = 1.0 / (kC + kL * d + kQ * d * d);
			}
			
			attenuation *= blockShadowIntensity;
			
			diffuseLight = diffuseLight - (2 * attenuation * 1.0);
			if (diffuseLight < -1.0) {
				diffuseLight = -1.0;
			}

		}

		
		//double diffuseLight = (blocked) ? MathExt.min(0, perspectives.dotProduct(N, L)) : MathExt.max(0, perspectives.dotProduct(N, L));
		diffuseColor[0] = diffuse * color[0] * diffuseLight;
		diffuseColor[1] = diffuse * color[1] * diffuseLight;
		diffuseColor[2] = diffuse * color[2] * diffuseLight;

		
		
		perspectives.subtract(eye, P, V);
		perspectives.normalize(V, V);
		perspectives.add(lightSource, V, H);
		perspectives.normalize(H, H);
		
		double effectiveSpecular = (blockDistance > 0.0) ? 0.0 : specular;
		
		double specularLight = perspectives.dotProduct(N, H);
		specularLight = MathExt.pow(MathExt.max(0, specularLight), shininess);
		if (diffuseLight <= 0) 
			specularLight = 0;
		specularColor[0] = effectiveSpecular * 0.7 * specularLight;
		specularColor[1] = effectiveSpecular * 0.7 * specularLight;
		specularColor[2] = effectiveSpecular * 0.7 * specularLight;


		// Add and clamp color channels
		rgba[0] = (int) clamp(255.0 * (emmisiveColor[0] + ambientColor[0] + diffuseColor[0] + specularColor[0]));
		rgba[1] = (int) clamp(255.0 * (emmisiveColor[1] + ambientColor[1] + diffuseColor[1] + specularColor[1]));
		rgba[2] = (int) clamp(255.0 * (emmisiveColor[2] + ambientColor[2] + diffuseColor[2] + specularColor[2]));

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
		return emmisive;
	}

	public void setEmmisive(double emmisive)
	{
		this.emmisive = emmisive;
	}

	public double getAmbient()
	{
		return ambient;
	}

	public void setAmbient(double ambient)
	{
		this.ambient = ambient;
	}

	public double getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(double diffuse)
	{
		this.diffuse = diffuse;
	}

	public double getSpecular()
	{
		return specular;
	}

	public void setSpecular(double specular)
	{
		this.specular = specular;
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

	
	
	
	
}	
