package us.wthr.jdem846.graphics;

public class Texture {
	public int[] texture = null;
	public int width = 0;
	public int height = 0;
	public double left = 0;
	public double front = 0;
	
	
	public Texture(int width, int height, int[] texture)
	{
		this.width = width;
		this.height = height;
		this.texture = texture;
	}
	
}
