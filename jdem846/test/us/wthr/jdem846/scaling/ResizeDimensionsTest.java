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

import junit.framework.TestCase;
import us.wthr.jdem846.scaling.ResizeDimensions;

public class ResizeDimensionsTest  extends TestCase
{
	

	public void testPercentageShrink() 
	{
		Rectangle rect = ResizeDimensions.resize(1000, 1000, 0.45f);
		assert rect.width == 450;
		assert rect.height == 450;
	}
	

	public void testPercentageGrow() 
	{
		Rectangle rect = ResizeDimensions.resize(1000, 1000, 1.5f);
		assert rect.width == 1500;
		assert rect.height == 1500;
	}
	

	public void testChangedDimensionWidth() 
	{
		Rectangle rect = ResizeDimensions.resize(2000, 1000, 800, ResizeDimensions.WIDTH);
		assert rect.width == 800;
		assert rect.height == 400;
	}
	

	public void testChangedDimensionHeight() 
	{
		Rectangle rect = ResizeDimensions.resize(2000, 1000, 800, ResizeDimensions.HEIGHT);
		assert rect.width == 1600;
		assert rect.height == 800;
	}
	
}
