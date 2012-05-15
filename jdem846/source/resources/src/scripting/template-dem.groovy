
import us.wthr.jdem846.*
import us.wthr.jdem846.logging.*
import us.wthr.jdem846.image.*
import us.wthr.jdem846.input.*
import java.io.*
import javax.imageio.ImageIO
import java.awt.image.*


class JDemScript {
	
	def log = Logging.getLog(JDemScript.class)
	
	def initialize(modelContext) {
		
	}
	
	def onModelBefore(modelContext) {
		
		
	}
	
	def onProcessBefore(modelContext, modelProcessContainer) {
		
		
	}
	
	def onProcessAfter(modelContext, modelProcessContainer) {
	
	
	}
	
	def onModelAfter(modelContext) {
	
	
	}
	
	def onGetElevationBefore(modelContext, latitude, longitude) {
		
		return null
	}
	
	def onGetElevationAfter(modelContext, latitude, longitude, elevation) {
		
		return null
	}
	
	
	def onGetPointColor(modelContext, latitude, longitude, elevation, elevationMinimum, elevationMaximum, color)
	{
		
	}
	
	def destroy(modelContext) {
	
	}
}
// And so on....