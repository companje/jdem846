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
import us.wthr.jdem846.scaling.ElevationScaler;

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
	public void getColorByMeters(double ratio, int[] color) 
	{
		
	}
	
	@Override
	public void getColorByPercent(double ratio, int[] color) 
	{
		defaultColor.toList(color);
		//return defaultColor.getCopy();
	}

	@Override
	public void getGradientColor(double elevation, double minElevation,
			double maxElevation, int[] color) 
	{
		defaultColor.toList(color);
		//return defaultColor.getCopy();
	}
	
	public double getMinimumSupported()
	{
		return 0;
	}
	
	public double getMaximumSupported()
	{
		return 1.0;
	}
	
	@Override
	public void setElevationScaler(ElevationScaler elevationScaler) 
	{
		
	}
	
	public ModelColoring copy() throws Exception
	{
		return new GrayTint();
	}
}
