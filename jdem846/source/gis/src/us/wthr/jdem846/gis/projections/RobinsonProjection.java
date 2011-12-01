package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Implements the Robinsopn map projection
 * 
 * @author Kevin M. Gill
 * @see http://en.wikipedia.org/wiki/Robinson_projection
 */
public class RobinsonProjection extends AbstractBaseProjection
{
	private static Log log = Logging.getLog(RobinsonProjection.class);
	
	private static final double[][] projectionTable = {
// 			Latitude		PLEN		PDFE
			{0.0,				1.0000,		0.0000},
			{5.0,				0.9986,		0.0620},
			{10.0,			0.9954,		0.1240},
			{15.0,			0.9900,		0.1860},
			{20.0,			0.9822,		0.2480},
			{25.0,			0.9730,		0.3100},
			{30.0,			0.9600,		0.3720},
			{35.0,			0.9427,		0.4340},
			{40.0,			0.9216,		0.4958},
			{45.0,			0.8962,		0.5571},
			{50.0,			0.8679,		0.6176},
			{55.0,			0.8350,		0.6769},
			{60.0,			0.7986,		0.7346},
			{65.0,			0.7597,		0.7903},
			{70.0,			0.7186,		0.8435},
			{75.0,			0.6732,		0.8936},
			{80.0,			0.6213,		0.9394},
			{85.0,			0.5722,		0.9761},
			{90.0,			0.5322,		1.0000}
	};
	

	
	public RobinsonProjection()
	{
		
	}
	
	public RobinsonProjection(double north, double south, double east, double west, double width, double height)
	{
		super(north, south, east, west, width, height);
	}
	

	
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		double latitudeOrig = latitude;
		latitude = Math.abs(latitude);
		
		double width = getWidth();
		double height = getHeight();
		
		int lowerIndex = getLowerIndex(latitude);
		int higherIndex = getHigherIndex(latitude);
		
		double[] lower = projectionTable[lowerIndex];
		double[] higher = projectionTable[higherIndex];
		
		double frac = getYFrac(latitude, lowerIndex, higherIndex);
		
		// PLEN -> The length of the parallel of latitude
		double plen = interpolate(lower[1], higher[1], frac);
		
		// PDFE -> Multiplied by 0.5072 to obtain the distance of that parallel from the equator
		double pdfe = interpolate(lower[2], higher[2], frac);
		
		double latLen = plen * width;
		point.column = ((width - latLen) / 2.0) + (longitudeToColumn(longitude) * (latLen / width));
		
		if (latitudeOrig >= 0) {
			point.row = (height / 2.0) - (height * (pdfe * 0.5 /*0.5072*/));
		} else {
			point.row = (height / 2.0) + (height * (pdfe * 0.5 /*0.5072*/)) - 1.0;
		}

	}
	
	protected double interpolate(double s00, double s01, double frac)
	{
		double s0 = (s01 - s00)*frac + s00;
		return s0;
	}
	
	protected double getYFrac(double latitude, int lowerIndex, int higherIndex)
	{
		double[] lower = projectionTable[lowerIndex];
		double[] higher = projectionTable[higherIndex];

		double range = higher[0] - lower[0];
		double yFrac = 0;
		if (range > 0) {
			yFrac = 1.0 - ((higher[0] - latitude) / range);
		}
		return yFrac;
	}
	
	protected int getLowerIndex(double latitude)
	{
		//double latFloor = Math.floor(latitude / 10);
		
		for (int i = 0; i < projectionTable.length; i++) {
			if (projectionTable[i][0] > latitude) {
				return i - 1;
			}
		}
		return (projectionTable.length - 1); 
	}
	
	protected int getHigherIndex(double latitude)
	{
		//double latCeil = Math.ceil(latitude / 10);
		
		for (int i = projectionTable.length - 1; i >= 0; i--) {
			if (projectionTable[i][0] < latitude) {
				return i + 1;
			}
		}
		return (projectionTable.length - 1); 
	}
	
}
