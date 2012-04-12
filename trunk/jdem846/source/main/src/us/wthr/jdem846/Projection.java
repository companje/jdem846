package us.wthr.jdem846;

import us.wthr.jdem846.model.ViewPerspective;

@Deprecated
public class Projection extends ViewPerspective
{

	public Projection()
	{
		
	}


	public Projection copy()
	{
		Projection copy = new Projection();
		
		copy.setRotateX(getRotateX());
		copy.setRotateY(getRotateY());
		copy.setRotateZ(getRotateZ());
		
		copy.setShiftX(getShiftX());
		copy.setShiftY(getShiftY());
		copy.setShiftZ(getShiftZ());
		
		copy.setZoom(getZoom());
		
		return copy;            
	}
	
}
