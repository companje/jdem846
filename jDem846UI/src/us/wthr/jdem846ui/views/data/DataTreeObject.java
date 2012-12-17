package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.IDataObject;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeObject;

public class DataTreeObject extends TreeObject<IDataObject> {

	private IconEnum icon;
	private IDataObject data;

	public DataTreeObject(String name, IDataObject data, IconEnum icon) {
		super(name, IDataObject.class);
		this.icon = icon;
		this.data = data;
	}

	
	public IDataObject getData() {
		return data;
	}

	public IconEnum getIcon() {
		return icon;
	}

	public String toString() {
		return getName();
	}
}
