package us.wthr.jdem846.swtui;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;

import us.wthr.jdem846.i18n.I18N;

public class ValueMonitoredScale extends Composite
{
	
	private Scale scale;
	private Label label;
	private MonitoredValueListener valueListener;
	
	public ValueMonitoredScale(Composite parent, MonitoredValueListener _valueListener)
	{
		super(parent, SWT.NONE);
		this.valueListener = _valueListener;
		
		setLayout(new GridLayout(2, false));
		
		scale = new Scale(this, SWT.FLAT | SWT.NO_BACKGROUND);
		scale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event arg0)
			{
				onValueChanged();
			}
		});
		scale.pack();
		
		label = new Label(this, SWT.FLAT);
		label.setText("");
		label.pack();
		
		pack();
	}
	
	protected void onValueChanged()
	{
		label.setText(valueListener.getValueString(scale.getSelection()));
		label.pack();
		label.getParent().pack();
	}
	
	public void setMinimum(int value)
	{
		scale.setMinimum(value);
	}
	
	public int getMinimum()
	{
		return scale.getMinimum();
	}
	
	public void setMaximum(int value)
	{
		scale.setMaximum(value);
	}
	
	public int getMaximum()
	{
		return scale.getMaximum();
	}
	
	public void setIncrement(int value)
	{
		scale.setIncrement(value);
	}
	
	public int getIncrement()
	{
		return scale.getIncrement();
	}
	
	public void setPageIncrement(int value)
	{
		scale.setPageIncrement(value);
	}
	
	public int getPageIncrement()
	{
		return scale.getPageIncrement();
	}
	
	public void setSelection(int value)
	{
		scale.setSelection(value);
		onValueChanged();
	}
	
	public int getSelection()
	{
		return scale.getSelection();
	}
	
	public interface MonitoredValueListener
	{
		public String getValueString(int value);
	}
	
	public void addSelectionListener(SelectionListener listener)
	{
		scale.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener)
	{
		scale.removeSelectionListener(listener);
	}
	
}
