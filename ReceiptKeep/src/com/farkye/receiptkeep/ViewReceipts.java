/**
 * 
 */
package com.farkye.receiptkeep;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


import com.farkye.receiptdatabase.*;

/**
 * @author Kwaku
 *
 */
public class ViewReceipts extends Activity {

	private Context context;
	private ReceiptDB rdb;
	private ListView receiptList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_receipts);
		
		rdb = new ReceiptDB(this); 
		
		try {
		   rdb.open();
		} catch (SQLException ex) {
			//DO Something
		}
		
		rdb.insertReceipt(1, "This is receipt 1");
		rdb.insertReceipt(2, "This is receipt 2");
		rdb.insertReceipt(3, "Kwaku's Receipt");
		
		
		Cursor receipts = rdb.getAllReceipts();
		receiptList = (ListView)findViewById(R.id.receipt_view);
		
		String[] from = {"receiptText"};
		int [] to = new int[] {android.R.id.text1};
		
		while (receipts.isAfterLast() == false) {
			receipts.moveToNext();
		}
		
		SimpleCursorAdapter cAdapter = new SimpleCursorAdapter(this, 
				android.R.layout.simple_list_item_1, receipts, from, to,
				0);
		//		R.layou
		//R.layout.view_receipts.setAdapter(cAdapter);
		
		receiptList.setAdapter(cAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.menu_settings:
				//Navigate to settings view
				//
				return true;
			case R.id.menu_save:
				//Run a save service
				//Notification: Would you like to store this receipt?
				return true;
		}
		super.onOptionsItemSelected(item);
		////receiveTab.append("\n"+item.getTitle());
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
