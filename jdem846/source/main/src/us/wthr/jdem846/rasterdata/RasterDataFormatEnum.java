package us.wthr.jdem846.rasterdata;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.rasterdata.bil.Bil16IntRasterDataProvider;
import us.wthr.jdem846.rasterdata.gridfloat.GridFloatRasterDataProvider;
//import us.wthr.jdem846.rasterdata.bil.Bil16IntRasterDataProvider;
//import us.wthr.jdem846.rasterdata.gridascii.GridAsciiRasterDataProvider;

public enum RasterDataFormatEnum
{
	
	BIL_16INT("us.wthr.jdem846.input.bilInt16.name", "bil", Bil16IntRasterDataProvider.class),
	//GRIDASCII("us.wthr.jdem846.input.esri.gridAscii.name", "asc", GridAsciiRasterDataProvider.class),
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
