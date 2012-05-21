package us.wthr.jdem846.model;

import java.util.Map;

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
	
	public static LightingTime fromString(String s)
	{
		Map<String, long[]> values = SimpleNumberListMapSerializer.parseLongListString(s);
		long[] time = values.get("time");
		return new LightingTime(time[0]);
		
	}
	
	public String toString()
	{
		String s = "time:[" + time + "]";
		return s;
	}
	
	
	public LightingTime copy()
	{
		LightingTime copy = new LightingTime(this.time);
		return copy;
	}
}
