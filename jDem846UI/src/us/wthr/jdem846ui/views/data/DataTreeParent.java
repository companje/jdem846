package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.IDataObject;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeParent;

public class DataTreeParent extends TreeParent<IDataObject> {

	private IconEnum icon;
	
	public DataTreeParent(String name, IconEnum icon) {
		super(name, IDataObject.class);
		this.icon = icon;
	}
	
	public IconEnum getIcon()
	{
		return icon;
	}

}
