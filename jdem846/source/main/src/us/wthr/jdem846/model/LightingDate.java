package us.wthr.jdem846.model;

import java.util.Map;

public class LightingDate
{
	private long date = 0;
	
	public LightingDate()
	{
		
	}
	
	public LightingDate(long date)
	{
		setDate(date);
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date - (date % 86400000);
	}
	
	public static LightingDate fromString(String s)
	{
		Map<String, long[]> values = SimpleNumberListMapSerializer.parseLongListString(s);
		long[] date = values.get("date");
		return new LightingDate(date[0]);
	}
	
	public String toString()
	{
		String s = "date:[" + date + "]";
		return s;
	}
	
	public LightingDate copy()
	{
		LightingDate copy = new LightingDate(this.date);
		return copy;
	}
	
}
