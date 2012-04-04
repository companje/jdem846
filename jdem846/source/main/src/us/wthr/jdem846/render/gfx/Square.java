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

import java.awt.geom.Path2D;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Defines a four-sided polygon existing in three-dimensional space.
 * 
 * @author Kevin M. Gill
 *
 */
@Deprecated
public class Square implements Renderable
{
	private static Log log = Logging.getLog(Square.class);
	
	private int[] color;
	
	Vector[] vectors = {null, null, null, null};
	
	private int[] lightColor = {0, 0, 0};
	private int[] pixelColor = {0, 0, 0};
	
	private double[] normal = {0, 0, 0};
	private boolean normalProvided = false;
	private double dotProduct = 0;
	
	private Perspectives perspectives = new Perspectives();
	
	private BoundedArea boundedArea = null;
	private Path2D.Double polygon = null;
	
	private static double p0Points[] = {0, 0, 0};
	private static double p1Points[] = {0, 0, 0};
	private static double p2Points[] = {0, 0, 0};
	
	public Square(int[] color, Vector p0, Vector p1, Vector p2, Vector p3)
	{
		this.boundedArea = new BoundedArea(0, 0, 0, 0);
		this.color = color;
		vectors[0] = p0;
		vectors[1] = p1;
		vectors[2] = p2;
		vectors[3] = p3;
	}
	
	public Square(int[] color, Vector p0, Vector p1, Vector p2, Vector p3, double[] normal)
	{
		this.boundedArea = new BoundedArea(0, 0, 0, 0);
		this.color = color;
		vectors[0] = p0;
		vectors[1] = p1;
		vectors[2] = p2;
		vectors[3] = p3;
		
		if (normal != null)
			this.setNormal(normal);
	}
	
	
	public int[] getColor()
	{
		return color;
	}
	
	public Renderable copy()
	{
		double[] copyNormal = (this.normalProvided) ? this.normal : null;
		return new Square(color, vectors[0].copy(), vectors[1].copy(), vectors[2].copy(), vectors[3].copy(), copyNormal);
	}
	
	public void translate(Vector trans)
	{
		for (Vector vector : vectors) 
			vector.translate(trans);
	}
	
	public void rotate(Vector angles)
	{
		for (Vector vector : vectors) 
			vector.rotate(angles);
	}
	
	public void rotate(double angle, int axis)
	{
		for (Vector vector : vectors) 
			vector.rotate(angle, axis);
	}
	
	public void projectTo(Vector eye, Vector near)//, double nearWidth, double nearHeight, double farDistance)
	{
		for (Vector vector : vectors) 
			vector.projectTo(eye, near);//, nearWidth, nearHeight, farDistance);
	}
	
	public void setNormal(double[] normal)
	{
		this.normal = normal;
		this.normalProvided = true;
	}

