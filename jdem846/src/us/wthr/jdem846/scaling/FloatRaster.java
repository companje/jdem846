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

import us.wthr.jdem846.DemConstants;

/** Implements a simple float raster using a two-dimensional array. 
 * No bounds checks are done (by design) and will result in IndexOutOfBoundsException
 * being thrown from within.
 *  
 * @author Kevin M. Gill
 *
 */
public class FloatRaster
{
	private float[][] raster;
	
	private int width;
	private int height;
	
	public FloatRaster(int width, int height)
	{
		this.width = width;
		this.height = height;
		raster = new float[height][width];
	}
	
	
	public void reset()
	{
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				set(x, y, 0x0);
			}
		}
	}
	
	public void set(int x, int y, float value)
	{
		raster[y][x] = value;
	}
	
	public float get(int x, int y)
	{
		return raster[y][x];
	}
	
	public float[][] getRaster()
	{
		return raster;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	/** For now: Just grabs the value in the middle like an
	 * over-simplified nearest-neighbor algorithm.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public float interpolate(int x, int y, int width, int height)
	{
		int mX = x + (int) ((float)width / 2.0);
		int mY = y + (int) ((float)height / 2.0);
		
		return get(mX, mY);
	}
	
	/** Interpolates a value within the raster given X/Y fractions. Uses a 
	 * simplified Bilinear algorithm
	 * 
	 * @param xFrac
	 * @param yFrac
	 * @return
	 */
	public float interpolate(float xFrac, float yFrac)
	{
		float xM = (float)getWidth() * xFrac;
		int x0 = (int) Math.floor(xM);
		int x1 = (int) Math.ceil(xM);
		
		if (x1 >= getWidth())
			x1 = getWidth() - 1;
		
		float x0DeltaFrac = (xM - (float)x0) / (float)(x1 - x0);
		
		
		float yM = (float)getHeight() * yFrac;
		int y0 = (int) Math.floor(yM);
		int y1 = (int) Math.ceil(yM);
		
		float y0DeltaFrac = (yM - (float)y0) / (float)(y1 - y0);
		
		if (y1 >= getHeight())
			y1 = getHeight() - 1;
		
		// Get raster values. If only one has a valid value, all will.
		float s00 = getValidData(raster[y0][x0], raster[y0][x1], raster[y1][x0], raster[y1][x1]);
		float s01 = getValidData(raster[y0][x1], raster[y0][x0], raster[y1][x1], raster[y1][x0]);
		float s10 = getValidData(raster[y1][x0], raster[y1][x1], raster[y0][x0], raster[y0][x1]);
		float s11 = getValidData(raster[y1][x1], raster[y1][x0], raster[y0][x1], raster[y0][x0]);
		
		// Check for NaN
		if ((!(x0DeltaFrac >= 0.0 && x0DeltaFrac <= 1.0)))
			x0DeltaFrac = 0.0f;
		
		// Check for NaN
		if ((!(y0DeltaFrac >= 0.0 && y0DeltaFrac <= 1.0)))
			y0DeltaFrac = 0.0f;
		

		float s0 = (s01 - s00)*x0DeltaFrac + s00;
        float s1 = (s11 - s10)*x0DeltaFrac + s10;
        float value = (s1 - s0)*y0DeltaFrac + s0;

        return value;

	}
	
	
	protected float getValidData(float s0, float s1, float s2, float s3)
	{
		if (s0 != DemConstants.ELEV_NO_DATA) {
			return s0;
		} else if (s1 != DemConstants.ELEV_NO_DATA) {
			return s1;
		} else if (s2 != DemConstants.ELEV_NO_DATA) {
			return s2;
		} else if (s3 != DemConstants.ELEV_NO_DATA) {
			return s3;
		} else {
			return DemConstants.ELEV_NO_DATA;
		}
	}
	
	
	public void overlay(FloatRaster other, int x, int y, int width, int height)
	{
		for (int yO = 0; yO < height; yO++) {
			for (int xO = 0; xO < width; xO++) {
				if (x+xO < getWidth() && y+yO < getHeight())
					this.set(x+xO, y+yO, other.get(xO, yO));
			}
		}
		
		
	}
	
	
}
