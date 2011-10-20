
def initialize(modelContext):
    pass

def on2DModelBefore(modelContext, tileCanvas):
    pass
 
def onTileBefore(modelContext, tileCanvas):
    pass

def onTileAfter(modelContext, tileCanvas):
    pass

def on2DModelAfter(modelContext, tileCanvas):
    pass

def onGetElevationBefore(modelContext, column, row):
    return None

def onGetElevationAfter(modelContext, column, row, elevation):
    return None

def destroy(modelContext):
    pass
# And so on...