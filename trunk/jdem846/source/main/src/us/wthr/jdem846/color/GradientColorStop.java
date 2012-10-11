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

package us.wthr.jdem846.color;

public class GradientColorStop 
{
	private double position;
	private DemColor color;
	
	public GradientColorStop(double position, double red, double green, double blue, double alpha)
	{
		if (red > 1.0) {
    		red = red / 255;
    	}
    	
    	if (green > 1.0) {
    		green = green / 255;
    	}
    	
    	if (blue > 1.0) {
    		blue = blue / 255;
    	}
    	
    	if (alpha > 1.0) {
    		alpha = alpha / 255;
    	}
    	
    	this.position = position;
    	color = new DemColor(red, green, blue, alpha);
	}

	public GradientColorStop(double position, DemColor color)
	{
		this.position = position;
		this.color = color;
	}
	
	public GradientColorStop copy()
	{
		return new GradientColorStop(this.position, this.color.copy());
	}
	
	
	public String toString()
	{
		String s = "" + position + ", "
			+ color.red + ", "
			+ color.green + ", "
			+ color.blue;
		
		return s;
	}
	
	public void setPosition(double position)
	{
		this.position = position;
	}
	
	public double getPosition()
	{
		return position;
	}
	
	public DemColor getColor()
	{
		return color;
	}
}
