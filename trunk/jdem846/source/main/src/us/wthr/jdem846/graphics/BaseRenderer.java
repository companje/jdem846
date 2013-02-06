package us.wthr.jdem846.graphics;

import us.wthr.jdem846.graphics.framebuffer.FrameBufferModeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.util.ColorUtil;

public abstract class BaseRenderer implements IRenderer
{
	private static Log log = Logging.getLog(BaseRenderer.class);

	protected RenderCodesEnum error = RenderCodesEnum.RENDER_NO_ERROR;

	public BaseRenderer()
	{

	}

	@Override
	public RenderCodesEnum getError()
	{
		return this.error;
	}

	protected void setError(RenderCodesEnum error)
	{
		this.error = error;
	}
	
	@Override
	public void setLighting(Vector position, double emission, double ambient, double diffuse, double specular, double shininess)
	{

		IColor emissionColor = new Color(emission, emission, emission, 1.0);
		IColor ambientColor = new Color(ambient, ambient, ambient, 1.0);
		IColor diffuseColor = new Color(diffuse, diffuse, diffuse, 1.0);
		IColor specularColor = new Color(specular, specular, specular, 1.0);
		
		setLighting(position, emissionColor, ambientColor, diffuseColor, specularColor, shininess);
	}
	
	@Override
	public void setMaterial(double emission, double ambient, double diffuse, double specular, double shininess)
	{
		IColor emissionColor = new Color(emission, emission, emission, 1.0);
		IColor ambientColor = new Color(ambient, ambient, ambient, 1.0);
		IColor diffuseColor = new Color(diffuse, diffuse, diffuse, 1.0);
		IColor specularColor = new Color(specular, specular, specular, 1.0);
		
		setMaterial(emissionColor, ambientColor, diffuseColor, specularColor, shininess);
	}
	
	@Override
	public boolean bindTexture(Texture texture)
	{
		return bindTexture(texture, new TextureMapConfiguration());
	}

	@Override
	public void viewPort(int x, int y, int width, int height)
	{
		viewPort(x, y, width, height, FrameBufferModeEnum.STANDARD);
	}
	
	@Override
	public void clear(IColor backgroundColor)
	{
		clear((backgroundColor != null) ? backgroundColor.asInt() : 0x0);
	}
	
	@Override
	public void color(IColor color)
	{
		color((color != null) ? color.asInt() : 0x0);
	}
	
	@Override
	public void color(int[] color)
	{
		color(ColorUtil.rgbaToInt(color));
	}

	@Override
	public void vertex(Vector v)
	{
		vertex(v.x, v.y, v.z);
	}

}
