/**
 * This package holds all classes for a variety of different
 * receipt types and receipt information that may be useful to
 * the user
 */
package com.farkye.receiptdata;

import java.util.Date;

/**
 * 
 * @author Kwaku Farkye
 *
 * This class provides a template for all types of receipts.
 * Any receipt received by the application shall have the information
 * in this class at the very least.
 *
 */
public abstract class ReceiptObject {

	/* Receipt count */
	private static int idNums = 1;
	
	/* Importance of the receipt */
	
	/* The original time the receipt was received */
	private final Date timeStamp;
	
	/* The receipt information as a string */
	private String receiptText;
	
	/* The Unique identifier ID of the receipt */
	private final int receiptID;
	
	/* Hash ID, this may be unique enough to replace the receipt ID */
	private final int hashID;
	
	/* Flag checking if original receipt info has been set */
	private boolean textSet = false;
	
	/* Flag specifying whether the receipt is important to the user */
	private boolean important = false;
	
	public ReceiptObject() {
		timeStamp = new Date();
		receiptID = idNums++;
		receiptText = "";
		hashID = this.hashCode();
	}
	
	public ReceiptObject(String info) {
		timeStamp = new Date();
		receiptID = idNums++;
		receiptText = info;
		textSet = true;
		hashID = this.hashCode();
	}
	
	/**
	 * Get the timestamp of this receipt
	 * 
	 * @return
	 *  Returns the timestamp for this receipt, in Date format
	 */
	public Date getDate() {
		return timeStamp;
	}
	
	/**
	 * Get the original receipt information
	 * 
	 * @return
	 *  Returns the unmodified, completely original receipt information
	 */
	public String getReceiptText() {
		return receiptText;
	}
	
	/**
	 * Get the ID of the receipt
	 * 
	 * @return
	 *  Returns the unique ID of the receipt
	 */
	public int getReceiptID() {
		return receiptID;
	}
	
	/**
	 * Get the object's hashed index
	 * 
	 * @return
	 * 	The hashcode of the instance of this object
	 */
	public int getUniqID() {
		return hashID;
	}
	
	/**
	 * Set the original receipt info for this receipt.
	 * The original info may only be set if it has not been set
	 * before.
	 * 
	 * @param info
	 * 	The text to set this receipt's original receipt info to
	 * 
	 *  @return
	 *  	Returns true if the info was set successfully, false if info has
	 *  already been set.
	 */
	public boolean setOrigInfo(String info) { //TODO: Have this throw an exception when  
		if (!textSet) {
			receiptText = info;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sets whether the receipt is important to the user or not
	 * 
	 * @param flag
	 * 
	 */
	public void setImportance(boolean flag) {
		important = flag;
	}
	
	/**
	 * Set the type of receipt this is. //TODO: May want to make this a tag system
	 * @param type
	 * 	The type of the receipt as specified by the user
	 */
	public abstract void setReceiptType(String type);
}
