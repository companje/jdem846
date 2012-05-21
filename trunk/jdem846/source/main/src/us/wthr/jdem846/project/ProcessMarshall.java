package us.wthr.jdem846.project;

import java.util.HashMap;
import java.util.Map;

public class ProcessMarshall
{
	private String id;
	private Map<String, String> options = new HashMap<String, String>();
	
	protected ProcessMarshall()
	{
		
	}
	
	protected ProcessMarshall(String id, Map<String, String> options)
	{
		this.id = id;
		this.options = options;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Map<String, String> getOptions()
	{
		return options;
	}

	public void setOptions(Map<String, String> options)
	{
		this.options = options;
	}

	
	
}
