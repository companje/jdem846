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

package us.wthr.jdem846;

import us.wthr.jdem846.math.MathExt;


/** Utility class for common three-dimensional projections calculations.
 * 
 * @author Kevin M. Gill
 *
 */
public class Perspectives 
{
	private double buffer0[];
	private double buffer1[];
	private double buffer2[];

	
	public Perspectives()
	{
		buffer0 = new double[3];
		buffer1 = new double[3];
		buffer2 = new double[3];
	}
	
	public  void subtract(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[0] - pt1[0];
		usr[1] = pt0[1] - pt1[1];
		usr[2] = pt0[2] - pt1[2];
	}
	
	public  void add(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[0] + pt1[0];
		usr[1] = pt0[1] + pt1[1];
		usr[2] = pt0[2] + pt1[2];
	}

	public  void crossProduct(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[1] * pt1[2] - pt1[1] * pt0[2];
		usr[1] = pt0[2] * pt1[0] - pt1[2] * pt0[0];
		usr[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
	}

	public  double dotProduct(double[] pt0, double[] pt1)
	{
		double dot = 0.0f;
		
		normalize(pt0, buffer0);
		normalize(pt1, buffer1);

		dot = buffer0[0] * buffer1[0] + buffer0[1] * buffer1[1] + buffer0[2] * buffer1[2];

		return dot;
	}

	public  void normalize(double[] pt0, double[] no)
	{
		double len = (double) (MathExt.sqrt((pt0[0] * pt0[0]) + (pt0[1] * pt0[1]) + (pt0[2] * pt0[2])));
		if (len == 0.0) 
			len = 1.0;

		no[0] = pt0[0] / len;
		no[1] = pt0[1] / len;
		no[2] = pt0[2] / len;
	}

	public  void calcNormal(double[] pt0, double[] pt1, double[] pt2, double[] no)
	{
		if (pt2 == null) {
			no[0] = pt0[1] * pt1[2] - pt1[2] * pt0[2];
	        no[1] = pt0[0] * pt1[2] - pt1[0] * pt0[2];
	        no[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
		} else {
			
			subtract(pt0, pt1, buffer0);
			subtract(pt1, pt2, buffer1);
			crossProduct(buffer0, buffer1, buffer2);
			normalize(buffer2, no);

		}
		
	}
	
	
}
