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

package us.wthr.jdem846.render.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ViewportBuffer
{
	private static Log log = Logging.getLog(ViewportBuffer.class);
	private static float NOT_SET = Float.NEGATIVE_INFINITY;
	private int width;
	private int height;
	
	//private ZPixel[][] buffer;
	
	private int[][] colorBuffer;
	private float[][] zBuffer;
	
	public ViewportBuffer(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		long colorBufferSize = height * width * Integer.SIZE;
		long zBufferSize = (long)height * (long)width * (long)Float.SIZE;
		
		//log.info("Allocating color buffer of " + colorBufferSize + " bytes");
		//log.info("Allocating z buffer of " + zBufferSize + " bytes");
		
		colorBuffer = new int[height][width];
		zBuffer = new float[height][width];
		reset();
		
	}
	
	public void reset()
	{
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				colorBuffer[h][w] = intsToARGB(0, 0, 0, 0);
				zBuffer[h][w] = NOT_SET;
			}
		}
	}

	public void dispose()
	{
		colorBuffer = null;
		zBuffer = null;
	}
	
	public void set(int x, int y, double z, int r, int g, int b)
	{
		if (x >= getWidth() || x < 0 || y >= getHeight() || y < 0)
			return;
		
		if (z > zBuffer[y][x] || zBuffer[y][x] == NOT_SET) {
			colorBuffer[y][x] = intsToARGB(255, r, g, b);
			//colorBuffer[y][x][0] = r;
			//colorBuffer[y][x][1] = g;
			//colorBuffer[y][x][2] = b;
			zBuffer[y][x] = (float)z;
		}
	}
	

	
	protected int getRGB(int x, int y)
	{
		return colorBuffer[y][x];
		//int rgb = intsToARGB(colorBuffer[y][x][0], colorBuffer[y][x][1], colorBuffer[y][x][2]);
		//return rgb;
	}
	
	protected boolean isPointSet(int x, int y)
	{
		return (zBuffer[y][x] != NOT_SET);
	}
	
	public BufferedImage paint(BufferedImage image, int imgY)
	{
		if (image == null) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		_paint(image, imgY);
		return image;
	}
	
	protected void _paint(BufferedImage image, int imgY)
	{
		
		
		for (int y = 0; y < height; y++) {
			
			if (y + imgY >= image.getHeight())
				break;
			
			for (int x = 0; x < width; x++) {
				
				if (x >= image.getWidth())
					break;
				
				if (isPointSet(x, y)) {
					int rgb = getRGB(x, y);
					image.setRGB(x, y + imgY, rgb);
				}
			}
		}
	}
	

	
	protected int intsToARGB(int a, int r, int g, int b)
	{

		int rgb = ((a & 0xFF) << 24) |
			((r & 0xFF) << 16) |
			((g & 0xFF) << 8) |
			(b & 0xFF);

		return rgb;
	}
	
	public int getWidth()
	{
		return width;
	}


	public void setWidth(int width)
	{
		this.width = width;
	}


	public int getHeight()
	{
		return height;
	}


	public void setHeight(int height)
	{
		this.height = height;
	}
	
	
	
	
}
