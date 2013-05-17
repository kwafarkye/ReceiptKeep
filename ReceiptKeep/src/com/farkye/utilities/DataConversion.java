/**
 * Data conversion class used for manipulating data
 */
package com.farkye.utilities;

/**
 * This class offers methods for data conversion (hex values to string, etc..)
 * Most methods in this class will be static, there should be no need to make an
 * instance of this class.
 * 
 * @author Kwaku Farkye
 *
 */
public class DataConversion {
	
	/**
	 * 
	 * @param str
	 * 	String argument to be converted to hex value
	 * 
	 * @return
	 * 	Returns a string of hex values
	 * 
	 * This method is based on mkyong's example class:
	 * 	http://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
	 * 
	 */
	public static String stringToHex(String str) {
		char[] charArray = str.toCharArray();
		
		StringBuffer hexBuf = new StringBuffer();
		for (int i = 0; i < charArray.length; i++) {
			hexBuf.append(Integer.toHexString((int)charArray[i]));
		}
		
		return hexBuf.toString();
	}
	
	/**
	 * 
	 * @param hex
	 * 	Hex value to be converted to ASCII string
	 * 
	 * @return
	 * 	An ASCII representation of the hex value.
	 * 
	 * This method is based on mkyong's example class:
	 * 	http://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
	 * 
	 */
	public static String hexToString(String hex) {
		StringBuilder string = new StringBuilder();
		
		for (int i = 0; i < hex.length() - 1; i++) {
			String output = hex.substring(i, (i+2));
			int decimal = Integer.parseInt(output, 16);
			string.append((char)decimal);
		}
		
		return string.toString();
	}
	
	/**
	 * 
	 * @param bytes
	 * 	The byte array that will be converted to hex
	 * 
	 * @return
	 * 	Returns an ASCII string representation of they byte array
	 */
	public static String bytesToASCIIString(byte[] bytes) {
		String text = "";
		try {
			text = new String(bytes, 0, bytes.length, "ASCII");
		} catch (Exception ex) {
			//TODO: Should do something here
		}
		
		return text;	
	}
}
