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

package us.wthr.jdem846.util;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageIcons
{
	
	private ImageIcons()
	{
		
	}
	
	public static Image loadImage(String path) throws IOException
	{
		URL url = ImageIcons.class.getResource(path);
		Image image = ImageIO.read(url);
		return image;
		
	}
	
	public static ImageIcon loadImageIcon(String path) throws IOException
	{
		URL url = ImageIcons.class.getResource(path);
		Image image = ImageIO.read(url);
		return new ImageIcon(image);
		
	}
	
}
