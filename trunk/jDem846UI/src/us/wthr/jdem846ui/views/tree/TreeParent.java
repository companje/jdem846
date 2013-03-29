package us.wthr.jdem846ui.views.tree;

import java.util.List;

import us.wthr.jdem846.IDataObject;

import com.google.common.collect.Lists;



public class TreeParent<E extends IDataObject> extends TreeObject<E> {
	
	private List<TreeObject<E>> children = Lists.newLinkedList();

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
	
	public List<TreeObject<E>> getChildren() {
		return Lists.newLinkedList(children);
		//return Sets.newHashSet(children);
	}
	
	public boolean hasChildren() {
		return children.size()>0;
	}
	
}
