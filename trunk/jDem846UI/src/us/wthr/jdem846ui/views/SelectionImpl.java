package us.wthr.jdem846ui.views;

import org.eclipse.jface.viewers.ISelection;

public class SelectionImpl implements ISelection
{

	private Selectable<?> selection;
	
	public SelectionImpl()
	{
		this(null);
	}
	
	public SelectionImpl(Selectable<?> selection)
	{
		this.selection = selection;
	}
	
	
	
	@Override
	public boolean isEmpty()
	{
		return selection == null;
	}

}
