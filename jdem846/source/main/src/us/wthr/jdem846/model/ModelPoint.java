package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.canvas.util.ColorUtil;

public class ModelPoint
{
	
	private double elevation = DemConstants.ELEV_NO_DATA;
	private double[] normal = new double[3];
	private int rgba;
	
	private List<ModelPointChangedListener> changeListeners = new LinkedList<ModelPointChangedListener>();
	
	public ModelPoint()
	{
		
	}

	public double getElevation()
	{
		return elevation;
	}

	public void setElevation(double elevation)
	{
		this.elevation = elevation;
	}

	public void getNormal(double[] fill)
	{
		fill[0] = normal[0];
		fill[1] = normal[1];
		fill[2] = normal[2];
	}
	
	public double[] getNormal()
	{
		return normal;
	}

	public void setNormal(double[] normal)
	{
		this.normal[0] = normal[0];
		this.normal[1] = normal[1];
		this.normal[2] = normal[2];
	}


	
	public void getRgba(int[] fill) 
	{
		ColorUtil.intToRGBA(getRgba(), fill);
	}
	
	public int getRgba()
	{
		return rgba;
	}

	public void setRgba(int rgba)
	{
		this.rgba = rgba;
	}
	

	public void setRgba(int[] rgba)
	{
		this.setRgba(ColorUtil.rgbaToInt(rgba));
	}
	
	
	public void setRgba(int r, int g, int b, int a)
	{
		this.setRgba(ColorUtil.rgbaToInt(r, g, b, a));
	}
	
	
	protected void fireModelPointChangedListeners()
	{
		for (ModelPointChangedListener listener : changeListeners)
		{
			listener.onModelPointChanged(this);
		}
	}
	
	public void addModelPointChangedListener(ModelPointChangedListener listener)
	{
		changeListeners.add(listener);
	}
	
	public void removeModelPointChangedListener(ModelPointChangedListener listener)
	{
		changeListeners.remove(listener);
	}
	
	public interface ModelPointChangedListener
	{
		public void onModelPointChanged(ModelPoint modelPoint);
	}
}
