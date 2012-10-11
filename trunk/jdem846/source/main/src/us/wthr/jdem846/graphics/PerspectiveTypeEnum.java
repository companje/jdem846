package us.wthr.jdem846.graphics;

import us.wthr.jdem846.i18n.I18N;

public enum PerspectiveTypeEnum
{
	ORTHOGRAPHIC("us.wthr.jdem846.graphics.perspectiveType.orthographic"),
	PERSPECTIVE("us.wthr.jdem846.graphics.perspectiveType.perspective");
	
	
	private final String identifier;
	
	
	PerspectiveTypeEnum(String identifier)
	{
		this.identifier = identifier;
	}
	
	
	public String identifier() { return identifier; }
	public String perspectiveName() { return I18N.get(identifier); }
	
	
	public static PerspectiveTypeEnum getPerspectiveTypeFromIdentifier(String identifier)
	{
		for (PerspectiveTypeEnum item : PerspectiveTypeEnum.values()) {
			if (item.identifier() != null && item.identifier().equals(identifier)) {
				return item;
			}
		}
		return null;
	}
	
}
