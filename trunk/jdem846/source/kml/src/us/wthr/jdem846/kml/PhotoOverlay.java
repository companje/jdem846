package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class PhotoOverlay extends Overlay
{
	// angle180
	private double rotation = 0;
	
	private ViewVolume viewVolume = null;
	private ImagePyramid imagePyramid = null;
	private Point point = null;
	private ShapeTypeEnum shape = null;
	
	
	public PhotoOverlay()
	{
		
	}
	
	
	
	public double getRotation()
	{
		return rotation;
	}



	public void setRotation(double rotation)
	{
		this.rotation = rotation;
	}



	public ViewVolume getViewVolume()
	{
		return viewVolume;
	}



	public void setViewVolume(ViewVolume viewVolume)
	{
		this.viewVolume = viewVolume;
	}



	public ImagePyramid getImagePyramid()
	{
		return imagePyramid;
	}



	public void setImagePyramid(ImagePyramid imagePyramid)
	{
		this.imagePyramid = imagePyramid;
	}



	public Point getPoint()
	{
		return point;
	}



	public void setPoint(Point point)
	{
		this.point = point;
	}



	public ShapeTypeEnum getShape()
	{
		return shape;
	}



	public void setShape(ShapeTypeEnum shape)
	{
		this.shape = shape;
	}



	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		element.addElement("rotation").addText(""+rotation);
		
		if (viewVolume != null) {
			viewVolume.toKml(element);
		}
		
		if (imagePyramid != null) {
			imagePyramid.toKml(element);
		}
		
		if (point != null) {
			point.toKml(element);
		}
		
		if (shape != null) {
			element.addElement("shape").addText(shape.text());
		}
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("PhotoOverlay");
		loadKmlChildren(element);
	}
}
