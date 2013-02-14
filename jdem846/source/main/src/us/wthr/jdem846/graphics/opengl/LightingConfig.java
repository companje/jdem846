package us.wthr.jdem846.graphics.opengl;

import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.math.Vector;

public class LightingConfig
{
	Vector lightPosition;
	IColor emission;
	IColor ambient;
	IColor diffuse;
	IColor specular;
	double shininess;
	
	public LightingConfig(Vector lightPosition, IColor emission, IColor ambient, IColor diffuse, IColor specular, double shininess)
	{
		this.lightPosition = lightPosition;
		this.emission = emission;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
	}
}
