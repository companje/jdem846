package us.wthr.jdem846.math;

public class Ellipsoid
{
	private double equitorialRadius;
	private double polarRadius;
	private double flattening;
	
	
	public Ellipsoid(double equitorialRadius, double flattening)
	{
		this.equitorialRadius = equitorialRadius;
		this.flattening = flattening;
		this.polarRadius = calculatePolarRadius(equitorialRadius, flattening);
	}
	
	public Ellipsoid(double equitorialRadius, double polarRadius, double flattening)
	{
		this.equitorialRadius = equitorialRadius;
		this.flattening = flattening;
		this.polarRadius = polarRadius;
	}
	
	
	public double getEquitorialRadius()
	{
		return equitorialRadius;
	}

	public double getPolarRadius()
	{
		return polarRadius;
	}

	public double getFlattening()
	{
		return flattening;
	}
	
	
	public void getXyzCoordinates(double latitude, double longitude, Vector vec)
	{
		getXyzCoordinates(latitude, longitude, 0, vec);
	}
	
	public void getXyzCoordinates(double latitude, double longitude, double elevation, Vector vec)
	{
		Vector v = this.getXyzCoordinates(latitude, longitude, elevation);
		vec.x = v.x;
		vec.y = v.y;
		vec.z = v.z;
	}
	
	public Vector getXyzCoordinates(double latitude, double longitude)
	{
		return getXyzCoordinates(latitude, longitude, 0);
	}
	
	public Vector getXyzCoordinates(double latitude, double longitude, double elevation)
	{
		latitude = Spheres.fixPhiDegrees(latitude);
		double radius = sphericalToEllipsoidRadiusGeocentric(latitude) + elevation;
		return Spheres.getPoint3D(longitude, latitude, radius);
	}
	
	
	public double sphericalToEllipsoidRadiusGeodetic(double latitude, double elevation)
	{
		//r = 1/sqrt(cos^2(x)/a^2 + sin^2(x)/b^2)		
		double ellipsoidRadius = 1.0 / MathExt.sqrt(MathExt.sqr(MathExt.cos_d(latitude)) / MathExt.sqr(equitorialRadius) + MathExt.sqr(MathExt.sin_d(latitude)) / MathExt.sqr(polarRadius));
		return ellipsoidRadius;
	}
	
	public double sphericalToEllipsoidRadiusGeocentric(double latitude)
	{
		double tanlat2 = MathExt.sqr(MathExt.tan(MathExt.radians(latitude)));
		double ellipsoidRadius = polarRadius*MathExt.pow((1+tanlat2), 0.5) /  MathExt.pow(( (MathExt.sqr(polarRadius) / MathExt.sqr(equitorialRadius))+tanlat2), 0.5);
		
		return ellipsoidRadius;
	}
	
	
	public static double calculatePolarRadius(double equitorialRadius, double flattening)
	{
		double polarRadius = equitorialRadius * (1.0 - flattening);
		return polarRadius;
	}

	
}
