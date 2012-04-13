package us.wthr.jdem846.model;

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
	
	public LightingDate copy()
	{
		LightingDate copy = new LightingDate(this.date);
		return copy;
	}
	
}
