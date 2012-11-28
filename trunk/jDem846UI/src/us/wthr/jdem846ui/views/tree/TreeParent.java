package us.wthr.jdem846ui.views.tree;

import java.util.ArrayList;
import java.util.List;



public class TreeParent extends TreeObject {
	
	private List<TreeObject> children = new ArrayList<TreeObject>();

	public TreeParent(String name) {
		super(name);
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
