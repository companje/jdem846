/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.shapefile;

/*
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
*/
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Geometric;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.modeling.FeatureType;

@SuppressWarnings("serial")
public class ShapePath extends Geometric //java.awt.Shape // Path2D.Double
{
	private static Log log = Logging.getLog(ShapePath.class);
	
	private List<ShapePath> subParts = new LinkedList<ShapePath>();
	private FeatureType featureType;
	//private Path2D.Double path = new Path2D.Double();
	
	public ShapePath()
	{
		setFeatureType(null);
	}
	
	
	
	
	public ShapePath(FeatureType featureType)
	{
		this.setFeatureType(featureType);
	}
	
	public void setFeatureType(FeatureType featureType)
	{
		this.featureType = featureType;
	}
	
	public FeatureType getFeatureType()
	{
		return featureType;
	}
	
	/*
	public boolean intersectsPoint(double x, double y, double w, double h)
	{
		if (!path.intersects(x, y, w, h))
			return false;
		
		for (ShapePath subPath : subParts) {
			if (subPath.intersects(x, y, w, h)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean containsPoint(double x, double y)
	{
		if (!path.contains(x, y))
			return false;
		
		for (ShapePath subPath : subParts) {
			if (subPath.contains(x, y)) {
				return false;
			}
		}
		return true;
	}
	*/

	
	public List<ShapePath> getSubParts()
	{
		return subParts;
	}
	
	public void addSubPart(ShapePath subPart)
	{
		subParts.add(subPart);
	}
	
	public boolean removeSubPart(ShapePath subPart)
	{
		return subParts.remove(subPart);
	}
	
	
	public ShapePath translate(PointTranslateHandler translateHandler, boolean closePath)
	{
		ShapePath newPath = new ShapePath();
		
		//PathIterator iterator = getPathIterator(null);
		Edge[] edges = getEdges();
		
		double[] coords = new double[3];

		for (Edge edge : edges) {
			
			coords[0] = edge.p0.x;
			coords[1] = edge.p0.y;
			coords[2] = edge.p0.z;
			
			translateHandler.translatePoint(coords);
			
			double x0 = coords[0];
			double y0 = coords[1];
			double z0 = coords[2];
			
			coords[0] = edge.p1.x;
			coords[1] = edge.p1.y;
			coords[2] = edge.p1.z;
			
			translateHandler.translatePoint(coords);
			
			double x1 = coords[0];
			double y1 = coords[1];
			double z1 = coords[2];
			
			Edge newEdge = new Edge(x0, y0, z0, x1, y1, z1);
			newPath.addEdge(newEdge);

			
		}
		
		//if (closePath)
		//	newPath.closePath();
	
		for (ShapePath subPath : subParts) {
			ShapePath newSubPath = subPath.translate(translateHandler, closePath);
			newPath.addSubPart(newSubPath);
		}
		
		newPath.setFeatureType(this.getFeatureType());
		
		return newPath;
	}
	
	public ShapePath getCopy()
	{
		ShapePath path = new ShapePath();
		Edge[] edges = getEdges();

		for (Edge edge : edges) {
			Edge newEdge = new Edge(edge);
			path.addEdge(newEdge);
		}
		
		path.featureType = this.featureType;
		for (ShapePath sub : this.subParts) {
			path.addSubPart(sub.getCopy());
		}
		
		return path;
	}




	@Override
	public double getInterpolatedZ(double x, double y)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*
	public void append(ShapePath shapePath, boolean connect)
	{
		path.append(shapePath, connect);
	}
	
	public void moveTo(double x, double y)
	{
		path.moveTo(x, y);
		
	}

	public void lineTo(double x, double y)
	{
		path.lineTo(x, y);
	}

	public Polygon toPolygon()
	{
		return toPolygon(null);
	}
	
	public Polygon toPolygon(PointTranslateHandler translateHandler)
	{
		Polygon polygon = new Polygon();
		
		PathIterator iterator = getPathIterator(null);
		double[] coords = new double[2];

		while(!iterator.isDone()) {
	
			iterator.currentSegment(coords);
			
			if (translateHandler != null)
				translateHandler.translatePoint(coords);
			
			double x = coords[0];
			double y = coords[1];
			
			polygon.addPoint((int)Math.round(x), (int)Math.round(y));
			
			iterator.next();
		}
		
		return polygon;
	}
	
	
	
	public void closePath()
	{
		this.path.closePath();
	}
	
	@Override
	public boolean contains(Point2D p)
	{
		return path.contains(p);
	}

	@Override
	public boolean contains(Rectangle2D r)
	{
		return path.contains(r);
	}

	@Override
	public boolean contains(double x, double y)
	{
		//return path.contains(x, y);
		return this.containsPoint(x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h)
	{
		return path.contains(x, y, w, h);
	}

	@Override
	public Rectangle getBounds()
	{
		return path.getBounds();
	}

	@Override
	public Rectangle2D getBounds2D()
	{
		return path.getBounds2D();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at)
	{
		return path.getPathIterator(at);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness)
	{
		return path.getPathIterator(at, flatness);
	}

	@Override
	public boolean intersects(Rectangle2D r)
	{
		return intersectsPoint(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h)
	{
		return intersectsPoint(x, y, w, h);
	}
	*/
}
