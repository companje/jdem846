package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class ItemIcon extends KmlElement
{
	private ItemIconModeEnum state = null;
	private String href = null;
	
	public ItemIcon()
	{
		
	}
	
	public ItemIcon(String href)
	{
		setHref(href);
	}
	
	public ItemIcon(String href, ItemIconModeEnum state)
	{
		setHref(href);
		setState(state);
	}
	
	
	public ItemIconModeEnum getState()
	{
		return state;
	}

	public void setState(ItemIconModeEnum state)
	{
		this.state = state;
	}

	public String getHref()
	{
		return href;
	}

	public void setHref(String href)
	{
		this.href = href;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (state != null) {
			element.addElement("state").addText(state.text());
		}
		
		if (href != null) {
			element.addElement("href").addText(href);
		}
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("ItemIcon");
		loadKmlChildren(element);
	}
}
