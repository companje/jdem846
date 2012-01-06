import us.wthr.jdem846.*
import us.wthr.jdem846.logging.*
import us.wthr.jdem846.image.*
import us.wthr.jdem846.input.*
import us.wthr.jdem846.gis.*
import us.wthr.jdem846.gis.projections.*
import us.wthr.jdem846.gis.datetime.*
import us.wthr.jdem846.lighting.*
import us.wthr.jdem846.math.*
import us.wthr.jdem846.rasterdata.*
import us.wthr.jdem846.render.*
import us.wthr.jdem846.render.render2d.*
import java.io.*
import javax.imageio.ImageIO
import java.awt.image.*

def rasterList = []
rasterList.add("C:\\srv\\elevation\\DataRaster-Testing\\PresRange_1-3as.flt")
rasterList.add("C:\\srv\\elevation\\DataRaster-Testing\\PresRange_1as.flt")

def log = Logging.getLog(this.getClass());


def lightingContext = new LightingContext()

def modelOptions = new ModelOptions()
modelOptions.setWidth(1000)
modelOptions.setHeight(1000)
modelOptions.setTileSize(1000)
modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR)
modelOptions.setBackgroundColor("255;255;255;0")
modelOptions.setAntialiased(false)
modelOptions.setDoublePrecisionHillshading(false)
modelOptions.setUseSimpleCanvasFill(false)
modelOptions.setConcurrentRenderPoolSize(1)

def rasterDataContext = new RasterDataContext()
rasterList.each { inputDataPath ->
	def rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
	rasterDataContext.addRasterData(rasterData);
}



def modelContext = ModelContext.createInstance(rasterDataContext, modelOptions, lightingContext)


def dem2d = new Dem2dGenerator(modelContext)
def product = dem2d.generate()
def canvas = product.getProduct()
canvas.save(outputFileName)

