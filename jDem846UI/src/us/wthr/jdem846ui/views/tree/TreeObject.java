package us.wthr.jdem846ui.views.tree;

import us.wthr.jdem846ui.views.Selectable;


public class TreeObject<E> extends Selectable<E> {
	
	private String name;
	private TreeParent<E> parent;
	
	
	
	public TreeObject(String name, Class<E> clazz)
	{
		super(clazz);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TreeParent<E> getParent() {
		return parent;
	}

	public void setParent(TreeParent<E> parent) {
		this.parent = parent;
	}
	
	public boolean equals(TreeObject<E> obj)
	{
		return this.name.equals(obj.name);
	}
	
	public String toString()
	{
		return this.name;
	}
	
}
