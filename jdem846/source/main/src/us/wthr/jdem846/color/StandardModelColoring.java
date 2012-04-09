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

import java.io.File;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.annotations.DemColoring;
import us.wthr.jdem846.exception.GradientLoadException;
import us.wthr.jdem846.scaling.ElevationScaler;

@DemColoring(name="us.wthr.jdem846.color.standardModelColoring.name", identifier="standard-coloring",allowGradientConfig=false, needsMinMaxElevation=true)
public class StandardModelColoring implements ModelColoring
{
	
	private GradientColoring hypsometric = null;
	private GradientColoring bathymetric = null;
	
	
	public StandardModelColoring() throws GradientLoadException
	{
		File hypsometricFile = JDemResourceLoader.getAsFile(JDem846Properties.getProperty("us.wthr.jdem846.gradients") + "/hypsometric-global.json");
		File bathymetricFile = JDemResourceLoader.getAsFile(JDem846Properties.getProperty("us.wthr.jdem846.gradients") + "/bathymetric.json");
		hypsometric = new GradientColoring(hypsometricFile.getAbsolutePath());
		bathymetric = new GradientColoring(bathymetricFile.getAbsolutePath());
		
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
	public void getColorByMeters(double ratio, int[] color) 
	{
		
	}
	
	@Override
	public void getColorByPercent(double ratio, int[] color) 
	{
		getColor(hypsometric, ratio, color);
	}
	
	
	public void getColor(ModelColoring coloring, double ratio, int[] color)
	{
		
	}

	@Override
	public void getGradientColor(double elevation, double min_elevation,
			double max_elevation, int[] color)
	{
		double effMin = 0;
		double effMax = 0;
		double ratio = 0;
		
		if (elevation <= 0) {
			effMin = min_elevation;
			if (max_elevation > 0)
				effMax = 0;
			else
				effMax = max_elevation;
			
			ratio = (elevation - effMin) / (effMax - effMin);
			
			bathymetric.getColorByPercent(ratio, color);
			//getColor(bathymetric, ratio, color);
		} else {
			effMax = max_elevation;
			
			if (min_elevation < 0)
				effMin = 0;
			else
				effMin = min_elevation;
			
			ratio = (elevation - effMin) / (effMax - effMin);
			
			hypsometric.getColorByPercent(ratio, color);
			//getColor(bathymetric, ratio, color);
		}
		
		
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
		this.hypsometric.setElevationScaler(elevationScaler);
		this.bathymetric.setElevationScaler(elevationScaler);
	}
}
