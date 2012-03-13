package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.LogConsole.ConsoleUpdateListener;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.TextArea;

@SuppressWarnings("serial")
public class LogOutputPanel extends ScrollPane
{
	private static Log log = Logging.getLog(LogOutputPanel.class);
	
	//private ScrollPane scrollPane;
	private LogConsole console;
	//private TextArea txtLog;
	
	public LogOutputPanel()
	{
		//txtLog = new TextArea();
		//txtLog.setEditable(false);
		//scrollPane = new ScrollPane(txtLog);
		//this.setvi
		console = new LogConsole();
		this.getViewport().add(console);
		
		console.addConsoleUpdateListener(new ConsoleUpdateListener() {
			public void onUpdate()
			{
				scrollToBotton();
			}
		});
		
		
		//setLayout(new BorderLayout());
		//add(scrollPane, BorderLayout.CENTER);
	}
	
	
	public void clear()
	{
		console.clear();
	}
	

	
	
	protected void scrollToBotton()
	{
		getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
	}
}
