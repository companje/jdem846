
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
import us.wthr.jdem846.canvas.util._
import us.wthr.jdem846.geom._
import us.wthr.jdem846.gis.planets._
import us.wthr.jdem846.model._
import us.wthr.jdem846.modelgrid._
import us.wthr.jdem846.model.processing.util._
import us.wthr.jdem846.model.processing.shading._
import us.wthr.jdem846.globe._
import us.wthr.jdem846.graphics._

/**
 * Note: I know next to nothing about programming in Scala... 
 */

 


class JDemScript {

	
	var log : Log = null
	var modelContext : ModelContext = null
    var globalOptionModel : GlobalOptionModel = null
    var modelGrid : IModelGrid = null
    var modelDimensions : ModelDimensions = null
	
	
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
	
	def onProcessBefore()
	{
			
	}
	
	def onProcessAfter() 
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
	
	def onBeforeVertex(latitude: Double, longitude: Double, elevation: Double, renderer : IRenderer, view : View)
	{
	
	}
	
	def preRender(renderer : GraphicsRenderer, view : View)
	{
	
	}
	
	def postRender(renderer : GraphicsRenderer, view : View)
	{
	
	}
	
	def destroy()
	{ 
	
	}

}
