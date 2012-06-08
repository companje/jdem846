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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class FileSaveThread extends Thread
{
	private static Log log = Logging.getLog(FileSaveThread.class);
	
	public static final int TYPE_UNSUPPORTED = -1;
	public static final int TYPE_JPEG = 0;
	public static final int TYPE_PNG = 1;
	
	private Image image;
	private String path;
	private int type;
	
	private List<SaveCompletedListener> saveCompletedListeners = new LinkedList<SaveCompletedListener>();
	

	public FileSaveThread(Image image, String path)
	{
		this.image = image;
		this.path = path;
		this.type = TYPE_JPEG;
	}
	
	public FileSaveThread(Image image, String path, int type)
	{
		this.image = image;
		this.path = path;
		this.type = type;
	}
	
	
	public void run()
	{
		
		String path = null;
		
		try {
			path = getCheckedPath();
		} catch(Exception ex) {
			log.error("Error checking filename", ex);
			this.fireSaveFailedListeners(ex);
			return;
		}
		log.info("Saving image to " + path);
		//String extension = null;
		
		if (path.lastIndexOf(".") >= 0) {
		//	extension = path.substring(path.lastIndexOf(".")+1);
		}
		
		
		//if (!isSupportedExtension())
			
		String formatName = getFormatType(type);
		//String formatName = path.substring(path.lastIndexOf(".")+1);
		File writeFile = new File(path);
		try {
			ImageIO.write((BufferedImage)image, formatName, writeFile);
			SharedStatusBar.setStatus("Image exported to " + writeFile);
			this.fireSaveSuccessfulListeners();
		} catch (IOException e) {
			log.error("Failed to write image to disk: " + e.getMessage(), e);
			this.fireSaveFailedListeners(e);
		} 
		
	}
	
	protected String getCheckedPath() throws IllegalArgumentException, InvalidFileFormatException
	{
		if (path == null)
			throw new IllegalArgumentException("path cannot be null");
		
		String extension = null;
		if (path.lastIndexOf(".") >= 0) {
			extension = path.substring(path.lastIndexOf(".") + 1);
			this.type = getFormatTypeConstant(extension);
			if (!isSupportedExtension(extension))
				throw new InvalidFileFormatException(extension);
		} else {
			extension = getTypeExtension(type);
			if (extension == null) {
				throw new InvalidFileFormatException("null");
			}
			path = path + "." + extension;
		}
		
		
		
		
		return path;
	}
	
	public static String getTypeExtension(int type)
	{
		switch(type) {
		case TYPE_PNG:
			return "png";
		case TYPE_JPEG:
			return "jpg";
		default:
			return null;
		}
	}
	
	public static String getFormatType(int type)
	{
		switch(type) {
		case TYPE_PNG:
			return "PNG";
		case TYPE_JPEG:
			return "JPEG";
		default:
			return null;
		}
	}
	
	public static int getFormatTypeConstant(String extension)
	{
		if (extension == null)
			return TYPE_UNSUPPORTED;
		
		if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
			return TYPE_JPEG;
		
		if (extension.equalsIgnoreCase("png"))
			return TYPE_PNG;
		
		return TYPE_UNSUPPORTED;
	}
	
	public static String getFormatType(String extension)
	{
		if (extension == null)
			return null;
		
		if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
			return getFormatType(TYPE_JPEG);
		
		if (extension.equalsIgnoreCase("png"))
			return getFormatType(TYPE_PNG);
		
		return null;
	}
	
	public static boolean isSupportedExtension(String extension)
	{
		if (extension == null)
			return false;
		
		if (extension.equalsIgnoreCase("png"))
			return true;
		if (extension.equalsIgnoreCase("jpg"))
			return true;
		if (extension.equalsIgnoreCase("jpeg"))
			return true;
		
		
		return false;
	}
	
	public void addSaveCompletedListener(SaveCompletedListener listener)
	{
		this.saveCompletedListeners.add(listener);
	}
	
	public boolean removeSaveCompletedListener(SaveCompletedListener listener)
	{
		return this.saveCompletedListeners.remove(listener);
	}
	
	protected void fireSaveSuccessfulListeners()
	{
		for (SaveCompletedListener listener : this.saveCompletedListeners) {
			listener.onSaveSuccessful();
		}
	}
	
	protected void fireSaveFailedListeners(Exception ex)
	{
		for (SaveCompletedListener listener : this.saveCompletedListeners) {
			listener.onSaveFailed(ex);
		}
	}
	
	
	public interface SaveCompletedListener
	{
		public void onSaveSuccessful();
		public void onSaveFailed(Exception ex);
	}
	
}
