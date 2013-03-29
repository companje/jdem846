package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.IDataObject;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeObject;

public class DataTreeObject<T> extends TreeObject<IDataObject> {

	private IconEnum icon;
	private T data;
	protected TreeSelectionListener selectionListener;
	
	public DataTreeObject(String name, T data, IconEnum icon, TreeSelectionListener selectionListener) {
		super(name, (Class<IDataObject>) data.getClass());
		this.icon = icon;
		this.data = data;
		this.selectionListener = selectionListener;
	}

	
	public T getData() {
		return data;
	}

	public IconEnum getIcon() {
		return icon;
	}

	public String toString() {
		return getName();
	}
}
