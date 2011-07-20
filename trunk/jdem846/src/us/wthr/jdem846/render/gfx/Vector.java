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

/** Defines a point in three dimensional space.
 * 
 * @author Kevin M. Gill
 *
 */
public class Vector
{
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int Z_AXIS = 2;
	
	private double x;
	private double y;
	private double z;
	
	private int[] color = null;
	
	private static double[] tmpPoints1 = {0, 0, 0};
	private static double matrix3x3[][] = {
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}
	};
	private static double[][] translateMatrix = {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
	};
	
	
	public Vector()
	{
		this(0, 0, 0);
	}
	
	public Vector(double[] points)
	{
		this.x = points[0];
		this.y = points[1];
		this.z = points[2];
	}
	
	public Vector(int[] color, double[] points)
	{
		this.color = color;
		this.x = points[0];
		this.y = points[1];
		this.z = points[2];
	}
	
	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(int[] color, double x, double y, double z)
	{
		this.color = color;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector copy()
	{
		return new Vector(color, x, y, z);
	}
	
	
	/**
	 * 
	 * See: http://en.wikipedia.org/wiki/3D_projection
	 * @param eye
	 * @param near
	 * @param nearWidth
	 * @param nearHeight
	 * @param farDistance
	 * @return
	 */
	public void projectTo(Vector eye, Vector near)
	{
		double thetaX = 0; // Orientation of the camera
		double thetaY = 0;
		double thetaZ = 0;
		
		Vector e = near; // Viewer's position relative to the display surface
		Vector a = this; // 3D position of points being projected
		Vector c = eye;  // Camera position
		
		double sinTX = sin(thetaX);
		double sinTY = sin(thetaY);
		double sinTZ = sin(thetaZ);
		
		double cosTX = cos(thetaX);
		double cosTY = cos(thetaY);
		double cosTZ = cos(thetaZ);
		
		double dX = cosTY * (sinTZ * (a.y - c.y) + cosTZ * (a.x - c.x)) - sinTY * (a.z - c.z);
		double dY = sinTX * (cosTY * (a.z - c.z) + sinTY * (sinTZ * (a.y - c.y) + cosTZ * (a.x - c.x))) + cosTX * (cosTZ * (a.y - c.y) - sinTZ * (a.x - c.x));
		double dZ = cosTX * (cosTY * (a.z - c.z) + sinTY * (sinTZ * (a.y - c.y) + cosTZ * (a.x - c.x))) - sinTX * (cosTZ * (a.y - c.y) - sinTZ * (a.x - c.x));
		
		double bX = (dX - e.x) * (e.z / dZ);
		double bY = (dY - e.y) * (e.z / dZ);
		double bZ = z;

		this.x = bX;
		this.y = bY;
	}
	
	public void translate(Vector trans)
	{
		tmpPoints1[0] = x;
		tmpPoints1[1] = y;
		tmpPoints1[2] = z;
		translate(trans.getX(), trans.getY(), trans.getZ(), tmpPoints1);
		x = tmpPoints1[0];
		y = tmpPoints1[1];
		z = tmpPoints1[2];
	}
	

	public static void translate(double x, double y, double z, double[] xyz)
	{
		translateMatrix[0][3] = x;
		translateMatrix[1][3] = y;
		translateMatrix[2][3] = z;
		
		double tX = (translateMatrix[0][0] * xyz[0]) + (translateMatrix[0][1] * xyz[1]) + (translateMatrix[0][2] * xyz[2]) + (translateMatrix[0][3] * 1);
		double tY = (translateMatrix[1][0] * xyz[0]) + (translateMatrix[1][1] * xyz[1]) + (translateMatrix[1][2] * xyz[2]) + (translateMatrix[1][3] * 1);
		double tZ = (translateMatrix[2][0] * xyz[0]) + (translateMatrix[2][1] * xyz[1]) + (translateMatrix[2][2] * xyz[2]) + (translateMatrix[2][3] * 1);
		
		xyz[0] = tX;
		xyz[1] = tY;
		xyz[2] = tZ;
	}
	
	public void rotate(Vector angles)
	{
		tmpPoints1[0] = x;
		tmpPoints1[1] = y;
		tmpPoints1[2] = z;
		rotate(angles.getX(), angles.getY(), angles.getZ(), tmpPoints1);
		x = tmpPoints1[0];
		y = tmpPoints1[1];
		z = tmpPoints1[2];
	}

	
	public static void rotate(double x, double y, double z, double[] xyz) 
	{
		double sinAX = sin(-x);
		double sinAY = sin(-y);
		double sinAZ = sin(-z);
		
		double cosAX = cos(-x);
		double cosAY = cos(-y);
		double cosAZ = cos(-z);
		
		double rX = ((cosAY * cosAZ) * xyz[0]) + ((-sinAX*-sinAY*cosAZ+cosAX*sinAZ) * xyz[1]) + ((cosAX*-sinAY*cosAZ+sinAX*sinAZ) * xyz[2]);
		double rY = ((cosAY * -sinAZ) * xyz[0]) + ((-sinAX*-sinAY*-sinAZ+cosAX*cosAZ) * xyz[1]) + ((cosAX*-sinAY*-sinAZ+sinAX*cosAZ) * xyz[2]);
		double rZ = (sinAY * xyz[0]) + ((-sinAX*cosAY) * xyz[1]) + ((cosAX*cosAY) * xyz[2]);
		
		xyz[0] = rX;
		xyz[1] = rY;
		xyz[2] = rZ;
		
	}
	
	
	
	public void rotate(double angle, int axis)
	{
		switch (axis) {
		case X_AXIS:
			rotateX(angle);
			break;
		case Y_AXIS:
			rotateY(angle);
			break;
		case Z_AXIS:
			rotateZ(angle);
			break;
		}
	}
	
	public void rotateY(double angle)
	{
		matrix3x3[0][0] = cos(angle); 	matrix3x3[0][1] = 0; 	matrix3x3[0][2] = sin(angle);
		matrix3x3[1][0] = 0; 			matrix3x3[1][1] = 1; 	matrix3x3[1][2] = 0;
		matrix3x3[2][0] = -sin(angle); 	matrix3x3[2][1] = 0; 	matrix3x3[2][2] = cos(angle);
		
		double rX = (matrix3x3[0][0] * x) + (matrix3x3[0][1] * y) + (matrix3x3[0][2] * z);
		double rY = (matrix3x3[1][0] * x) + (matrix3x3[1][1] * y) + (matrix3x3[1][2] * z);
		double rZ = (matrix3x3[2][0] * x) + (matrix3x3[2][1] * y) + (matrix3x3[2][2] * z);
		
		this.x = rX;
		this.y = rY;
		this.z = rZ;
	}
	
	public void rotateX(double angle)
	{
		matrix3x3[0][0] = 1; 			matrix3x3[0][1] = 0; 			matrix3x3[0][2] = 0;
		matrix3x3[1][0] = 0; 			matrix3x3[1][1] = cos(angle); 	matrix3x3[1][2] = -sin(angle);
		matrix3x3[2][0] = 0; 			matrix3x3[2][1] = sin(angle); 	matrix3x3[2][2] = cos(angle);

		double rX = (matrix3x3[0][0] * x) + (matrix3x3[0][1] * y) + (matrix3x3[0][2] * z);
		double rY = (matrix3x3[1][0] * x) + (matrix3x3[1][1] * y) + (matrix3x3[1][2] * z);
		double rZ = (matrix3x3[2][0] * x) + (matrix3x3[2][1] * y) + (matrix3x3[2][2] * z);
		
		this.x = rX;
		this.y = rY;
		this.z = rZ;
	}
	
	public void rotateZ(double angle)
	{
		
		matrix3x3[0][0] = cos(angle); 	matrix3x3[0][1] = -sin(angle); 	matrix3x3[0][2] = 0;
		matrix3x3[1][0] = sin(angle); 	matrix3x3[1][1] = cos(angle); 	matrix3x3[1][2] = 0;
		matrix3x3[2][0] = 0; 			matrix3x3[2][1] = 0; 			matrix3x3[2][2] = 1;

		double rX = (matrix3x3[0][0] * x) + (matrix3x3[0][1] * y) + (matrix3x3[0][2] * z);
		double rY = (matrix3x3[1][0] * x) + (matrix3x3[1][1] * y) + (matrix3x3[1][2] * z);
		double rZ = (matrix3x3[2][0] * x) + (matrix3x3[2][1] * y) + (matrix3x3[2][2] * z);
		
		this.x = rX;
		this.y = rY;
		this.z = rZ;
	}
	
	public void setColor(int r, int g, int b)
	{
		if (this.color == null) {
			color = new int[3];
		}
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
	public void setColor(int[] color)
	{
		this.color = color;
	}
	
	public int[] getColor()
	{
		return color;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getZ()
	{
		return z;
	}

	public void setZ(double z)
	{
		this.z = z;
	}

	protected static double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected static double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}
}
