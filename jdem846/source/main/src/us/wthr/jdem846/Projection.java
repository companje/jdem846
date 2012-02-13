package us.wthr.jdem846;

public class Projection
{
	
	private double rotateX = 0;
	private double rotateY = 0;
	private double rotateZ = 0;
	
	private double shiftX = 0;
	private double shiftY = 0;
	private double shiftZ = 0;
	
	private double zoom = 1.0;
	
	public Projection()
	{
		
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

	public Projection copy()
	{
		Projection copy = new Projection();
		
		copy.rotateX = this.rotateX;
		copy.rotateY = this.rotateY;
		copy.rotateZ = this.rotateZ;
		
		copy.shiftX = this.shiftX;
		copy.shiftY = this.shiftY;
		copy.shiftZ = this.shiftZ;
		copy.zoom = this.zoom;
		
		return copy;            
	}
	
}
