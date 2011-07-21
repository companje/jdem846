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

package us.wthr.jdem846.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class ProcessWorkingSpinner extends JLabel
{
	private static int rows = 4;
	private static int columns = 8;
	private static String imagePath = "icons/process-working.png";
	
	private BufferedImage rawImage;
	private BufferedImage currentImage;
	
	private int imageWidth;
	private int imageHeight;
	
	private int tileWidth;
	private int tileHeight;
	
	private int currentRow = 0;
	private int currentCol = 0;
	
	private Dimension dimension = new Dimension(24, 24);
	
	private Timer timer;
	
	public ProcessWorkingSpinner()
	{
		rawImage = loadImage(imagePath);
		
		imageWidth = rawImage.getWidth(this);
		imageHeight = rawImage.getHeight(this);
		
		tileWidth = (int) Math.round((double)imageWidth / (double)columns);
		tileHeight = (int) Math.round((double)imageHeight / (double)rows);
		
		currentImage = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
		
		timer = new Timer(75, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				step();
			}
		});
		
	}
	
	public void start()
	{
		if (!isRunning()) {
			step();
			timer.start();
		}
	}
	
	public void stop()
	{
		if (isRunning()) {
			timer.stop();
			step();
			repaint();
		}
	}
	
	public boolean isRunning()
	{
		return timer.isRunning();
	}
	
	protected BufferedImage loadImage(String imagePath)
	{
		ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
		Image raw = icon.getImage();
		
		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.drawImage(raw, 0, 0, icon.getIconWidth(), icon.getIconHeight(), this);
		
		return image;
		
	}
	
	public void step()
	{
		currentCol++;
		if (currentCol >= columns) {
			currentCol = 0;
			currentRow++;
		}
		
		if (currentRow >= rows) {
			currentRow = 0;
		}
		
		// Skip blank
		if (isRunning() && currentRow == 0 && currentCol == 0) {
			currentCol = 1;
		} else if (!isRunning()) {
			currentRow = 0;
			currentCol = 0;
		}
		setCurrentImage();
	}
	
	public void setBlank()
	{
		currentCol = 0;
		currentRow = 0;
	}
	
	protected void setCurrentImage()
	{
		int tileX = (currentCol * tileWidth);
		int tileY = (currentRow * tileHeight);
		
		
		currentImage = rawImage.getSubimage(tileX, tileY, tileWidth, tileHeight);
		Image scaled = currentImage.getScaledInstance(dimension.width, dimension.height, Image.SCALE_SMOOTH);
		this.setIcon(new ImageIcon(scaled));
	}

	public Dimension getDimension()
	{
		return dimension;
	}

	public void setDimension(Dimension dimension)
	{
		this.dimension = dimension;
	}
	
	
	
}
