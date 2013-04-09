package us.wthr.jdem846.math;

public class Vector
{
	public double x = 0;
	public double y = 0;
	public double z = 0;
	public double w = 1.0;

	public Vector()
	{

	}

	public Vector(Vector copy)
	{
		this.x = copy.x;
		this.y = copy.y;
		this.z = copy.z;
		this.w = copy.w;
	}

	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(double x, double y, double z, double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void copyTo(Vector v)
	{
		v.x = this.x;
		v.y = this.y;
		v.z = this.z;
	}

	public double getLength()
	{
		return MathExt.sqrt(MathExt.sqr(x) + MathExt.sqr(y) + MathExt.sqr(z));
	}

	public double getDistanceTo(Vector other)
	{
		Vector v = new Vector();
		Vectors.subtract(this, other, v);
		return v.getLength();
	}

	public Vector getUnitVector()
	{
		double len = this.getLength();
		if (len <= 0.0) {
			return new Vector(0, 0, 0);
		} else {
			return new Vector(x / len, y / len, z / len, w / len);
		}
	}

	public Vector getNormalized()
	{
		double len = this.getLength();
		if (len == 0.0)
			len = 1.0;
		return new Vector(x / len, y / len, z / len, w / len);
	}
	
	public void normalize()
	{
		Vector n = getNormalized();
		this.x = n.x;
		this.y = n.y;
		this.z = n.z;
		this.w = n.w;
	}
	

	public Vector getInversed()
	{
		return new Vector(x * -1, y * -1, z * -1);
	}

	public double dotProduct(Vector other)
	{
		Vector v0 = this.getNormalized();
		Vector v1 = other.getNormalized();

		return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z;
	}

	public double dotProduct4(Vector other)
	{
		Vector v0 = this.getNormalized();
		Vector v1 = other.getNormalized();

		return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z + v0.w * v1.w;
	}

	public Vector crossProduct(Vector other)
	{
		return new Vector(this.y * other.z - other.y * this.z, this.z * other.x - other.z * this.x, this.x * other.y - other.x * this.y);
	}

	public double angle(Vector other)
	{
		double dot = this.dotProduct(other);
		return MathExt.degrees(MathExt.acos(MathExt.radians(dot)));
	}

	public Vector subtract(Vector other)
	{
		return new Vector(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w);
	}

	public Vector getDirectionTo(Vector other)
	{
		return other.subtract(this).getNormalized();
	}

	public double intersectDistance(Plane plane, Vector direction)
	{
		double ldotv = plane.getPlaneVector().dotProduct(direction);
		if (ldotv == 0) {
			return 0; // If I wasn't being lazy then I should be checking for infinity instead
		}
		return -plane.getPlaneVector().dotProduct4(this) / ldotv;
	}
	
	public Vector intersectPoint(Vector direction, double intersectDistance)
	{
		if (intersectDistance == 0) {
			return null;
		}
		Vector intersect = new Vector(
				this.x + (direction.x * intersectDistance),
				this.y + (direction.y * intersectDistance),
				this.z + (direction.z * intersectDistance));
		return intersect;
	}

	public Vector getCopy()
	{
		return new Vector(x, y, z);
	}
}
