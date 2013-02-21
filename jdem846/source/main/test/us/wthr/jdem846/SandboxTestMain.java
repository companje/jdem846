package us.wthr.jdem846;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.math.Plane;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

public class SandboxTestMain extends AbstractTestMain
{
	private static Log log = null;
	

	
	public static void main(String[] args)
	{
		try {
			AbstractTestMain.initialize(false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			
			int startX = 32;
			int endX = startX + 32;;
			
			int numWide = endX - startX;
			int numHigh = 32;
			
			int dimension = 512;
			
			int totalWidth = dimension * numWide;
			int totalHeight = dimension * numHigh;
			
			System.out.println("Image width/height: " + totalWidth + "/" + totalHeight);
			
			
			BufferedImage composite = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = (Graphics2D) composite.createGraphics();
			
			for (int x = startX; x < endX; x++) {
				for (int y = 0; y < numHigh; y++) {
					
					String filePath = "C:\\Users\\kgill\\Desktop\\Mars\\tx_" + x + "_" + y + ".png";
					System.out.println("Loading " + filePath);
					BufferedImage tile = ImageIO.read(new File(filePath));
					
					int tileX = (x - startX) * dimension;
					int tileY = y * dimension;
					
					g2d.drawImage(tile, tileX, tileY, null);
					
				}
			}
		
			g2d.dispose();
			
			File writeComposite = new File("C:\\Users\\kgill\\Desktop\\Mars\\tx_composite_B.jpg");
			ImageWriter.saveImage(composite, writeComposite.getAbsolutePath());
			//ImageIO.write(composite, "JPG", writeComposite);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*double radius = 60268000;
		
		Vector p0 = new Vector();
		Vector p1 = new Vector();
		Vector p2 = new Vector();
		
		Spheres.getPoint3D(0.0, 0.0, radius, p0);
		Spheres.getPoint3D(90, 0.0, radius, p1);
		Spheres.getPoint3D(-90, 0.0, radius, p2);
		
		
		Plane plane = new Plane(p0
								, p1
								, p2);
		
		
		double innerRingRadius = (6630000 + radius);
		double outterRingRadius = (120700000 + radius);

		boolean anyPointsInShadow = false;
		for (double lat = 89; lat > -89.0; lat--) {
			//for (double lon = -180; lon < 180; lon++) {
			double lon = 0.0;
				boolean inShadow = test(lat, lon, radius, plane, innerRingRadius, outterRingRadius);
				if (inShadow) {
					anyPointsInShadow = true;
				}
				System.err.println("Lat/Lon " + lat + "/" + lon + " in shadow: " + inShadow);
			//}
		}
		
		System.err.println("Points were in the shadow: " + anyPointsInShadow);*/
		 //X/Y/Z: 0.018730007437911395/-136.11805408976977/-136.11805408977233
		 //2013.02.06 12:33:58.977 EST     INFO JavaScriptProxy Sun Z/Y/Z: -54289586911.33092/-18629619855.001747/-135611588101.05907
		
/*		for (int i = 0; i < 256; i++) {
			Color c0 = new Color(i, i, i, i);
			String hex = c0.toString();
			
			try {
				Color c1 = new Color(hex);
			
				System.err.println("Color: " + c0.toString() + ", " + c1.toString());
			} catch (Exception ex) {
				System.err.println("Hex: " + hex);
				ex.printStackTrace();
			}
			
			
		}*/
		
		
		
		//MessageDialog md = new MessageDialog("Hello", JDem846Properties.getProperty("us.wthr.jdem846.ui.notifications.error"), "This is a message", null);
		//md.setVisible(true);
//		
//		List<String> interpolations = new LinkedList<String>();
//		String template = "dfhdfusd ${foo.bar} fdfdush ${bar.foo[0]} fdfdf ${mega.deth?metal}";
//		
//		Pattern pattern = Pattern.compile("\\$\\{[a-zA-Z0-9.\\[\\]\\?]+\\}");
//		Matcher matcher = pattern.matcher(template);
//		while (matcher.find()) {
//			String var = template.substring(matcher.start(), matcher.end());
//			String varStripped = var.substring(2, var.length() - 1);
//			interpolations.add(varStripped);
//		}
//		
//		
//		for (String interpolation : interpolations) {
//			System.err.println("Found '" + interpolation + "'");
//		}
		
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
