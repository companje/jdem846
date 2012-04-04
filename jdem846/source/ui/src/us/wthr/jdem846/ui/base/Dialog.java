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

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class Dialog extends JDialog
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(Dialog.class);

	public Dialog()
	{
		super();
	}

	public Dialog(Frame owner)
	{
		super(owner);
	}

	public Dialog(java.awt.Dialog owner, boolean modal)
	{
		super(owner, modal);
		// TODO Auto-generated constructor stub
	}

	public Dialog(java.awt.Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc)
	{
		super(owner, title, modal, gc);
		// TODO Auto-generated constructor stub
	}

	public Dialog(java.awt.Dialog owner, String title, boolean modal)
	{
		super(owner, title, modal);
		// TODO Auto-generated constructor stub
	}

	public Dialog(java.awt.Dialog owner, String title)
	{
		super(owner, title);
		// TODO Auto-generated constructor stub
	}

	public Dialog(java.awt.Dialog owner)
	{
		super(owner);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Frame owner, boolean modal)
	{
		super(owner, modal);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc)
	{
		super(owner, title, modal, gc);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Frame owner, String title, boolean modal)
	{
		super(owner, title, modal);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Frame owner, String title)
	{
		super(owner, title);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Window owner, ModalityType modalityType)
	{
		super(owner, modalityType);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Window owner, String title, ModalityType modalityType,
			GraphicsConfiguration gc)
	{
		super(owner, title, modalityType, gc);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Window owner, String title, ModalityType modalityType)
	{
		super(owner, title, modalityType);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Window owner, String title)
	{
		super(owner, title);
		// TODO Auto-generated constructor stub
	}

	public Dialog(Window owner)
	{
		super(owner);
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
