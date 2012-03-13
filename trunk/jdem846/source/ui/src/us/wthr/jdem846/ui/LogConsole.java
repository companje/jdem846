package us.wthr.jdem846.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.PropertiesChangeListener;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.TextArea;

@SuppressWarnings("serial")
public class LogConsole extends TextArea
{
	private static Log log = Logging.getLog(LogConsole.class);
	
	private boolean fixedWidth = false;
	private int columnWidth = 0;
	
	private boolean limitOutput = true;
	private int bufferSize = 0;
	
	private StringBuilder buffer;
	
	private List<ConsoleUpdateListener> consoleUpdateListeners = new LinkedList<ConsoleUpdateListener>();
	
	public LogConsole()
	{
		setEditable(false);
		
		initProperties();
		
		JDem846Properties.addPropertiesChangeListener(new PropertiesChangeListener() {
			public void onPropertyChanged(String property, String oldValue, String newValue)
			{
				if (property != null && property.startsWith("")) {
					initProperties();
				}
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
	}
	
	private void initProperties()
	{
		fixedWidth = JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.console.fixedWidth");
		columnWidth = JDem846Properties.getIntProperty("us.wthr.jdem846.general.ui.console.columnWidth");
		
		limitOutput = JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.console.limitOuput");
		
		int oldBufferSize = bufferSize;
		bufferSize = JDem846Properties.getIntProperty("us.wthr.jdem846.general.ui.console.bufferSize");
		if (bufferSize != oldBufferSize) {
			initBuffer();
		}
	}
	
	private void initBuffer()
	{
		StringBuilder oldBuffer = buffer;
		
		buffer = new StringBuilder(bufferSize);
		
		if (oldBuffer != null) {
			buffer.append(oldBuffer.toString());
		}
	}
	
	private void checkBufferLength()
	{
		if (buffer.length() > bufferSize) {
			buffer.delete(0, buffer.length() - bufferSize);
		}
	}
	
	@Override
	public void append(String text)
	{
		buffer.append(text);
		checkBufferLength();
		super.setText(buffer.toString());
		fireConsoleUpdateListeners();
	}
	
	public void clear()
	{
		buffer.delete(0, buffer.length() - 1);
	}
	
	public void fireConsoleUpdateListeners()
	{
		for (ConsoleUpdateListener listener : this.consoleUpdateListeners) {
			listener.onUpdate();
		}
	}
	
	public void addConsoleUpdateListener(ConsoleUpdateListener listener)
	{
		consoleUpdateListeners.add(listener);
	}
	
	public boolean removeConsoleUpdateListener(ConsoleUpdateListener listener)
	{
		return consoleUpdateListeners.remove(listener);
	}
	
	public interface ConsoleUpdateListener
	{
		public void onUpdate();
	}
	
}
