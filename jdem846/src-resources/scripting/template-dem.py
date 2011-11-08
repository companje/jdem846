
def initialize(modelContext):
    pass

def on2DModelBefore(modelContext, modelCanvas):
    pass
 
def onTileBefore(modelContext, modelCanvas):
    pass

def onTileAfter(modelContext, modelCanvas):
    pass

def on2DModelAfter(modelContext, modelCanvas):
    pass

def onGetElevationBefore(modelContext, latitude, longitude):
    return None

def onGetElevationAfter(modelContext, latitude, longitude, elevation):
    return None

def destroy(modelContext):
    pass
# And so on...