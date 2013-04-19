package us.wthr.jdem846.jogl.view;

import us.wthr.jdem846.graphics.Color;
import us.wthr.jdem846.graphics.IColor;

public class HaloColoring
{
	private IColor colorLower = new Color("688AB0FF");
	private IColor colorUpper = new Color("688AB0FF");
	private IColor colorFaded = new Color("688AB000");
	
	private IColor emissive = new Color("#000000FF");
	private IColor ambient = new Color("#0F161EFF");
	private IColor diffuse = new Color("#688AB0FF");
	private IColor specular = new Color("#0000000");
	private double shininess = 5.0;
	
	public HaloColoring()
	{
		
	}

	public IColor getColorLower()
	{
		return colorLower;
	}

	public void setColorLower(IColor colorLower)
	{
		this.colorLower = colorLower;
	}

	public IColor getColorUpper()
	{
		return colorUpper;
	}

	public void setColorUpper(IColor colorUpper)
	{
		this.colorUpper = colorUpper;
	}

	public IColor getColorFaded()
	{
		return colorFaded;
	}

	public void setColorFaded(IColor colorFaded)
	{
		this.colorFaded = colorFaded;
	}

	public IColor getEmissive()
	{
		return emissive;
	}

	public void setEmissive(IColor emissive)
	{
		this.emissive = emissive;
	}

	public IColor getAmbient()
	{
		return ambient;
	}

	public void setAmbient(IColor ambient)
	{
		this.ambient = ambient;
	}

	public IColor getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(IColor diffuse)
	{
		this.diffuse = diffuse;
	}

	public IColor getSpecular()
	{
		return specular;
	}

	public void setSpecular(IColor specular)
	{
		this.specular = specular;
	}

	public double getShininess()
	{
		return shininess;
	}

	public void setShininess(double shininess)
	{
		this.shininess = shininess;
	}
	
	
	
	
}
