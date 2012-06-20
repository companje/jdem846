package us.wthr.jdem846;

import us.wthr.jdem846.articles.Spheres;
import us.wthr.jdem846.math.Vectors;

public class SphericalTesting
{
	
	
	public static void main(String[] args)
	{
		
		double[] P = new double[3];
		
		Spheres.getPoint3D(0, 0, 100, P);
		System.out.println("0/0: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		Spheres.getPoint3D(90, 0, 100, P);
		System.out.println("90/0: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		Spheres.getPoint3D(180, 0, 100, P);
		System.out.println("180/0: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		Spheres.getPoint3D(270, 0, 100, P);
		System.out.println("270/0: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		System.out.println("-------------------------------------------");
		
		Spheres.getPoint3D(0, -90, 100, P);
		System.out.println("0/-90: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		Spheres.getPoint3D(0, -45, 100, P);
		System.out.println("0/-45: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		Spheres.getPoint3D(0, 0, 100, P);
		System.out.println("0/0: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		Spheres.getPoint3D(0, 45, 100, P);
		System.out.println("0/45: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		Spheres.getPoint3D(0, 90, 100, P);
		System.out.println("0/90: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		
		System.out.println("-------------------------------------------");
		
		
		P = new double[] {100, 0, 0};
		Vectors.rotate(0, 90, 0, P);
		System.out.println("Rotate Y Axis 90 Degrees: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		P = new double[] {100, 0, 0};
		Vectors.rotate(0, 180, 0, P);
		System.out.println("Rotate Y Axis 180 Degrees: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
		P = new double[] {100, 0, 0};
		Vectors.rotate(0, 270, 0, P);
		System.out.println("Rotate Y Axis 270 Degrees: X: " + P[0] + ", Y: " + P[1] + ", Z: " + P[2]);
		
	}
	
}
