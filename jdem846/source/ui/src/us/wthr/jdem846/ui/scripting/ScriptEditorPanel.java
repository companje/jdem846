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

package us.wthr.jdem846.ui.scripting;

import java.awt.BorderLayout;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.ui.MainButtonBar;
import us.wthr.jdem846.ui.MainMenuBar;
import us.wthr.jdem846.ui.TitledRoundedPanel;
import us.wthr.jdem846.ui.base.EditorPane;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.panels.RoundedPanel;
import us.wthr.jdem846.ui.scripting.ScriptEditorButtonBar.ScriptEditButtons;
import us.wthr.jdem846.ui.scripting.ScriptEditorButtonBar.ScriptEditorButtonClickedListener;

@SuppressWarnings("serial")
public class ScriptEditorPanel extends Panel
{
	
	private static Log log = Logging.getLog(ScriptEditorPanel.class);
	
	private JEditorPane editorPane;
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	private ScriptEditorButtonBar buttonBar;
	
	private ScriptLanguageEnum scriptLanguage = ScriptLanguageEnum.GROOVY;
	
	static {
		
	}
	
	public ScriptEditorPanel()
	{
		//super(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.title"));
		
		jsyntaxpane.DefaultSyntaxKit.initKit();
		
		if (JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.scriptEditorPane.textAA")) {
			jsyntaxpane.DefaultSyntaxKit.setProperty("TextAA", "ON");
		} else {
			jsyntaxpane.DefaultSyntaxKit.setProperty("TextAA", "OFF");
		}
		
		// Create Components
		buttonBar = new ScriptEditorButtonBar(this);
		buttonBar.addScriptEditorButtonClickedListener(new ScriptEditorButtonClickedListener() {
			public void onButtonClicked(ScriptEditButtons button)
			{
				log.info("Button clicked: " + button);
				JOptionPane.showMessageDialog(getRootPane(),
						I18N.get("us.wthr.jdem846.ui.notYetImplemented.message"),
					    I18N.get("us.wthr.jdem846.ui.notYetImplemented.title"),
					    JOptionPane.INFORMATION_MESSAGE);
			}
		});
		//MainButtonBar.addToolBar(buttonBar);
		
		
		final JEditorPane editorPane = new JEditorPane();
		this.editorPane = editorPane;
		
		log.info("Document: " + editorPane.getDocument());
		//Action[] actions = editorPane.getActions();
		//for (Action action : actions) {
		//	log.info("Action: " + action.toString());
		//}
		

		
		JScrollPane scrollPane = new JScrollPane(editorPane);
		
		// Set Layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonBar, BorderLayout.NORTH);
		
		this.doLayout();
		
		if (this.scriptLanguage != null) {
			editorPane.setContentType(scriptLanguage.mime());
		}
		
		editorPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				fireChangeListeners();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				fireChangeListeners();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				fireChangeListeners();
			}
			
		});
	}
	
	
	@Override
	public void dispose() throws ComponentException
	{
		log.info("Closing output image pane.");
		//MainMenuBar.removeMenu(modelMenu);
		MainButtonBar.removeToolBar(buttonBar);
	}
	
	public void fireChangeListeners()
	{
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(e);
		}
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	
	
	
	
	
	public String getScriptContent()
	{
		return editorPane.getText();
	}
	
	public void setScriptContent(String scriptContent)
	{
		editorPane.setText(scriptContent);
		editorPane.setCaretPosition(0);
	}
	
	public ScriptLanguageEnum getScriptLanguage()
	{
		return scriptLanguage;
	}
	
	public void setScriptLanguage(ScriptLanguageEnum scriptLanguage)
	{
		this.scriptLanguage = scriptLanguage;
	}
}
