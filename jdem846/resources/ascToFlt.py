#!/usr/bin/python

import sys
import io
import struct

ascFile = "gebco_08.asc"
fltFile = "gebco_08.flt"
hdrFile = "gebco_08.hdr"

ascIn = open(ascFile, "r")
fltOut = open(fltFile, "wb")
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



print "Writing Raster Data..."
while (True):
	b = ascIn.read(1)
	if b == ' ' or b == '\n' or b == '\r':
		if len(buff) > 0:
			dataPoint = float(buff)
			bin = struct.pack("f", dataPoint)
			fltOut.write(bin)
			buff = ""
	else:
		buff += b

print "Closing..."
ascIn.close()
fltOut.close()
hdrOut.close()

print "Done..."
