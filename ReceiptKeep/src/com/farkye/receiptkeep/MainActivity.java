package com.farkye.receiptkeep;

import java.io.IOException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.nfc.*;
import android.nfc.tech.*;

import com.farkye.utilities.*;
import com.farkye.receiptdata.*;



public class MainActivity extends Activity { //FragmentActivity implements
		//ActionBar.TabListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final int RECEIVE_TAB = 1;
	private static final int REPORT_TAB = 2;
	
	private static TextView receiveTab; //Text View of receive activity tab
	private static TextView reportTab; //Text View of report activity tab
	
	Button addReceiptBtn;
	Button viewReceiptsBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/**
		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.receive_receipt)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section3)
				.setTabListener(this));
		**/
		
		addReceiptBtn = (Button) findViewById(R.id.add);
		viewReceiptsBtn = (Button) findViewById(R.id.view_db);
		
		
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
	protected void onNewIntent(Intent intent) {
		toast("INTENT");
		//NDEF Exchange
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			
		}
		else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			
		}
		else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			
		}
	}
	
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			//getActionBar().setSelectedNavigationItem(
			//		savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		//outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
		//		.getSelectedNavigationIndex());
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

	/**
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		Toast.makeText(this, "New tab selected",
				Toast.LENGTH_SHORT).show();
		// When the given tab is selected, show the tab contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
				tab.getPosition() + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	**/

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	
	//public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
	/**
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setMovementMethod(new ScrollingMovementMethod());
			textView.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			switch (getArguments().getInt(ARG_SECTION_NUMBER))
			{
				case (RECEIVE_TAB):
					//receiveTab = textView; //Assign this tab a text view
					textView.append("This is the receive a receipt tab");
					break;
				case (REPORT_TAB):
					reportTab = textView;
					textView.append("This is the report analysis tab");
					break;
				default:
					textView.append("This is the default tab. I am goooood");
					break;
			}
			return textView;
		}
	}
	**/

	
	public void retrieveReceipt(View view1) {
		Intent retrieve = new Intent(getApplicationContext(), AddReceiptActivity.class);
		vibrate(500);
		
		startActivity(retrieve);
	}
	
	public void viewReceipts(View view1) {
		Intent viewDB = new Intent(getApplicationContext(), ViewReceipts.class);
		vibrate(1000);
		
		startActivity(viewDB);
	}
	
	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	
	private void vibrate(long time) {
		//Log.d(TAG, "vibrate");
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(time);
	}
}
