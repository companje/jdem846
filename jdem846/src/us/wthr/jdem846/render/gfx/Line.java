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

import java.awt.Color;

import us.wthr.jdem846.util.NumberUtil;


/** Defines a line existing in three-dimensional space
 * 
 * @author Kevin M. Gill
 *
 */
public class Line implements Renderable
{
	private Vector p0;
	private Vector p1;
	private int[] color;
	
	public Line(int[] color, Vector p0, Vector p1)
	{
		this.color = color;
		this.p0 = p0;
		this.p1 = p1;
	}
	
	public int[] getColor()
	{
		return color;
	}
	
	public Line copy()
	{
		return new Line(color, p0.copy(), p1.copy());
	}
	
	public void translate(Vector trans)
	{
		p0.translate(trans);
		p1.translate(trans);
	}
	
	public void rotate(Vector angles)
	{
		p0.rotate(angles);
		p1.rotate(angles);
	}
	
	public void rotate(double angle, int axis)
	{
		p0.rotate(angle, axis);
		p1.rotate(angle, axis);
	}
	
	public void projectTo(Vector eye, Vector near)//, double nearWidth, double nearHeight, double farDistance)
	{
		p0.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
		p1.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
		//return new Line(color, proj0, proj1);
	}
	
	public void setNormal(double[] normal)
	{
		
	}

	public void prepareForRender(double[] lightSource, double specularExponent)
	{
		
	}
	
	public void render(ViewportBuffer buffer, int viewportWidth, int viewportHeight)
	{
		double width = viewportWidth;
		double height = viewportHeight;
		
		int halfW = (int) Math.round(width / 2.0);
		int halfH = (int) Math.round(height / 2.0);
		
		Vector pMax = NumberUtil.getMaxPoint(p0, p1);
		Vector pMin = NumberUtil.getMinPoint(p0, p1, pMax);
		
		double x0 = pMax.getX();
		double y0 = pMax.getY();
		
		double x1 = pMin.getX();
		double y1 = pMin.getY();
		
		double xMn = NumberUtil.getLowest(x0, x1);
		double xMx = NumberUtil.getHighest(x0, x1);
		
		double yMn = NumberUtil.getLowest(y0, y1);
		double yMx = NumberUtil.getHighest(y0, y1);

		double mxX = pMax.getX();
		double mxY = pMax.getY();

		double mnX = pMin.getX();
		double mnY = pMin.getY();
		
		double s = (mnY - mxY) / (mnX - mxX);
		boolean isValidSlope = NumberUtil.isValidNumber(s);
		
		int[] color = {this.color[0], this.color[1], this.color[2]};
		
		if (Math.abs(x1 - x0) > Math.abs(y1 - y0)) {
			// Long

			for (double x = xMn; x <= xMx; x++) {
				double y = (isValidSlope) ? ((s * (x - mxX)) + mxY) : mxY;
				double f = (x - xMn) / (xMx - xMn);
				double z = interpolateZ(pMax, pMin, f);
				
				int screenX = (int)Math.round(x)+halfW;
				int screenY = (int)Math.round(y)+halfH;
				
				buffer.set(screenX, screenY, z, color[0], color[1], color[2]);
				//buffer.set(screenX, screenY, new ZPixel(z, color));
			}

		} else {
			// Tall
			
			//double iter = yMx - yMn;
			
			for (double y = yMn; y <= yMx; y++) {
				double x =  (isValidSlope) ? (((y - mxY) / s) + mxX) : mxX;
				double f = (y - yMn) / (yMx - yMn);
				double z = interpolateZ(pMax, pMin, f);
				
				int screenX = (int)Math.round(x)+halfW;
				int screenY = (int)Math.round(y)+halfH;
				
				buffer.set(screenX, screenY, z, color[0], color[1], color[2]);
				//buffer.set(screenX, screenY, new ZPixel(z, color));
			}

		}
	}
	
	
	
	
	public double interpolateZ(Vector p0, Vector p1, double frac)
	{
		//Vector pMin = getMinPoint();
		//Vector pMax = getMaxPoint();
		
		
		
		double z0 = p0.getZ();
		double z1 = p1.getZ();
		
		double value = 0;
		
		if (!NumberUtil.isValidNumber(frac)) {
			value = p0.getZ();
		} else {
			value = (z1 - z0)*frac + z0;
		}
		
		//System.out.println("z0/z1/frac/interpolated: " + z0 + "/" + z1 + "/" + frac + "/" + value);
		
		
		return value;
	}
	
	public Vector getVector(int i)
	{
		switch (i) {
		case 0:
			return p0;
		case 1:
			return p1;
		}
		return null;
	}
}
