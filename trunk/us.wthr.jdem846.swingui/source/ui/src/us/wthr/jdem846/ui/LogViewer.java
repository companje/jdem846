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
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.LogConsole.ConsoleUpdateListener;
import us.wthr.jdem846.ui.base.CheckBox;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.ToolBar;

/** A really simple log listing panel
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class LogViewer extends JdemPanel
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(LogViewer.class);
	
	private ScrollPane scrollPane;
	//private TextArea txtLog;
	private LogConsole console;
	private ToolBar toolBar;
	private ToolbarButton btnClear;
	private CheckBox chkScrollLock;
	
	private boolean scrollLock = false;
	
	private JMenu logMenu;
	
	public LogViewer()
	{
		// Create Components
		////txtLog = new TextArea();
		//txtLog.setEditable(false);
		console = new LogConsole();
		scrollPane = new ScrollPane(console);
		
		toolBar = new ToolBar();
		chkScrollLock = new CheckBox(I18N.get("us.wthr.jdem846.ui.logViewerPanel.scrollLock.label"));
		chkScrollLock.setToolTipText(I18N.get("us.wthr.jdem846.ui.logViewerPanel.scrollLock.tooltip"));
		chkScrollLock.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0)
			{
				onScrollLockToggled();
			}
		});
		
		
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
		

		
		// Set Layout
		toolBar.add(btnClear);
		toolBar.add(chkScrollLock);
		setLayout(new BorderLayout());
		add(toolBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		scrollLock = JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.state.scrollLock");
		chkScrollLock.getModel().setSelected(scrollLock);
	}
	
	
	protected void onScrollLockToggled()
	{
		scrollLock = chkScrollLock.getModel().isSelected();
		JDem846Properties.setProperty("us.wthr.jdem846.general.ui.state.scrollLock", ""+chkScrollLock.getModel().isSelected());
	}
	
	protected void clear()
	{
		console.clear();
	}
	
	
	
	protected void scrollToBotton()
	{
		if (!scrollLock) {
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		}
	}
	

}
