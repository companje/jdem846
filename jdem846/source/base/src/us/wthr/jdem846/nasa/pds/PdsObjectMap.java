package us.wthr.jdem846.nasa.pds;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

public class PdsObjectMap
{
	private String name;
	private File file;
	
	private Map<String, PdsFieldValue<?>> fieldMap = Maps.newHashMap();
	
	public PdsObjectMap(String name)
	{
		setName(name);
	}
	
	
	public void setField(String name, PdsFieldValue<?> fieldValue)
	{
		fieldMap.put(name, fieldValue);
	}
	
	protected void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setFile(File file)
	{
		this.file = file;
	}
	
	public File getFile()
	{
		return this.file;
	}
	
	public void putField(String name, PdsFieldValue<?> field)
	{
		fieldMap.put(name, field);
	}
	
}
