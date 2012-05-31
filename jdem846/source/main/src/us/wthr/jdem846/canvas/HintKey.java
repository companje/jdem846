package us.wthr.jdem846.canvas;


public class HintKey
{
	private int key;
	
	public HintKey(int key)
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
		if (other != null && other instanceof HintKey) {
			return (((HintKey)other).key == this.key);
		} else {
			return false;
		}
	}
}
