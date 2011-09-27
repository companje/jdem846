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

package us.wthr.jdem846.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import us.wthr.jdem846.color.DemColor;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ImageUtilities;

public class DemCanvas implements ImageObserver
{
	private static Log log = Logging.getLog(DemCanvas.class);
	
	BufferedImage image;
	Graphics2D graphics;
	
	Color background = Color.white;
	
	int width;
	int height;
	
	public DemCanvas(Color background, int width, int height)
	{
		this.width = width;
		this.height = height;
		this.background = background;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D) image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		graphics.setColor(background);
		graphics.fillRect(0, 0, width, height);
	}
	
	public DemCanvas(Image rawImage)
	{
		width = rawImage.getWidth(this);
		height = rawImage.getHeight(this);


		image = new BufferedImage(rawImage.getWidth(this), rawImage.getHeight(this), BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D) image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		overlay(rawImage, 0, 0);
	}
	
	public void setColor(int x, int y, float r, float g, float b)
	{
		setColor(x, y, (int)(r * 0xFF), (int)(g * 0xFF), (int)(b * 0xFF));
	}
	
	public void setColor(int x, int y, DemColor demColor)
	{
		setColor(x, y, (int) (demColor.getRed() * 0xFF),  (int)(demColor.getGreen() * 0xFF), (int) (demColor.getBlue() * 0xFF));
	}
	

	public void setColor(int x, int y, int[] color)
	{
		setColor(x, y, color[0], color[1], color[2]);
	}
	
	public void setColor(int x, int y, int r, int g, int b)
	{
		int rgb = (0xFF << 24) |
			((r & 0xff) << 16) |
			((g & 0xff) << 8) |
			(b & 0xff);
		
		//int abgr = (0xFF << 24) |
		//((b & 0xFF) << 16) |
		//((g & 0xFF) << 8) |
		//(r & 0xFF);
		try {
			//WritableRaster raster = image.getRaster();
			image.setRGB(x, y, rgb);
		} catch (Exception ex) {
			log.error("Failed to set color to x/y coordinate: " + x + "/" + y + ": " + ex.getMessage(), ex);
			///ex.printStackTrace();
			System.exit(1);
		}

	}
	
	public int getColor(int x, int y)
	{
		if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight())
			return 0;
		
		return image.getRGB(x, y);
	}
	
	public DemCanvas getScaled(int width, int height)
	{
		boolean higherQuality = (width < this.width || height < this.height);
		
		BufferedImage scaled = ImageUtilities.getScaledInstance((BufferedImage)getImage(),
                width,
                height,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR,
                higherQuality);
		DemCanvas newCanvas = new DemCanvas(scaled);
		return newCanvas;
	}
	

	
	
	public void overlay(DemCanvas other, int x, int y, int width, int height)
	{
		overlay(other.getImage(), x, y, width, height);
	}
	
	public void overlay(Image other, int x, int y)
	{
		overlay(other, x, y, other.getWidth(this), other.getHeight(this));
	}
	
	public void overlay(Image other, int x, int y, int width, int height)
	{

		graphics.drawImage(other, x, y, width, height, this);
	}
	
	public Image getImage()
	{

		return image;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void reset()
	{
		WritableRaster raster = image.getRaster();
		int[] rasterPixel = {0, 0, 0, 0};
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				raster.setPixel(x, y, rasterPixel);
			}
		}
		
		//graphics.setColor(background);
		//graphics.fillRect(0, 0, getWidth(), getHeight());
	}
	
	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y,
			int width, int height) {
		
		return true;
	}
	
	
	public void save(String saveTo)
	{
		String format = null;
		if (saveTo.toLowerCase().endsWith(".png"))
			format = "png";
		else if (saveTo.toLowerCase().endsWith(".jpeg") || saveTo.toLowerCase().endsWith(".jpg"))
			format = "jpg";
		
		File writeFile = new File(saveTo);
		try {
			ImageIO.write((BufferedImage)getImage(), format, writeFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error("Failed to write image to disk: " + e.getMessage(), e);
		} 
	}

}
