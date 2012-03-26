package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Frame;

@SuppressWarnings("serial")
public class LogViewerDialog extends Dialog
{
	private static Log log = Logging.getLog(LogViewerDialog.class);
	
	private LogViewer logViewer;
	
	public LogViewerDialog(Frame owner)
	{
		super(owner, I18N.get("us.wthr.jdem846.ui.logViewerDialog.title"), false);

		//this.setSize(700, 300);
		//this.setLocationRelativeTo(null);
		int frameWidth = JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.logViewerDialog.windowWidth");
		int frameHeight = JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.logViewerDialog.windowHeight");
		this.setSize(frameWidth, frameHeight);
		
		int topLeftX = JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.logViewerDialog.topLeftX");
		int topLeftY = JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.logViewerDialog.topLeftY");
		if (topLeftX == -9999 || topLeftY == -9999) {
			this.setLocationRelativeTo(null);
		} else {
			this.setLocation(topLeftX, topLeftY);
		}
		
		this.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e)
			{
				
			}
			public void componentMoved(ComponentEvent e)
			{
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.logViewerDialog.topLeftX", ""+getX());
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.logViewerDialog.topLeftY", ""+getY());
			}
			public void componentResized(ComponentEvent e)
			{
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.logViewerDialog.windowHeight", ""+getHeight());
				JDem846Properties.setProperty("us.wthr.jdem846.state.ui.logViewerDialog.windowWidth", ""+getWidth());
			}
			public void componentShown(ComponentEvent e)
			{
				
			}
		});
		
		
		logViewer = new LogViewer();
		
		setLayout(new BorderLayout());
		add(logViewer, BorderLayout.CENTER);
	}
	
	
}
