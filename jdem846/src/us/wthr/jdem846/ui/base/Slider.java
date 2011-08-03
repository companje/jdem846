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
import javax.swing.JSlider;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class Slider extends JSlider
{
	private static Log log = Logging.getLog(Slider.class);

	public Slider()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Slider(BoundedRangeModel brm)
	{
		super(brm);
		// TODO Auto-generated constructor stub
	}

	public Slider(int orientation, int min, int max, int value)
	{
		super(orientation, min, max, value);
		// TODO Auto-generated constructor stub
	}

	public Slider(int min, int max, int value)
	{
		super(min, max, value);
		// TODO Auto-generated constructor stub
	}

	public Slider(int min, int max)
	{
		super(min, max);
		// TODO Auto-generated constructor stub
	}

	public Slider(int orientation)
	{
		super(orientation);
		// TODO Auto-generated constructor stub
	}
	
	
}
