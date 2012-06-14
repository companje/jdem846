package us.wthr.jdem846.model.processing.util;

public class LightingValues
{
	
	/** Configured level
	 * 
	 */
	public double emmisiveLevel = 0;
	public double ambientLevel = 0;
	public double diffuseLevel = 0;
	public double specularLevel = 0;
	
	/** Calculated levels
	 * 
	 */
	public double emmisiveLight = 0;
	public double ambientLight = 0;
	public double diffuseLight = 0;
	public double specularLight = 0;
	
	/** Applied colors
	 * 
	 */
	public double[] emmisiveColor = new double[4];
	public double[] ambientColor = new double[4];
	public double[] diffuseColor = new double[4];
	public double[] specularColor = new double[4];
	
	
	
	public LightingValues()
	{
		
	}
	
	
	public LightingValues(double emmisiveLevel, double ambientLevel, double diffuseLevel, double specularLevel)
	{
		this.emmisiveLevel = emmisiveLevel;
		this.ambientLevel = ambientLevel;
		this.diffuseLevel = diffuseLevel;
		this.specularLevel = specularLevel;
	}
	
}
