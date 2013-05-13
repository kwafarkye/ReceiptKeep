package com.farkye.receiptkeep;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final int RECEIVE_TAB = 1;
	private static final int REPORT_TAB = 2;
	
	NfcAdapter nfcAdapter;
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mNdefExchangeFilters;
	IntentFilter[] mMifareFilters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		
		//Initialize NFC and add Intent
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null)
		{
			Toast.makeText(this, "No NFC Adapter, Maybe try Bluetooth",
					Toast.LENGTH_LONG).show();
		}
		
		mNfcPendingIntent = PendingIntent.getActivity(this, 0,
			new Intent(
					this, 
					this.getClass())
			.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
			0);
				
				
		//Intent filters for reading a note from a tag or exchanging over p2p
		IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException ex) { }
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };
				
		//Intent filter for reading a mifare classic card/data
		IntentFilter mifareDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		mMifareFilters = new IntentFilter[] { mifareDetected };
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		toast("App Resumed");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		toast("App Paused");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		//NDEF Exchange
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			vibrate(500); //Vibrate for half a second
			NdefMessage[] msgs = getNdefData(intent);
			if (msgs == null) {
				return;
			}
			else {
				Toast.makeText(this, "There are messages",
						Toast.LENGTH_LONG).show();
			}
		}
		
		//Mifare Classic Mode
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))
		{
			vibrate(1000); //Vibrate for a second
			Toast.makeText(this, "Mifare Type received",
					Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

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

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.TOP);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			switch (getArguments().getInt(ARG_SECTION_NUMBER))
			{
				case (RECEIVE_TAB):
					textView.append("This is the receive a receipt tab");
					break;
				case (REPORT_TAB):
					textView.append("This is the report analysis tab");
					break;
				default:
					textView.append("This is the default tab. I am goooood");
					break;
			}
			return textView;
		}
	}

	protected NdefMessage[] getNdefData(Intent intent) {
		NdefMessage[] messages = null;
		String action = intent.getAction();
		
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
				NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
				NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
					NfcAdapter.EXTRA_NDEF_MESSAGES);
			messages = getMessages(rawMsgs);
		}
		else {
			Toast.makeText(this, "Not sure which nfc type this is",
					Toast.LENGTH_LONG).show();
		}
		
		return messages;
	}

	private NdefMessage[] getMessages(Parcelable[] rawMsgs) {
		NdefMessage[] msgs = null;
		if (rawMsgs != null) {
			msgs = new NdefMessage[rawMsgs.length];
			
			for (int i = 0; i < rawMsgs.length; i++) {
				msgs[i] = (NdefMessage) rawMsgs[i];
			}
			
			return msgs;
		}
		else {
			byte[] empty = new byte[] {};
			NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty,
					empty, empty);
			NdefMessage msg = new NdefMessage(new NdefRecord[] {
					record
			});
			msgs = new NdefMessage[] {msg};
			
			return msgs;
		}
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
