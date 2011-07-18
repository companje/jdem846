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

package us.wthr.jdem846.render.gfx;

public class ZPixel
{
	private double z;
	private int[] color = {0, 0, 0};
	
	public ZPixel(double z, int[] color)
	{
		this.z = z;
		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
	}

	public double getZ()
	{
		return z;
	}

	public void setZ(double z)
	{
		this.z = z;
	}

	public int[] getColor()
	{
		return color;
	}

	public void setColor(int[] color)
	{
		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
	}
	
	
	
}
