package us.wthr.jdem846.graphics.framebuffer;


import us.wthr.jdem846.i18n.I18N;

public enum FrameBufferModeEnum 
{
	STANDARD("us.wthr.jdem846.graphics.framebuffer.bufferMode.standard", true),
	BINARY_SPACE_PARTITIONING("us.wthr.jdem846.graphics.framebuffer.bufferMode.binarySpacePartitioning", true),
	CONCURRENT_PARTIAL_FRAME_BUFFER("us.wthr.jdem846.graphics.frameBuffer.bufferMode.concurrentPartial", false);
	
	
	private final String identifier;
	private final boolean displayOnUserInterfaces;
	
	FrameBufferModeEnum(String identifier, boolean displayOnUserInterfaces)
	{
		this.identifier = identifier;
		this.displayOnUserInterfaces = displayOnUserInterfaces;
	}
	
	
	public String identifier() { return identifier; }
	public String modeName() { return I18N.get(identifier); }
	public boolean displayOnUserInterfaces() { return displayOnUserInterfaces; }
	
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
