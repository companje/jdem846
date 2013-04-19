package us.wthr.jdem846.jogl.view;

import java.util.ArrayList;
import java.util.List;

import us.wthr.jdem846.graphics.IColor;

public class Planet
{
	
	private IColor fogColor;
	private String surfaceTexture;
	private IColor materialLightColor;
	private String cloudsTexture;
	private double flattening = 1.0;
	private List<String> shapes = new ArrayList<String>();
	private List<Renderable> objects = new ArrayList<Renderable>();
	
	
	
	public Planet(String surfaceTexture, IColor materialLightColor, String cloudsTexture, IColor fogColor, double flattening)
	{
		this.surfaceTexture = surfaceTexture;
		this.materialLightColor = materialLightColor;
		this.cloudsTexture = cloudsTexture;
		this.fogColor = fogColor;
		this.flattening = flattening;
	}


	public String getSurfaceTexture()
	{
		return surfaceTexture;
	}


	public void setSurfaceTexture(String surfaceTexture)
	{
		this.surfaceTexture = surfaceTexture;
	}

	

	public IColor getMaterialLightColor()
	{
		return materialLightColor;
	}


	public void setMaterialLightColor(IColor materialLightColor)
	{
		this.materialLightColor = materialLightColor;
	}


	public String getCloudsTexture()
	{
		return cloudsTexture;
	}


	public void setCloudsTexture(String cloudsTexture)
	{
		this.cloudsTexture = cloudsTexture;
	}


	public List<String> getShapes()
	{
		return shapes;
	}


	public void setShapes(List<String> shapes)
	{
		this.shapes = shapes;
	}


	public IColor getFogColor()
	{
		return fogColor;
	}


	public void setFogColor(IColor fogColor)
	{
		this.fogColor = fogColor;
	}


	public double getFlattening()
	{
		return flattening;
	}


	public void setFlattening(double flattening)
	{
		this.flattening = flattening;
	}


	public List<Renderable> getObjects()
	{
		return objects;
	}


	public void setObjects(List<Renderable> objects)
	{
		this.objects = objects;
	}
	
	
	
}
