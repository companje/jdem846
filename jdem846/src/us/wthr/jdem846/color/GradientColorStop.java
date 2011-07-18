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
	private float position;
	private DemColor color;
	
	//1.000000, 1.000000, 1.000000, 1.000000
	public GradientColorStop(String source_line)
	{
		float _pos = 0;
		float _red = 0;
		float _green = 0;
		float _blue = 0;
		float _alpha = 1.0f;
		
		String[] parts = source_line.split(", ");
		if (parts.length == 4) {
			_pos = Float.parseFloat(parts[0]);
			_red = Float.parseFloat(parts[1]);
			_green = Float.parseFloat(parts[2]);
			_blue = Float.parseFloat(parts[3]);
			
			if (_red > 1)
				_red = _red / 255.0f;
			if (_green > 1)
				_green = _green / 255.0f;
			if (_blue > 1)
				_blue = _blue / 255.0f;
		}

		position = _pos;
		color = new DemColor(_red, _green, _blue, _alpha);
	}
	
	public String toString()
	{
		String s = "" + position + ", "
			+ color.red + ", "
			+ color.green + ", "
			+ color.blue;
		
		return s;
	}
	
	public void setPosition(float position)
	{
		this.position = position;
	}
	
	public float getPosition()
	{
		return position;
	}
	
	public DemColor getColor()
	{
		return color;
	}
}