	public void prepareForRender(double[] lightSource, double specularExponent)
	{
		polygon = new Path2D.Double();
		polygon.moveTo(vectors[0].getX(), vectors[0].getY());
		polygon.lineTo(vectors[1].getX(), vectors[1].getY());
		polygon.lineTo(vectors[2].getX(), vectors[2].getY());
		polygon.lineTo(vectors[3].getX(), vectors[3].getY());
		polygon.closePath();


		double lowX = NumberUtil.getLowX(vectors);
		double lowY = NumberUtil.getLowY(vectors);
		
		double highX = NumberUtil.getHighX(vectors);
		double highY = NumberUtil.getHighY(vectors);
		
		boundedArea.set(lowX, lowY, highX - lowX, highY - lowY);

		lightColor[0] = color[0];
		lightColor[1] = color[1];
		lightColor[2] = color[2];
		
		p0Points[0] = vectors[0].getX(); p0Points[1] = vectors[0].getY(); p0Points[2] = vectors[0].getZ();
		p1Points[0] = vectors[1].getX(); p1Points[1] = vectors[1].getY(); p1Points[2] = vectors[1].getZ();
		p2Points[0] = vectors[2].getX(); p2Points[1] = vectors[2].getY(); p2Points[2] = vectors[2].getZ();

		if (!this.normalProvided)
			perspectives.calcNormal(p0Points, p1Points, p2Points, normal);
		
		double dot = perspectives.dotProduct(normal, lightSource);
		double dotOrig = dot;
		dot = Math.pow(dot, specularExponent);
		
		if (dotOrig < 0 && dot > 0)
			dot *= -1;
		
		this.dotProduct = dot;
		
		ColorAdjustments.adjustBrightnessAndContrast(lightColor, dot, 0);
	}
	
	
	public void render(ViewportBuffer buffer, int viewportWidth, int viewportHeight)
	{
		double width = viewportWidth;
		double height = viewportHeight;

		int halfWidth = (int) Math.round(width / 2.0);
		int halfHeight = (int) Math.round(height / 2.0);

		
		
		
		double maxX = boundedArea.getX() + boundedArea.getWidth();
		double maxY = boundedArea.getY() + boundedArea.getHeight();
		// TODO: limit to image bounds
		
		double minX = boundedArea.getX();
		double minY = boundedArea.getY();
		
		if (minX >= halfWidth || maxX < -halfWidth)
			return;
		if (minY >= halfHeight || minY < -halfHeight)
			return;
		
		if (minY < -halfHeight)
			minY = -halfHeight;
		if (maxY > halfHeight)
			maxY = halfHeight;
		if (minX < -halfWidth)
			minX = -halfWidth;
		if (maxX > halfWidth)
			maxX = halfWidth;
		
		//log.info("Square Bounds: " + boundedArea.getX() + "/" + boundedArea.getY() + " -> " + maxX + "/" + maxY);
		
		for (double y = minY; y < maxY; y++) {
			
			int pxY = (int)Math.round(y);
			int screenY = pxY + halfHeight;
			double yFrac = (y - boundedArea.getY()) / (boundedArea.getHeight());
			
			if (screenY < 0 || screenY > height)
				continue;
			
			for (double x = minX; x < maxX; x++) {
				
				int pxX = (int)Math.round(x);
				int screenX = pxX + halfWidth;
				double xFrac = (x - boundedArea.getX()) / (boundedArea.getWidth());
				
				if (screenX < 0 || screenX > width)
					continue;
				
				if (polygon.intersects(x, y, 1, 1)) {
					
					
					
					double z = interpolateZ(xFrac, yFrac);
					
					interpolateColor(xFrac, yFrac, pixelColor);
					getLightedColor(pixelColor);
					
					buffer.set(screenX, screenY, z, pixelColor[0], pixelColor[1], pixelColor[2]);
					
					//buffer.set(screenX, screenY, new ZPixel(z, pixelColor));
				}
			}
		}

	}
	
	public void getLightedColor(int[] color)
	{
		ColorAdjustments.adjustBrightnessAndContrast(color, dotProduct, 0);
	}
	
	public int pointOrder()
	{
		
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
	
	
	
	public void interpolateColor(double xFrac, double yFrac, int[] color)
	{
		int[] c0 = (vectors[0].getColor() == null) ? this.lightColor : vectors[0].getColor();
		int[] c1 = (vectors[1].getColor() == null) ? this.lightColor : vectors[1].getColor();
		int[] c2 = (vectors[2].getColor() == null) ? this.lightColor : vectors[2].getColor();
		int[] c3 = (vectors[3].getColor() == null) ? this.lightColor : vectors[3].getColor();

		int r = interpolate(c0[0], c3[0], c1[0], c2[0], xFrac, yFrac);
		int g = interpolate(c0[1], c3[1], c1[1], c2[1], xFrac, yFrac);
		int b = interpolate(c0[2], c3[1], c1[1], c2[1], xFrac, yFrac);
		

		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
	
	public double interpolateZ(double xFrac, double yFrac)
	{
		
		double s00 = vectors[0].getZ();
		double s01 = vectors[1].getZ();
		double s10 = vectors[2].getZ();
		double s11 = vectors[3].getZ();
		
		return interpolate(s00, s01, s10, s11, xFrac, yFrac);
	}
	
	public int interpolate(int i00, int i01, int i10, int i11, double xFrac, double yFrac)
	{
		double s00 = (double) i00;
		double s01 = (double) i01;
		double s10 = (double) i10;
		double s11 = (double) i11;

        return (int) Math.round(interpolate(s00, s01, s10, s11, xFrac, yFrac));
	}
	
	public double interpolate(double s00, double s01, double s10, double s11, double xFrac, double yFrac)
	{
		double s0 = (s01 - s00)*xFrac + s00;
        double s1 = (s11 - s10)*xFrac + s10;
        return (s1 - s0)*yFrac + s0;
	}

	
	public BoundedArea getBoundedArea()
	{
		return this.boundedArea;
	}
	
	public Vector getVector(int i)
	{
		if (i >= 0 && i < vectors.length)
			return vectors[i];
		else
			return null;
	}

}
