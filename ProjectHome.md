# Overview #

<table>
<tr>
<td width='150' valign='top'>
<a href='http://www.flickr.com/photos/kevinmgill/6956567565/' title='Earth - Global DEM'><img src='http://jdem846.googlecode.com/svn/trunk/jdem846/resources/homepage_title_image.png' alt='Earth - Global DEM' width='150' /></a>
</td>
<td valign='top'>
jDem846 is a geospatial software application designed to produce high-quality two and three dimensional digital elevation model images. It provides advanced rendering capabilities, an extensible data input and processing framework, and a powerful integrated scripting API. jDem846 content has been featured on Universe Today, Huffington Post, Discovery News, The Atlantic and more.<br>
<br>
It is pure Java and attempts to implement a full stack of components, including GIS, data format support, rendering, etc. Though a specialized plug-in architecture has not yet been implemented, the software is designed to be easily extended with 3rd party add-ons or used as an API with a larger application.<br>
<br>
jDem846 is a “learning” project and has a fluid and evolving design with new features and functionality being added all the time. It is not expected to become a commercial grade product preserving the freedom to tweak and try out new ideas, methods, and algorithms.<br>
<br>
Being open-source software, the jDem846 source code is free to download. Feedback and defect reports are always welcome.<br>
<br>
<br>
</td>
</tr>
</table>


---


# Primary Modules #
  * **Base** - Basic classes and functionality shared across modules
  * **dBase** - dBase read support
  * **Shapefile** - Read support for ESRI Shapefiles
  * **GIS/Physics** - Geographical and Physics functionality
  * **KML** - Write support for the KML format
  * **jDem846 Core** - Primary class for data input, rendering, projects, etc.
  * **UI** - Primary graphical user interface for jDem846

# Input Support #
## Elevation Raster ##
  * GridFloat
  * BIL INT16

## Shape Files ##
  * ESRI Shapefiles

## Data Files ##
  * dBase (just enough to support the shapefiles)

## Scripting ##
  * Integrated scripting is available which allows for injecting additional logic into several points of the rendering process.
  * Supported Languages:
    * Groovy
    * Scala
    * JavaScript

# Output Support #
  * PNG/JPEG
  * KML (needs reimplementation)
  * Data raster conversions/exports (planned)


# Status Summary #
## Overview ##
jDem846 is in relatively early in it's development stages. Some components may come with varying levels of stability.
## 2D Projection ##
  * Elevation modeling is in good shape
  * Shapefile support is very early in development
  * Error checking is minimal and needs expansion

## 3D Projection ##
  * Most structures are in place
  * Projection algorithm still needing some tweaking

## Spherical/Global Projection ##
  * Most structures are in place

## KML Output Support ##
  * Needs significant update following API changes
  * Requires additional options not yet on the model config panel
  * Does not yet utilize shapefiles
    * Should shapes be rendered on top of the tile images?
    * Should shapes be converted to KML polygons?

## Hardware Rendering With OpenGL ##
  * Implementation is in progress, about 75% usable.

## C++ Native Library ##
  * I created a native port of the jDem846 algorithms and data structures in C++ which is considerably faster and more memory efficient. Needs fine tuning and Java native tie-in. This code is not yet publicly available.

## New User Interface ##
  * A new user interface is in development that uses the Eclipse platform. This new UI will be much more stable, professional looking, and easier to use.

