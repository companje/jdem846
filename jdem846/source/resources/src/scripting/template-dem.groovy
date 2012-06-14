
import us.wthr.jdem846.*
import us.wthr.jdem846.logging.*
import us.wthr.jdem846.image.*
import us.wthr.jdem846.input.*
import us.wthr.jdem846.geom.*
import us.wthr.jdem846.gis.*
import us.wthr.jdem846.gis.projections.*
import us.wthr.jdem846.gis.planets.*
import us.wthr.jdem846.model.*
import us.wthr.jdem846.model.util.*
import java.io.*
import javax.imageio.ImageIO
import java.awt.image.*


class JDemScript {

	def log
	def modelContext
	
	def initialize = {
		
	}
	
	def onModelBefore = { 
		
		
	}
	
	def onProcessBefore = { modelProcessContainer ->
		
		
	}
	
	def onProcessAfter = { modelProcessContainer ->
	
	
	}
	
	def onModelAfter = { 
	
	
	}
	
	def onGetElevationBefore = {latitude, longitude ->
		
		
	}
	
	def onGetElevationAfter = { latitude, longitude, elevation ->
		
		
	}
	
	
	def onGetPointColor = { latitude, longitude, elevation, elevationMinimum, elevationMaximum, color ->
		
	}
	
	def onLightLevels = { latitude, longitude, lightingValues ->
		
		
	}
	
	def destroy = { 
	
	}
}
// And so on....