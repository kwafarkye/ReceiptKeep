/**
 * 
 */
package com.farkye.receiptkeep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


import com.farkye.receiptdatabase.*;

/**
 * @author Kwaku
 *
 */
public class ViewReceipts extends Activity {

	private Context context;
	private ReceiptDB rdb;
	private ListView receiptList;
	SimpleCursorAdapter cAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_receipts);
		
		rdb = ((ReceiptKeepApplication)getApplication()).rdb; 
		
		try {
		   rdb.open();
		} catch (SQLException ex) {
			//DO Something
		}
		
		
		Cursor receipts = rdb.getAllReceipts();
		receiptList = (ListView)findViewById(R.id.receipt_view);
		
		String[] from = {"receiptText"};
		int [] to = new int[] {android.R.id.text1};
		
		while (receipts.isAfterLast() == false) {
			receipts.moveToNext();
		}
		
		cAdapter = new SimpleCursorAdapter(this, 
				android.R.layout.simple_list_item_1, receipts, from, to,
				0);
		
		receiptList.setAdapter(cAdapter);
		
		receiptList.setOnItemClickListener(new OnItemClickListener() {		
			@Override public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = ((TextView)view).getText().toString();
				
				Toast.makeText(getApplicationContext(), 
						"Selected Item: " + item, Toast.LENGTH_SHORT)
						.show();
			}

		});
		
		
		receiptList.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Yes button clicked
							rdb.deleteReceipt(receiptList.getSelectedItemId());
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							//No button clicked
							break;
						}
						
					}
				};
				
				/*
				final AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Delete this receipt?").setPositiveButton("Yes", dialogClickListener)
				 .setNegativeButton("No", dialogClickListener).show();
				*/
				Toast.makeText(getApplicationContext(), 
						"This is where the options will go", Toast.LENGTH_SHORT)
						.show();
				
				return true;
			}
		});
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
