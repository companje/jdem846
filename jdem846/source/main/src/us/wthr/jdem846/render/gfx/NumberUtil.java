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

@Deprecated
public class NumberUtil
{
	public static boolean isValidNumber(double n)
	{
		return ((!Double.isNaN(n)) && (!Double.isInfinite(n)));
	}
	
	public static double getLowest(double ... values)
	{
		double value = values[0];
		
		int len = values.length;
		
		for (int i = 0; i < len; i++) {
			if (values[i] <= value)
				value = values[i];
		}
		
		return value;
	}

	public static int getLowest(int ... values)
	{
		int value = values[0];
		
		int len = values.length;
		
		for (int i = 0; i < len; i++) {
			if (values[i] <= value)
				value = values[i];
		}
		
		return value;
	}
	
	public static double getHighest(double ... values)
	{
		double value = values[0];
		
		int len = values.length;
		
		for (int i = 0; i < len; i++) {
			if (values[i] >= value)
				value = values[i];
		}
		
		return value;
	}
	
	public static int getHighest(int ... values)
	{
		int value = values[0];
		
		int len = values.length;
		
		for (int i = 0; i < len; i++) {
			if (values[i] >= value)
				value = values[i];
		}
		
		return value;
	}
	
	public static double getLowX(Vector ... pt)
	{
		double value = pt[0].getX();
		for (int i = 0; i < pt.length; i++) {
			if (pt[i].getX() < value)
				value = pt[i].getX();
		}
		return value;

	}
	
	public static double getHighX(Vector ... pt)
	{
		double value = pt[0].getX();
		for (int i = 0; i < pt.length; i++) {
			if (pt[i].getX() > value)
				value = pt[i].getX();
		}
		return value;
	}
	
	
	public static double getLowY(Vector ... pt)
	{
		double value = pt[0].getY();
		for (int i = 0; i < pt.length; i++) {
			if (pt[i].getY() < value)
				value = pt[i].getY();
		}
		return value;
	}
	
	public static double getHighY(Vector ... pt)
	{
		double value = pt[0].getY();
		for (int i = 0; i < pt.length; i++) {
			if (pt[i].getY() > value)
				value = pt[i].getY();
		}
		return value;
	}
	
	public static double getLowZ(Vector ... pt)
	{
		double value = pt[0].getZ();
		for (int i = 0; i < pt.length; i++) {
			if (pt[i].getZ() < value)
				value = pt[i].getZ();
		}
		return value;
	}
	
	public static double getHighZ(Vector ... pt)
	{
		double value = pt[0].getZ();
		for (int i = 0; i < pt.length; i++) {
			if (pt[i].getZ() > value)
				value = pt[i].getZ();
		}
		return value;
	}	
	
	
	public static Vector getMinPoint(Vector p0, Vector p1)
	{
		return getMinPoint(p0, p1, null);
	}
	
	public static Vector getMinPoint(Vector p0, Vector p1, Vector test)
	{
		if (p0.getX() < p1.getX() && test != p0)
			return p0;
		else if (p1 != test)
			return p1;
		else 
			return p0;
	}
	
	
	
	public static Vector getMaxPoint(Vector p0, Vector p1)
	{
		return getMaxPoint(p0, p1, null);
	}
	
	public static Vector getMaxPoint(Vector p0, Vector p1, Vector test)
	{
		if (p0.getX() > p1.getX() && test != p0)
			return p0;
		else if (p1 != test)
			return p1;
		else 
			return p0;
	}
	
	
	/** Normalized sinc function
	 * 
	 * @param x
	 * @return
	 */
	public static double sinc(double x)
	{
		return (Math.sin(Math.PI * x) / (Math.PI * x));
	}
	
	/** Unnormalized sinc function
	 * 
	 * @param x
	 * @return
	 */
	public static double sincu(double x)
	{
		return (Math.sin(x) / x);
	}
}
