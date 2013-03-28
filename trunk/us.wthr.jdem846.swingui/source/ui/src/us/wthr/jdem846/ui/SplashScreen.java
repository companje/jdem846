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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.StartupLoadNotifyQueue;
import us.wthr.jdem846.StartupLoadNotifyQueue.NotificationAddedListener;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.ui.base.Window;

@SuppressWarnings("serial")
public class SplashScreen extends Window
{
	private static Log log = Logging.getLog(SplashScreen.class);
	
	private Image splashImage = null;
	private String copyright = null;
	
	private static List<SplashIcon> splashIcons = new LinkedList<SplashIcon>();
	private static SplashScreen instance = null;
	
	private SplashNotifyList notifyList = new SplashNotifyList();;
	
	public SplashScreen()
	{

		try {
			splashImage = ImageIcons.loadImage(JDem846Properties.getProperty("us.wthr.jdem846.splash"));
		} catch (Exception ex) {
			log.error("Error loading splash screen image: " + ex.getMessage(), ex);
		}
		
		this.setSize(splashImage.getWidth(this), splashImage.getHeight(this));

		Graphics2D g2d = (Graphics2D) splashImage.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 100));
		int rectY = getHeight() - 10 - (notifyList.notifications.length * 17);
		int rectHeight = getHeight() - rectY - 5;
		g2d.fillRoundRect(5, rectY, getWidth() - 10, rectHeight, 10, 10);
		
		g2d.setColor(new Color(255, 255, 255, 80));
		rectY = getHeight() - 10 - (notifyList.notifications.length * 17) + 3;
		int rectX = getWidth() - 10 - 3;
		rectHeight = getHeight() - rectY - 5 - 3;
		int rectWidth = 3;
		g2d.fillRect(rectX, rectY, rectWidth, rectHeight);
		

		
		this.setLocationRelativeTo(null);
		
		SplashScreen.instance = this;

		StartupLoadNotifyQueue.addNotificationAddedListener(new NotificationAddedListener() {
			public void onNotificationAdded(String notification)
			{
				notifyList.add(notification);
				repaint();
			}
		});
		
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
			synchronized(SplashScreen.splashIcons) {
				SplashScreen.splashIcons.add(new SplashIcon(text, image));
				if (SplashScreen.instance.isVisible()) {
					SplashScreen.instance.repaint();
				}
			}
		}
	}
	
	
	protected Image createSplashDisplayImage()
	{
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.createGraphics();
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		
		if (splashImage != null) {
			g2d.drawImage(splashImage, 0, 0, getWidth(), getHeight(), this);
		}
		
		if (copyright != null) {
			int strWidth = fontMetrics.stringWidth(copyright);
			
			
			g2d.setColor(new Color(0, 0, 0, 100));
			g2d.fillRoundRect(getWidth() - strWidth - 10, 2, getWidth(), 18, 3, 6);
			
			g2d.setColor(Color.WHITE);
			
			g2d.drawString(copyright, getWidth() - strWidth - 5, 15);
		}
		
		int iconPaddingX = 40;
		int iconPaddingY = 10;
		int iconX = iconPaddingX;
		
		
		
		g2d.setColor(Color.WHITE);
		synchronized(SplashScreen.splashIcons) {
			for (SplashIcon splashIcon : splashIcons) {
				
				g2d.drawImage(splashIcon.getImage(), iconX, iconPaddingY, this);
				
				int strWidth = fontMetrics.stringWidth(splashIcon.getText());
				int strX = (int) ( iconX + ((double)splashIcon.getImageWidth() / 2) - ((double)strWidth / 2));
				
				g2d.drawString(splashIcon.getText(), strX, splashIcon.getImageHeight() + (iconPaddingY * 2));
				
				iconX += splashIcon.getImageWidth() + iconPaddingX;
				
			}
		}
		
		int rectY = getHeight() - 10 - (notifyList.notifications.length * 17) + 3;
		int rectX = getWidth() - 10 - 3;
		int rectHeight = getHeight() - rectY - 5 - 3;
		int rectWidth = 3;
		
		g2d.setColor(new Color(255, 255, 255, 180));
		double pct = (double) notifyList.notifications.length / (double)notifyList.totalAdded;
		if (pct > 1.0)
			pct = 1.0;
		int rectBottom = rectY + rectHeight;
		rectY = rectBottom - (int)MathExt.round(rectHeight * pct);
		rectHeight = rectBottom - rectY;
		g2d.fillRect(rectX, rectY, rectWidth, rectHeight);
		
		
		g2d.setColor(Color.WHITE);
		for (int i = notifyList.notifications.length - 1; i >= 0; i--) {
			
			String notification = notifyList.notifications[i];
			if (notification == null)
				continue;
			
			int x = 10;
			int y = getHeight() - 10 - (i * 17);
			
			g2d.drawString(notification, x, y);
			
			
		}
		
		g2d.dispose();
		return image;
	}
	
	@Override
	public boolean isDoubleBuffered()
	{
		return true;
	}
	
	@Override
	public void paint(Graphics g)
	{
		
		Graphics2D g2d = (Graphics2D) g;
		Image splash = createSplashDisplayImage();
		g2d.drawImage(splash, 0, 0, null);
		
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
