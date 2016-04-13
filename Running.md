# Requirements #
  * Java runtime environment 5.0 or newer
  * 500MB system memory (more depending on input data)

# Building via Apache Ant #
**NOTE:** The Ant build.xml file is old and needs some updating. Building this way won't currently work.

Common ant tasks:
  * Compile: `ant compile`
  * Build Jars: `ant jar`
  * Generate Javadocs: `ant javadoc`
  * Generate Release (default): `ant dist`
  * Clean Build Files: `ant clean`

The release file will be located in `<projectdir>/build/dist/jdem846-<version>.zip`


# Running via Eclipse #
The project is developed primarily within Eclipse, so the project files are all there.

For the main user interface, use the following Main class:

```
us.wthr.jdem846.ui.JDemUIMain
```

When executing, make sure the following JVM arguments are provided (adjust as necessary):

```
-Xms512m -Xmx1024m -Dus.wthr.jdem846.installPath=${workspace_loc:jdem846}/bin
```



# Command-line Options #
| -no-splash-screen   | Do not display splash screen on startup |
|:--------------------|:----------------------------------------|
| -show-splash-screen | Display splash screen on startup        |
| -debug              | Display log console                     |
| -no-debug           | Do not display log console              |
| -show-toolbar-text  | Show text besides icons on the toolbar  |
| -no-toolbar-text    | Do not show text besides icons on the toolbar |
| -language `<lang>`  | Use specified language (2 character ISO 639-1 code) |