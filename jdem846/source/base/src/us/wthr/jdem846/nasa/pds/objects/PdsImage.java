package us.wthr.jdem846.nasa.pds.objects;

import us.wthr.jdem846.nasa.pds.PdsWindow;
import us.wthr.jdem846.nasa.pds.StandardValueTypeEnum;
import us.wthr.jdem846.nasa.pds.annotations.PdsField;
import us.wthr.jdem846.nasa.pds.annotations.PdsObject;

@PdsObject(name="image", aliases={"image_structure"})
public class PdsImage
{
	
	private PdsWindow window;
	
	private int lineSamples;
	private int lines;
	private int sampleBits;
	private String sampleType;
	private String bandSequence;
	private String bandStorageType;
	private int bands;
	private int checksum;
	private double derivedMaximum;
	private double derivedMinimum;
	private String description;
	private String encodingType;
	private int firstLine;
	private int firstLineSample;
	private String invalidConstant;
	private String lineDisplayDirection;
	private int linePrefixBytes;
	private int lineSuffixBytes;
	private String missingConstant;
	private double offset;
	
	private String sampleBitMask;
	private String sampleDisplayDirection;
	private double samplingFactor;
	private double scalingFactor;
	private String sourceFileName;
	private int sourceLineSamples;
	private int sourceLines;
	private int sourceSampleBits;
	private int stretchMaximum;
	private int stretchMinimum;
	private boolean stretchedFlag;

	
	public PdsImage()
	{
		
	}

	
	@PdsField(name="window", required=false)
	public PdsWindow getWindow()
	{
		return window;
	}



	public void setWindow(PdsWindow window)
	{
		this.window = window;
	}



	@PdsField(name="line_samples", required=true)
	public int getLineSamples()
	{
		return lineSamples;
	}

	
	public void setLineSamples(int lineSamples)
	{
		this.lineSamples = lineSamples;
	}

	@PdsField(name="lines", required=true)
	public int getLines()
	{
		return lines;
	}


	public void setLines(int lines)
	{
		this.lines = lines;
	}

	@PdsField(name="sample_bits", required=true)
	public int getSampleBits()
	{
		return sampleBits;
	}


	public void setSampleBits(int sampleBits)
	{
		this.sampleBits = sampleBits;
	}

	@PdsField(name="sample_type"
				, required=true
				, standardValues={"CHARACTER"
									, "IEEE_REAL"
									, "LSB_INTEGER"
									, "LSB_UNSIGNED_INTEGER"
									, "MSB_INTEGER"
									, "MSB_UNSIGNED_INTEGER"
									, "PC_REAL"
									, "UNSIGNED_INTEGER"
									, "VAX_REAL" }
				, standardValueType=StandardValueTypeEnum.DYNAMIC)
	public String getSampleType()
	{
		return sampleType;
	}


	public void setSampleType(String sampleType)
	{
		this.sampleType = sampleType;
	}

	@PdsField(name="band_sequence"
				, required=false
				, standardValues={"(BLUE, GREEN, RED)"
									, "(BLUE, RED, GREEN)"
									, "(GREEN, BLUE, RED)"
									, "(GREEN, RED, BLUE)"
									, "(RED, BLUE, GREEN)"
									, "(RED, GREEN, BLUE)"}
				, standardValueType=StandardValueTypeEnum.DYNAMIC)
	public String getBandSequence()
	{
		return bandSequence;
	}


	public void setBandSequence(String bandSequence)
	{
		this.bandSequence = bandSequence;
	}

	@PdsField(name="band_storage_type"
				, required=false
				, standardValues={"BAND_SEQUENTIAL"
									, "LINE_INTERLEAVED"
									, "SAMPLE_INTERLEAVED"}
				, standardValueType=StandardValueTypeEnum.DYNAMIC)
	public String getBandStorageType()
	{
		return bandStorageType;
	}


	public void setBandStorageType(String bandStorageType)
	{
		this.bandStorageType = bandStorageType;
	}

	@PdsField(name="bands", required=false)
	public int getBands()
	{
		return bands;
	}


	public void setBands(int bands)
	{
		this.bands = bands;
	}

	@PdsField(name="checksum", required=false)
	public int getChecksum()
	{
		return checksum;
	}


	public void setChecksum(int checksum)
	{
		this.checksum = checksum;
	}

	@PdsField(name="derived_maximum", required=false)
	public double getDerivedMaximum()
	{
		return derivedMaximum;
	}


	public void setDerivedMaximum(double derivedMaximum)
	{
		this.derivedMaximum = derivedMaximum;
	}

	@PdsField(name="derived_minimum", required=false)
	public double getDerivedMinimum()
	{
		return derivedMinimum;
	}


	public void setDerivedMinimum(double derivedMinimum)
	{
		this.derivedMinimum = derivedMinimum;
	}

