package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ToolItemFactory
{
	private static Log log = Logging.getLog(ToolItemFactory.class);
	
	private static JDem846Properties properties;
	
	static {
		properties = new JDem846Properties(JDem846Properties.UI_PROPERTIES);
	}

	public static ToolItem createToolItem(ToolBar toolBar, String imageResourcePath, String text, String toolTipText, Listener selectionListener)
	{
		Image image = ImageFactory.loadImageResource(imageResourcePath);
		return createToolItem(toolBar, image, text, toolTipText, selectionListener);
	}
	
	public static ToolItem createToolItem(ToolBar toolBar, String text, String toolTipText, Listener selectionListener)
	{
		return createToolItem(toolBar, (Image)null, text, toolTipText, selectionListener);
	}
	
	public static ToolItem createToolItem(ToolBar toolBar, Image image, String text, String toolTipText, Listener selectionListener)
	{
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		
		if (image != null) {
			toolItem.setImage(image);
		}
		
		if (text != null && properties.getBooleanProperty("us.wthr.jdem846.ui.mainToolBar.displayText"))
			toolItem.setText(text);
		
		if (toolTipText != null) {
			toolItem.setToolTipText(toolTipText);
		}
		
		if (selectionListener != null) {
			toolItem.addListener(SWT.Selection, selectionListener);
		}
		
		return toolItem;
	}
	
}
