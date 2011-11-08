
import us.wthr.jdem846.*
import us.wthr.jdem846.logging.*
import us.wthr.jdem846.image.*
import us.wthr.jdem846.input.*

class JDemScript {
	
	def log = Logging.getLog(JDemScript.class)
	
	def initialize(modelContext) {
		
	}
	
	def on2DModelBefore(modelContext, modelCanvas) {
		
		
	}
	
	def onTileBefore(modelContext, modelCanvas) {
		
		
	}
	
	def onTileAfter(modelContext, modelCanvas) {
	
	
	}
	
	def on2DModelAfter(modelContext, modelCanvas) {
	
	
	}
	
	def onGetElevationBefore(modelContext, latitude, longitude) {
		
		return null
	}
	
	def onGetElevationAfter(modelContext, latitude, longitude, elevation) {
		
		return null
	}
	
	
	def destroy(modelContext) {
	
	}
}
// And so on....