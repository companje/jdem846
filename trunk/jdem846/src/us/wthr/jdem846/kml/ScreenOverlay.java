package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class ScreenOverlay extends Overlay
{
	private double rotation;
	private Vec2Type overlayXY = null;
	private Vec2Type screenXY = null;
	private Vec2Type rotationXY = null;
	private Vec2Type size = null;
	
	public ScreenOverlay()
	{
		
	}
	
	
	public Vec2Type getOverlayXY()
	{
		return overlayXY;
	}


	public void setOverlayXY(Vec2Type overlayXY)
	{
		this.overlayXY = overlayXY;
	}


	public Vec2Type getScreenXY()
	{
		return screenXY;
	}


	public void setScreenXY(Vec2Type screenXY)
	{
		this.screenXY = screenXY;
	}


	public Vec2Type getRotationXY()
	{
		return rotationXY;
	}


	public void setRotationXY(Vec2Type rotationXY)
	{
		this.rotationXY = rotationXY;
	}


	public Vec2Type getSize()
	{
		return size;
	}


	public void setSize(Vec2Type size)
	{
		this.size = size;
	}


	public double getRotation()
	{
		return rotation;
	}


	public void setRotation(double rotation)
	{
		this.rotation = rotation;
	}


	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (overlayXY != null) {
			overlayXY.setFieldName("overlayXY");
			overlayXY.toKml(element);
		}
		
		if (screenXY != null) {
			screenXY.setFieldName("screenXY");
			screenXY.toKml(element);
		}
		
		if (rotationXY != null) {
			rotationXY.setFieldName("rotationXY");
			rotationXY.toKml(element);
		}
		
		if (size != null) {
			size.setFieldName("size");
			size.toKml(element);
		}
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("ScreenOverlay");
		loadKmlChildren(element);
	}
}
