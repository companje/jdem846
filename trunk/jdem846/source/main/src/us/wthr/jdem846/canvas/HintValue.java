package us.wthr.jdem846.canvas;

public class HintValue
{
	private int key;
	
	public HintValue(int key)
	{
		this.key = key;
	}
	
	public int getKey()
	{
		return key;
	}
	
	public int hashCode()
	{
		return System.identityHashCode(this);
	}
	
	public boolean equals(Object other)
	{
		if (other != null && other instanceof HintValue) {
			return (((HintValue)other).key == this.key);
		} else {
			return false;
		}
	}
}
