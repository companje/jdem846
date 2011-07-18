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

public class DemColor 
{
	double red;
	double green;
	double blue;
	double alpha;
	
	public DemColor()
	{
		red = 0;
		green = 0;
		blue = 0;
		alpha = 0;
	}
	
	public DemColor(double r, double g, double b, double a)
	{
		this.setColor(r, g, b, a);
	}
	
	public DemColor(int r, int g, int b, int a)
	{
		this.setColor(r, g, b, a);
	}
	
	
	public void setColor(double r, double g, double b, double a)
	{
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}
	
	public void setColor(int r, int g, int b, int a)
	{
		red = ((double)r) / 255.0;
		green = ((double)g) / 255.0;
		blue = ((double)b) / 255.0;
		alpha = ((double)a) / 255.0;
	}
	
	public boolean isBlack()
	{
		return (red == 0.0 && green == 0.0 && blue == 0.0 && alpha == 0.0);
	}

	
	public void lightenColor(double pct)
	{
		red = (red * (1.0 - pct)) + (1.0 * pct);
		green = (green * (1.0 - pct)) + (1.0 * pct);
		blue = (blue * (1.0 - pct)) + (1.0 * pct);
	}


	public void darkenColor(double pct)
	{
		red = (red * (1.0 - pct)) + (0.0 * pct);
		green = (green * (1.0 - pct)) + (0.0 * pct);
		blue = (blue * (1.0 - pct)) + (0.0 * pct);
	}
	
	
	public void adjustBrightness(double pct)
	{
		if (pct > 0 && pct <= 1.0) {
			lightenColor(pct);
		} else if (pct < 0 && pct >= -1.0) {
			darkenColor(Math.abs(pct));
		}
	}
	
	
	
	
	public double getRed() 
	{
		return red;
	}

	public double getGreen()
	{
		return green;
	}

	public double getBlue() 
	{
		return blue;
	}

	public double getAlpha()
	{
		return alpha;
	}
	
	public DemColor getCopy()
	{
		return new DemColor(red, green, blue, alpha);
	}
	
	public void toList(double[] color)
	{
		color[0] = red;
		color[1] = green;
		color[2] = blue;
		color[3] = alpha;
	}
	
	public void toList(int[] color)
	{
		color[0] = (int) Math.round(red * 0xFF);
		color[1] = (int) Math.round(green * 0xFF);
		color[2] = (int) Math.round(blue * 0xFF);
		color[3] = (int) Math.round(alpha * 0xFF);
	}
	
}
