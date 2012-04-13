package us.wthr.jdem846.model;

public class LightingTime
{
	
	private long time = 0; /* Giggity! */
	
	public LightingTime()
	{
		
	}
	
	public LightingTime(long time)
	{
		setTime(time);
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = (time % 86400000);
	}
	
	public LightingTime copy()
	{
		LightingTime copy = new LightingTime(this.time);
		return copy;
	}
}
