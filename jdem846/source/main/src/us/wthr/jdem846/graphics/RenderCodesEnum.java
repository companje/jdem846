package us.wthr.jdem846.graphics;

public enum RenderCodesEnum {
	
	RENDER_NO_ERROR(0x0),
	RENDER_ERR_NO_FRAME_BUFFER_DEFINED(0x0A),
	RENDER_ERR_INVALID_DIMENSIONS(0x0B),
	RENDER_ERR_INVALID_OPERATION(0x0C),
	RENDER_ERR_INVALID_TEXTURE(0x0D),
	RENDER_ERR_INVALID_ENUM(0x0E),
	RENDER_ERR_INVALID_PARAMETERS(0x0F),
	RENDER_ERR_UNSPECIFIED(0x10);
	
	private final int code;
	
	RenderCodesEnum(int code)
	{
		this.code = code;
	}
	
	public int code()
	{
		return code;
	}
	
}
