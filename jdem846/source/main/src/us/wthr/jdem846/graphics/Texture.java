package us.wthr.jdem846.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.util.ColorUtil;

public class Texture {
	public IIntBuffer texture = null;
	public int width = 0;
	public int height = 0;
	public double north = 0;
	public double south = 0;
	public double east = 0;
	public double west = 0;
	public double left = 0;
	public double front = 0;
	
	public double xResolution = 0;
	public double yResolution = 0;
	
	public Texture(int width, int height, IIntBuffer texture)
	{
		this(width, height, 0, 0, 0, 0, texture);
	}
	
	
	public Texture(int width, int height, double north, double south, double east, double west, IIntBuffer texture)
	{
		this.width = width;
		this.height = height;
		this.texture = texture;
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		
		this.xResolution = (east - west) / width;
		this.yResolution = (north - south) / height;
		
	}
	
	
	
	public double getLeft()
	{
		return left;
	}



	public void setLeft(double left)
	{
		this.left = left;
	}



	public double getFront()
	{
		return front;
	}



	public void setFront(double front)
	{
		this.front = front;
	}



	public int getWidth()
	{
		return width;
	}



	public int getHeight()
	{
		return height;
	}

	

	public double getNorth()
	{
		return north;
	}


	public double getSouth()
	{
		return south;
	}


	public double getEast()
	{
		return east;
	}


	public double getWest()
	{
		return west;
	}

	
	
	public double getXResolution()
	{
		return xResolution;
	}


	public double getYResolution()
	{
		return yResolution;
	}


	protected int index(double x, double y)
	{
		return index((int) MathExt.round(x), (int) MathExt.round(y));		
	}
	
	protected int index(int x, int y)
	{
		//if (x < 0 || x >= width || y < 0 || y >= height) {
		//	return -1;
		//}
		
		if (x < 0 || y < 0) {
			return -1;
		}
		
		if (x >= width) {
			x = x - width;
		}
		
		if (y >= height) {
			y = y - height;
		}
		
		int index = (y * width) + x;
		return index;
	}
	
	public int getColor(double x, double y)
	{
		return getColor((int)MathExt.round(x), (int) MathExt.round(y));
	}
	
	public int getColor(int x, int y)
	{
		int index = index(x, y);
		return getColor(index);
	}
	
	protected int getColor(int index)
	{
		if (index >= 0 && index < texture.capacity()) {
			return texture.get(index);
		} else {
			return 0x0;
		}
	}
	
	public int getColorNearest(double left, double front)
	{
		int x = (int) MathExt.round(left * (double)width);
		int y = (int) MathExt.round(front * (double)height);
		
		return getColor(x, y);
	}
	
	public int getColorLinear(double left, double front)
	{
		double x = (left * (double)width);
		double y = (front * (double)height); 
		
		double _x = MathExt.floor(x);
		double _y = MathExt.floor(y);
		
		double xFrac = x - _x;
		double yFrac = y - _y;
		
		boolean b00 = isValidCoordinate(_x + 0, _y + 0);
		boolean b01 = isValidCoordinate(_x + 1, _y + 0);
		boolean b10 = isValidCoordinate(_x + 0, _y + 1);
		boolean b11 = isValidCoordinate(_x + 1, _y + 1);
		
		
		int c00 = (b00) ? getColor(_x + 0, _y + 0) : 0x0;
		int c01 = (b01) ? getColor(_x + 1, _y + 0) : 0x0;
		int c10 = (b10) ? getColor(_x + 0, _y + 1) : 0x0;
		int c11 = (b11) ? getColor(_x + 1, _y + 1) : 0x0;
		
		c00 = getValidColor(c00, b00, c01, b01, c10, b10, c11, b11);
		c01 = getValidColor(c01, b01, c00, b00, c11, b11, c10, b10);
		c10 = getValidColor(c10, b10, c11, b11, c00, b00, c01, b01);
		c11 = getValidColor(c11, b11, c10, b10, c01, b01, c00, b00);
	

		int color =  ColorUtil.interpolateColor(c00, c01, c10, c11, xFrac, yFrac);

		return color;
	}
	
