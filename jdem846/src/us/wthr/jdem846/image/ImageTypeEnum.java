package us.wthr.jdem846.image;

public enum ImageTypeEnum {
	
	UNDEFINED_TYPE(null, null),
	PNG("png", "PNG"),
	JPEG("jpeg", "JPG");
	
	private final String extension;
	private final String formatName;
	
	ImageTypeEnum(String extension, String formatName)
	{
		this.extension = extension;
		this.formatName = formatName;
	}
	
	public String extension() { return extension; }
	public String formatName() { return formatName; }
	
	
	/** Determines the image format type from the file name.
	 * 
	 * @param fileName A file name with an image format extension (e.g. 'foo.jpg' or 'foo.png')
	 * @return The type number
	 */
	protected static ImageTypeEnum imageTypeFromFileName(String fileName)
	{
		if (fileName == null) {
			return ImageTypeEnum.UNDEFINED_TYPE;
		}
		
		if (fileName.toLowerCase().endsWith(".jpg")
				|| fileName.toLowerCase().endsWith(".jpeg")) {
			return ImageTypeEnum.JPEG;
		} else if (fileName.toLowerCase().endsWith(".png")) {
			return ImageTypeEnum.PNG;
		} else {
			return ImageTypeEnum.UNDEFINED_TYPE;
		}
		
		
	}
	
	/** Determines if the specified format type number is supported by this class.
	 * 
	 * @param format A format type number.
	 * @return True if the type number is supported, otherwise false.
	 */
	protected static boolean isSupportedFormat(ImageTypeEnum format)
	{
		switch (format) {
		case JPEG:
		case PNG:
			return true;
		default:
			return false;
		}
	}
	
	

}
