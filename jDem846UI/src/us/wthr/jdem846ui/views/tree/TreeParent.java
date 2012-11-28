package us.wthr.jdem846ui.views.tree;

import java.util.ArrayList;
import java.util.List;



public class TreeParent<E> extends TreeObject<E> {
	
	private List<TreeObject<E>> children = new ArrayList<TreeObject<E>>();

	public TreeParent(String name, Class<E> clazz) {
		super(name, clazz);
	}
	
	public void addChild(TreeObject<E> child) {
		children.add(child);
		child.setParent(this);
	}
	
	public void removeChild(TreeObject<E> child) {
		children.remove(child);
		child.setParent(null);
	}
	
	public TreeObject<E>[] getChildren() {
		return (TreeObject<E>[]) children.toArray(new TreeObject<?>[children.size()]);
	}
	
	public boolean hasChildren() {
		return children.size()>0;
	}
	
}
