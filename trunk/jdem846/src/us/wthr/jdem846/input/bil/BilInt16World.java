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

package us.wthr.jdem846.input.bil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class BilInt16World {
	
	private static Log log = Logging.getLog(BilInt16World.class);
	
	private float xDim = 0;
	private float rotationTerm1 = 0;
	private float rotationTerm2 = 0;
	private float yDim = 0;
	private float xUpperLeft = 0;
	private float yUpperLeft = 0;
	
	
	private String filePath;
	
	
	public BilInt16World(String filePath)
	{
		this.filePath = filePath;
		init(filePath);
	}
	
	private void init(String filePath)
	{
		File headerFile = new File(filePath);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(headerFile));
			String line = null;
			int lineNum = 0;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				switch (lineNum) {
				case 0:
					xDim = Float.parseFloat(line);
					break;
				case 1:
					rotationTerm1 = Float.parseFloat(line);
					break;
				case 2:
					rotationTerm2 = Float.parseFloat(line);
					break;
				case 3:
					yDim = Float.parseFloat(line);
					break;
				case 4:
					xUpperLeft = Float.parseFloat(line);
					break;
				case 5:
					yUpperLeft = Float.parseFloat(line);
					break;
				}
				lineNum++;
			}
			reader.close();
				
			
		} catch (FileNotFoundException ex) {
			log.error("File Not Found error opening BIL world file: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("IO error reading from BIL world file: " + ex.getMessage(), ex);
		}
	}

	public float getxDim() 
	{
		return xDim;
	}

	public void setxDim(float xDim) 
	{
		this.xDim = xDim;
	}

	public float getRotationTerm1() 
	{
		return rotationTerm1;
	}

	public void setRotationTerm1(float rotationTerm1) 
	{
		this.rotationTerm1 = rotationTerm1;
	}

	public float getRotationTerm2() 
	{
		return rotationTerm2;
	}

	public void setRotationTerm2(float rotationTerm2)
	{
		this.rotationTerm2 = rotationTerm2;
	}

	public float getyDim()
	{
		return yDim;
	}

	public void setyDim(float yDim) 
	{
		this.yDim = yDim;
	}

	public float getxUpperLeft() 
	{
		return xUpperLeft;
	}

	public void setxUpperLeft(float xUpperLeft) 
	{
		this.xUpperLeft = xUpperLeft;
	}

	public float getyUpperLeft() 
	{
		return yUpperLeft;
	}

	public void setyUpperLeft(float yUpperLeft) 
	{
		this.yUpperLeft = yUpperLeft;
	}

	


	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath) 
	{
		this.filePath = filePath;
	}
	
	
	
	
	
}
