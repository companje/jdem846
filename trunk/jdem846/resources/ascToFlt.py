#!/usr/bin/python

import sys
import io
import struct
root = "F:/GEBCO_08/"
ascFile = root+"gebco_08.asc"
#fltFile = root+"gebco_08.flt"
hdrFile = root+"gebco_08.hdr"

ascIn = open(ascFile, "rb")
#fltOut = open(fltFile, "wb")
hdrOut = open(hdrFile, "w")

buff = ""


print "Writing Header..."
hdrMap = {}
for i in range(0, 5):
	line = ascIn.readline().strip()
	parts = line.split(" ")
	field = parts[0]
	value = parts[1]
	hdrMap[field] = value


cellsize = float(hdrMap["cellsize"])
halfCell = cellsize / 2.0

xllCenter = float(hdrMap["xllcorner"]) + halfCell
yllCenter = float(hdrMap["yllcorner"]) - halfCell

hdrOut.write("ncols			{value}\r\n".format(value=hdrMap["ncols"]))
hdrOut.write("nrows			{value}\r\n".format(value=hdrMap["nrows"]))
hdrOut.write("xllcenter			{value}\r\n".format(value=xllCenter)) 
hdrOut.write("yllcenter			{value}\r\n".format(value=yllCenter))
hdrOut.write("NODATA_value  -99999\r\n")
hdrOut.write("byteorder     LSBFIRST\r\n")
hdrOut.write("cellsize			{value}\r\n".format(value=hdrMap["cellsize"]))

columns = int(hdrMap["ncols"])
rows = int(hdrMap["nrows"])
totalPoints = columns * rows

print "Writing Raster Data..."

outBuffer = ""
inBuffer = ""

dataPoints = 0
lastPct = 0


"""
col = 0
while (True):

        inBuffer = ascIn.read(104857600)
        if inBuffer == '':
                break
        outBuffer = ""
        for b in inBuffer:
                if b in (' ', '\n', '\r', ''):
                        if len(buff) > 0:
                                outBuffer += struct.pack("f", float(buff))
                                dataPoints += 1
                                buff = ""
                else:
                        buff += b
        fltOut.write(outBuffer)
        fltOut.flush()
        pctComplete = int((dataPoints / totalPoints) * 100)

        print "Percent Complete: " , pctComplete, "%", " (", dataPoints, " of ", totalPoints, ")"
"""        

print "Closing..."
ascIn.close()
#fltOut.close()
hdrOut.close()

print "Done..."
