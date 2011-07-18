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

package us.wthr.jdem846.shapefile.modeling;

import java.awt.BasicStroke;
import java.awt.Color;

public class LineStroke extends BasicStroke
{
	
	private Color color = null;
	
	public LineStroke()
	{
		super();
	}

	public LineStroke(Color color)
	{
		super();
		this.color = color;
	}
	
	public LineStroke(float arg0, int arg1, int arg2, float arg3, float[] arg4, float arg5, Color color)
	{
		super(arg0, arg1, arg2, arg3, arg4, arg5);
		this.color = color;
	}

	public LineStroke(float arg0, int arg1, int arg2, float arg3, Color color)
	{
		super(arg0, arg1, arg2, arg3);
		this.color = color;
	}

	public LineStroke(float arg0, int arg1, int arg2, Color color)
	{
		super(arg0, arg1, arg2);
		this.color = color;
	}

	public LineStroke(float arg0, Color color)
	{
		super(arg0);
		this.color = color;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}
	
	
	
	
	
}
