package us.wthr.jdem846ui.views;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846ui.View;
import us.wthr.jdem846ui.actions.ActionListener;
import us.wthr.jdem846ui.actions.logview.ClearLogConsoleAction;

public class LogConsoleView extends ViewPart
{
	private static Log log = Logging.getLog(LogConsoleView.class);
	
	public static final String ID = "jdem846ui.logConsoleView";
	
	private ClearLogConsoleAction clearLogConsoleAction;
	
	private Text logText;
	
	@Override
	public void createPartControl(Composite parent) 
	{
		logText = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		logText.setEditable(false);
		FontData defaultFont = new FontData("Courier New",8,SWT.NORMAL);
		logText.setFont(new Font(parent.getDisplay(), defaultFont));
		logText.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
		
		this.clearLogConsoleAction = new ClearLogConsoleAction("Clear", View.ID);
		IActionBars actionBars = getViewSite().getActionBars();
		IMenuManager dropDownMenu = actionBars.getMenuManager();
		IToolBarManager toolBar = actionBars.getToolBarManager();
		dropDownMenu.add(clearLogConsoleAction);
		toolBar.add(clearLogConsoleAction);
		   
		clearLogConsoleAction.addActionListener(new ActionListener() {
			public void onAction() {
				logText.setText("");
			}
		});
		   
		Logging.addHandler(new Handler() {
			public void close() throws SecurityException
			{
				
			}
			public void flush()
			{
				
			}
			public void publish(LogRecord record)
			{
				String formatted = this.getFormatter().format(record);
				logText.append(formatted);
			}
		});
		
		
		log.info("Test A");
		log.info("Test B");
	}

	@Override
	public void setFocus() {
		logText.setFocus();
	}

}
