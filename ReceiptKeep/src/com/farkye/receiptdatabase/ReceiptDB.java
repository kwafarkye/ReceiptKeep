/**
 *
 */
package com.farkye.receiptdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.util.Log;

/**
 * This is a SQLite Database Adapter Class for storing receipts
 * into a SQLite Database.
 * 
 * A portion of this code is based on the tutorial here:
 * http://www.devx.com/wireless/Article/40842
 * 
 * @author Kwaku Farkye
 *
 */
public class ReceiptDB {

	public static final String KEY_ID = "_id";
	public static final String KEY_ReceiptID = "receiptID";
	public static final String KEY_ReceiptText = "receiptText";
	public static final String KEY_ReceiptObject = "receiptObject";
	private static final String TAG = "ReceiptDB";
	
	private static final String DATABASE_NAME = "receipts";
	private static final String DATABASE_TABLE = "receiptTable";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = 
			"create table receiptTable (_id integer primary key autoincrement, "
			+ "receiptID integer not null, receiptText text not null );";
	
	private final Context context;
	
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public ReceiptDB(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper (Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS receiptTable");
			onCreate(db);
		}
	}
	
	/**
	 * Open the database for updating/reading/writing
	 *
	 */
	public ReceiptDB open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Close the database
	 * 
	 */
	public void close() {
		DBHelper.close();
	}
	
	/**
	 * Inserts a receipt into the SQLite Database
	 * 
	 * @param receiptID
	 *  The unique ID of the receipt (Not the unique id in the table)
	 *  
	 * @param receiptText
	 *   The text of the receipt
	 *   
	 * @return
	 */
	public long insertReceipt(int receiptID, String receiptText) {
		ContentValues values = new ContentValues();
		values.put(KEY_ReceiptID, receiptID);
		values.put(KEY_ReceiptText, receiptText);
		
		return db.insert(DATABASE_TABLE, null, values);
	}
	
	/**
	 * Deletes a receipt, specified by its row id
	 * 
	 * @param rowNum
	 *  The row that this receipt is contained in the database
	 *  
	 * @return
	 *  Whether the attempt to delete was successful or not
	 */
	public boolean deleteReceipt(long rowNum) {
		return db.delete(DATABASE_TABLE, KEY_ID, new String[] {"=",String.valueOf(rowNum) }) > 0;	
	}
	
	/**
	 * Retrieves all of the receipts in the database
	 * 
	 * @return
	 *  Cursor with all the receipts in it
	 */
	public Cursor getAllReceipts() {
		return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_ReceiptText}, 
				null, null, null, null, null);
	}
}
