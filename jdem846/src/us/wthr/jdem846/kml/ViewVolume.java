package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class ViewVolume extends KmlElement
{


	
	// angle180
	private double leftFov = 0;
	
	// angle180
	private double rightFov = 0;
	
	// angle90
	private double bottomFov = 0;
	
	// angle90
	private double topFov = 0;
	
	private double near = 0;
	
	public ViewVolume()
	{
		
	}

	
	public double getLeftFov()
	{
		return leftFov;
	}


	public void setLeftFov(double leftFov)
	{
		this.leftFov = leftFov;
	}


	public double getRightFov()
	{
		return rightFov;
	}


	public void setRightFov(double rightFov)
	{
		this.rightFov = rightFov;
	}


	public double getBottomFov()
	{
		return bottomFov;
	}


	public void setBottomFov(double bottomFov)
	{
		this.bottomFov = bottomFov;
	}


	public double getTopFov()
	{
		return topFov;
	}


	public void setTopFov(double topFov)
	{
		this.topFov = topFov;
	}


	public double getNear()
	{
		return near;
	}


	public void setNear(double near)
	{
		this.near = near;
	}


	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		element.addElement("leftFov").setText(""+leftFov);
		element.addElement("rightFov").setText(""+rightFov);
		element.addElement("bottomFov").setText(""+bottomFov);
		element.addElement("topFov").setText(""+topFov);
		element.addElement("near").setText(""+near);
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("ViewVolume");
		loadKmlChildren(element);
	}
	
}
