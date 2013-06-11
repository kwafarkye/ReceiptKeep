/**
 * 
 */
package com.farkye.receiptkeep;

import com.farkye.receiptdatabase.ReceiptDB;

import android.app.Application;
import android.database.SQLException;

/**
 * @author Kwaku
 * 
 * This application class stores all global variables
 * and other necessary things that need 
 *
 */
public class ReceiptKeepApplication extends Application {

	/* The SQLite Receipt Database */
	protected ReceiptDB rdb = new ReceiptDB(this);
	
	/**
	 * 
	 */
	public ReceiptKeepApplication() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate()
	{
		try {
		   rdb.open();
		} catch (SQLException ex) {
			//DO Something
		}
		
		super.onCreate();
	}
	
	@Override
	public void onTerminate()
	{
		super.onTerminate();
	}
}
