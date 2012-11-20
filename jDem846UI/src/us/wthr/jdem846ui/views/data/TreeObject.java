package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846ui.project.IconEnum;

public class TreeObject {
	private String name;
	private TreeParent parent;
	private IconEnum icon;
	private InputSourceData data;

	public TreeObject(String name, InputSourceData data, IconEnum icon) {
		this.name = name;
		this.icon = icon;
		this.data = data;
	}

	public String getName() {
		return name;
	}
	
	public InputSourceData getData() {
		return data;
	}

	public IconEnum getIcon() {
		return icon;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}
}
