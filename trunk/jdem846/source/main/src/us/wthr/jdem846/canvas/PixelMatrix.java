package us.wthr.jdem846.canvas;

import java.util.HashMap;
import java.util.Map;

import us.wthr.jdem846.canvas.util.ColorUtil;

public class PixelMatrix extends AbstractBuffer
{
	
	private Map<HintKey, HintValue> renderingHints = new HashMap<HintKey, HintValue>();
	
	private final static float NO_Z_VALUE = -999999.999f;
	private final static int DEFAULT_STACK_DEPTH = 24;
	
	private int stackDepth = DEFAULT_STACK_DEPTH;
	private int[][] rgbaMatrix;
	private float[][] zMatrix;
	
	private int[] rgbaBufferA = new int[4];
	private int[] rgbaBufferB = new int[4];
	
	public PixelMatrix(int width, int height, int subpixelWidth)
	{
		this(width, height, DEFAULT_STACK_DEPTH, subpixelWidth);
	}
	
	public PixelMatrix(int width, int height, int stackDepth, int subpixelWidth)
	{
		super(width, height, subpixelWidth);
		
		this.stackDepth = stackDepth;
		rgbaMatrix = new int[getBufferLength()][];
		zMatrix = new float[getBufferLength()][];
		
		// Set default rendering hints
		
		if (stackDepth <= 0) {
			setRenderingHint(CanvasRenderingHints.KEY_PIXEL_DEPTH, CanvasRenderingHints.VALUE_DEPTH_UNLIMITED);
		} else {
			setRenderingHint(CanvasRenderingHints.KEY_PIXEL_DEPTH, CanvasRenderingHints.VALUE_DEPTH_LIMITED);
		}
		
		if (stackDepth == 1) {
			setRenderingHint(CanvasRenderingHints.KEY_ALPHA_HANDLING, CanvasRenderingHints.VALUE_ALPHA_OVERLAY);
		} else {
			setRenderingHint(CanvasRenderingHints.KEY_ALPHA_HANDLING, CanvasRenderingHints.VALUE_ALPHA_STACK_ONLY);
		}
		
		reset();
	}
	
	public void dispose()
	{
		
	}
	
	@Override
	public void reset()
	{
		reset(0x0);
	}
	
	public void reset(int backgroundColor)
	{
		if (rgbaMatrix == null && zMatrix == null) {
			return;
		}
		
		
		for (int i = 0; i < getBufferLength(); i++) {
			if (rgbaMatrix != null) {
				rgbaMatrix[i] = new int[1];
				rgbaMatrix[i][0] = backgroundColor;
			}
			if (zMatrix != null) {
				zMatrix[i] = new float[1];
				zMatrix[i][0] = NO_Z_VALUE;
			}
		}
		
		
	}
	
	
	public int[] getRgbaStack(double x, double y)
	{
		int matrixIndex = this.getIndex(x, y);
		return rgbaMatrix[matrixIndex];
	}
	
	public boolean isPixelFilled(double x, double y)
	{
		return (getMaxZIndex(x, y) != NO_Z_VALUE);
	}
	
	public double getMaxZIndex(double x, double y)
	{
		int matrixIndex = this.getIndex(x, y);
		
		return zMatrix[matrixIndex][0];
	}
	
	public void set(double x, double y, double z, int rgba)
	{
		
		int matrixIndex = this.getIndex(x, y);
		
		if (!isVisible(x, y, z)) {
			return;
		}
		
		if (isPixelDepthLimited() && stackDepth == 1 && overlayTransparentColors()) {
			
			if (zMatrix[matrixIndex][0] < z) {
				int existing = rgbaMatrix[matrixIndex][0];
				
				if (existing != 0x0) {
					rgba = ColorUtil.overlayColor(rgba, existing);
				}
				
				rgbaMatrix[matrixIndex][0] = rgba;
				zMatrix[matrixIndex][0] = (float) z;
				
			}

			
		} else {
		
			
			int stackIndex = determineStackIndex(x, y, z);
			
			if (isPixelDepthLimited() && stackIndex >= stackDepth) {
				return;
			}
			
			if (!isPixelDepthLimited() || (isPixelDepthLimited() && zMatrix[matrixIndex].length < stackDepth)) {
				float[] newZStack = new float[zMatrix[matrixIndex].length + 1];
				int[] newRgbaStack = new int[rgbaMatrix[matrixIndex].length + 1];
				
				for (int i = 0; i < zMatrix[matrixIndex].length; i++) {
					newZStack[i] = zMatrix[matrixIndex][i];
					newRgbaStack[i] = rgbaMatrix[matrixIndex][i];
				}
				
				zMatrix[matrixIndex] = newZStack;
				rgbaMatrix[matrixIndex] = newRgbaStack;
				
			}
			
			for (int i = zMatrix[matrixIndex].length - 1; i > stackIndex; i--) {
				rgbaMatrix[matrixIndex][i] = rgbaMatrix[matrixIndex][i - 1];
				zMatrix[matrixIndex][i] = zMatrix[matrixIndex][i - 1];
			}
			
			zMatrix[matrixIndex][stackIndex] = (float) z;
			rgbaMatrix[matrixIndex][stackIndex] = rgba;
		}
		
	}

	protected int determineStackIndex(double x, double y, double z)
	{
		int matrixIndex = this.getIndex(x, y);
		
		for (int i = 0; i < zMatrix[matrixIndex].length; i++) {
			if (z > zMatrix[matrixIndex][i]) {
				return i;
			}
		}
		
		return zMatrix[matrixIndex].length;
				
	}
	
	/** Determine if a point would be visible given the specified z-index. Visiblity is determined
	 * a false if there is a non-transparent pixel with a higher z-index a the x/y coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	protected boolean isVisible(double x, double y, double z)
	{

		int matrixIndex = this.getIndex(x, y);
		
		for (int i = 0; i < zMatrix[matrixIndex].length; i++) {
			
			ColorUtil.intToRGBA(rgbaMatrix[matrixIndex][i], rgbaBufferB);
			
			if (zMatrix[matrixIndex][i] > z && rgbaBufferB[3] == 255) {
				return false;
			}

		}
		
		return true;
	}

	public boolean isVisibleAbsolute(double x, double y, double z)
	{
		int matrixIndex = this.getIndex(x, y);
		
		for (int i = 0; i < zMatrix[matrixIndex].length; i++) {
			if (zMatrix[matrixIndex][i] > z) {
				return false;
			}

		}
		
		return true;
		
	}

		
	
	
	protected boolean overlayTransparentColors()
	{
		return (renderingHints.get(CanvasRenderingHints.KEY_ALPHA_HANDLING) != null && renderingHints.get(CanvasRenderingHints.KEY_ALPHA_HANDLING).equals(CanvasRenderingHints.VALUE_ALPHA_OVERLAY));
	}
	
	protected boolean isPixelDepthLimited()
	{
		return (renderingHints.get(CanvasRenderingHints.KEY_PIXEL_DEPTH) != null && renderingHints.get(CanvasRenderingHints.KEY_PIXEL_DEPTH).equals(CanvasRenderingHints.VALUE_DEPTH_LIMITED));
	}
	
	
	public void setRenderingHint(HintKey key, HintValue value)
	{
		renderingHints.put(key, value);
	}
	
	public void setRenderingHints(Map<HintKey, HintValue> hints)
	{
		renderingHints.putAll(hints);
	}
	
	public HintValue getRenderingHint(HintKey key)
	{
		return renderingHints.get(key);
	}
	
	public Map<HintKey, HintValue> getRenderingHints()
	{
		return renderingHints;
	}
	
}
