package us.wthr.jdem846.geo;

import static java.lang.Math.PI;

public class Units
{
	
	//http://publib.boulder.ibm.com/infocenter/db2luw/v8/index.jsp?topic=/com.ibm.db2.udb.doc/opt/rsbp4119.htm
	
	/*
	 * Linear Units
	 */
	public static final Unit METER = new Unit("Meter", 1.0);
	public static final Unit FOOT_INTERNATIONAL = new Unit("Foot (International)", 0.3048);
	public static final Unit FOOT_US = new Unit("U.S. Foot", 12/39.37);
	public static final Unit FOOT_MODIFIED_AMERICAN = new Unit("Modified American Foot", 12.0004584/39.37);
	public static final Unit FOOT_CLARKS = new Unit("Clarke's Foot", 12/39.370432);
	public static final Unit FOOT_INDIAN = new Unit("Indian Foot", 12/39.370141);
	public static final Unit FOOT_LINK = new Unit("Link", 7.92/39.370432);
	public static final Unit FATHOM = new Unit("Fathom", 1.8288);
	public static final Unit NAUTICAL_MILE = new Unit("Nautical Mile", 1852.0);
	
	/*
	 * Angular Units
	 */
	public static final Unit RADIAN = new Unit("Radian", -PI/2, PI/2, -PI, PI, 1.0);
	public static final Unit DECIMAL_DEGREE = new Unit("Decimal Degree", -90, 90, -180, 180, PI/180);
	public static final Unit DECIMAL_MINUTE = new Unit("Decimal Minute", -5400, 5400, -10800, 10800, (PI/180)/60);
	public static final Unit DECIMAL_SECOND = new Unit("Decimal Second", -324000, 324000, -648000, 648000, (PI/180)*3600);
	public static final Unit GON = new Unit("Gon", -100, 100, -200, 200, PI/200);
	public static final Unit GRAD = new Unit("Grad", -100, 100, -200, 200, PI/200);
	

	
	public static Unit getUnitByName(String name)
	{
		Unit unit = null;
		
		
		if (METER.getName().equalsIgnoreCase(name)) {
			unit = METER;
		} else if (FOOT_INTERNATIONAL.getName().equalsIgnoreCase(name)) {
			unit = FOOT_INTERNATIONAL;
		} else if (FOOT_US.getName().equalsIgnoreCase(name)) {
			unit = FOOT_US;
		} else if (FOOT_MODIFIED_AMERICAN.getName().equalsIgnoreCase(name)) {
			unit = FOOT_MODIFIED_AMERICAN;
		} else if (FOOT_CLARKS.getName().equalsIgnoreCase(name)) {
			unit = FOOT_CLARKS;
		} else if (FOOT_INDIAN.getName().equalsIgnoreCase(name)) {
			unit = FOOT_INDIAN;
		} else if (FOOT_LINK.getName().equalsIgnoreCase(name)) {
			unit = FOOT_LINK;
		} else if (FATHOM.getName().equalsIgnoreCase(name)) {
			unit = FATHOM;
		} else if (NAUTICAL_MILE.getName().equalsIgnoreCase(name)) {
			unit = NAUTICAL_MILE;
		} else if (RADIAN.getName().equalsIgnoreCase(name)) {
			unit = RADIAN;
		} else if (DECIMAL_DEGREE.getName().equalsIgnoreCase(name)) {
			unit = DECIMAL_DEGREE;
		} else if (DECIMAL_MINUTE.getName().equalsIgnoreCase(name)) {
			unit = DECIMAL_MINUTE;
		} else if (DECIMAL_SECOND.getName().equalsIgnoreCase(name)) {
			unit = DECIMAL_SECOND;
		} else if (GON.getName().equalsIgnoreCase(name)) {
			unit = GON;
		} else if (GRAD.getName().equalsIgnoreCase(name)) {
			unit = GRAD;
		}
		
		return unit;
	}
	
	
	private Units()
	{
		
	}
	
	
	
	
}
