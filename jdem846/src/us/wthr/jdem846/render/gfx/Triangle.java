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
import java.awt.geom.Path2D;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;

public class Triangle implements Renderable
{
	
	private int[] color;
	private int[] lightColor = {0, 0, 0};
	
	private double[] normal = {0, 0, 0};
	private boolean normalProvided = false;
	
	private BoundedArea boundedArea = null;
	private Path2D.Double polygon = null;
	
	private Vector p0;
	private Vector p1;
	private Vector p2; 
	
	public Triangle(int[] color, Vector p0, Vector p1, Vector p2)
	{
		this.color = color;
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		
		//prepareForRender();
	}
	
	public Triangle(int[] color, Vector p0, Vector p1, Vector p2, double[] normal)
	{
		this.color = color;
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		
		if (normal != null)
			setNormal(normal);
		//prepareForRender();
	}
	
	public int[] getColor()
	{
		return color;
	}
	
	public Renderable copy()
	{
		double[] copyNormal = (this.normalProvided) ? this.normal : null;
		
		return new Triangle(color, p0.copy(), p1.copy(), p2.copy(), copyNormal);
	}
	
	
	
	
	

	public void translate(Vector trans)
	{
		p0.translate(trans);
		p1.translate(trans);
		p2.translate(trans);
	}
	
	public void rotate(Vector angles)
	{
		p0.rotate(angles);
		p1.rotate(angles);
		p2.rotate(angles);
	}
	
	public void rotate(double angle, int axis)
	{
		p0.rotate(angle, axis);
		p1.rotate(angle, axis);
		p2.rotate(angle, axis);
	}
	
	public void projectTo(Vector eye, Vector near, double nearWidth, double nearHeight, double farDistance)
	{
		p0.projectTo(eye, near, nearWidth, nearHeight, farDistance);
		p1.projectTo(eye, near, nearWidth, nearHeight, farDistance);
		p2.projectTo(eye, near, nearWidth, nearHeight, farDistance);
		
		//double[] copyNormal = (this.normalProvided) ? this.normal : null;
		
		
		//return new Triangle(color, proj0, proj1, proj2, copyNormal);
	}
	
	public void setNormal(double[] normal)
	{
		this.normal = normal;
		this.normalProvided = true;
	}
	
	public void prepareForRender(double[] lightSource, double specularExponent)
	{
		polygon = new Path2D.Double();
		polygon.moveTo(p0.getX(), p0.getY());
		polygon.lineTo(p1.getX(), p1.getY());
		polygon.lineTo(p2.getX(), p2.getY());
		polygon.closePath();

		double lowX = NumberUtil.getLowX(p0, p1, p2);
		double lowY = NumberUtil.getLowY(p0, p1, p2);
		
		double highX = NumberUtil.getHighX(p0, p1, p2);
		double highY = NumberUtil.getHighY(p0, p1, p2);
		
		boundedArea = new BoundedArea(lowX, lowY, highX - lowX, highY - lowY);

		int setToColor[] = {color[0], color[1], color[2]};
		
		double p0Points[] = {p0.getX(), p0.getY(), p0.getZ()};
		double p1Points[] = {p1.getX(), p1.getY(), p1.getZ()};
		double p2Points[] = {p2.getX(), p2.getY(), p2.getZ()};;
		
		if (!this.normalProvided)
			Perspectives.calcNormal(p0Points, p1Points, p2Points, normal);
		
		double dot = Perspectives.dotProduct(normal, lightSource);
		dot = Math.pow(dot, specularExponent);

		//ColorUtil.adjustBrightness(dot, setToColor);
		
		ColorAdjustments.adjustBrightness(setToColor, dot);

		lightColor = setToColor;
		
		//this.lightColor = new Color(setToColor[0], setToColor[1], setToColor[2]);
		
	}
	
	
	
	public void render(ViewportBuffer buffer, int viewportWidth, int viewportHeight)
	{
		
		
		double width = viewportWidth;
		double height = viewportHeight;

		int halfWidth = (int) Math.round(width / 2.0);
		int halfHeight = (int) Math.round(height / 2.0);
		
		if (pointOrder() != COUNTERCLOCKWISE)
			return;
		
		
		double maxX = boundedArea.getX() + boundedArea.getWidth();
		double maxY = boundedArea.getY() + boundedArea.getHeight();
		
		
		
		for (double y = boundedArea.getY(); y < maxY; y++) {
			for (double x = boundedArea.getX(); x < maxX; x++) {
				if (polygon.intersects(x, y, 1, 1)) {
					int pxX = (int)Math.round(x);
					int pxY = (int)Math.round(y);

					double z = interpolateZ(x, y);
					buffer.set(pxX + halfWidth, pxY + halfHeight, z, lightColor[0], lightColor[1], lightColor[2]);
					
					//int[] color = {lightColor.getRed(), lightColor.getGreen(), lightColor.getBlue()};
					//buffer.set(pxX + halfWidth, pxY + halfHeight, new ZPixel(z, lightColor));
				}
			}
		}

		
	}
	
	
	/** I'm guessing here, this is probably _very_ wrong.
	 * 
	 */
	protected double interpolateZ(double x, double y)
	{
		
		double d0 = Math.sqrt(Math.abs(Math.pow(p0.getX() - x, 2) + Math.pow(p0.getY() - y, 2)));
		double d1 = Math.sqrt(Math.abs(Math.pow(p1.getX() - x, 2) + Math.pow(p1.getY() - y, 2)));
		double d2 = Math.sqrt(Math.abs(Math.pow(p2.getX() - x, 2) + Math.pow(p2.getY() - y, 2)));
		
		double sumZ = p0.getZ() + p1.getZ() + p2.getZ();
		double sumD = d0 + d1 + d2;
		
		double _z0 = p0.getZ() * (1 - (d0 / sumD));
		double _z1 = p1.getZ() * (1 - (d1 / sumD));
		double _z2 = p2.getZ() * (1 - (d2 / sumD));
		
		double sum_Z = _z0 + _z1 + _z2;
		
		double z0 = _z0 / (sum_Z / sumZ);
		double z1 = _z1 / (sum_Z / sumZ);
		double z2 = _z2 / (sum_Z / sumZ);
		
		double z = (z0 + z1 + z2) / 3;
		return z;
		
	}
	
	public int pointOrder()
	{
		Vector[] vectors = {p0, p1, p2};
		return pointOrder(vectors);
	}
	
	public int pointOrder(Vector[] vectors)
	{
		int i,j,k;
		int count = 0;
		int n = vectors.length;
		double z;

		if (vectors.length < 3)
			return(UNDEFINED);

		for (i=0;i<n;i++) {
			j = (i + 1) % n;
			k = (i + 2) % n;
			z  = (vectors[j].getX() - vectors[i].getX()) * (vectors[k].getY() - vectors[j].getY());
			z -= (vectors[j].getY() - vectors[i].getY()) * (vectors[k].getX() - vectors[j].getX());
			if (z < 0)
				count--;
			else if (z > 0)
				count++;
		}
		if (count > 0)
			return(COUNTERCLOCKWISE);
		else if (count < 0)
			return(CLOCKWISE);
		else
			return(UNDEFINED);
	}
}
