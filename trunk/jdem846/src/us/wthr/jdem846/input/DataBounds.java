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

package us.wthr.jdem846.input;

import java.awt.Rectangle;

public class DataBounds
{
	private Rectangle rectangle;
	
	
	public DataBounds(int x, int y, int width, int height)
	{
		rectangle = new Rectangle(x, y, width, height);
	}
	
	
	public boolean contains(int x, int y)
	{
		return rectangle.contains(x, y);
	}
	
	public boolean contains(DataBounds bounds)
	{
		return rectangle.contains(bounds.rectangle);
	}
	
	public boolean overlaps(DataBounds bounds)
	{
		return rectangle.contains(bounds.getLeftX(), bounds.getTopY())
					|| rectangle.contains(bounds.getLeftX(), bounds.getBottomY())
					|| rectangle.contains(bounds.getRightX(), bounds.getTopY())
					|| rectangle.contains(bounds.getRightX(), bounds.getBottomY())
					|| bounds.contains(getLeftX(), getTopY())
					|| bounds.contains(getLeftX(), getBottomY())
					|| bounds.contains(getRightX(), getTopY())
					|| bounds.contains(getRightX(), getBottomY());
	}
	
	
	public int getLeftX()
	{
		return rectangle.x;
	}
	
	public int getRightX()
	{
		return rectangle.x + rectangle.width;
	}
	
	
	public int getTopY()
	{
		return rectangle.y;
	}
	
	public int getBottomY()
	{
		return rectangle.y + rectangle.height;
	}
	
	public int getWidth()
	{
		return rectangle.width;
	}
	
	public int getHeight()
	{
		return rectangle.height;
	}
	
	public DataBounds copy()
	{
		DataBounds copy = new DataBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		return copy;
	}
}
