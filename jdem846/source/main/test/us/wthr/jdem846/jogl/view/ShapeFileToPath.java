package us.wthr.jdem846.jogl.view;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.shapefile.Shape;
import us.wthr.jdem846.shapefile.ShapeFile;
import us.wthr.jdem846.shapefile.ShapePath;

public class ShapeFileToPath
{
	
	
	public static List<Path2D.Double> load(String shapeFilePath) throws Exception
	{
		ShapeFile shapeFile = new ShapeFile(shapeFilePath);
		
		List<Path2D.Double> paths = new ArrayList<Path2D.Double>();
		
		for (int i = 0; i < shapeFile.getShapeCount(); i++) {
			Shape shape = shapeFile.getShape(i);
			Path2D.Double path = new Path2D.Double();
			
			ShapePath shapePath = shape.getShapePath();
			shapePathToPaths(shapePath, paths);
			
			paths.add(path);
		}
		
		return paths;
	}
	
	
	protected static void shapePathToPaths(ShapePath shapePath, List<Path2D.Double> paths)
	{

		Path2D.Double path = new Path2D.Double();
		
		Edge[] edges = shapePath.getEdges();
		int c = 0;
		for (Edge edge : edges) {
			if (c == 0) {
				path.moveTo(edge.p0.x(), edge.p0.y());
			} else {
				path.lineTo(edge.p0.x(), edge.p0.y());
			}
			if (c == edges.length - 1) {
				path.lineTo(edge.p1.x(), edge.p1.y());
			}
			c++;
		}

		paths.add(path);
		
		for (ShapePath subPath : shapePath.getSubParts()) {
			shapePathToPaths(subPath, paths);
		}
	}
	
}
