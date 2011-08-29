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

import java.awt.Component;

import javax.swing.JSplitPane;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.Disposable;


@SuppressWarnings("serial")
public class SplitPane extends JSplitPane implements Disposable
{
	private static Log log = Logging.getLog(SplitPane.class);
	private boolean disposed = false;

	
	public SplitPane()
	{
		super();
		// TODO Auto-generated constructor stub
	}





	public SplitPane(int newOrientation, boolean newContinuousLayout,
			Component newLeftComponent, Component newRightComponent)
	{
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
		// TODO Auto-generated constructor stub
	}





	public SplitPane(int newOrientation, boolean newContinuousLayout)
	{
		super(newOrientation, newContinuousLayout);
		// TODO Auto-generated constructor stub
	}





	public SplitPane(int newOrientation, Component newLeftComponent,
			Component newRightComponent)
	{
		super(newOrientation, newLeftComponent, newRightComponent);
		// TODO Auto-generated constructor stub
	}





	public SplitPane(int newOrientation)
	{
		super(newOrientation);
		// TODO Auto-generated constructor stub
	}





	@Override
	public void dispose() throws ComponentException
	{
		disposed = true;
	}
	
	public boolean isDisposed()
	{
		return disposed;
	}

}
