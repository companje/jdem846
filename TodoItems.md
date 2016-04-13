# Introduction #

The following is a list of outstanding tasks for development and/or design. These are reminders with items added and removed as tasks are created and/or completed.

**`*`Note:** Actual defect issues are to be kept using the standard issue tracker.


---


# Global #
  * Code documentation
  * Improved error handling
  * Add unit test cases
  * Add Doxygen support?

# User Interface #
  * Language selection control
  * Persisted settings in user home
  * Improve layout and process flow
  * Additional image tweaking features for completed models
    * Lighten/darken/contrast/etc...
    * Add annotations
  * Latitude/longitude label for 3D projections on completed model panel
  * Improved thread-safety for Swing components
  * Options required for KML output


# Image/Canvas #
  * Additional output formats
    * GeoTIFF

# Scaling / Resampling #
  * Reimplement data interpolation

# Registry Model #
  * Strengthen model for stateful and transient instances

# DEM 2D #
  * Add elevation gradient level key to model
  * Add north arrow to model
  * Add customizable copyright label to model
  * Reimplement process stop functionality
  * Add a "pause" option

# DEM 3D #
  * Fix color bleedthrough of hidden layers

# Raster Input Formats #
  * Validate BIL 16INT
  * Add additional specifications

# Data Input Proxy (DataPackage) #
  * Dearchitect
    * It's still more-or-less based on the original hastily-written C++ code from which it was ported
    * Switch from row/column iteration to latitude/longitude
    * Add support for multiple data resolutions.

# Scripting #
  * Strengthen script model/callbackd
  * Create reasonable API
  * Better error handling/user feedback

# Internationalization (I18N) #
  * Utility to list available translations
  * Translations
    * Chinese (Simplified) (zh-CN)
    * Dutch (nl)
    * English (en) (grammer)
    * French (fr)
    * German (de) (needs verification)
    * Italian (it)
    * Japanese (ja)
    * Brazilian Portuguese (pt\_BR)
    * Russian (ru)
    * Spanish (es)
    * Tamil (ta)
    * Thai (th)

# ESRI ShapeFile Support #
  * Add additional supported shapes

# dBase Support #

# Build Automation #
  * Strengthen build.xml
  * Strengthen jdem.bat and jdem.sh
  * Test build distribution files

# Elevation Data Set Exchange Format #
  * Strengthen & finalize specification