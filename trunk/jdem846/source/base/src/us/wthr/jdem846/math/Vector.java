package us.wthr.jdem846.math;

public class Vector
{

	public static final Vector X_AXIS_VECTOR = new Vector(1, 0, 0);
	public static final Vector Y_AXIS_VECTOR = new Vector(0, 1, 0);
	public static final Vector Z_AXIS_VECTOR = new Vector(0, 0, 1);

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

	public void set(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double length()
	{
		return MathExt.sqrt(MathExt.sqr(x) + MathExt.sqr(y) + MathExt.sqr(z));
	}

	public void scale(double f)
	{
		this.x *= f;
		this.y *= f;
		this.z *= f;
	}

	public double getDistanceTo(Vector other)
	{
		Vector v = new Vector();
		Vectors.subtract(this, other, v);
		return v.length();
	}

	public Vector getUnitVector()
	{
		double len = this.length();
		if (len <= 0.0) {
			return new Vector(0, 0, 0);
		} else {
			return new Vector(x / len, y / len, z / len, w / len);
		}
	}

	public Vector getNormalized()
	{
		double len = this.length();
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

	public void add(Vector b)
	{
		add(this, b);
	}

	/** this = a + b */
	public void add(Vector a, Vector b)
	{
		x = a.x + b.x;
		y = a.y + b.y;
		z = a.z + b.z;
	}

	public Vector plus(Vector arg)
	{
		Vector tmp = new Vector();
		tmp.add(this, arg);
		return tmp;
	}

	public Vector getDirectionTo(Vector other)
	{
		return other.subtract(this).getNormalized();
	}

	public double intersectDistance(Plane plane, Vector direction)
	{
		double ldotv = plane.getPlaneVector().dotProduct(direction);
		if (ldotv == 0) {
			return 0; // If I wasn't being lazy then I should be checking for
						// infinity instead
		}
		return -plane.getPlaneVector().dotProduct4(this) / ldotv;
	}

	public Vector intersectPoint(Vector direction, double intersectDistance)
	{
		if (intersectDistance == 0) {
			return null;
		}
		Vector intersect = new Vector(this.x + (direction.x * intersectDistance), this.y + (direction.y * intersectDistance), this.z + (direction.z * intersectDistance));
		return intersect;
	}
	
	public void rotate(double x, double y, double z)
	{
		Vectors.rotate(x, y, z, this);
	}
	
	public void rotate(double angle, int axis)
	{
		if (axis == Vectors.X_AXIS) {
			Vectors.rotateX(angle, this);
		} else if (axis == Vectors.Y_AXIS) {
			Vectors.rotateY(angle, this);
		} else if (axis == Vectors.Z_AXIS) {
			Vectors.rotateZ(angle, this);
		}
	}

	public Vector getCopy()
	{
		return new Vector(x, y, z);
	}
}
