package us.wthr.jdem846.model;

import java.util.Map;

public class ViewPerspective
{
	private double rotateX = 0;
	private double rotateY = 0;
	private double rotateZ = 0;
	
	private double shiftX = 0;
	private double shiftY = 0;
	private double shiftZ = 0;
	
	private double zoom = 1.0;
	
	public ViewPerspective()
	{
		
	}
	
	public ViewPerspective(double rotateX, 
							double rotateY, 
							double rotateZ,
							double shiftX,
							double shiftY,
							double shiftZ,
							double zoom)
	{
		setRotateX(rotateX);
		setRotateY(rotateY);
		setRotateZ(rotateZ);
		setShiftX(shiftX);
		setShiftY(shiftY);
		setShiftZ(shiftZ);
		setZoom(zoom);
	}

	public double getRotateX()
	{
		return rotateX;
	}

	public void setRotateX(double rotateX)
	{
		this.rotateX = rotateX;
	}

	public double getRotateY()
	{
		return rotateY;
	}

	public void setRotateY(double rotateY)
	{
		this.rotateY = rotateY;
	}

	public double getRotateZ()
	{
		return rotateZ;
	}

	public void setRotateZ(double rotateZ)
	{
		this.rotateZ = rotateZ;
	}

	public double getShiftX()
	{
		return shiftX;
	}

	public void setShiftX(double shiftX)
	{
		this.shiftX = shiftX;
	}

	public double getShiftY()
	{
		return shiftY;
	}

	public void setShiftY(double shiftY)
	{
		this.shiftY = shiftY;
	}

	public double getShiftZ()
	{
		return shiftZ;
	}

	public void setShiftZ(double shiftZ)
	{
		this.shiftZ = shiftZ;
	}

	public double getZoom()
	{
		return zoom;
	}

	public void setZoom(double zoom)
	{
		this.zoom = zoom;
	}
	
	public static ViewPerspective fromString(String s)
	{
		Map<String, double[]> values = SimpleNumberListMapSerializer.parseDoubleListString(s);
		
		double[] rotate = values.get("rotate");
		double[] shift = values.get("shift");
		double[] zoom = values.get("zoom");
		
		ViewPerspective viewPerspective = new ViewPerspective();
		
		viewPerspective.setRotateX(rotate[0]);
		viewPerspective.setRotateY(rotate[1]);
		viewPerspective.setRotateZ(rotate[2]);
		
		viewPerspective.setShiftX(shift[0]);
		viewPerspective.setShiftY(shift[1]);
		viewPerspective.setShiftZ(shift[2]);
		
		viewPerspective.setZoom(zoom[0]);
		
		return viewPerspective;
		
	}
	
	public String toString()
	{
		String s = "rotate:[" + 
					getRotateX() + "," +
					getRotateY() + "," +
					getRotateZ() + "];" +
					"shift:[" +
					getShiftX() + "," +
					getShiftY() + "," + 
					getShiftZ() + "];" +
					"zoom:[" + getZoom() + "]";
		return s;
	}
	
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof ViewPerspective) {
			ViewPerspective other = (ViewPerspective) obj;
			
			if (other.rotateX == this.rotateX 
					&& other.rotateY == this.rotateY
					&& other.rotateZ == this.rotateZ
					&& other.shiftX == this.shiftX
					&& other.shiftY == this.shiftY
					&& other.shiftZ == this.shiftZ
					&& other.zoom == this.zoom) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}
	
	
	public ViewPerspective copy()
	{
		ViewPerspective copy = new ViewPerspective();
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
