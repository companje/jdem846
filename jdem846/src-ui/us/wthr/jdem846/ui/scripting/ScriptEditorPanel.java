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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

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
		
		JScrollPane scrollPane = new JScrollPane(editorPane);
		
		// Set Layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(scrollPane, BorderLayout.CENTER);
		
		this.doLayout();
		
		editorPane.setContentType("text/groovy");
		editorPane.setText(template);
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
