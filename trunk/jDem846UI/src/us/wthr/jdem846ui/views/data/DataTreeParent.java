package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeParent;

public class DataTreeParent extends TreeParent {

	private IconEnum icon;
	
	public DataTreeParent(String name, IconEnum icon) {
		super(name);
		this.icon = icon;
	}
	
	public IconEnum getIcon()
	{
		return icon;
	}

}
