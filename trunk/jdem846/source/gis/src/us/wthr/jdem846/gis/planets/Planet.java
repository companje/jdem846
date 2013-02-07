package us.wthr.jdem846.gis.planets;

import us.wthr.jdem846.math.Ellipsoid;

public class Planet
{
	private String name;
	private String elevationSamplesPath;
	
	// Orbital characteristics
	private double aphelion; // km
	private double perihelion; // km
	private double semiMajorAxis; //km
	private double eccentricity;
	private double orbitalPeriod; // days
	private double synodicPeriod; // days
	private double averageOrbitalSpeed; // km/s
	private double meanAnomaly; // degrees
	private double inclinationToEcliptic; // degrees
	private double inclinationToSunsEquator; // degrees
	private double inclinationToInvariablePlane; // degrees
	private double longitudeOfAscendingNode; // degrees
	private double argumentOfPerihelion; // degrees;
	
	// Physical characteristics
	private double meanRadius; // km
	private double equatorialRadius; //km
	private double polarRadius; // km
	private double flattening;
	private double circumferenceEquatorial; //km
	private double circumferenceMeridional; // km
	private double surfaceArea; // km^2
	private double volume; // km^3
	private double mass; // kg
	private double meanDensity; // g/cm^3
	private double equatorialSurfaceGravity; // m/s^2
	private double escapeVelocity; // km/s
	private double siderealRotationPeriod; // d
	private double equatorialRotationVelocity; // km/h
	private double axialTilt; //degrees
	private double northPoleRightAscension; // degrees
	private double northPoleDeclination; // degrees
	private double albedoGeometric; 
	private double albedoBond;
	
	private Ellipsoid ellipse;
	
