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

import junit.framework.TestCase;
import us.wthr.jdem846.scaling.FloatRaster;

public class FloatRasterTest extends TestCase
{
	
	public void testDimensions() 
	{
		FloatRaster raster = new FloatRaster(4, 5);
		assert raster.getWidth() == 4;
		assert raster.getHeight() == 5;
		
		assert raster.getRaster().length == 5;
		assert raster.getRaster()[0].length == 4;
		
	}

	public void testGetSet()
	{
		FloatRaster raster = new FloatRaster(4, 5);
		raster.set(0, 0, 1);
		raster.set(1, 1, 2);
		raster.set(2, 2, 3);
		raster.set(3, 3, 4);
		raster.set(0, 4, 5);
		raster.set(3, 0, 6);
		raster.set(3, 4, 7);
		
		assert raster.get(0, 0) == 1;
		assert raster.get(1, 1) == 2;
		assert raster.get(2, 2) == 3;
		assert raster.get(3, 3) == 4;
		assert raster.get(0, 4) == 5;
		assert raster.get(3, 0) == 6;
		assert raster.get(3, 4) == 7;
	}
}
