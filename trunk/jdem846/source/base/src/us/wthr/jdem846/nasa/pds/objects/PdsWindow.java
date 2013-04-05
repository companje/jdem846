package us.wthr.jdem846.nasa.pds.objects;

import us.wthr.jdem846.nasa.pds.annotations.PdsField;
import us.wthr.jdem846.nasa.pds.annotations.PdsObject;

@PdsObject(name="window")
public class PdsWindow
{
	private String name;
	private String description;
	private String targetName;
	
	private int firstLine;
	private int firstLineSample;
	private int lineSamples;
	private int lines;
	
	public PdsWindow()
	{
		
	}
	
	@PdsField(name="name", required=false)
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	@PdsField(name="description", required=true)
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@PdsField(name="target_name", required=false)
	public String getTargetName()
	{
		return targetName;
	}

	public void setTargetName(String targetName)
	{
		this.targetName = targetName;
	}

	@PdsField(name="first_line", required=true)
	public int getFirstLine()
	{
		return firstLine;
	}

	public void setFirstLine(int firstLine)
	{
		this.firstLine = firstLine;
	}

	@PdsField(name="first_line_sample", required=true)
	public int getFirstLineSample()
	{
		return firstLineSample;
	}

	public void setFirstLineSample(int firstLineSample)
	{
		this.firstLineSample = firstLineSample;
	}

	@PdsField(name="line_samples", required=true)
	public int getLineSamples()
	{
		return lineSamples;
	}

	public void setLineSamples(int lineSamples)
	{
		this.lineSamples = lineSamples;
	}

	@PdsField(name="lines", required=true)
	public int getLines()
	{
		return lines;
	}

	public void setLines(int lines)
	{
		this.lines = lines;
	}
	
	
	
}
