package us.wthr.jdem846.exception;

import us.wthr.jdem846.graphics.RenderCodesEnum;

@SuppressWarnings("serial")
public class GraphicsRenderException extends Exception
{
	
	private RenderCodesEnum renderCode = RenderCodesEnum.RENDER_ERR_UNSPECIFIED;
	
	
	public GraphicsRenderException(String message)
	{
		super(message);
	}
	
	public GraphicsRenderException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	public GraphicsRenderException(String message, RenderCodesEnum renderCode)
	{
		super(message);
		this.renderCode = renderCode;
	}
	
	public GraphicsRenderException(String message, Throwable thrown, RenderCodesEnum renderCode)
	{
		super(message, thrown);
		this.renderCode = renderCode;
	}
	
	public RenderCodesEnum getRenderCode()
	{
		return this.renderCode;
	}
	
	
}
