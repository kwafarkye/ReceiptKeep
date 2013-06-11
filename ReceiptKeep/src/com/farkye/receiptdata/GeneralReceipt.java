/**
 * 
 */
package com.farkye.receiptdata;

/**
 * @author Kwaku
 *
 * This class represents a general receipt that doesnt have
 * a specific type. It is given the receipt type of RECEIPT_GENERAL
 *
 */
public class GeneralReceipt extends ReceiptObject {

	private String receiptType;
	
	public GeneralReceipt(String text)
	{
		super(text);
	}
	
	public GeneralReceipt()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see com.farkye.receiptdata.ReceiptObject#setReceiptType(java.lang.String)
	 */
	@Override
	public void setReceiptType(String type) {
		// TODO Auto-generated method stub
		this.receiptType = type;

	}

}
