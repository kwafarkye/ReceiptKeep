package com.farkye.receiptkeep;

import java.io.IOException;

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
import android.nfc.*;
import android.nfc.tech.*;

import com.farkye.utilities.*;



public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final int RECEIVE_TAB = 1;
	private static final int REPORT_TAB = 2;
	
	private static TextView receiveTab; //Text View of receive activity tab
	private static TextView reportTab; //Text View of report activity tab
	
	NfcAdapter nfcAdapter;
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mNdefExchangeFilters;
	IntentFilter[] mMifareFilters;
	String[][] mTechLists;
	
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
		//receiveTab.append("\nIn Create");
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
		
		try {
			mifareDetected.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			
			throw new RuntimeException("fail", e);
		}
		IntentFilter someTag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		mMifareFilters = new IntentFilter[] { mifareDetected, someTag };
		if (mMifareFilters != null) {
			toast("Filter created");
		}
		mTechLists = new String[][] {new String[] {MifareClassic.class.getName(),
				NfcA.class.getName(), NfcB.class.getName(), NfcF.class.getName(),
				NfcV.class.getName(), IsoDep.class.getName(), Ndef.class.getName(),
				NdefFormatable.class.getName()} };
		Intent intent = getIntent();
		resolveIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		nfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mMifareFilters, mTechLists);
		receiveTab.append("\nAdded new mifare intent filter");
		Intent intent = getIntent();
		resolveIntent(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
		toast("App Paused");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		toast("INTENT");
		//NDEF Exchange
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
		{
			//vibrate(500); //Vibrate for half a secondb.
			receiveTab.append("\nWe Discovered an NDEsF tag");
			NdefMessage[] msgs = getNdefData(intent);
			if (msgs == null) {
				receiveTab.append("\nThere are NO messages");
				return;
			}
			else {
				receiveTab.append("\nThere ARE MESSAGES");
				Toast.makeText(this, "There are messages",
						Toast.LENGTH_LONG).show();
			}
		}
		else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			receiveTab.append("\nNew  Card found");
			resolveIntent(intent); //From example on mifareclassic..blogspot
		}
		else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			receiveTab.append("\nTag found from card");
			resolveIntent(intent);
		}
		
		//Mifare Classic Mode
		/*
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))
		{
			vibrate(1000); //Vibrate for a second
			Toast.makeText(this, "Mifare Type received",
					Toast.LENGTH_LONG).show();
		}
		*/
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
					receiveTab = textView; //Assign this tab a text view
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
	
	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		toast("resolving intent");
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			toast("RESOLVE THIS INTENT");
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			receiveTab.append("\nResolve Intent for NDEF discovery");
		}
		else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			byte[] data;
			receiveTab.append("\nNew tech tag received");
			try {
				mfc.connect();
				boolean auth = false;
				String cardData = null;
				int secCount = mfc.getSectorCount();
				int bCount = 0;
				int bIndex = 0;
				for (int j=0; j < secCount; j++) {
					auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
					if (auth) {
						bCount = mfc.getBlockCountInSector(j);
						bIndex = 0;
						for (int i =0; i < bCount; i++) {
							bIndex = mfc.sectorToBlock(j);
							data = mfc.readBlock(bIndex);
							cardData = new String(data);
							receiveTab.append("\n" + cardData);
							bIndex++;
						}
					}
					else { Toast.makeText(this, "Auth failed",
							Toast.LENGTH_LONG).show(); 
					}
				}
			} catch (IOException ex) {
				Toast.makeText(this, "IO Error while attempting to read mifare",
						Toast.LENGTH_LONG).show();
			}
		}
		else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			receiveTab.append("\nSome Tag found from card");
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			receiveTab.append("\n" + tagFromIntent.toString());
			for (int k = 0; k < tagFromIntent.getTechList().length; k++) {
				receiveTab.append("\nTech List: " + tagFromIntent.getTechList()[k]);
				if (tagFromIntent.getTechList()[k].equals("android.nfc.tech.NfcA")) {
					// Run connection method
					parseNfcATag(tagFromIntent);
					//connectToNDEF(tagFromIntent);
					//parseMifareTag(tagFromIntent);
				}
				else if (tagFromIntent.getTechList()[k].equals("android.nfc.tech.Ndef")) {
					connectToNDEF(tagFromIntent);
					//receiveTab.append("\nRun connect to NDEF");
				}
			}
		}
	}
	
	private void parseMifareTag(Tag tag) {
		MifareClassic mfc = MifareClassic.get(tag);
		try {
			mfc.connect();
			receiveTab.append("\nThis worked for Mifare");
		}
		catch (IOException ex) {
			receiveTab.append("\nMifare read attempt threw an error");
		}
	}
	
	private void connectToNDEF(Tag tag) {
		Ndef ndef = Ndef.get(tag);
		try {
			receiveTab.append("\n" + ndef.getType());
			NdefMessage message = ndef.getCachedNdefMessage();
			if (message != null) {
				NdefRecord[] recordsFromMessage = message.getRecords();
				for (int i = 0; i < recordsFromMessage.length; i++) {
					printMessage(recordsFromMessage[i]);
				}
			}
			else {
				receiveTab.append("\nNull message");
			}
			receiveTab.append("\nNDEF did work!");
		}
		catch (Exception ex) {
			receiveTab.append("\nNDEF did not work");
		}
	}
	
	private void parseNfcATag(Tag tag) {
		//Get Instance for given tag
		NfcA nfcATag = NfcA.get(tag);
		byte bytesRead[] = null;
		short sakVal;
		byte bytesSent[] = "Kwaku Farkye's tag".getBytes();
		try {
		   nfcATag.connect();
		   
		   if (nfcATag.isConnected()) {
		      bytesRead = nfcATag.getAtqa();
		      sakVal = nfcATag.getSak();
			  //bytesRead = nfcATag.transceive(bytesSent);
			  int maxBytes = nfcATag.getMaxTransceiveLength();
			  receiveTab.append("\nMax Bytes: " + maxBytes);
			  receiveTab.append("\nSak Val is: " + sakVal);
			   //String str;
			   //for (int j = 0; j < bytesRead.length; j++) {
				   //str = new String(bytesRead[j]);
				 //  receiveTab.append("\nByte " + j + " equals " + bytesRead[j]);
			   //}
			  /*
			  Tag t = nfcATag.getTag();
			  Parcel parcel = Parcel.obtain();
			  t.writeToParcel(parcel, 0);
			  bytesRead = parcel.marshall();
			for (int j = 0; j < bytesRead.length; j++) {
			   //str = new String(bytesRead[j]);
			   receiveTab.append("\nByte " + j + " equals " + bytesRead[j]);
		   }
		   */
			   //nfcATag.close();
		   }
		}
		catch (IOException ex) { receiveTab.append("\nError connecting to tag"); }
		
		return;
	}
	
	private void printMessage(NdefRecord record) {
		byte[] id; //Id of the record
		short tnf; //TNF: See android.nfc.NdefRecord
		byte[] payload; //Payload of the record (the actual message)
		String finalMessage, id_string;
		id = record.getId();
		tnf = record.getTnf();
		payload = record.getPayload();
		id_string = DataConversion.bytesToASCIIString(id);
		receiveTab.append("\nID is " + id_string);
		receiveTab.append("\nTNF is " + tnf);
		finalMessage = DataConversion.bytesToASCIIString(payload);
		receiveTab.append("\n" + finalMessage + "\n");
	}
	
	private void vibrate(long time) {
		//Log.d(TAG, "vibrate");
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(time);
	}
}
