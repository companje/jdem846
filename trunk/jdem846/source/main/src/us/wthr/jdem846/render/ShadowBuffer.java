package us.wthr.jdem846.render;

import java.awt.geom.Rectangle2D;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.RayTracingException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.Equirectangular3dProjection;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;

public class ShadowBuffer  {
	
	private static final double Z_VALUE_NOT_SET = Double.NaN;
	private MatrixBuffer<Double> zBuffer;
	private MapPoint point;
	private Quadrangle3d quad = new Quadrangle3d();
	
	private Equirectangular3dProjection projection;
	
	int width = 0;
	int height = 0;
	
	public ShadowBuffer(ModelContext modelContext)
	{
		/*this(modelContext.getNorth(), 
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelContext.getModelDimensions().getOutputWidth(),
				modelContext.getModelDimensions().getOutputHeight(),
				modelContext.getLightingContext().getLightingElevation(),
				modelContext.getLightingContext().getLightingAzimuth());
				*/
		//this.setUp(modelContext);
		
		//this.setRotateX(modelContext.getLightingContext().getLightingElevation());
		//this.setRotateY(modelContext.getLightingContext().getLightingAzimuth()+180);
		//this.setRotateX(5.0);
		//this.setRotateY(0);
		projection = new Equirectangular3dProjection();
		projection.setUp(modelContext);
		projection.setRotateX(modelContext.getLightingContext().getLightingElevation());
		projection.setRotateY(modelContext.getLightingContext().getLightingAzimuth()+180);
		
		width = modelContext.getModelDimensions().getOutputWidth();
		height = modelContext.getModelDimensions().getOutputHeight();
		
		point = new MapPoint();
		
		zBuffer = new MatrixBuffer<Double>((int)Math.ceil(width), (int)Math.ceil(height));
		
		zBuffer.fill(ShadowBuffer.Z_VALUE_NOT_SET);
	}
	
	public void setRectangle(double lat0, double lon0, double elev0,
			double lat1, double lon1, double elev1,
			double lat2, double lon2, double elev2,
			double lat3, double lon3, double elev3) throws RayTracingException
	{
		//pathBuffer.reset();
		

		double row0, row1, row2, row3;
		double column0, column1, column2, column3;
		double z0, z1, z2, z3;
		//double z = 0;
		try {
			projection.getPoint(lat0, lon0, elev0, point);
			row0 = point.row;
			column0 = point.column;
			z0 = point.z;
			//z += mapPoint.z;
			
			
			projection.getPoint(lat1, lon1, elev1, point);
			row1 = point.row;
			column1 = point.column;
			z1 = point.z;
			//z += mapPoint.z;

			projection.getPoint(lat2, lon2, elev2, point);
			row2 = point.row;
			column2 = point.column;
			z2 = point.z;
			//z += mapPoint.z;

			
			projection.getPoint(lat3, lon3, elev3, point);
			row3 = point.row;
			column3 = point.column;
			z3 = point.z;
			//z += mapPoint.z;
			
			//double midRow = (row0 + row1 + row2 + row3) / 4.0;
			//double midCol = (column0 + column1 + column2 + column3) / 4.0;
			
			//if (mapPoint.z != 0) {
			//	if (!zBuffer.isVisible((int)Math.round(midCol), (int)Math.round(midRow), (z/4.0))) {
			//		return;
			//	}
			//}
			
		} catch (MapProjectionException ex) {
			throw new RayTracingException("Failed to project coordates to shadow buffer: " + ex.getMessage(), ex);
		}
		
		//if (row1 < row0 || row2 < row3)
		//	return;
		
		//pathBuffer.moveTo(column0, row0);
		//pathBuffer.lineTo(column1, row1);
		//pathBuffer.lineTo(column2, row2);
		//pathBuffer.lineTo(column3, row3);
		//pathBuffer.closePath();
		
		quad.set(0, column0, row0, z0);
		quad.set(1, column1, row1, z1);
		quad.set(2, column2, row2, z2);
		quad.set(3, column3, row3, z3);
		
		//int alpha = 255;
		////if (color.length >= 4) {
		//	alpha = color[3];
		//}
		
		//fillShape(quad, color);
		//Color fillColor = new Color(color[0], color[1], color[2], alpha);
		//fillShape(fillColor, null, pathBuffer);
		
		/*
		
		*/
		set(quad);
		
	}
	
	protected void set(Quadrangle3d quad) throws RayTracingException
	{
		Rectangle2D bounds = quad.getBounds2D();
		
		double minX = bounds.getMinX();
		double maxX = bounds.getMaxX();
		double minY = bounds.getMinY();
		double maxY = bounds.getMaxY();
		
		for (double y = minY; y <= maxY; y++) {
			for (double x = minX; x <= maxX; x++) {
				//if (quad.contains(x, y)) {
				if (quad.intersects(x, y, 1, 1)) {
					int _x = (int) Math.round(x);
					int _y = (int) Math.round(y);
					
					double xFrac = (x - minX) / (maxX - minX);
					double yFrac = (y - minY) / (maxY - minY);
					double z = quad.interpolateZ(xFrac, yFrac);
					
					set(_x, _y, z);
				}
			}
		}
	}
	
	protected void set(int x, int y, double z) throws RayTracingException
	{
		if (x < 0 || x >= zBuffer.getWidth() || y < 0 || y >= zBuffer.getHeight()) {
			return;
		}
		
		
		double existing = zBuffer.get(x, y);
		if (Double.isNaN(existing) || existing < z) {
			zBuffer.set(x, y, z);
		}
	}
	
