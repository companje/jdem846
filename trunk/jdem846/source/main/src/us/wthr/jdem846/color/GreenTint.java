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
import us.wthr.jdem846.graphics.Color;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.scaling.ElevationScaler;

@DemColoring(name="us.wthr.jdem846.color.greenTint.name", identifier="green-tint", allowGradientConfig=false, needsMinMaxElevation=false)
public class GreenTint implements ModelColoring
{

	private IColor defaultColor = new Color(146, 173, 144, 255);
	
	public GreenTint()
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
	public IColor getColorByMeters(double ratio) 
	{
		return defaultColor;
	}
	
	@Override
	public IColor getColorByPercent(double ratio) 
	{
		return defaultColor;
	}

	@Override
	public IColor getGradientColor(double elevation, double minElevation, double maxElevation) 
	{
		return defaultColor;
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
		return new GreenTint();
	}
}
