
import us.wthr.jdem846._
import us.wthr.jdem846.logging._
import us.wthr.jdem846.image._
import us.wthr.jdem846.input._
import us.wthr.jdem846.math._
import java.io._
import javax.imageio._
import java.awt.image._
import us.wthr.jdem846.color._
import us.wthr.jdem846.gis.projections._
import us.wthr.jdem846.canvas._
import us.wthr.jdem846.geom._
import us.wthr.jdem846.gis.planets._
import us.wthr.jdem846.model._
import us.wthr.jdem846.model.processing.util._
import us.wthr.jdem846.model.processing.shading._
import us.wthr.jdem846.globe._


/**
 * Note: I know next to nothing about programming in Scala... 
 */

 


class JDemScript {

	
	var log : Log = null
	var modelContext : ModelContext = null
	
	
	def setLog(l:Log)
	{
		log = l
	}
	
	def setModelContext(mc:ModelContext)
	{
		modelContext = mc
	}
	
	def initialize() 
	{
		
	}
	
	def onModelBefore() 
	{ 
		
	}
	
	def onProcessBefore(modelProcessContainer : ModelProcessContainer)
	{
			
	}
	
	def onProcessAfter(modelProcessContainer : ModelProcessContainer) 
	{
	
	}
	
	def onModelAfter()
	{ 
	
	}
	
	def onGetElevationBefore(latitude: Double, longitude: Double) : Double = {	
		DemConstants.ELEV_UNDETERMINED
	}
	
	def onGetElevationAfter(latitude: Double, longitude: Double, elevation: Double) : Double = {	
		elevation
	}
	
	
	def onGetPointColor(latitude: Double, longitude: Double, elevation: Double, elevationMinimum: Double, elevationMaximum: Double, color: Array[Int])
	{
		
	}
	
	def onLightLevels(latitude: Double, longitude: Double, lightingValues: LightingValues)
	{	
		
	}
	
	def destroy()
	{ 
	
	}

}
