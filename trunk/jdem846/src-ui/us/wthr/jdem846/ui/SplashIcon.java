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

import java.awt.Image;
import java.awt.image.ImageObserver;

public class SplashIcon implements ImageObserver
{
	private String text;
	private Image image;
	
	public SplashIcon(String text, Image image)
	{
		this.text = text;
		this.image = image;
	}

	public String getText()
	{
		return text;
	}

	public Image getImage()
	{
		return image;
	}

	public int getImageWidth()
	{
		return image.getWidth(this);
	}
	
	public int getImageHeight()
	{
		return image.getHeight(this);
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y,
			int width, int height)
	{
		return true;
	}
	
}
