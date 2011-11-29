package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class Scale extends KmlElement
{
	private double x = 1.0;
	private double y = 1.0;
	private double z = 1.0;
	
	public Scale()
	{
		
	}
	
	public Scale(double x, double y, double z)
	{
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getZ()
	{
		return z;
	}

	public void setZ(double z)
	{
		this.z = z;
	}

	public void toKml(Element parent)
	{
		Element element = parent.addElement("Scale");
		
		element.addElement("x").addText(""+x);
		element.addElement("y").addText(""+y);
		element.addElement("z").addText(""+z);
	}
}
