package us.wthr.jdem846.graphics.framebuffer;


import us.wthr.jdem846.i18n.I18N;

public enum FrameBufferModeEnum 
{
	STANDARD("us.wthr.jdem846.graphics.framebuffer.bufferMode.standard"),
	BINARY_SPACE_PARTITIONING("us.wthr.jdem846.graphics.framebuffer.bufferMode.binarySpacePartitioning");
	
	
	private final String identifier;
	
	
	FrameBufferModeEnum(String identifier)
	{
		this.identifier = identifier;
	}
	
	
	public String identifier() { return identifier; }
	public String modeName() { return I18N.get(identifier); }
	
	
	public static FrameBufferModeEnum getBufferModeFromIdentifier(String identifier)
	{
		for (FrameBufferModeEnum item : FrameBufferModeEnum.values()) {
			if (item.identifier() != null && item.identifier().equals(identifier)) {
				return item;
			}
		}
		return null;
	}
	
}
