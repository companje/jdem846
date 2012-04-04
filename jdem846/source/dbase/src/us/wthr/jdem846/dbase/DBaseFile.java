/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.dbase;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import us.wthr.jdem846.dbase.exception.DBaseException;
import us.wthr.jdem846.util.ByteConversions;

public class DBaseFile
{
	
	private DBaseLastUpdate lastUpdate;
	private int numRecords;
	private int numHeaderBytes;
	private int numRecordBytes;
	
	private int numFields;
	
	private int firstRecordOffset;
	
	private File dbaseFile;
	private RandomAccessFile file;
	
	private int fieldDescriptorCount;
	
	private DBaseFieldDescriptor[] fieldDescriptors;
	//private DBaseRecord[] records;
	
	
	public DBaseFile(String filePath) throws DBaseException
	{
		dbaseFile = new File(filePath);
		if (!dbaseFile.exists())
			throw new DBaseException(filePath, "dBase file does not exist");
		
		try {
			file = new RandomAccessFile(dbaseFile, "r");
		} catch (Exception ex) {
			throw new DBaseException(filePath, "Error opening dBase file for reading", ex);
		}
		
		try {
			file.seek(1);
			int year = 1900 + file.readUnsignedByte();
			int month = file.readUnsignedByte();
			int day = file.readUnsignedByte();
			lastUpdate = new DBaseLastUpdate(year, month, day);
			
			file.seek(4);
			byte[] buffer4 = new byte[4];
			byte[] buffer2 = new byte[2];
			
			file.readFully(buffer4);
			numRecords = ((buffer4[3] & 0xff) << 24) |
				((buffer4[2] & 0xff) << 16) |
				((buffer4[1] & 0xff) << 8) |
				(buffer4[0] & 0xff);
			
			file.readFully(buffer2);
			numHeaderBytes = (0x0 << 24) | (0x0 << 16) | ((buffer2[1] & 0xFF) << 8) | (buffer2[0] & 0xFF);
			
			file.readFully(buffer2);
			numRecordBytes = (0x0 << 24) | (0x0 << 16) | ((buffer2[1] & 0xFF) << 8) | (buffer2[0] & 0xFF);
			
			
			fieldDescriptorCount = 0;
			
			for (int offset = 32; ; offset += 32) {
				fieldDescriptorCount++;
				char term = 0x0;
				
				file.seek(offset + 32);
				term = (char) file.readUnsignedByte();
				
				if (term == DBaseConstants.DBASE_FIELD_DESCRIP_TERM) {
					break;
				}
			}
			
			fieldDescriptors = new DBaseFieldDescriptor[fieldDescriptorCount];
			
			for (int i = 0; i < fieldDescriptorCount; i++) {
				int offset = 32 + (i * 32);
				DBaseFieldDescriptor fieldDescriptor = this.readFieldDescriptor(offset);
				fieldDescriptors[i] = fieldDescriptor;
			}
			
			numFields = fieldDescriptorCount;
			//records = new DBaseRecord[numRecords];
			firstRecordOffset = 32 + (fieldDescriptorCount * 32);
			//for (int i = 0;  i < numRecords; i++) {
			//	int recordOffset = firstRecordOffset + (i * numRecordBytes);
			//	DBaseRecord record = readRecord(recordOffset);
			//	records[i] = record;
			//}
		} catch (Exception ex) {
			throw new DBaseException(filePath, "Error when reading from dBase file", ex);
		}
		
	}
	
	public void close() throws DBaseException
	{
		try {
			if (file != null) {
				file.close();
				file = null;
			}
		} catch (Exception ex) {
			throw new DBaseException("Error when closing dBase file", ex);
		}
	}

	public DBaseRecord getRecord(int index) throws DBaseException
	{
		int recordOffset = firstRecordOffset + (index * numRecordBytes);
		DBaseRecord record = readRecord(recordOffset);
		return record;
		//return records[index];
	}
	
	public DBaseFieldDescriptor getFieldDescriptor(int index)
	{
		return fieldDescriptors[index];
	}
	
