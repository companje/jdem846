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


/** Utility class for common three-dimensional projections calculations.
 * 
 * @author Kevin M. Gill
 *
 */
public class Perspectives 
{
	

	public static void subtract(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[0] - pt1[0];
		usr[1] = pt0[1] - pt1[1];
		usr[2] = pt0[2] - pt1[2];
	}

	public static void crossProduct(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[1] * pt1[2] - pt1[1] * pt0[2];
		usr[1] = pt0[2] * pt1[0] - pt1[2] * pt0[0];
		usr[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
	}

	public static double dotProduct(double[] pt0, double[] pt1)
	{
		double dot = 0.0f;
		
		double no0[] = {0.0f, 0.0f, 0.0f};
		double no1[] = {0.0f, 0.0f, 0.0f};
		
		normalize(pt0, no0);
		normalize(pt1, no1);

		dot = no0[0] * no1[0] + no0[1] * no1[1] + no0[2] * no1[2];

		return dot;
	}

	public static void normalize(double[] pt0, double[] no)
	{
		double len = (double) (Math.sqrt((pt0[0] * pt0[0]) + (pt0[1] * pt0[1]) + (pt0[2] * pt0[2])));
		if (len == 0.0) 
			len = 1.0f;

		no[0] = pt0[0] / len;
		no[1] = pt0[1] / len;
		no[2] = pt0[2] / len;
	}

	public static void calcNormal(double[] pt0, double[] pt1, double[] pt2, double[] no)
	{
		if (pt2 == null) {
			no[0] = pt0[1] * pt1[2] - pt1[2] * pt0[2];
	        no[1] = pt0[0] * pt1[2] - pt1[0] * pt0[2];
	        no[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
		} else {
			double e0[] = {0.0f, 0.0f, 0.0f};
			double e1[] = {0.0f, 0.0f, 0.0f};
			double cp[] = {0.0f, 0.0f, 0.0f};

			subtract(pt0, pt1, e0);
			subtract(pt1, pt2, e1);
			crossProduct(e0, e1, cp);
			normalize(cp, no);
		}
		
	}
	
	
}
