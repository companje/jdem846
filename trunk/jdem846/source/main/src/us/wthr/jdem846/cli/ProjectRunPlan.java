package us.wthr.jdem846.cli;

import java.util.HashMap;
import java.util.Map;

public class ProjectRunPlan
{
	
	private String projectPath;
	private String saveImagePath;
	private Map<String, String> optionOverrides = new HashMap<String, String>();
	
	public ProjectRunPlan(String projectPath, String saveImagePath)
	{
		this.projectPath = projectPath;
		this.saveImagePath = saveImagePath;
	}

	public String getProjectPath()
	{
		return projectPath;
	}

	public String getSaveImagePath()
	{
		return saveImagePath;
	}

	public void addOptionOverride(String id, String value)
	{
		this.optionOverrides.put(id, value);
	}
	
	public Map<String, String> getOptionOverrides()
	{
		return optionOverrides;
	}
	
	
	
	
	
}
