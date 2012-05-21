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

package us.wthr.jdem846.util;

import us.wthr.jdem846.ByteOrder;

/** Provides a set of common byte-wise manipulation and datatype conversion operations.
 * 
 * @author Kevin M. Gill
 *
 */
public class ByteConversions
{
	public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.LSBFIRST;
	
	protected ByteConversions()
	{
		
	}
	
	/** Converts a 4 byte array to a float using the default byte order.
	 * 
	 * @param bytes A 4 byte array
	 * @return
	 */
	public static float bytesToFloat(byte[] bytes)
	{
		return bytesToFloat(bytes,  DEFAULT_BYTE_ORDER);
	}
	
	/** Converts a 4 byte array to a float using the specified byte order.
	 * 
	 * @param bytes A 4 byte array
	 * @param byteOrder
	 * @return
	 */
	public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder)
	{
		return bytesToFloat(bytes[0], bytes[1], bytes[2], bytes[3], byteOrder);
	}
	
	/** Converts 4 bytes to a float using the specified byte order.
	 * 
	 * @param b00
	 * @param b01
	 * @param b10
	 * @param b11
	 * @param byteOrder
	 * @return
	 */
	public static float bytesToFloat(byte b00, byte b01, byte b10, byte b11, ByteOrder byteOrder)
	{
		int intBits = 0;
		if (byteOrder == ByteOrder.MSBFIRST) {
			intBits = ((b00 & 0xFF) << 24) |
				((b01 & 0xFF) << 16) |
				((b10 & 0xFF) << 8) |
				(b11 & 0xFF);
		} else if (byteOrder == ByteOrder.LSBFIRST || byteOrder == ByteOrder.INTEL_BYTE_ORDER) {
			intBits = ((b11 & 0xFF) << 24) |
				((b10 & 0xFF) << 16) |
				((b01 & 0xFF) << 8) |
				(b00 & 0xFF);
		} else if (byteOrder == ByteOrder.INTEL_OR_MOTOROLA) {
			intBits = ((b10 & 0xFF) << 24) |
				((b11 & 0xFF) << 16) |
				((b00 & 0xFF) << 8) |
				(b01 & 0xFF);
		}
		
		return Float.intBitsToFloat(intBits);

	}
	
	/** Translates the byte to a two-character hexidecimal string representation.
	 * 
	 * @param b00
	 * @return
	 */
	public static String toHex(byte b00)
	{
		String s = Integer.toString(b00 & 0xFF, 16).toUpperCase();
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
	
	/** Converts a float to a 4 byte array using the default byte order.
	 * 
	 * @param value
	 * @return A 4 byte array
	 */
	public static byte[] floatToBytes(float value)
	{
		return floatToBytes(value, DEFAULT_BYTE_ORDER);
	}
	
	/** Converts a float to a 4 byte array using the specified byte order.
	 * 
	 * @param value
	 * @param byteOrder
	 * @return A 4 byte array/
	 */
	public static byte[] floatToBytes(float value, ByteOrder byteOrder)
	{
		int bits = Float.floatToIntBits(value);

		byte[] buffer = null;
		
		if (byteOrder == ByteOrder.MSBFIRST) {
			buffer = new byte[] {
                (byte)(bits >>> 24),
                (byte)(bits >>> 16),
                (byte)(bits >>> 8),
                (byte)bits};
		} else {
			buffer = new byte[] {
	                (byte)(bits),
	                (byte)(bits >>> 8),
	                (byte)(bits >>> 16),
	                (byte)(bits >>> 24)};
		}
		
		return buffer;
	}
	
	/** Converts a float to a 4 byte array using the specified byte order.
	 * 
	 * @param value
	 * @param byteOrder
	 * @return A 4 byte array/
	 */
	public static byte[] doubleToBytes(double value)
	{
		return doubleToBytes(value, DEFAULT_BYTE_ORDER);
	}
	
	/** Converts a float to a 4 byte array using the specified byte order.
	 * 
	 * @param value
	 * @param byteOrder
	 * @return A 4 byte array/
	 */
	public static byte[] doubleToBytes(double value, ByteOrder byteOrder)
	{

		long bits = Double.doubleToLongBits(value);
		
		byte[] buffer = null;
		
		if (byteOrder == ByteOrder.MSBFIRST) {
			buffer = new byte[] {
				(byte)(bits >>> 56),
				(byte)(bits >>> 48),
				(byte)(bits >>> 40),
				(byte)(bits >>> 32),
                (byte)(bits >>> 24),
                (byte)(bits >>> 16),
                (byte)(bits >>> 8),
                (byte)bits};
		} else {
			buffer = new byte[] {
	                (byte)(bits),
	                (byte)(bits >>> 8),
	                (byte)(bits >>> 16),
	                (byte)(bits >>> 24),
	                (byte)(bits >>> 32),
	                (byte)(bits >>> 40),
	                (byte)(bits >>> 48),
	                (byte)(bits >>> 56)
			};
		}
		
		return buffer;
	}
	
	public static byte[] intToBytes(int value)
	{
		return intToBytes(value, DEFAULT_BYTE_ORDER);
	}

	public static byte[] intToBytes(int bits, ByteOrder byteOrder)
	{

		byte[] buffer = null;
		
		if (byteOrder == ByteOrder.MSBFIRST) {
			buffer = new byte[] {
                (byte)(bits >>> 24),
                (byte)(bits >>> 16),
                (byte)(bits >>> 8),
                (byte)bits};
		} else {
			buffer = new byte[] {
	                (byte)(bits),
	                (byte)(bits >>> 8),
	                (byte)(bits >>> 16),
	                (byte)(bits >>> 24)};
		}
		
		return buffer;
	}
	
	/** Converts a 4 byte array to an integer using the default byte order.
	 * 
	 * @param bytes A 4 byte array.
	 * @return
	 */
	public static int bytesToInt(byte[] bytes)
	{
		return bytesToInt(bytes, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	
	/** Converts a 4 byte array to an integer using the specified byte order.
	 * 
	 * @param bytes A 4 byte array.
	 * @param byteOrder
	 * @return
	 */
	public static int bytesToInt(byte[] bytes, ByteOrder byteOrder)
	{
		return bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3], byteOrder);
	}
	
	/** Converts 4 bytes to an integer using the default byte order.
	 * 
	 * @param b00
	 * @param b01
	 * @param b10
	 * @param b11
	 * @return
	 */
	public static int bytesToInt(byte b00, byte b01, byte b10, byte b11)
	{
		return bytesToInt(b00, b01, b10, b11, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	
	/** Translates a single byte (considered unsigned) into an integer.
	 * 
	 * @param b00
	 * @return
	 */
	public static int byteToInt(byte b00)
	{
		int intBits = 0;
		intBits = (b00 & 0xFF);
		return intBits;
	}
	
	/** Converts the bytes to an integer using the specified byte order.
	 * 
	 * @param b00
	 * @param b01
	 * @param b10
	 * @param b11
	 * @param byteOrder
	 * @return
	 */
	public static int bytesToInt(byte b00, byte b01, byte b10, byte b11, ByteOrder byteOrder)
	{
		int intBits = 0;
		if (byteOrder == ByteOrder.MSBFIRST) {
			intBits = ((b11 & 0xFF) << 24) |
				((b10 & 0xFF) << 16) |
				((b01 & 0xFF) << 8) |
				(b00 & 0xFF);
		} else if (byteOrder == ByteOrder.LSBFIRST || byteOrder == ByteOrder.INTEL_BYTE_ORDER) {
			intBits = ((b00 & 0xFF) << 24) |
				((b01 & 0xFF) << 16) |
				((b10 & 0xFF) << 8) |
				(b11 & 0xFF);
		}
		return intBits;
	}
	
	/** Converts an 8 byte array to a double using the default byte order.
	 * 
	 * @param bytes
	 * @return
	 */
	public static double bytesToDouble(byte[] bytes)
	{
		return bytesToDouble(bytes, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	
	/** Converts an 8 byte array to a double using the specified byte order.
	 * 
	 * @param bytes
	 * @param byteOrder
	 * @return
	 */
	public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder)
	{
		return Double.longBitsToDouble(bytesToLong(bytes, byteOrder));
	}
	
	/** Converts an 8 byte array to a long using the default byte order.
	 * 
	 * @param bytes
	 * @return
	 */
	public static long bytesToLong(byte[] bytes)
	{
		return bytesToLong(bytes, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	
	/** Converts an 8 byte array to a long using the specified byte order.
	 * 
	 * @param bytes
	 * @param byteOrder
	 * @return
	 */
	public static long bytesToLong(byte[] bytes, ByteOrder byteOrder)
	{
		long longBits = 0;
		
		if (byteOrder == ByteOrder.MSBFIRST) {
			for(int i =0; i < 8; i++){      
				longBits <<= 8;  
				longBits ^= (long)bytes[i] & 0xFF;      
			}  
		} else if (byteOrder == ByteOrder.LSBFIRST || byteOrder == ByteOrder.INTEL_BYTE_ORDER) {
			for(int i = 7; i >= 0; i--){      
				longBits <<= 8;  
				longBits ^= (long)bytes[i] & 0xFF;      
			}  
		}
		return longBits;

	}	
	
	
}
