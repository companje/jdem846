package us.wthr.jdem846.model.listModels;

import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.model.OptionListModel;

public class FrameBufferModeListModel extends OptionListModel<String>
{
	public FrameBufferModeListModel()
	{
		for (FrameBufferModeEnum mode : FrameBufferModeEnum.values()) {
			if (mode.displayOnUserInterfaces()) {
				this.addItem(mode.modeName(), mode.identifier());
			}
		}
	}
}
