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

package us.wthr.jdem846.ui.base;

import javax.swing.JTextArea;
import javax.swing.text.Document;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class TextArea extends JTextArea
{
	private static Log log = Logging.getLog(TextArea.class);

	public TextArea()
	{
		super();
		
	}

	public TextArea(Document doc, String text, int rows, int columns)
	{
		super(doc, text, rows, columns);
		
	}

	public TextArea(Document doc)
	{
		super(doc);
		
	}

	public TextArea(int rows, int columns)
	{
		super(rows, columns);
		
	}

	public TextArea(String text, int rows, int columns)
	{
		super(text, rows, columns);
		
	}

	public TextArea(String text)
	{
		super(text);
		
	}
	
	
	
}
