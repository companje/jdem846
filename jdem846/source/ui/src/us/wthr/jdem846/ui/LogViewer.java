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

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.LogConsole.ConsoleUpdateListener;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.TextArea;
import us.wthr.jdem846.ui.base.ToolBar;

/** A really simple log listing panel
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class LogViewer extends JdemPanel
{
	
	private static Log log = Logging.getLog(LogViewer.class);
	
	private ScrollPane scrollPane;
	//private TextArea txtLog;
	private LogConsole console;
	private ToolBar toolBar;
	private ToolbarButton btnClear;
	
	private JMenu logMenu;
	
	public LogViewer()
	{
		// Create Components
		////txtLog = new TextArea();
		//txtLog.setEditable(false);
		console = new LogConsole();
		scrollPane = new ScrollPane(console);
		
		toolBar = new ToolBar();
		btnClear = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.logViewerPanel.clearButton.label"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/edit-clear.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		btnClear.setToolTipText(I18N.get("us.wthr.jdem846.ui.logViewerPanel.clearButton.tooltip"));
		
		logMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.logViewerPanel.menu"), KeyEvent.VK_L);
		MainMenuBar.insertMenu(logMenu);
		
		logMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.logViewerPanel.menu.clear"), JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/edit-clear.png", KeyEvent.VK_C, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				clear();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK)));
		
		console.addConsoleUpdateListener(new ConsoleUpdateListener() {
			public void onUpdate()
			{
				scrollToBotton();
			}
		});
		
		/*
		txtLog.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e)
			{
				scrollToBotton();
			}
		});
		*/
		// Add Listeners
		
		
		
		/*
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
		*/
		
		// Set Layout
		toolBar.add(btnClear);
		setLayout(new BorderLayout());
		add(toolBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		
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
		console.clear();
	}
	
	
	
	protected void scrollToBotton()
	{
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
	}
	

}
