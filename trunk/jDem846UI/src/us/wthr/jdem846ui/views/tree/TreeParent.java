package us.wthr.jdem846ui.views.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;



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
	
	public Set<TreeObject<E>> getChildren() {
		return Sets.newHashSet(children);
	}
	
	public boolean hasChildren() {
		return children.size()>0;
	}
	
}
