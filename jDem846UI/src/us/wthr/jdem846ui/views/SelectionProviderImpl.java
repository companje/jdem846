package us.wthr.jdem846ui.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class SelectionProviderImpl implements ISelectionProvider
{
	
	private List<ISelectionChangedListener> selectionChangeListeners = new LinkedList<ISelectionChangedListener>();
	
	private ISelection selection = null;
	
	public SelectionProviderImpl()
	{
		
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) 
	{
		this.selectionChangeListeners.add(listener);
	}

	@Override
	public ISelection getSelection() 
	{
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.selectionChangeListeners.remove(listener);
	}
	
	
	public void setSelection(Selectable<?> selection)
	{
		setSelection(new SelectionImpl(selection));
	}
	
	@Override
	public void setSelection(ISelection selection) 
	{
		this.selection = selection;
		fireSelectionChangedListeners();
	}
	
	protected void fireSelectionChangedListeners()
	{
		SelectionChangedEvent e = new SelectionChangedEvent(this, selection);
		
		for (ISelectionChangedListener listener : this.selectionChangeListeners) {
			listener.selectionChanged(e);
		}
	}

}
