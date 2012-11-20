package us.wthr.jdem846ui.views.data;

import java.util.ArrayList;

import us.wthr.jdem846ui.project.IconEnum;

public class TreeParent extends TreeObject {
	private ArrayList children;

	public TreeParent(String name, IconEnum icon) {
		super(name, null, icon);
		children = new ArrayList();
	}
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}
	public boolean hasChildren() {
		return children.size()>0;
	}
}
