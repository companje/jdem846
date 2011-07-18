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

public class ResizeDimensions
{
	public static final byte HEIGHT = 0;
	public static final byte WIDTH = 1;
	
	
	protected ResizeDimensions()
	{
		
	}
	
	public static Rectangle resize(int fromWidth, int fromHeight, int toSize, byte changedDimension)
	{
		int outputWidth = 0;
		int outputHeight = 0;
		
		if (changedDimension == ResizeDimensions.HEIGHT) {
			outputHeight = toSize;
			float sizeRatio = (float)toSize / (float)fromHeight;
			outputWidth = Math.round((float)fromWidth * sizeRatio);
		} else if (changedDimension == ResizeDimensions.WIDTH) {
			outputWidth = toSize;
			float sizeRatio = (float)toSize / (float)fromWidth;
			outputHeight = Math.round((float)fromHeight * sizeRatio);
		}
		
		return new Rectangle(outputWidth, outputHeight);
	}
	
	public static Rectangle resize(int fromWidth, int fromHeight, int toWidth, int toHeight)
	{
		float sizeRatio = 1.0f;
		
		int outputWidth = toWidth;
		int outputHeight = toHeight;

		if (fromHeight > fromWidth) {
			sizeRatio = (float)fromWidth / (float)fromHeight;
			outputWidth = Math.round(((float) outputHeight) * sizeRatio);
		} else if (fromWidth > fromHeight) {
			sizeRatio = (float)fromHeight / (float)fromWidth;
			outputHeight = Math.round(((float)outputWidth) * sizeRatio);
		}
		
		return new Rectangle(outputWidth, outputHeight);
	}
	
	public static Rectangle resize(int fromWidth, int fromHeight, float percentage)
	{
		
		int outputWidth = Math.round((float)fromWidth * percentage );
		int outputHeight = Math.round((float)fromHeight * percentage );
		
		return new Rectangle(outputWidth, outputHeight);
	}
	
}
