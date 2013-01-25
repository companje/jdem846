package us.wthr.jdem846.graphics;

public class TextureMapConfiguration
{
	public enum InterpolationTypeEnum {
		NEAREST,
		LINEAR
	};
	
	public enum TextureWrapTypeEnum {
		REPEAT,
		CLAMP,
		CLAMP_TO_EDGE
	};
	
	
	protected boolean createMipMaps = false;
	protected InterpolationTypeEnum interpolationType = InterpolationTypeEnum.NEAREST;
	protected TextureWrapTypeEnum textureWrapType = TextureWrapTypeEnum.CLAMP;
	
	
	public TextureMapConfiguration()
	{
		
	}
	
	public TextureMapConfiguration(boolean createMipMaps, InterpolationTypeEnum interpolationType, TextureWrapTypeEnum textureWrapType)
	{
		this.createMipMaps = createMipMaps;
		this.interpolationType = interpolationType;
		this.textureWrapType = textureWrapType;
	}


	public boolean getCreateMipMaps()
	{
		return createMipMaps;
	}


	public void setCreateMipMaps(boolean createMipMaps)
	{
		this.createMipMaps = createMipMaps;
	}


	public InterpolationTypeEnum getInterpolationType()
	{
		return interpolationType;
	}


	public void setInterpolationType(InterpolationTypeEnum interpolationType)
	{
		this.interpolationType = interpolationType;
	}


	public TextureWrapTypeEnum getTextureWrapType()
	{
		return textureWrapType;
	}


	public void setTextureWrapType(TextureWrapTypeEnum textureWrapType)
	{
		this.textureWrapType = textureWrapType;
	}
	
	
	
}
