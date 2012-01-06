package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.TextArea;

@SuppressWarnings("serial")
public class LogOutputPanel extends ScrollPane
{
	private static Log log = Logging.getLog(LogOutputPanel.class);
	
	private ScrollPane scrollPane;
	private TextArea txtLog;
	
	public LogOutputPanel()
	{
		txtLog = new TextArea();
		txtLog.setEditable(false);
		//scrollPane = new ScrollPane(txtLog);
		//this.setvi
		this.getViewport().add(txtLog);
		
		txtLog.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e)
			{
				scrollToBotton();
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
				append(formatted);
				
			}
		});
		
		
		//setLayout(new BorderLayout());
		//add(scrollPane, BorderLayout.CENTER);
	}
	
	
	public void clear()
	{
		txtLog.setText("");
	}
	
	protected void append(String record)
	{
		txtLog.setText(txtLog.getText() + record);
		txtLog.setCaretPosition(txtLog.getText().length());
	}
	
	
	protected void scrollToBotton()
	{
		getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
	}
}
