package us.wthr.jdem846.rasterdata.generic;

import us.wthr.jdem846.rasterdata.IRasterDataValidator;

public class GenericRasterDataValidator implements IRasterDataValidator
{

	private GenericRasterDataProvider provider;

	public GenericRasterDataValidator(GenericRasterDataProvider provider)
	{
		this.provider = provider;
	}

	@Override
	public boolean validate()
	{
		return true;
	}

}
