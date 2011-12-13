package us.wthr.jdem846.ui;

import java.awt.BorderLayout;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Frame;

@SuppressWarnings("serial")
public class LogViewerDialog extends Frame
{
	private static Log log = Logging.getLog(LogViewerDialog.class);
	
	private LogViewer logViewer;
	
	public LogViewerDialog()
	{
		this.setTitle(I18N.get("us.wthr.jdem846.ui.logViewerDialog.title"));
		this.setSize(700, 300);
		this.setLocationRelativeTo(null);
		
		logViewer = new LogViewer();
		
		setLayout(new BorderLayout());
		add(logViewer, BorderLayout.CENTER);
	}
	
	
}
