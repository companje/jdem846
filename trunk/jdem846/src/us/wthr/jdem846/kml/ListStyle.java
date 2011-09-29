package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

public class ListStyle extends SubStyle
{
	
	private ListItemTypeEnum listItemType = null;
	private String bgColor = null;
	private List<ItemIcon> itemIcons = new LinkedList<ItemIcon>();
	
	
	public ListStyle()
	{
		
	}

	public ListStyle(ListItemTypeEnum listItemType)
	{
		this.listItemType = listItemType;
	}
	
	public ListItemTypeEnum getListItemType()
	{
		return listItemType;
	}

	public void setListItemType(ListItemTypeEnum listItemType)
	{
		this.listItemType = listItemType;
	}

	public String getBgColor()
	{
		return bgColor;
	}

	public void setBgColor(String bgColor)
	{
		this.bgColor = bgColor;
	}
	
	public void addItemIcon(ItemIcon itemIcon)
	{
		itemIcons.add(itemIcon);
	}
	
	public boolean removeItemIcon(ItemIcon itemIcon)
	{
		return itemIcons.remove(itemIcon);
	}

	public List<ItemIcon> getItemIcons()
	{
		return itemIcons;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (listItemType != null) {
			element.addElement("listItemType").addText(listItemType.text());
		}
		
		if (bgColor != null) {
			element.addElement("bgColor").addText(bgColor);
		}
		
		for (ItemIcon itemIcon : itemIcons) {
			itemIcon.toKml(element);
		}
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("ListStyle");
		loadKmlChildren(element);
	}
	

}
