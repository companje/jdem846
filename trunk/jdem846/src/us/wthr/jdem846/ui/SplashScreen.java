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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JWindow;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Window;
import us.wthr.jdem846.util.ImageIcons;

@SuppressWarnings("serial")
public class SplashScreen extends Window
{
	private static Log log = Logging.getLog(SplashScreen.class);
	
	private Image splashImage = null;
	private String copyright = null;
	
	private static List<SplashIcon> splashIcons = new LinkedList<SplashIcon>();
	private static SplashScreen instance = null;
	
	public SplashScreen()
	{
		
		
		
		try {
			splashImage = ImageIcons.loadImage(JDem846Properties.getProperty("us.wthr.jdem846.splash"));
		} catch (Exception ex) {
			log.error("Error loading splash screen image: " + ex.getMessage(), ex);
		}
		
		if (splashImage != null) {
			this.setSize(splashImage.getWidth(this), splashImage.getHeight(this));
		}
		
		
		//this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(null);
		
		SplashScreen.instance = this;
	}
	

	public static void addIcon(String path, String text)
	{
		try {
			Image image = ImageIcons.loadImage(path);
			SplashScreen.addIcon(image, text);
		} catch (IOException ex) {
			log.warn("Failed to load image: " + ex.getMessage(), ex);
		}
	}
	
	public static void addIcon(Image image, String text)
	{
		if (SplashScreen.instance != null) {
			SplashScreen.splashIcons.add(new SplashIcon(text, image));
			if (SplashScreen.instance.isVisible()) {
				SplashScreen.instance.repaint();
			}
		}
	}
	
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		if (splashImage != null) {
			g2d.drawImage(splashImage, 0, 0, getWidth(), getHeight(), this);
		}
		
		if (copyright != null) {
			g2d.setColor(Color.WHITE);
			g2d.drawString(copyright, 5, getHeight() - 5);
		}
		
		int iconPaddingX = 40;
		int iconPaddingY = 10;
		int iconX = iconPaddingX;
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		
		g2d.setColor(Color.WHITE);
		for (SplashIcon splashIcon : splashIcons) {
			
			g2d.drawImage(splashIcon.getImage(), iconX, iconPaddingY, this);
			
			int strWidth = fontMetrics.stringWidth(splashIcon.getText());
			int strX = (int) ( iconX + ((double)splashIcon.getImageWidth() / 2) - ((double)strWidth / 2));
			
			g2d.drawString(splashIcon.getText(), strX, splashIcon.getImageHeight() + (iconPaddingY * 2));
			
			iconX += splashIcon.getImageWidth() + iconPaddingX;
			
		}
		
		
	}


	public String getCopyright()
	{
		return copyright;
	}


	public void setCopyright(String copyright)
	{
		this.copyright = copyright;
	}
	

}
