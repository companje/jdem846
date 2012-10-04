package us.wthr.jdem846.graphics;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.DemKitPerformanceCompareMain;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Vector;

public class GraphicsRendererTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(GraphicsRendererTestMain.class);

		try {
			GraphicsRendererTestMain testMain = new GraphicsRendererTestMain();
			testMain.doTest();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	public void doTest() throws Exception
	{
		
		double[] a = new double[16];
		double[] b = new double[16];
		double[] p = new double[16];
		
		for (int i = 0; i < 16; i++) {
			a[i] = i;
			b[i] = i * 2;
		}
		
		
		Matrix A = new Matrix(a);
		Matrix B = new Matrix(b);
		Matrix P = new Matrix(p);
		
		A.multiply(B);
		
		Vector in = new Vector(444, 555, 666);
		in.w = 1.0;
		Vector out = new Vector();
		
		A.multiply(in, out);
		
		//Matrix.matmul4(p, a, b);
		
		int j = 0;
		
		
		int[] c0 = {0xFF, 0x0, 0x0, 0xFF};
		log.info("Red: " + ColorUtil.rgbaToInt(c0));
		
		int[] c1 = {0x0, 0xFF, 0x0, 0xFF};
		log.info("Green: " + ColorUtil.rgbaToInt(c1));
		
		int[] c2 = {0x0, 0x00, 0xFF, 0xFF};
		log.info("Blue: " + ColorUtil.rgbaToInt(c2));
		
		int[] c3 = {0x0, 0x00, 0x00, 0xFF};
		log.info("Black: " + ColorUtil.rgbaToInt(c3));
		
		int[] c4 = {0xFF, 0xFF, 0xFF, 0xFF};
		log.info("White: " + ColorUtil.rgbaToInt(c4));
		
		GraphicsRenderer renderer = new GraphicsRenderer();
		
		renderer.viewPort(500, 500);
		renderer.matrixMode(MatrixModeEnum.PROJECTION);
		
		double radius = DemConstants.EARTH_MEAN_RADIUS * 1000;
		
		renderer.ortho(-radius, radius, -radius, radius, -radius, radius);
		
		renderer.matrixMode(MatrixModeEnum.MODELVIEW);
		
		//renderer.rotate(10, AxisEnum.X_AXIS);
		//renderer.rotate(10, AxisEnum.Y_AXIS);
		//renderer.rotate(10, AxisEnum.Z_AXIS);
		
		renderer.scale(10, 10, 10);
		
		Vector vec = new Vector(-113750.25162290165, 6516747.5509679215, -0.0);
		
		renderer.project(vec);
		
		
		//256.34111441145734, 424.13899008841105, 0.2977610148672918
		
		int i = 0;
		
		
	}
	
	
}
