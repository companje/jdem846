package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeObject;

public class DataTreeObject extends TreeObject<InputSourceData> {

	private IconEnum icon;
	private InputSourceData data;

	public DataTreeObject(String name, InputSourceData data, IconEnum icon) {
		super(name, InputSourceData.class);
		this.icon = icon;
		this.data = data;
	}

	
	public InputSourceData getData() {
		return data;
	}

	public IconEnum getIcon() {
		return icon;
	}

	public String toString() {
		return getName();
	}
}
