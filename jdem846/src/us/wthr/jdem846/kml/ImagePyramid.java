package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class ImagePyramid extends KmlElement
{

	private int tileSize = 256;
	private int maxWidth = -1;
	private int maxHeight = -1;
	private GridOriginEnum gridOrigin = GridOriginEnum.LOWER_LEFT;
	
	public ImagePyramid()
	{
		
	}
	

	public int getTileSize()
	{
		return tileSize;
	}


	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
	}


	public int getMaxWidth()
	{
		return maxWidth;
	}


	public void setMaxWidth(int maxWidth)
	{
		this.maxWidth = maxWidth;
	}


	public int getMaxHeight()
	{
		return maxHeight;
	}


	public void setMaxHeight(int maxHeight)
	{
		this.maxHeight = maxHeight;
	}


	public GridOriginEnum getGridOrigin()
	{
		return gridOrigin;
	}


	public void setGridOrigin(GridOriginEnum gridOrigin)
	{
		this.gridOrigin = gridOrigin;
	}


	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		element.addElement("tileSize").addText(""+tileSize);
		element.addElement("maxWidth").addText(""+maxWidth);
		element.addElement("maxHeight").addText(""+maxHeight);
		
		if (gridOrigin != null) {
			element.addElement("gridOrigin").addText(gridOrigin.text());
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("ImagePyramid");
		loadKmlChildren(element);
	}

}
