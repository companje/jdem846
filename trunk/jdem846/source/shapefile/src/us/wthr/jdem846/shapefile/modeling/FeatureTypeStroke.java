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

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public class FeatureTypeStroke
{
	
	private String name;
	private List<LineStroke> lineStrokes = new LinkedList<LineStroke>();
	
	public FeatureTypeStroke()
	{
		
	}
	
	public FeatureTypeStroke(String name)
	{
		setName(name);
	}
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void addLineStroke(LineStroke lineStroke)
	{
		lineStrokes.add(lineStroke);
	}
	
	public boolean removeLineStroke(LineStroke lineStroke)
	{
		return lineStrokes.remove(lineStroke);
	}
	
	
	public List<LineStroke> getLineStrokes()
	{
		return lineStrokes;
	}
	
	public static FeatureTypeStroke getDefaultFeatureTypeStroke()
	{
		LineStroke defaultLineStroke = new LineStroke(Color.BLACK);
		
		FeatureTypeStroke defaultFeatureTypeStroke = new FeatureTypeStroke();
		defaultFeatureTypeStroke.addLineStroke(defaultLineStroke);
		
		return defaultFeatureTypeStroke;
	}
	
}
