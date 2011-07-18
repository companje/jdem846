/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** A really simple log listing panel
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class LogViewer extends JdemPanel
{
	
	private static Log log = Logging.getLog(LogViewer.class);
	
	private JScrollPane jscrollPane;
	private JTextArea jtxtLog;
	private JToolBar jbar;
	private ToolbarButton jbtnClear;
	
	private JMenu logMenu;
	
	public LogViewer()
	{
		// Create Components
		jtxtLog = new JTextArea();
		jtxtLog.setEditable(false);
		jscrollPane = new JScrollPane(jtxtLog);
		
		jbar = new JToolBar();
		jbtnClear = new ToolbarButton("Clear", "/us/wthr/jdem846/ui/icons/edit-clear.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		
		
		logMenu = new ComponentMenu(this, "Log/Debug", KeyEvent.VK_L);
		MainMenuBar.insertMenu(logMenu);
		
		logMenu.add(new MenuItem("Clear", "/us/wthr/jdem846/ui/icons/edit-clear.png", KeyEvent.VK_C, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				clear();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK)));
		
		
		jtxtLog.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e)
			{
				scrollToBotton();
			}
		});
		
		// Add Listeners
		
		
		
		
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
		
		
		// Set Layout
		jbar.add(jbtnClear);
		setLayout(new BorderLayout());
		add(jbar, BorderLayout.NORTH);
		add(jscrollPane, BorderLayout.CENTER);
		
		/*
		Timer timer = new Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Date date = new Date();
				log.info("Test Entry -- " + date);
			}
		});
		timer.start();
		*/
		
	}
	
	protected void clear()
	{
		jtxtLog.setText("");
	}
	
	protected void append(String record)
	{
		jtxtLog.setText(jtxtLog.getText() + record);
		jtxtLog.setCaretPosition(jtxtLog.getText().length());
	}
	
	
	protected void scrollToBotton()
	{
		jscrollPane.getVerticalScrollBar().setValue(jscrollPane.getVerticalScrollBar().getMaximum());
	}
	
	
	@Override
	public void cleanUp()
	{
		log.info("cleanUp()");
	}

}
