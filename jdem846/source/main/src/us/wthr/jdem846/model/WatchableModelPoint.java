package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.canvas.util.ColorUtil;

public class WatchableModelPoint extends BasicModelPoint
{
	
	private List<ModelPointChangedListener> changeListeners = new LinkedList<ModelPointChangedListener>();
	
	public WatchableModelPoint()
	{
		
	}
	
	
	public void setElevation(double elevation)
	{
		super.setElevation(elevation);
		fireModelPointChangedListeners();
	}

	public void setElevation(float elevation)
	{
		super.setElevation(elevation);
		fireModelPointChangedListeners();
	}


	
	public void setNormal(double[] normal)
	{
		this.setNormal(normal);
		fireModelPointChangedListeners();
	}
	
	public void setNormal(float[] normal)
	{
		this.setNormal(normal);
		fireModelPointChangedListeners();
	}



	public void setRgba(int rgba)
	{
		this.setRgba(rgba);
		fireModelPointChangedListeners();
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
