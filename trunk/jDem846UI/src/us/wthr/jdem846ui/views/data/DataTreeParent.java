package us.wthr.jdem846ui.views.data;

import us.wthr.jdem846.IDataObject;
import us.wthr.jdem846ui.project.IconEnum;
import us.wthr.jdem846ui.views.tree.TreeParent;

public class DataTreeParent extends TreeParent<IDataObject> {

	private IconEnum icon;
	private TreeSelectionListener selectionListener;
	
	public DataTreeParent(String name, IconEnum icon, TreeSelectionListener selectionListener) {
		super(name, null);
		this.icon = icon;
		this.selectionListener = selectionListener;
	}
	
	public IconEnum getIcon()
	{
		return icon;
	}

	@Override
	public void onSelected()
	{
		selectionListener.onRenderedModelSelectionChanged(null);
		selectionListener.onSourceDataSelectionChanged(null);
	}
	
	
}
