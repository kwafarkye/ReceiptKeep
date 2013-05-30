/**
 * 
 */
package com.farkye.receiptkeep;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Kwaku
 *
 */
public class ViewReceipts extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_receipts);
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
