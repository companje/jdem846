package us.wthr.jdem846;

import us.wthr.jdem846.cli.ProjectExecutor;
import us.wthr.jdem846.cli.ProjectRunPlan;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Plane;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

public class SandboxTestMain extends AbstractTestMain
{
	private static Log log = null;
	

	
	public static void main(String[] args)
	{
		try {
			AbstractTestMain.initialize(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		SandboxTestMain main = new SandboxTestMain();
		main.run();
		
		
		//int frameNum = 1000000;
		//for (double e = start; e <= stop; e+=1) {
		//	System.setProperty("seaLevel.elevation", "" + e);
			
			/*
			ProjectRunPlan runPlan = new ProjectRunPlan("C:\\Users\\GillFamily\\Google Drive\\jDem Visuals\\Earth Flooding"
														, "C:\\Users\\GillFamily\\Google Drive\\jDem Visuals\\Earth Flooding\\test-output.jpg");
			
			runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.eyeDistance", ""+2500);
			ProjectExecutor exec = new ProjectExecutor();
			try {
				exec.executeProject(runPlan);
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			*/
		//	frameNum++;
	//	}
	}
	
	public void run()
	{
		double start = 0;
		double stop = 360;
		double step = 0.25;
		double startAngle = 185.0;
		long startTime = -4620000;
		

		String projectPath = "C:\\Users\\GillFamily\\Google Drive\\Wet Mars\\wet-mars-v6-remodeled-satimg";
		
		
		int multiple = 5;
		int frameNum = 1000030 + multiple;
		String framePath = "F:\\animations\\single-frames\\frame-" + frameNum + ".jpg";
		double angle = (1000.0 * step);
		double rotation = startAngle + angle;
		long frameTime = startTime + (long)MathExt.round((86400000 * (angle / (stop - start))));
		doFrame(projectPath, framePath, rotation, frameTime, multiple);
		/*
		for (double angle = start; angle < stop; angle+=step) {
			String framePath = "F:\\animations\\frames-hd\\frame-" + frameNum + ".jpg";
			double rotation = startAngle + angle;
			long frameTime = startTime + (long)MathExt.round((86400000 * (angle / (stop - start))));
			
			if (frameNum >= 1000682 && frameNum < 1000901) {
				doFrame(projectPath, framePath, rotation, frameTime);
			}
			frameNum++;
		}
		*/
	}
	
	
	public void doFrame(String projectPath, String savePath, double angle, long time, double elevationMultiple)
	{
		System.err.println("Starting render for rotation angle " + angle);
		
		ProjectRunPlan runPlan = new ProjectRunPlan(projectPath
													, savePath);
		
		String viewAngle = "rotate:[-17.0," + angle + ",0.0];shift:[0.0,0.0,0.0];zoom:[1.0]";
		String sunlightTime = "time:[" + time + "]";
		
		//runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.useScripting", "false");
		runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.modelQuality", ""+0.85);
		runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.width", ""+4000);
		runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.height", ""+4000);
		runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.elevationMultiple", ""+elevationMultiple);
		runPlan.addOptionOverride("us.wthr.jdem846.model.RenderLightingOptionModel.sunlightTime", sunlightTime);
		runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.viewAngle", viewAngle);
		runPlan.addOptionOverride("s.wthr.jdem846.model.GlobalOptionModel.saveModelGrid", "false");
		ProjectExecutor exec = new ProjectExecutor();
		try {
			exec.executeProject(runPlan);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	
	class RenderThread extends Thread
	{
		private String projectPath;
		private double start = 0;
		private double stop = 0;
		private double step = 0;
		private int frameIndexStart;
		
		public RenderThread(String projectPath, double start, double stop, double step, int frameIndexStart)
		{
			this.projectPath = projectPath;
			this.start = start;
			this.stop = stop;
			this.step = step;
			this.frameIndexStart = frameIndexStart;
		}
		
		public void run()
		{
			
			System.err.println("Starting thread from " + start + " to " + stop);
			int frameNum = 1000000 + frameIndexStart;
			for (double e = start; e <= stop; e+=step) {
				//System.setProperty("seaLevel.elevation", "" + e);
				
				System.err.println("Starting render for elevation " + e);
				
				ProjectRunPlan runPlan = new ProjectRunPlan(projectPath
															, "E:\\frames\\frame-" + frameNum + ".jpg");
				
				String viewAngle = "rotate:[-17.0," + e + ",0.0];shift:[0.0,0.0,0.0];zoom:[1.0]";
				runPlan.addOptionOverride("us.wthr.jdem846.model.GlobalOptionModel.viewAngle", ""+e);
				ProjectExecutor exec = new ProjectExecutor();
				try {
					exec.executeProject(runPlan);
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				frameNum++;
			}
		}
		
	}
	
	
	public SandboxTestMain() 
	{

	}
	
	
	public static boolean test(double latitude, double longitude, double radius, Plane plane,  double innerRingRadius, double outterRingRadius)
	{
		Vector point = new Vector();
		Spheres.getPoint3D(longitude, latitude, radius, point);
		
		//Vector point = new Vector(118258.6343130089, -42615747.405093856, -42615747.403190926);
		Vector sun = new Vector(-54289586911.33092, -18629619855.001747, -135611588101.05907);
		
		
		
		Vector direction = point.getDirectionTo(sun);
		double intersectDistance = point.intersectDistance(plane, direction) * radius;

		//System.err.println("Intersect Distance: " + intersectDistance);
		
		Vector intersect = point.intersectPoint(direction, intersectDistance);
		
		if (intersect != null && intersectDistance >= 0) {

			//System.err.println("Intersect Point: " + intersect.x + "/" + intersect.y + "/" + intersect.z);
			
			double intersectRadius = intersect.getLength();
			System.err.println("Intersect Radius: " + intersectRadius + ", Intersect Distance: " + intersectDistance);
			if (intersectRadius >= innerRingRadius && intersectRadius <= outterRingRadius) {
				return true;
			}
			
			
		
		} 
		
		
		return false;
	}
	
	public static Vector findPlane(Vector pt0, Vector pt1, Vector pt2)
	{
		
		Vector vec0 = new Vector();
		Vector vec1 = new Vector();
		Vector plane = new Vector();
		
		vec0.x = pt1.x - pt0.x;
	    vec0.y = pt1.y - pt0.y;
	    vec0.z = pt1.z - pt0.z;
	    
	    vec1.x = pt2.x - pt0.x;
	    vec1.y = pt2.y - pt0.y;
	    vec1.z = pt2.z - pt0.z;
	    
	    plane.x = vec0.y * vec1.z - vec0.z * vec1.y;
	    plane.y = -(vec0.x * vec1.z - vec0.z * vec1.x);
	    plane.z = vec0.x * vec1.y - vec0.y * vec1.x;
	    plane.w = -(plane.x * pt0.x + plane.y * pt0.y + plane.z * pt0.z);
	    
		return plane;
		
	}
	
	
	 public static double intersectDistance(Vector plane, Vector origin, Vector direction)
	 {
		 double ldotv = plane.dotProduct(direction);
		 if (ldotv == 0) {
			 return 0;
		 }
		 return -plane.dotProduct4(origin) / ldotv;
	 }
	
	
	

}
