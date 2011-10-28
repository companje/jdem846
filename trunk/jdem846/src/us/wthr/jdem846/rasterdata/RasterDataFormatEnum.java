package us.wthr.jdem846.rasterdata;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.rasterdata.gridfloat.GridFloatRasterDataProvider;

public enum RasterDataFormatEnum
{
	
	GRIDFLOAT("us.wthr.jdem846.input.gridFloat.name", "flt", GridFloatRasterDataProvider.class);
	
	
	private final String formatName;
	private final String extension;
	private final Class<?> provider;
	
	RasterDataFormatEnum(String formatName, String extension, Class<?> provider)
	{
		this.formatName = formatName;
		this.extension = extension;
		this.provider = provider;
	}
	
	public String extension() { return extension; }
	public String identifier() { return formatName; }
	public String formatName() { return I18N.get(formatName, formatName); }
	public Class<?> provider() { return provider; }
	
}
