package us.wthr.jdem846.project;

import java.util.Map;

import us.wthr.jdem846.exception.ProjectMarshalException;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.exceptions.ModelContainerException;

public class ProcessMarshall
{
	private String id;
	private Map<String, String> options;
	
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

	public Map<String, String> getOptions()
	{
		return options;
	}
	
	
	public static ProcessMarshall marshalProcess(ModelProcessContainer processContainer) throws ProjectMarshalException
	{
		ProcessMarshall pm = new ProcessMarshall();
		
		pm.id = processContainer.getProcessId();
		
		try {
			pm.options = processContainer.getOptionModelContainer().getPropertyMapById();
		} catch (ModelContainerException ex) {
			throw new ProjectMarshalException("Error fetching process options: " + ex.getMessage(), ex);
		}
		
		return pm;
	}
	
}
