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

@DemColoring(name="us.wthr.jdem846.color.standardModelColoring.name", identifier="standard-coloring",allowGradientConfig=false, needsMinMaxElevation=true)
public class StandardModelColoring implements ModelColoring
{
	
	private GradientColoring hypsometric = new GradientColoring("hypsometric-global.gradient");
	private GradientColoring bathymetric = new GradientColoring("bathymetric-2.gradient");
	
	
	public StandardModelColoring()
	{
		
	}

	@Override
	public void reset()
	{
		// I've got nothing...
	}

	@Override
	public GradientLoader getGradientLoader()
	{
		return null;
	}

	
	@Override
	public void getColor(double ratio, int[] color)
	{
		getColor(hypsometric, ratio, color);
	}
	
	
	public void getColor(ModelColoring coloring, double ratio, int[] color)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getGradientColor(float elevation, float min_elevation,
			float max_elevation, int[] color)
	{
		float effMin = 0;
		float effMax = 0;
		double ratio = 0;
		
		if (elevation <= 0) {
			effMin = min_elevation;
			if (max_elevation > 0)
				effMax = 0;
			else
				effMax = max_elevation;
			
			ratio = (elevation - effMin) / (effMax - effMin);
			
			bathymetric.getColor(ratio, color);
			//getColor(bathymetric, ratio, color);
		} else {
			effMax = max_elevation;
			
			if (min_elevation < 0)
				effMin = 0;
			else
				effMin = min_elevation;
			
			ratio = (elevation - effMin) / (effMax - effMin);
			
			hypsometric.getColor(ratio, color);
			//getColor(bathymetric, ratio, color);
		}
		
		
	}
	
	
	
	
}
