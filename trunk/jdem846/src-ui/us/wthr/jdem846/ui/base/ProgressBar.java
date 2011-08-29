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

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class ProgressBar extends JProgressBar
{
	private static Log log = Logging.getLog(ProgressBar.class);

	public ProgressBar()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ProgressBar(BoundedRangeModel newModel)
	{
		super(newModel);
		// TODO Auto-generated constructor stub
	}

	public ProgressBar(int orient, int min, int max)
	{
		super(orient, min, max);
		// TODO Auto-generated constructor stub
	}

	public ProgressBar(int min, int max)
	{
		super(min, max);
		// TODO Auto-generated constructor stub
	}

	public ProgressBar(int orient)
	{
		super(orient);
		// TODO Auto-generated constructor stub
	}
	
	
}
