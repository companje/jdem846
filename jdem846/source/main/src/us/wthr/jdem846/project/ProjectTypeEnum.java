package us.wthr.jdem846.project;

public enum ProjectTypeEnum
{
	STANDARD_PROJECT("us.wthr.jdem846.project.types.standardProject"),
	SCRIPT_PROJECT("us.wthr.jdem846.project.types.scriptProject"),
	DEM_IMAGE("us.wthr.jdem846.project.types.modelImage");
	
	private final String identifier;
	
	ProjectTypeEnum(String identifier)
	{
		this.identifier = identifier;
	}
	
	public String identifier() { return identifier; }
	
	
	public static ProjectTypeEnum getProjectTypeFromIdentifier(String identifier)
	{
		if (identifier == null) {
			return null;
		}
		
		for (ProjectTypeEnum value : ProjectTypeEnum.values()) {
			if (value.identifier.equalsIgnoreCase(identifier)) {
				return value;
			}
		}
		
		return null;
	}
}
