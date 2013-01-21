package us.wthr.jdem846.graphics;

import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.modelgrid.IModelGrid;

public class TextureFactory
{
	
	
	public static Texture createTexture(IModelGrid modelGrid)
	{
		Texture texture = null;
		
		int width = modelGrid.getWidth();
		int height = modelGrid.getHeight();
		
		double north = modelGrid.getNorth();
		double south = modelGrid.getSouth();
		double east = modelGrid.getEast();
		double west = modelGrid.getWest();

		IIntBuffer modelTextureBuffer = modelGrid.getModelTexture();
		texture = new Texture(width, height, north, south, east, west, modelTextureBuffer);
		
		return texture;
	}
	
}
