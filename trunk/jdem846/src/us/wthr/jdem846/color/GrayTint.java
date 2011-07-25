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

package us.wthr.jdem846.color;

import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.i18n.I18N;

@DemColoring(name="us.wthr.jdem846.color.grayTint.name", identifier="gray-tint", allowGradientConfig=false, needsMinMaxElevation=false)
public class GrayTint implements ModelColoring
{
	
	private DemColor defaultColor = new DemColor(.5, .5, .5, 1.0);
	
	public GrayTint()
	{
		
	}
	
	@Override
	public void reset()
	{
		// Nothing to reset...
	}
	
	
	@Override
	public GradientLoader getGradientLoader()
	{
		return null;
	}
	
	@Override
	public void getColor(double ratio, int[] color) 
	{
		defaultColor.toList(color);
		//return defaultColor.getCopy();
	}

	@Override
	public void getGradientColor(float elevation, float minElevation,
			float maxElevation, int[] color) 
	{
		defaultColor.toList(color);
		//return defaultColor.getCopy();
	}
	
	
	
}