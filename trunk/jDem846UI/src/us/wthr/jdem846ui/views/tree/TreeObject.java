package us.wthr.jdem846ui.views.tree;


public class TreeObject {
	
	private String name;
	private TreeParent parent;
	
	public TreeObject(String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TreeParent getParent() {
		return parent;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	
	public boolean equals(TreeObject obj)
	{
		return this.name.equals(obj.name);
	}
	
	public String toString()
	{
		return this.name;
	}
	
}
