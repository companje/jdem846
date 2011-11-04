package us.wthr.jdem846.netcdf;

import java.io.IOException;
import java.util.List;

import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class NetCdfTesting
{
	private static Log log = Logging.getLog(NetCdfTesting.class);
	
	
	public static void main(String[] args)
	{
		String filePath = "C:\\srv\\elevation\\GEBCO_08\\gebco_08_-83_24_-56_49.nc";
		try {
			log.info("Opening " + filePath);
			NetcdfFile ncfile = NetcdfFile.open(filePath);
			
			log.info("ID: " + ncfile.getId());
			log.info("Title: " + ncfile.getTitle());
			log.info("Detail Info: " + ncfile.getDetailInfo());
			
			List<Attribute> attrList = ncfile.getGlobalAttributes();
			for (Attribute attribute : attrList) {
				
				log.info("Global Attribute Name: " + attribute.getName() + ", Value: " + attribute.getStringValue());
			}
			
			List<Variable> varList = ncfile.getVariables();
			
			for (Variable variable : varList) {
				String fullName = variable.getFullName();
				DataType dataType = variable.getDataType();
				String dataTypeName = dataType.getPrimitiveClassType().getName();
				log.info("Variable Full Name: " + fullName + ", Type: " + dataTypeName);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
