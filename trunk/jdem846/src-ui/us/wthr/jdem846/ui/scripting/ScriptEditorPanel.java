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

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.TitledRoundedPanel;
import us.wthr.jdem846.ui.base.EditorPane;
import us.wthr.jdem846.ui.base.ScrollPane;

@SuppressWarnings("serial")
public class ScriptEditorPanel extends TitledRoundedPanel
{
	
	private static Log log = Logging.getLog(ScriptEditorPanel.class);
	
	private JEditorPane editorPane;
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	
	static {
		
	}
	
	public ScriptEditorPanel()
	{
		super(I18N.get("us.wthr.jdem846.ui.scriptEditorPane.title"));
		
		jsyntaxpane.DefaultSyntaxKit.initKit();
		
		String template = "";
		try {
			template = loadTemplateFile("/scripting/template-dem.groovy");
		} catch (IOException ex) {
			log.warn("Failed to load template file: " + ex.getMessage(), ex);
		}
		
		// Create Components
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
		
		this.doLayout();
		
		editorPane.setContentType("text/groovy");
		editorPane.setText(template);
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
	
	
	
	
	protected String loadTemplateFile(String path) throws IOException
	{
		StringBuffer templateBuffer = new StringBuffer();

		BufferedInputStream in = new BufferedInputStream(ScriptEditorPanel.class.getResourceAsStream(path));
		
		int length = 0;
		byte[] buffer = new byte[1024];
		
		while((length = in.read(buffer)) > 0) {
			templateBuffer.append(new String(buffer, 0, length));
		}
		
		return templateBuffer.toString();
	}
}
