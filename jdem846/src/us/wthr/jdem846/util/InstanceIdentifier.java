package us.wthr.jdem846.util;


public class InstanceIdentifier
{
	
	private static String instanceId;
	
	static {
		instanceId = UniqueIdentifierUtil.getNewIdentifier();
		
		System.setProperty("us.wthr.jdem846.instance.id", instanceId);
	}
	
	
	public static String getInstanceId()
	{
		return InstanceIdentifier.instanceId;
	}
	
}