	public Planet()
	{
		
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getElevationSamplesPath()
	{
		return elevationSamplesPath;
	}

	public void setElevationSamplesPath(String elevationSamplesPath)
	{
		this.elevationSamplesPath = elevationSamplesPath;
	}

	public double getAphelion()
	{
		return aphelion;
	}

	public void setAphelion(double aphelion)
	{
		this.aphelion = aphelion;
	}

	public double getPerihelion()
	{
		return perihelion;
	}

	public void setPerihelion(double perihelion)
	{
		this.perihelion = perihelion;
	}

	public double getSemiMajorAxis()
	{
		return semiMajorAxis;
	}

	public void setSemiMajorAxis(double semiMajorAxis)
	{
		this.semiMajorAxis = semiMajorAxis;
	}

	public double getEccentricity()
	{
		return eccentricity;
	}

	public void setEccentricity(double eccentricity)
	{
		this.eccentricity = eccentricity;
	}

	public double getOrbitalPeriod()
	{
		return orbitalPeriod;
	}

	public void setOrbitalPeriod(double orbitalPeriod)
	{
		this.orbitalPeriod = orbitalPeriod;
	}

	
	public double getSynodicPeriod()
	{
		return synodicPeriod;
	}

	public void setSynodicPeriod(double synodicPeriod)
	{
		this.synodicPeriod = synodicPeriod;
	}

	public double getAverageOrbitalSpeed()
	{
		return averageOrbitalSpeed;
	}

	public void setAverageOrbitalSpeed(double averageOrbitalSpeed)
	{
		this.averageOrbitalSpeed = averageOrbitalSpeed;
	}

	public double getMeanAnomaly()
	{
		return meanAnomaly;
	}

	public void setMeanAnomaly(double meanAnomaly)
	{
		this.meanAnomaly = meanAnomaly;
	}

	
	public double getInclinationToEcliptic()
	{
		return inclinationToEcliptic;
	}

	public void setInclinationToEcliptic(double inclinationToEcliptic)
	{
		this.inclinationToEcliptic = inclinationToEcliptic;
	}

	public double getInclinationToSunsEquator()
	{
		return inclinationToSunsEquator;
	}

	public void setInclinationToSunsEquator(double inclinationToSunsEquator)
	{
		this.inclinationToSunsEquator = inclinationToSunsEquator;
	}

	public double getInclinationToInvariablePlane()
	{
		return inclinationToInvariablePlane;
	}

	public void setInclinationToInvariablePlane(double inclinationToInvariablePlane)
	{
		this.inclinationToInvariablePlane = inclinationToInvariablePlane;
	}

	public double getLongitudeOfAscendingNode()
	{
		return longitudeOfAscendingNode;
	}

	public void setLongitudeOfAscendingNode(double longitudeOfAscendingNode)
	{
		this.longitudeOfAscendingNode = longitudeOfAscendingNode;
	}

	public double getArgumentOfPerihelion()
	{
		return argumentOfPerihelion;
	}

	public void setArgumentOfPerihelion(double argumentOfPerihelion)
	{
		this.argumentOfPerihelion = argumentOfPerihelion;
	}

	public double getMeanRadius()
	{
		return meanRadius;
	}

	public void setMeanRadius(double meanRadius)
	{
		this.meanRadius = meanRadius;
	}

	public double getEquatorialRadius()
	{
		return equatorialRadius;
	}

	public void setEquatorialRadius(double equatorialRadius)
	{
		this.equatorialRadius = equatorialRadius;
	}

	public double getPolarRadius()
	{
		return polarRadius;
	}

	public void setPolarRadius(double polarRadius)
	{
		this.polarRadius = polarRadius;
	}

	public double getFlattening()
	{
		return flattening;
	}

	public void setFlattening(double flattening)
	{
		this.flattening = flattening;
	}

	public double getCircumferenceEquatorial()
	{
		return circumferenceEquatorial;
	}

	public void setCircumferenceEquatorial(double circumferenceEquatorial)
	{
		this.circumferenceEquatorial = circumferenceEquatorial;
	}

	public double getCircumferenceMeridional()
	{
		return circumferenceMeridional;
	}

	public void setCircumferenceMeridional(double circumferenceMeridional)
	{
		this.circumferenceMeridional = circumferenceMeridional;
	}

	public double getSurfaceArea()
	{
		return surfaceArea;
	}

	public void setSurfaceArea(double surfaceArea)
	{
		this.surfaceArea = surfaceArea;
	}

	public double getVolume()
	{
		return volume;
	}

	public void setVolume(double volume)
	{
		this.volume = volume;
	}

	public double getMass()
	{
		return mass;
	}

	public void setMass(double mass)
	{
		this.mass = mass;
	}

	public double getMeanDensity()
	{
		return meanDensity;
	}

	public void setMeanDensity(double meanDensity)
	{
		this.meanDensity = meanDensity;
	}

	public double getEquatorialSurfaceGravity()
	{
		return equatorialSurfaceGravity;
	}

	public void setEquatorialSurfaceGravity(double equatorialSurfaceGravity)
	{
		this.equatorialSurfaceGravity = equatorialSurfaceGravity;
	}

	public double getEscapeVelocity()
	{
		return escapeVelocity;
	}

	public void setEscapeVelocity(double escapeVelocity)
	{
		this.escapeVelocity = escapeVelocity;
	}

	public double getSiderealRotationPeriod()
	{
		return siderealRotationPeriod;
	}

	public void setSiderealRotationPeriod(double siderealRotationPeriod)
	{
		this.siderealRotationPeriod = siderealRotationPeriod;
	}

	public double getEquatorialRotationVelocity()
	{
		return equatorialRotationVelocity;
	}

	public void setEquatorialRotationVelocity(double equatorialRotationVelocity)
	{
		this.equatorialRotationVelocity = equatorialRotationVelocity;
	}

	public double getAxialTilt()
	{
		return axialTilt;
	}

	public void setAxialTilt(double axialTilt)
	{
		this.axialTilt = axialTilt;
	}

	public double getNorthPoleRightAscension()
	{
		return northPoleRightAscension;
	}

	public void setNorthPoleRightAscension(double northPoleRightAscension)
	{
		this.northPoleRightAscension = northPoleRightAscension;
	}

	public double getNorthPoleDeclination()
	{
		return northPoleDeclination;
	}

	public void setNorthPoleDeclination(double northPoleDeclination)
	{
		this.northPoleDeclination = northPoleDeclination;
	}

	public double getAlbedoGeometric()
	{
		return albedoGeometric;
	}

	public void setAlbedoGeometric(double albedoGeometric)
	{
		this.albedoGeometric = albedoGeometric;
	}

	public double getAlbedoBond()
	{
		return albedoBond;
	}

	public void setAlbedoBond(double albedoBond)
	{
		this.albedoBond = albedoBond;
	}
	
	
	public Ellipsoid getEllipsoid()
	{
		if (ellipse == null) {
			ellipse = new Ellipsoid(this.equatorialRadius, this.polarRadius, this.flattening);
		}
		return ellipse;
	}
	
}
