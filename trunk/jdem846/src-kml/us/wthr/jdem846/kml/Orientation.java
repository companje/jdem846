package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class Orientation extends KmlElement
{
	
	private double heading = 0;
	private double tilt = 0;
	private double roll = 0;
	
	public Orientation()
	{
		
	}
	
	public Orientation(double heading)
	{
		setHeading(heading);
	}
	
	public Orientation(double heading, double tilt, double roll)
	{
		setHeading(heading);
		setTilt(tilt);
		setRoll(roll);
	} 
	
	public double getHeading()
	{
		return heading;
	}

	

	public void setHeading(double heading)
	{
		this.heading = heading;
	}



	public double getTilt()
	{
		return tilt;
	}



	public void setTilt(double tilt)
	{
		this.tilt = tilt;
	}



	public double getRoll()
	{
		return roll;
	}



	public void setRoll(double roll)
	{
		this.roll = roll;
	}



	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);

		element.addElement("heading").addText(""+heading);
		element.addElement("tilt").addText(""+tilt);
		element.addElement("roll").addText(""+roll);
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Orientation");
		loadKmlChildren(element);
	}
	
}