## Render Performance ##
  * Rendering is not particularly fast. This is due to the current reliance on memory mapped files for storing most of the huge data sets that are worked with. Doing this adds considerable disk IO overhead. One workaround is to modify the maximum heap buffer size in the preferences to a larger size, but this will require a larger configured JVM max heap size and will quickly overrun the integer-based indexing Java uses for arrays (The MMap'd files use longs for indexing). Also recommended is the use of a solid state hard drive which reduces disk seek times. In the future I do plan to add smarter memory caching of data to reduce the constant read/writes from disk

---



**Screenshots:**

<a href='http://googledrive.com/host/0B5gEWNRhmLMTSlhZbnJxRmM4QVU/jDem846 Eclipse UI - Screenshot Feb 18 2013.jpg' title='jDem846 Screenshot'><img src='http://googledrive.com/host/0B5gEWNRhmLMTSlhZbnJxRmM4QVU/jDem846 Eclipse UI - Screenshot Feb 18 2013.jpg' alt='jDem846 Screenshot - Model Rendered' width='500' height='329' /></a>


**Example using**<a href='http://www.gebco.net/'>GEBCO_08</a> Dataset:

<a href='http://www.flickr.com/photos/kevinmgill/6790302628/' title='GEBCO_08 with Shaded Blue Marble Landmass by Kevin M. Gill, on Flickr'><img src='http://farm8.staticflickr.com/7066/6790302628_0d94375c62.jpg' alt='GEBCO_08 with Shaded Blue Marble Landmass' width='500' height='250' /></a>

**Examples of Spherical Projection**

<table width='500'>
<tr>
<td>
<a href='http://www.flickr.com/photos/kevinmgill/6810273194/' title='Earth - Global DEM - Version 2 by Kevin M. Gill, on Flickr'><img src='http://farm8.staticflickr.com/7069/6810273194_ea476532f8.jpg' alt='Earth - Global DEM - Version 2' width='225' /></a>
</td>
<td>
<a href='http://www.flickr.com/photos/kevinmgill/6799076346/' title='Moon - Digital Elevation Model - Version 2 by Kevin M. Gill, on Flickr'><img src='http://farm8.staticflickr.com/7058/6799076346_1a65ff5577.jpg' alt='Moon - Digital Elevation Model - Version 2' width='225' /></a>
</td>
</tr>
<tr>
<td>
<a href='http://www.flickr.com/photos/kevinmgill/6815952656/' title='Earth - Digital Elevation Model by Kevin M. Gill, on Flickr'><img src='http://farm8.staticflickr.com/7187/6815952656_55260d43b4_n.jpg' alt='Earth - Digital Elevation Model' width='225' /></a>
</td>
<td>
<a href='http://www.flickr.com/photos/kevinmgill/6897598788/' title='Mars - Digital Elevation Model by Kevin M. Gill, on Flickr'><img src='http://farm6.staticflickr.com/5328/6897598788_ca62d442fc_n.jpg' alt='Mars - Digital Elevation Model' width='225' /></a>
</td>
</tr>
</table>

**Model Enhanced By Integrated Scripting**

<a href='http://www.flickr.com/photos/kevinmgill/8165909516/' title='A Living Mars: A Visualization of Mars, Very Much Alive by Kevin M. Gill, on Flickr'><img src='http://farm9.staticflickr.com/8342/8165909516_f0a83395bf.jpg' alt='A Living Mars: A Visualization of Mars, Very Much Alive' width='500' height='500' /></a>

**Visualization of a Living Mars**

<a href='http://www.youtube.com/watch?feature=player_embedded&v=7RSY37H_sFU' target='_blank'><img src='http://img.youtube.com/vi/7RSY37H_sFU/0.jpg' width='500' height=369 /></a>

**Ray Traced Shadows Animation**

<a href='http://www.youtube.com/watch?feature=player_embedded&v=rzlnlmkXZIY' target='_blank'><img src='http://img.youtube.com/vi/rzlnlmkXZIY/0.jpg' width='500' height=369 /></a>

**Ray Traced Shadows Animation Using Satellite Imagery**

<a href='http://www.youtube.com/watch?feature=player_embedded&v=7ztM0dpel0M' target='_blank'><img src='http://img.youtube.com/vi/7ztM0dpel0M/0.jpg' width='500' height=369 /></a>

**jDem846 Model Used as a Google Earth Layer:**

<a href='http://www.flickr.com/photos/kevinmgill/6200730720/' title='jDem846 Example: KML Support, Google Earth by Kevin M. Gill, on Flickr'><img src='http://farm7.static.flickr.com/6013/6200730720_ee3c58c01a.jpg' alt='jDem846 Example: KML Support, Google Earth' width='500' /></a>