package us.wthr.jdem846.nasa.pds.objects;

import us.wthr.jdem846.nasa.pds.annotations.PdsObject;

@PdsObject(name="file", aliases={"uncompressed_file"})
public class PdsFile
{
	
	private int fileRecords;
	private String recordType;
	private String fileName;
	private int labelRecords;
	private int recordBytes;
	private int sequenceNumber;
	
	public PdsFile()
	{
		
	}

	public int getFileRecords()
	{
		return fileRecords;
	}

	public void setFileRecords(int fileRecords)
	{
		this.fileRecords = fileRecords;
	}

	public String getRecordType()
	{
		return recordType;
	}

	public void setRecordType(String recordType)
	{
		this.recordType = recordType;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public int getLabelRecords()
	{
		return labelRecords;
	}

	public void setLabelRecords(int labelRecords)
	{
		this.labelRecords = labelRecords;
	}

	public int getRecordBytes()
	{
		return recordBytes;
	}

	public void setRecordBytes(int recordBytes)
	{
		this.recordBytes = recordBytes;
	}

	public int getSequenceNumber()
	{
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber)
	{
		this.sequenceNumber = sequenceNumber;
	}
	
	
	
	
	
}
