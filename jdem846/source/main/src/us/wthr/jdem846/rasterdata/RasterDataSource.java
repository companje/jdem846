package us.wthr.jdem846.rasterdata;

import us.wthr.jdem846.rasterdata.generic.IRasterDefinition;

public class RasterDataSource
{
	private String filePath;
	private IRasterDefinition definition;
	
	
	public RasterDataSource(String filePath, IRasterDefinition definition)
	{
		this.filePath = filePath;
		this.definition = definition;
	}


	public String getFilePath()
	{
		return filePath;
	}


	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}


	public IRasterDefinition getDefinition()
	{
		return definition;
	}


	public void setDefinition(IRasterDefinition definition)
	{
		this.definition = definition;
	}
	
	
}
