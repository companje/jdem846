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

package us.wthr.jdem846.scaling;

import java.awt.Rectangle;

public class RasterScale
{
	
	public static FloatRaster scale(FloatRaster src, int toWidth, int toHeight)
	{
		Rectangle dimensions = ResizeDimensions.resize(src.getWidth(), src.getHeight(), toWidth, toHeight);
		
		toWidth = dimensions.width;
		toHeight = dimensions.height;
		FloatRaster dest = new FloatRaster(toWidth, toHeight);
		
		return scale(src, dest);
	}
	
	public static FloatRaster scale(FloatRaster src, FloatRaster dest)
	{

		float toWidth = dest.getWidth();
		float toHeight = dest.getHeight();

		for (int y = 0; y < toHeight; y++) {
			for (int x = 0; x < toWidth; x++) {
				
				float xFrac = (float)x / (float)toWidth;
				float yFrac = (float)y / (float)toHeight;
				float value = src.interpolate(xFrac, yFrac);
				dest.set(x, y, value);
			}
		}
		
		
		return dest;
	}
	
}