	@PdsField(name="description", required=false)
	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}

	@PdsField(name="encoding_type", required=false)
	public String getEncodingType()
	{
		return encodingType;
	}


	public void setEncodingType(String encodingType)
	{
		this.encodingType = encodingType;
	}

	@PdsField(name="first_line", required=false)
	public int getFirstLine()
	{
		return firstLine;
	}


	public void setFirstLine(int firstLine)
	{
		this.firstLine = firstLine;
	}

	@PdsField(name="first_line_sample", required=false)
	public int getFirstLineSample()
	{
		return firstLineSample;
	}


	public void setFirstLineSample(int firstLineSample)
	{
		this.firstLineSample = firstLineSample;
	}

	@PdsField(name="invalid_constant", required=false)
	public String getInvalidConstant()
	{
		return invalidConstant;
	}


	public void setInvalidConstant(String invalidConstant)
	{
		this.invalidConstant = invalidConstant;
	}

	@PdsField(name="line_display_direction", required=false)
	public String getLineDisplayDirection()
	{
		return lineDisplayDirection;
	}


	public void setLineDisplayDirection(String lineDisplayDirection)
	{
		this.lineDisplayDirection = lineDisplayDirection;
	}

	@PdsField(name="line_prefix_bytes", required=false)
	public int getLinePrefixBytes()
	{
		return linePrefixBytes;
	}


	public void setLinePrefixBytes(int linePrefixBytes)
	{
		this.linePrefixBytes = linePrefixBytes;
	}

	@PdsField(name="line_suffix_bytes", required=false)
	public int getLineSuffixBytes()
	{
		return lineSuffixBytes;
	}


	public void setLineSuffixBytes(int lineSuffixBytes)
	{
		this.lineSuffixBytes = lineSuffixBytes;
	}

	@PdsField(name="missing_constant", required=false)
	public String getMissingConstant()
	{
		return missingConstant;
	}


	public void setMissingConstant(String missingConstant)
	{
		this.missingConstant = missingConstant;
	}

	@PdsField(name="offset", required=false)
	public double getOffset()
	{
		return offset;
	}


	public void setOffset(double offset)
	{
		this.offset = offset;
	}

	@PdsField(name="sample_bit_mask", required=false)
	public String getSampleBitMask()
	{
		return sampleBitMask;
	}


	public void setSampleBitMask(String sampleBitMask)
	{
		this.sampleBitMask = sampleBitMask;
	}

	@PdsField(name="sample_display_direction", required=false)
	public String getSampleDisplayDirection()
	{
		return sampleDisplayDirection;
	}


	public void setSampleDisplayDirection(String sampleDisplayDirection)
	{
		this.sampleDisplayDirection = sampleDisplayDirection;
	}

	@PdsField(name="sampling_factor", required=false)
	public double getSamplingFactor()
	{
		return samplingFactor;
	}


	public void setSamplingFactor(double samplingFactor)
	{
		this.samplingFactor = samplingFactor;
	}

	@PdsField(name="scaling_factor", required=false)
	public double getScalingFactor()
	{
		return scalingFactor;
	}


	public void setScalingFactor(double scalingFactor)
	{
		this.scalingFactor = scalingFactor;
	}

	@PdsField(name="source_file_name", required=false)
	public String getSourceFileName()
	{
		return sourceFileName;
	}

	
	public void setSourceFileName(String sourceFileName)
	{
		this.sourceFileName = sourceFileName;
	}

	@PdsField(name="source_line_samples", required=false)
	public int getSourceLineSamples()
	{
		return sourceLineSamples;
	}


	public void setSourceLineSamples(int sourceLineSamples)
	{
		this.sourceLineSamples = sourceLineSamples;
	}

	@PdsField(name="source_lines", required=false)
	public int getSourceLines()
	{
		return sourceLines;
	}


	public void setSourceLines(int sourceLines)
	{
		this.sourceLines = sourceLines;
	}

	@PdsField(name="source_sample_bits", required=false)
	public int getSourceSampleBits()
	{
		return sourceSampleBits;
	}


	public void setSourceSampleBits(int sourceSampleBits)
	{
		this.sourceSampleBits = sourceSampleBits;
	}

	@PdsField(name="stretch_maximum", required=false)
	public int getStretchMaximum()
	{
		return stretchMaximum;
	}


	public void setStretchMaximum(int stretchMaximum)
	{
		this.stretchMaximum = stretchMaximum;
	}

	@PdsField(name="stretch_minimum", required=false)
	public int getStretchMinimum()
	{
		return stretchMinimum;
	}


	public void setStretchMinimum(int stretchMinimum)
	{
		this.stretchMinimum = stretchMinimum;
	}

	@PdsField(name="stretched_flag", required=false)
	public boolean isStretchedFlag()
	{
		return stretchedFlag;
	}


	public void setStretchedFlag(boolean stretchedFlag)
	{
		this.stretchedFlag = stretchedFlag;
	}
	
	
}
