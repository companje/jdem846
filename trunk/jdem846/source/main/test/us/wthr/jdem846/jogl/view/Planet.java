package us.wthr.jdem846.jogl.view;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import us.wthr.jdem846.graphics.ExamineView;
import us.wthr.jdem846.graphics.IColor;

public class Planet implements Renderable
{
	
	private IColor fogColor;
	private String surfaceTexture;
	private IColor materialLightColor;
	private String cloudsTexture;
	private double flattening = 1.0;
	private List<String> shapes = new ArrayList<String>();
	private List<Renderable> objects = new ArrayList<Renderable>();
	
	private TexturedSphere surface;
	private TexturedSphere clouds;
	private boolean useLighting = true;
	
	public Planet(GLProfile glProfile, String surfaceTexture, IColor materialLightColor, String cloudsTexture, IColor fogColor, double flattening, boolean useLighting) throws Exception
	{
		this.surfaceTexture = surfaceTexture;
		this.materialLightColor = materialLightColor;
		this.cloudsTexture = cloudsTexture;
		this.fogColor = fogColor;
		this.flattening = flattening;
		this.useLighting = useLighting;

		if (getSurfaceTexture() != null) {
			surface = new TexturedSphere(glProfile, getSurfaceTexture(), 0.5, true);
		}

		if (getCloudsTexture() != null) {
			clouds = new TexturedSphere(glProfile, getCloudsTexture(), 0.5, true);
		}

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


	public boolean isUseLighting() {
		return useLighting;
	}


	public void setUseLighting(boolean useLighting) {
		this.useLighting = useLighting;
	}


	public void setObjects(List<Renderable> objects)
	{
		this.objects = objects;
	}

	
	
	
	@Override
	public void render(GL2 gl, GLU glu, ExamineView examineView) {
		
		if (surface != null) {
			surface.render(gl, glu, examineView);
		}
		
		if (clouds != null) {
			clouds.render(gl, glu, examineView);
		}
		
		for(Renderable renderable : getObjects()) {
			renderable.render(gl, glu, examineView);
		}
	}
	
	
	
}