	protected int getValidColor(int c00, boolean b00, int c01, boolean b01, int c10, boolean b10, int c11, boolean b11)
	{
		if (b00)
			return c00;
		if (b01)
			return c01;
		if (b10)
			return c10;
		if (b11)
			return c11;
		
		return 0x0;
	}
	
	protected boolean isValidCoordinate(double x, double y)
	{
		if (texture == null) {
			return false;
		}
		
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/** Creates a copy of this texture object with the option to either use the same memory address for the
	 * texture bits or create a new array in memory
	 * 
	 * @param copyTexture Creates a new array in memory with the texture data copied over if true, otherwise the
	 * object returned will contain a reference to the same memory space.
	 * @return
	 */
	public Texture copy(boolean copyTexture)
	{
		Texture copy = new Texture(width, height, (IIntBuffer) texture.duplicate());	
		return copy;
	}
	
	
	public Texture getSubTexture(double north, double south, double east, double west)
	{
		
		int y0 = (int) MathExt.floor(((this.north - north) / (this.north - this.south)) * (double)this.height);
		int x0 = (int) MathExt.floor((1.0 - ((this.east - west) / (this.east - this.west))) * (double)this.width);
		
		int y1 = (int) MathExt.ceil(((this.north - south) / (this.north - this.south)) * (double)this.height);
		int x1 = (int) MathExt.ceil((1.0 - ((this.east - east) / (this.east - this.west))) * (double)this.width);
		
		if (x0 < 0)
			x0 = 0;
		if (y0 < 0)
			y0 = 0;
		if (x1 >= width)
			x1 = width - 1;
		if (y1 >= height)
			y1 = height - 1;
		
		int width = x1 - x0;
		int height = y1 - y0;
		
	//	width = (int) (MathExt.floor(((double)width / 4.0)) * 4.0);
		//height = (int) (MathExt.floor(((double)height / 4.0)) * 4.0);
		
		return getSubTexture(x0, y0, width, height);
	}
	
	public Texture getSubTexture(int x, int y, int width, int height)
	{
		if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
			return null; // or throw? Probably throw...
		}
		
		//if (x + width >= this.width) {
	//		width = this.width - x;
	//	}
		
		//if (y + height >= this.height) {
		//	height = this.height - y;
		//}
		
		//int subtexLength = width * height;
		
		IIntBuffer subtexBuffer = new SubTextureIntBuffer(texture, this.width, this.height, x, y, width, height);
		
		//IIntBuffer subtexBuffer = IntBuffer.allocate(subtexLength);
//		IIntBuffer subtexBuffer = BufferFactory.allocateIntBuffer(subtexLength);
//		for (int row = 0; row < height; row++) {
//			for (int col = 0; col < width; col++) {
//				int subtexIndex = (row * width) + col;
//				int maintexIndex = index(x + col, y + row);
//				subtexBuffer.put(subtexIndex, getColor(maintexIndex));
//			}
//		}
		
		double north = this.north - ((double)y / (double)this.height) * ((double)this.height * this.yResolution);
		double south = this.north - ((double)(y + height) / (double)this.height) * ((double)this.height * this.yResolution);
		
		double west = this.west + ((double)x / (double)this.width) * ((double)this.width * this.xResolution);
		double east = this.west + ((double)(x + width) / (double)this.width) * ((double)this.width * this.xResolution);
		
		Texture subtex = new Texture(width, height, north, south, east, west, subtexBuffer);
		return subtex;
	}
	
	public IntBuffer getAsIntBuffer()
	{
		return getAsByteBuffer().asIntBuffer();
	}
	
	public ByteBuffer getAsByteBuffer()
	{
		int capacityBytes = (int) texture.capacityBytes();
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(capacityBytes);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		
		for (int i = 0; i < texture.capacity(); i++) {
			int value = texture.get(i);
			intBuffer.put(i, value);
		}

		return byteBuffer;
	}
}
