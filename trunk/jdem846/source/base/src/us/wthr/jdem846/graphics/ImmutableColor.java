package us.wthr.jdem846.graphics;

public final class ImmutableColor extends Color
{

	public ImmutableColor(double r, double g, double b, double a)
	{
		super(r, g, b, a);
	}

	public ImmutableColor(double r, double g, double b)
	{
		super(r, g, b);
	}

	public ImmutableColor(float r, float g, float b, float a)
	{
		super(r, g, b, a);
	}

	public ImmutableColor(float r, float g, float b)
	{
		super(r, g, b);
	}

	public ImmutableColor(int r, int g, int b, int a)
	{
		super(r, g, b, a);
	}

	public ImmutableColor(int r, int g, int b)
	{
		super(r, g, b);
	}

	public ImmutableColor(int c)
	{
		super(c);
	}

	public ImmutableColor(int[] a, int offset)
	{
		super(a, offset);
	}

	public ImmutableColor(int[] a)
	{
		super(a);
	}

	public ImmutableColor(String hex)
	{
		super(hex);
	}

}
