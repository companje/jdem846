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

import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.scaling.ElevationScaler;

public interface ModelColoring 
{
	public void reset() throws Exception;
	public GradientLoader getGradientLoader();
	
	public void setElevationScaler(ElevationScaler elevationScaler);
	
	public IColor getColorByPercent(double ratio);
	public IColor getColorByMeters(double meters);
	public IColor getGradientColor(double elevation, double min_elevation, double max_elevation);

	
	
	
	public double getMinimumSupported();
	public double getMaximumSupported();
	
	public ModelColoring copy() throws Exception;
}
