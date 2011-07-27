package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class MenuItemFactory
{
	private static Log log = Logging.getLog(MenuItemFactory.class);
	private static JDem846Properties properties;
	
	static {
		properties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
	}
	
	public static MenuItem createMenuItem(Menu submenu, String text, Listener selectionListener)
	{
		return MenuItemFactory.createMenuItem(submenu, null, text, selectionListener, -1);
	}
	
	public static MenuItem createMenuItem(Menu submenu, String text, Listener selectionListener, int accel)
	{
		return MenuItemFactory.createMenuItem(submenu, null, text, selectionListener, accel);
	}
	
	public static MenuItem createMenuItem(Menu submenu, Image image, String text, Listener selectionListener)
	{
		return MenuItemFactory.createMenuItem(submenu, image, text, selectionListener, -1);
	}
	
	
	
	public static MenuItem createMenuItem(Menu submenu, Image image, String text, Listener selectionListener, int accel)
	{
		MenuItem item = new MenuItem (submenu, SWT.PUSH);
		

		item.setText(text);

		
		if (image != null) {
			item.setImage(image);
		}
		
		if (selectionListener != null) {
			item.addListener(SWT.Selection, selectionListener);
		}

		if (accel != -1) {
			item.setAccelerator(accel);
		}
		
		return item;
	}
	
	
}
