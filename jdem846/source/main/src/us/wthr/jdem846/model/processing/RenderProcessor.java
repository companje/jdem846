package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;

public interface RenderProcessor
{
	
	public void setFrameBuffer(FrameBuffer frameBuffer);
	public void setView(View view);
	
}