	protected DBaseFieldDescriptor readFieldDescriptor(int offset) throws DBaseException
	{
		DBaseFieldDescriptor fieldDescriptor = new DBaseFieldDescriptor();
		
		try {
			file.seek(offset);
			
			byte[] buffer10 = new byte[10];
			file.readFully(buffer10);
			fieldDescriptor.setName((new String(buffer10)).replace((char)0x00, ' ').trim());
			
			file.seek(offset + 11);
			byte[] buffer1 = new byte[1];
			file.readFully(buffer1);
			fieldDescriptor.setType(buffer1[0]);
			
			file.seek(offset + 12);
			byte[] buffer4 = new byte[4];
			file.readFully(buffer4);
			int displacement = ((buffer4[0] & 0xFF) << 24) | ((buffer4[1] & 0xFF) << 16) | ((buffer4[2] & 0xFF) << 8) | (buffer4[3] & 0xFF);
			fieldDescriptor.setDisplacement(displacement);
			
			file.seek(offset + 16);
			file.readFully(buffer1);
			int length = ByteConversions.byteToInt(buffer1[0]);
			fieldDescriptor.setLength(length);
			
			file.seek(offset + 17);
			file.readFully(buffer1);
			fieldDescriptor.setDecCount(buffer1[0]);
			
			//System.out.println("Field Name: " + fieldDescriptor.getName());
			//System.out.println("Field Type: " + fieldDescriptor.getType());
			//System.out.println("Displacement: " + fieldDescriptor.getDisplacement());
			//System.out.println("Field Length (bytes): " + fieldDescriptor.getLength());
			//System.out.println("Field decimal places: " + fieldDescriptor.getDecCount());
			
			return fieldDescriptor;
		} catch (Exception ex) {
			throw new DBaseException("Error reading record from dBase file", ex);
		}
	}
	
	protected DBaseRecord readRecord(int offset) throws DBaseException
	{
		DBaseRecord record = new DBaseRecord(numFields);
		
		try {
			int fieldOffset = offset + 2;
			for (int f = 0; f < numFields; f++) {
				DBaseFieldDescriptor fieldDescriptor = fieldDescriptors[f];
				DBaseField field = readField(fieldOffset, fieldDescriptor);
				record.setField(f, field);
				record.setFieldDescriptor(f, fieldDescriptor);
				fieldOffset += fieldDescriptor.getLength();
			}
		} catch (Exception ex) {
			throw new DBaseException("Error reading record from dBase file", ex);
		}
		
		return record;
	}
	
	protected DBaseField readField(int offset, DBaseFieldDescriptor fieldDescriptor) throws DBaseException
	{
		DBaseField field = new DBaseField();
		field.setType(fieldDescriptor.getType());
		
		try {
			byte[] buffer = new byte[fieldDescriptor.getLength()];
			file.seek(offset);
			file.readFully(buffer);
			
			String sBuffer = new String((new String(buffer)).replace((char)0x0, ' ').trim());
			
			switch (field.getType()) {
			case DBaseConstants.DBASE_TYPE_CHAR:
				field.setStringValue(sBuffer);
				break;
			case DBaseConstants.DBASE_TYPE_NUMERIC:
				if (sBuffer != null && sBuffer.length() > 0)
					field.setIntegerValue(Integer.parseInt(sBuffer));
				break;
			case DBaseConstants.DBASE_TYPE_FLOAT:
				if (sBuffer != null && sBuffer.length() > 0) 
					field.setFloatValue(Float.parseFloat(sBuffer));
				break;
			case DBaseConstants.DBASE_TYPE_DATE:
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				try {
					Date date = sdf.parse(sBuffer);
					field.setDateValue(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
	
				break;
			case DBaseConstants.DBASE_RECORD_DELETED:
				// Awww, damn...
				break;
			case DBaseConstants.DBASE_RECORD_ACTIVE:
				// Isn't that nice
				break;
			default:
				//System.out.println("Unsupported field type: " + field.getType() + ", name: " + fieldDescriptor.getName());
			}
			
			return field;
		} catch (Exception ex) {
			throw new DBaseException("Error reading field from dBase file", ex);
		}
	}
	
	
	

	public DBaseLastUpdate getLastUpdate()
	{
		return lastUpdate;
	}


	public void setLastUpdate(DBaseLastUpdate lastUpdate)
	{
		this.lastUpdate = lastUpdate;
	}


	public int getNumRecords()
	{
		return numRecords;
	}


	public void setNumRecords(int numRecords)
	{
		this.numRecords = numRecords;
	}


	public int getNumHeaderBytes()
	{
		return numHeaderBytes;
	}


	public void setNumHeaderBytes(int numHeaderBytes)
	{
		this.numHeaderBytes = numHeaderBytes;
	}


	public int getNumRecordBytes()
	{
		return numRecordBytes;
	}


	public void setNumRecordBytes(int numRecordBytes)
	{
		this.numRecordBytes = numRecordBytes;
	}


	public int getNumFields()
	{
		return numFields;
	}


	public void setNumFields(int numFields)
	{
		this.numFields = numFields;
	}


	public File getDbaseFile()
	{
		return dbaseFile;
	}


	public void setDbaseFile(File dbaseFile)
	{
		this.dbaseFile = dbaseFile;
	}

	
	public int getFieldDescriptorCount()
	{
		return fieldDescriptorCount;
	}

	public void setFieldDescriptorCount(int fieldDescriptorCount)
	{
		this.fieldDescriptorCount = fieldDescriptorCount;
	}

	public DBaseFieldDescriptor[] getFieldDescriptors()
	{
		return fieldDescriptors;
	}


	public void setFieldDescriptors(DBaseFieldDescriptor[] fieldDescriptors)
	{
		this.fieldDescriptors = fieldDescriptors;
	}


	
	
}
