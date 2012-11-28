package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeParent;

public class DataTreeParent extends TreeParent<InputSourceData> {

	private IconEnum icon;
	
	public DataTreeParent(String name, IconEnum icon) {
		super(name, InputSourceData.class);
		this.icon = icon;
	}
	
	public IconEnum getIcon()
	{
		return icon;
	}

}
