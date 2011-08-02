package us.wthr.jdem846.util;

import java.util.UUID;

public class InstanceIdentifier
{
	
	private static String instanceId;
	
	static {
		UUID id = UUID.randomUUID();
		instanceId = id.toString();
		
		System.setProperty("us.wthr.jdem846.instance.id", instanceId);
	}
	
	
	public static String getInstanceId()
	{
		return InstanceIdentifier.instanceId;
	}
	
}
