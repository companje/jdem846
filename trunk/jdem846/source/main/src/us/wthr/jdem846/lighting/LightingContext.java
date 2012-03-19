package us.wthr.jdem846.lighting;

import java.util.Date;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.MappedOptions;
import us.wthr.jdem846.ModelOptionNamesEnum;

public class LightingContext extends MappedOptions
{

	
	
	public LightingContext()
	{
		addOptionPrefix("us.wthr.jdem846.lightingContext");
		
		
		for (LightingOptionNamesEnum optionName : LightingOptionNamesEnum.values()) {
			String property = JDem846Properties.getProperty(optionName.optionName());
			if (property != null) {
				setOption(optionName.optionName(), property);
			}
		}
	}
	
	
	public void setOption(LightingOptionNamesEnum name, Object value)
	{
		setOption(name.optionName(), value);
	}
	

	
	public String getOption(LightingOptionNamesEnum name)
	{
		return getOption(name.optionName());
	}

	
	public boolean hasOption(LightingOptionNamesEnum name)
	{
		return hasOption(name.optionName());
	}

	
	public String removeOption(LightingOptionNamesEnum name)
	{
		return removeOption(name.optionName());
	}

	
	public boolean getBooleanOption(LightingOptionNamesEnum name)
	{
		return getBooleanOption(name.optionName());
	}
	

	
	public int getIntegerOption(LightingOptionNamesEnum name)
	{
		return getIntegerOption(name.optionName());
	}

	
	public double getDoubleOption(LightingOptionNamesEnum name)
	{
		return getDoubleOption(name.optionName());
	}

	
	public float getFloatOption(LightingOptionNamesEnum name)
	{
		return getFloatOption(name.optionName());
	}

	
	public long getLongOption(LightingOptionNamesEnum name)
	{
		return getLongOption(name.optionName());
	}
	
	
	
	public boolean isLightingEnabled() 
	{
		return getBooleanOption(LightingOptionNamesEnum.LIGHTING_ENABLED);
	}


	public void setLightingEnabled(boolean lightingEnabled) 
	{
		setOption(LightingOptionNamesEnum.LIGHTING_ENABLED, lightingEnabled);
	}
	
	public void setLightSourceSpecifyType(String value)
	{
		setOption(LightingOptionNamesEnum.LIGHT_SOURCE_SPECIFY_TYPE, value);
	}
	
	public void setLightSourceSpecifyType(LightSourceSpecifyTypeEnum value)
	{
		setOption(LightingOptionNamesEnum.LIGHT_SOURCE_SPECIFY_TYPE, value.optionValue());
	}
	
	public LightSourceSpecifyTypeEnum getLightSourceSpecifyType()
	{
		return LightSourceSpecifyTypeEnum.getByOptionValue(getOption(LightingOptionNamesEnum.LIGHT_SOURCE_SPECIFY_TYPE));
	}

	public double getLightingMultiple() 
	{
		return getDoubleOption(LightingOptionNamesEnum.LIGHTING_MULTIPLE);
	}


	public void setLightingMultiple(double lightingMultiple)
	{
		setOption(LightingOptionNamesEnum.LIGHTING_MULTIPLE, lightingMultiple);
	}


	public double getRelativeLightIntensity()
	{
		return getDoubleOption(LightingOptionNamesEnum.RELATIVE_LIGHT_INTENSITY);
	}


	public void setRelativeLightIntensity(double relativeLightIntensity)
	{
		setOption(LightingOptionNamesEnum.RELATIVE_LIGHT_INTENSITY, relativeLightIntensity);
	}


	public double getRelativeDarkIntensity()
	{
		return getDoubleOption(LightingOptionNamesEnum.RELATIVE_DARK_INTENSITY);
	}


	public void setRelativeDarkIntensity(double relativeDarkIntensity)
	{
		setOption(LightingOptionNamesEnum.RELATIVE_DARK_INTENSITY, relativeDarkIntensity);
	}


	public int getSpotExponent()
	{
		return getIntegerOption(LightingOptionNamesEnum.SPOT_EXPONENT);
	}
	
	/** Sets the spot exponent for the intensity distribution of the lighting. 
	 * 
	 * @param spotExponent A value between 1.0 and 10.0 (default: 1.0)
	 */
	public void setSpotExponent(int spotExponent)
	{
		setOption(LightingOptionNamesEnum.SPOT_EXPONENT, spotExponent);
	}

	public double getLightingAzimuth()
	{
		return getDoubleOption(LightingOptionNamesEnum.LIGHTING_AZIMUTH);
	}


	public void setLightingAzimuth(double lightingAzimuth)
	{
		setOption(LightingOptionNamesEnum.LIGHTING_AZIMUTH, lightingAzimuth);
	}

	public double getLightingElevation()
	{
		return getDoubleOption(LightingOptionNamesEnum.LIGHTING_ELEVATION);
	}

	public void setLightingElevation(double lightingElevation)
	{
		setOption(LightingOptionNamesEnum.LIGHTING_ELEVATION, lightingElevation);
	}

	public double getLightZenith()
	{
		return getDoubleOption(LightingOptionNamesEnum.LIGHT_ZENITH);
	}

	public void setLightZenith(double lightZenith)
	{
		setOption(LightingOptionNamesEnum.LIGHT_ZENITH, lightZenith);
	}
	
	public double getDarkZenith()
	{
		return getDoubleOption(LightingOptionNamesEnum.DARK_ZENITH);
	}

	public void setDarkZenith(double darkZenith)
	{
		setOption(LightingOptionNamesEnum.DARK_ZENITH, darkZenith);
	}
	
	public long getLightingOnDate()
	{
		return getLongOption(LightingOptionNamesEnum.LIGHTING_ON_DATE);
	}
	
	public void setLightingOnDate(long lightingOnDate)
	{
		setOption(LightingOptionNamesEnum.LIGHTING_ON_DATE, lightingOnDate);
	}
	
	public void setLightingOnDate(Date lightingOnDate)
	{
		setLightingOnDate(lightingOnDate.getTime());
	}
	
	public boolean getRecalcLightOnEachPoint()
	{
		return getBooleanOption(LightingOptionNamesEnum.RECALC_LIGHT_ON_EACH_POINT);
	}
	
	public void setRecalcLightOnEachPoint(boolean recalcLightOnEachPoint)
	{
		setOption(LightingOptionNamesEnum.RECALC_LIGHT_ON_EACH_POINT, recalcLightOnEachPoint);
	}
	
	
	public boolean getRayTraceShadows()
	{
		return getBooleanOption(LightingOptionNamesEnum.RAY_TRACE_SHADOWS);
	}
	
	public void setRayTraceShadows(boolean rayTraceShadows)
	{
		setOption(LightingOptionNamesEnum.RAY_TRACE_SHADOWS, rayTraceShadows);
	}
	
	public double getShadowIntensity()
	{
		return getDoubleOption(LightingOptionNamesEnum.SHADOW_INTENSITY);
	}
	
	public void setShadowIntensity(double shadowIntensity)
	{
		setOption(LightingOptionNamesEnum.SHADOW_INTENSITY, shadowIntensity);
	}
	
	
	
	
	
	
	
	
	
	
	public LightingContext copy()
	{
		LightingContext clone = new LightingContext();
		
		for (String optionName : getOptionNames()) {
			clone.setOption(optionName, getOption(optionName).toString());
		}
		
		return clone;
	}
}
