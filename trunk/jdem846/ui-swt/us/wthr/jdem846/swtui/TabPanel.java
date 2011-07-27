package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public abstract class TabPanel extends Composite
{
	private static Log log = Logging.getLog(TabPanel.class);
	private String title;
	
	
	public TabPanel(Composite parent)
	{
		this(parent, null);
	}
	
	public TabPanel(Composite parent, String _title)
	{
		super(parent, SWT.NONE);
		this.title = _title;

	}
	
	public abstract void onPanelVisible();
	public abstract void onPanelHidden();

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	
	
	
}
