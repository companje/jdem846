package us.wthr.jdem846.nasa.pds;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PdsObjectMap
{
	private String type;
	private File file;
	
	private Map<String, PdsFieldValue<?>> fieldMap = Maps.newHashMap();
	private List<PdsObjectMap> objects = Lists.newArrayList();
	
	public PdsObjectMap(String name)
	{
		setType(name);
	}
	
	
	public void addSubObject(PdsObjectMap subObject)
	{
		this.objects.add(subObject);
	}
	
	public List<PdsObjectMap> getSubObjects()
	{
		return objects;
	}
	
	public void setField(String name, PdsFieldValue<?> fieldValue)
	{
		fieldMap.put(name, fieldValue);
	}
	
	protected void setType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return type;
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
