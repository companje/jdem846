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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.ToolBar;

@SuppressWarnings("serial")
public class TopButtonBar extends ComponentButtonBar
{

	public static final int BTN_NEW_STANDARD_PROJECT = 0;
	public static final int BTN_NEW_SCRIPT_PROJECT = 1;
	public static final int BTN_OPEN_PROJECT = 2;
	public static final int BTN_SAVE_PROJECT = 3;
	public static final int BTN_SAVE_PROJECT_AS = 4;
	public static final int BTN_EXIT = 5;
	
	private List<ButtonClickedListener> buttonClickedListeners = new LinkedList<ButtonClickedListener>();
	
	
	private DropDownButton jbtnNewProject;
	private ToolbarButton jbtnOpenProject;
	private ToolbarButton jbtnSaveProject;
	private ToolbarButton jbtnSaveProjectAs;
	private ToolbarButton jbtnExit;
	
	public TopButtonBar()
	{
		super(null);
		// Create components
		/*
		jbtnNewProject = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.new"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_NEW_PROJECT);
			}
		});
		*/
		jbtnNewProject = new DropDownButton(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.new"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_NEW_STANDARD_PROJECT);
			}
		});
		MenuItem newStdProject = new MenuItem(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectButton.newStandardProject"), new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				fireButtonClickedListeners(BTN_NEW_STANDARD_PROJECT);
			}
		});
		jbtnNewProject.addMenuItem(newStdProject);
		MenuItem newScriptProject = new MenuItem(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectButton.newScriptProject"), new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				fireButtonClickedListeners(BTN_NEW_SCRIPT_PROJECT);
			}
		});
		jbtnNewProject.addMenuItem(newScriptProject);
		
		jbtnOpenProject = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.topToolbar.openProjectButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.open"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_OPEN_PROJECT);
			}
		});
		
		jbtnSaveProject = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.save"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_SAVE_PROJECT);
			}
		});
		
		jbtnSaveProjectAs = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectAsButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.project.saveAs"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_SAVE_PROJECT_AS);
			}
		});
		
		jbtnExit = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.topToolbar.exitButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.exit"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_EXIT);
			}
		});
		
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.mainToolBar.displayText") == false) {
			jbtnNewProject.setTextDisplayed(false);
			jbtnOpenProject.setTextDisplayed(false);
			jbtnSaveProject.setTextDisplayed(false);
			jbtnSaveProjectAs.setTextDisplayed(false);
			jbtnExit.setTextDisplayed(false);
		}
		
		jbtnNewProject.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.newProjectTooltip"));
		jbtnOpenProject.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.openProjectTooltip"));
		jbtnSaveProject.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectTooltip"));
		jbtnSaveProjectAs.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.saveProjectAsTooltip"));
		jbtnExit.setToolTipText(I18N.get("us.wthr.jdem846.ui.topToolbar.exitTooltip"));
		
		//this.setMargin(new Insets(3, 3, 3, 3));
		
		// Set layout
		//add(jbtnNewProject);
		jbtnNewProject.addToToolBar(this);
		add(jbtnOpenProject);
		add(jbtnSaveProject);
		add(jbtnSaveProjectAs);
		addSeparator();
		add(jbtnExit);
		addSeparator();
	}
	
	
	public void setButtonEnabled(int button, boolean enabled)
	{

		switch (button) {
		case BTN_NEW_STANDARD_PROJECT:
		case BTN_NEW_SCRIPT_PROJECT:
			jbtnNewProject.setEnabled(enabled);
			break;
		case BTN_OPEN_PROJECT:
			jbtnOpenProject.setEnabled(enabled);
			break;
		case BTN_SAVE_PROJECT:
			jbtnSaveProject.setEnabled(enabled);
			break;
		case BTN_SAVE_PROJECT_AS:
			jbtnSaveProjectAs.setEnabled(enabled);
			break;
		case BTN_EXIT:
			jbtnExit.setEnabled(enabled);
			break;
		}

	}
	
	public void addButtonClickedListener(ButtonClickedListener listener)
	{
		buttonClickedListeners.add(listener);
	}
	
	protected void fireButtonClickedListeners(int button)
	{
		for (ButtonClickedListener listener : buttonClickedListeners) {
			switch (button) {
			case BTN_NEW_STANDARD_PROJECT:
				listener.onNewStandardProjectClicked();
				break;
			case BTN_NEW_SCRIPT_PROJECT:
				listener.onNewScriptProjectClicked();
				break;
			case BTN_OPEN_PROJECT:
				listener.onOpenProjectClicked();
				break;
			case BTN_SAVE_PROJECT:
				listener.onSaveProjectClicked();
				break;
			case BTN_SAVE_PROJECT_AS:
				listener.onSaveProjectAsClicked();
				break;
			case BTN_EXIT:
				listener.onExitClicked();
				break;
			}
		}
	}
	
	public interface ButtonClickedListener
	{
		public void onNewStandardProjectClicked();
		public void onNewScriptProjectClicked();
		public void onOpenProjectClicked();
		public void onSaveProjectClicked();
		public void onSaveProjectAsClicked();
		public void onExitClicked();
	}
}
