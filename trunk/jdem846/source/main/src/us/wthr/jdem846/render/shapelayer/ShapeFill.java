package us.wthr.jdem846.render.shapelayer;

import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.render.Edge;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.shapefile.ShapeConstants;
import us.wthr.jdem846.shapefile.ShapePath;
import us.wthr.jdem846.shapefile.modeling.LineStroke;

public class ShapeFill
{
	
	private int[] color;
	private int shapeType;
	private boolean fill = true;
	private LineStroke lineStroke;
	private ShapePath path;
	
	
	public ShapeFill(int[] color, int shapeType, ShapePath path, LineStroke lineStroke, boolean fill)
	{
		this.color = new int[4];
		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
		this.color[3] = color[3];
		
		this.shapeType = shapeType;
		this.path = path;
		this.lineStroke = lineStroke;
		this.fill = fill;
		
	}
	
	
	public void fill(ModelCanvas modelCanvas) throws CanvasException, RenderEngineException
	{
		// TODO: Restore this!
		if (shapeType == ShapeConstants.TYPE_POLYGON ||
			shapeType == ShapeConstants.TYPE_POLYGONM ||
			shapeType == ShapeConstants.TYPE_POLYGONZ) {
			lineStroke.getColor(color);
			color[3] = 255;
			modelCanvas.fillShape(path, color);
		} else if (shapeType == ShapeConstants.TYPE_POLYLINE ||
					shapeType == ShapeConstants.TYPE_POLYLINEM ||
					shapeType == ShapeConstants.TYPE_POLYLINEZ) {
		} else {
			throw new RenderEngineException("Unsupported shape type: " + shapeType);
		}
	}
	
	
	
}
