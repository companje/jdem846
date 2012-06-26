package us.wthr.jdem846.globe;

public class AtmosphereLighting
{
	double elevation;
    double shininess;
    double glowExtent;
    int[] color;
    
    public AtmosphereLighting()
    {
    	
    }
    
    public AtmosphereLighting(double elevation, double shininess, double glowExtent, int[] color)
    {
    	this.elevation = elevation;
    	this.shininess = shininess;
    	this.glowExtent = glowExtent;
    	this.color = color;
    }
    
}