	/*
	public void setPoint(double latitude, double longitude, double elevation) throws RayTracingException
	{
		
		try {
			getPoint(latitude, longitude, elevation, point);
		} catch (MapProjectionException ex) {
			throw new RayTracingException("Error projection point to shadow buffer space: " + ex.getMessage(), ex);
		}
		
		int x = (int) Math.round(point.column);
		int y = (int) Math.round(point.row);
		double z = point.z;
		
		
		if (x < 0 || x >= zBuffer.getWidth() || y < 0 || y >= zBuffer.getHeight()) {
			return;
		}
		
		
		double existing = zBuffer.get(x, y);
		if (Double.isNaN(existing) || existing < z) {
			zBuffer.set(x, y, z);
		}
		
		
	}
	*/
	
	/*
	public boolean isShaded(double latitude, double longitude, double elevation) throws RayTracingException
	{
		try {
			projection.getPoint(latitude, longitude, elevation, point);
		} catch (MapProjectionException ex) {
			throw new RayTracingException("Error projection point to shadow buffer space: " + ex.getMessage(), ex);
		}
		
		int x = (int) Math.round(point.column);
		int y = (int) Math.round(point.row);
		double z = point.z;
		
		//if (x < 0 || x >= zBuffer.getWidth() || y < 0 || y >= zBuffer.getHeight()) {
		//	return false;
	//	}
		
		
		boolean bl = isShaded(x, y, z);
		return bl;
		//boolean fl = isShaded(x, y+1, z);
		//boolean fr = isShaded(x+1, y+1, z);
		//boolean br = isShaded(x+1, y, z);
		
		//return (bl && fl && fr && br);
		
		
	}
	*/
	
	public double isShaded(double lat0, double lon0, double elev0,
			double lat1, double lon1, double elev1,
			double lat2, double lon2, double elev2,
			double lat3, double lon3, double elev3) throws RayTracingException
	{
		//pathBuffer.reset();
		

		double row0, row1, row2, row3;
		double column0, column1, column2, column3;
		double z0, z1, z2, z3;
		//double z = 0;
		try {
			projection.getPoint(lat0, lon0, elev0, point);
			row0 = point.row;
			column0 = point.column;
			z0 = point.z;
			//z += mapPoint.z;
			
			
			projection.getPoint(lat1, lon1, elev1, point);
			row1 = point.row;
			column1 = point.column;
			z1 = point.z;
			//z += mapPoint.z;

			projection.getPoint(lat2, lon2, elev2, point);
			row2 = point.row;
			column2 = point.column;
			z2 = point.z;
			//z += mapPoint.z;

			
			projection.getPoint(lat3, lon3, elev3, point);
			row3 = point.row;
			column3 = point.column;
			z3 = point.z;
			//z += mapPoint.z;
			
			//double midRow = (row0 + row1 + row2 + row3) / 4.0;
			//double midCol = (column0 + column1 + column2 + column3) / 4.0;
			
			//if (mapPoint.z != 0) {
			//	if (!zBuffer.isVisible((int)Math.round(midCol), (int)Math.round(midRow), (z/4.0))) {
			//		return;
			//	}
			//}
			
		} catch (MapProjectionException ex) {
			throw new RayTracingException("Failed to project coordates to shadow buffer: " + ex.getMessage(), ex);
		}
		
		//if (row1 < row0 || row2 < row3)
		//	return;
		
		//pathBuffer.moveTo(column0, row0);
		//pathBuffer.lineTo(column1, row1);
		//pathBuffer.lineTo(column2, row2);
		//pathBuffer.lineTo(column3, row3);
		//pathBuffer.closePath();
		
		quad.set(0, column0, row0, z0);
		quad.set(1, column1, row1, z1);
		quad.set(2, column2, row2, z2);
		quad.set(3, column3, row3, z3);
		
		double shadeCount = 0;
		double cellCount = 0;
		
		
		Rectangle2D bounds = quad.getBounds2D();
		
		double minX = bounds.getMinX();
		double maxX = bounds.getMaxX();
		double minY = bounds.getMinY();
		double maxY = bounds.getMaxY();
		
		for (double y = minY; y <= maxY; y++) {
			for (double x = minX; x <= maxX; x++) {
				if (quad.intersects(x, y, 1, 1)) {
					int _x = (int) Math.round(x);
					int _y = (int) Math.round(y);
					
					double xFrac = (x - minX) / (maxX - minX);
					double yFrac = (y - minY) / (maxY - minY);
					double z = quad.interpolateZ(xFrac, yFrac);
					
					cellCount+=1.0;
					if (isShaded(_x, _y, z)) {
						shadeCount+=1.0;
					}
				}
			}
		}
		
		if (cellCount == 0 || shadeCount == 0) {
			return 0;
		} else {
			return shadeCount / cellCount;
		}
		
		//if (shadeCount / cellCount >= 0.5) {
		///	return true;
		//} else {
		//	return false;
		//}
		
		//int alpha = 255;
		////if (color.length >= 4) {
		//	alpha = color[3];
		//}
		
		//fillShape(quad, color);
		//Color fillColor = new Color(color[0], color[1], color[2], alpha);
		//fillShape(fillColor, null, pathBuffer);
		
		/*
		
		*/
		//set(quad);
		
	}
	
	protected boolean isShaded(int x, int y, double z) throws RayTracingException
	{
		if (x < 0 || x >= zBuffer.getWidth() || y < 0 || y >= zBuffer.getHeight()) {
			return false;
		}
		
		double existing = zBuffer.get(x, y);
		existing = Math.round(existing);
		if (!Double.isNaN(existing) && z < existing) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public void dispose()
	{
		zBuffer.dispose();
		zBuffer = null;
		
	}
	
	
}
