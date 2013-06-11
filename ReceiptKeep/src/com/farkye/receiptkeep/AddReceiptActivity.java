/**
 * 
 */
package com.farkye.receiptkeep;

import java.io.IOException;

import com.farkye.receiptdata.GeneralReceipt;
import com.farkye.receiptdata.ReceiptObject;
import com.farkye.receiptdatabase.ReceiptDB;
import com.farkye.utilities.DataConversion;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.database.SQLException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity to scan for and add a new receipt
 * 
 * @author Kwaku
 *
 */
public class AddReceiptActivity extends Activity {
	
	TextView receiptText;
	
	
	NfcAdapter nfcAdapter;
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mNdefExchangeFilters;
	IntentFilter[] mMifareFilters;
	String[][] mTechLists;
	ReceiptDB rdb;
	
	private boolean receiptAdded = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.add_receipt);
		
		rdb = ((ReceiptKeepApplication)getApplication()).rdb;
		try {
			   rdb.open();
		} catch (SQLException ex) {
				//DO Something
		}
		
		receiptText = (TextView) findViewById(R.id.receipt_text);
		receiptText.setText("Waiting for a New Receipt...\n");
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
		////receiveTab.append("\nAdded new mifare intent filter");
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
			//vibrate(500); //Vibrate for half a second.
			////receiveTab.append("\nWe Discovered an NDEsF tag");
			NdefMessage[] msgs = getNdefData(intent);
			if (msgs == null) {
				////receiveTab.append("\nThere are NO messages");
				return;
			}
			else {
				////receiveTab.append("\nThere ARE MESSAGES");
				Toast.makeText(this, "There are messages",
						Toast.LENGTH_LONG).show();
			}
		}
		else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			////receiveTab.append("\nNew  Card found");
			resolveIntent(intent); //From example on mifareclassic..blogspot
		}
		else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			////receiveTab.append("\nTag found from card");
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
		//if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			//getActionBar().setSelectedNavigationItem(
			//		savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		//}
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
	
	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		toast("resolving intent");
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			toast("RESOLVE THIS INTENT");
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			////receiveTab.append("\nResolve Intent for NDEF discovery");
		}
		else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			MifareClassic mfc = MifareClassic.get(tagFromIntent);
			byte[] data;
			////receiveTab.append("\nNew tech tag received");
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
							////receiveTab.append("\n" + cardData);
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
			////receiveTab.append("\nSome Tag found from card");
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			
			////receiveTab.append("\n" + tagFromIntent.toString());
			for (int k = 0; k < tagFromIntent.getTechList().length; k++) {
				////receiveTab.append("\nTech List: " + tagFromIntent.getTechList()[k]);
				if (tagFromIntent.getTechList()[k].equals("android.nfc.tech.NfcA")) {
					// Run connection method
					parseNfcATag(tagFromIntent);
					//connectToNDEF(tagFromIntent);
					//parseMifareTag(tagFromIntent);
				}
				else if (tagFromIntent.getTechList()[k].equals("android.nfc.tech.Ndef")) {
					connectToNDEF(tagFromIntent);
					////receiveTab.append("\nRun connect to NDEF");
				}
			}
		}
	}
	
	private void parseMifareTag(Tag tag) {
		MifareClassic mfc = MifareClassic.get(tag);
		try {
			mfc.connect();
			////receiveTab.append("\nThis worked for Mifare");
		}
		catch (IOException ex) {
			////receiveTab.append("\nMifare read attempt threw an error");
		}
	}
	
	private void connectToNDEF(Tag tag) {
		Ndef ndef = Ndef.get(tag);
		try {
			////receiveTab.append("\n" + ndef.getType());
			NdefMessage message = ndef.getCachedNdefMessage();
			if (message != null) {
				NdefRecord[] recordsFromMessage = message.getRecords();
				for (int i = 0; i < recordsFromMessage.length; i++) {
					printMessage(recordsFromMessage[i]);
				}
			}
			else {
				////receiveTab.append("\nNull message");
			}
			////receiveTab.append("\nNDEF did work!");
		}
		catch (Exception ex) {
			////receiveTab.append("\nNDEF did not work");
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
			  ////receiveTab.append("\nMax Bytes: " + maxBytes);
			  ////receiveTab.append("\nSak Val is: " + sakVal);
			   //String str;
			   //for (int j = 0; j < bytesRead.length; j++) {
				   //str = new String(bytesRead[j]);
				 //  //receiveTab.append("\nByte " + j + " equals " + bytesRead[j]);
			   //}
			  /*
			  Tag t = nfcATag.getTag();
			  Parcel parcel = Parcel.obtain();
			  t.writeToParcel(parcel, 0);
			  bytesRead = parcel.marshall();
			for (int j = 0; j < bytesRead.length; j++) {
			   //str = new String(bytesRead[j]);
			   //receiveTab.append("\nByte " + j + " equals " + bytesRead[j]);
		   }
		   */
			   //nfcATag.close();
		   }
		}
		catch (IOException ex) { }////receiveTab.append("\nError connecting to tag"); }
		
		return;
	}
	
	private void printMessage(NdefRecord record) {
		ReceiptObject rObj;
		byte[] id; //Id of the record
		short tnf; //TNF: See android.nfc.NdefRecord
		byte[] payload; //Payload of the record (the actual message)
		String finalMessage, id_string;
		id = record.getId();
		tnf = record.getTnf();
		payload = record.getPayload();
		id_string = DataConversion.bytesToASCIIString(id);
		if (!receiptAdded)
		{ //This receipt hasnt been added yet so lets add it now
			receiptText.append("\nID is: " + id_string);
			receiptAdded = true; // Dont add this receipt again
			////receiveTab.append("\nID is " + id_string);
			////receiveTab.append("\nTNF is " + tnf);
			finalMessage = DataConversion.bytesToASCIIString(payload);
			receiptText.append("\n" + finalMessage + "\n");
			rObj = new GeneralReceipt(finalMessage);
			rObj.setReceiptType(ReceiptObject.generalType);
			rdb.insertReceipt(rObj.getReceiptID(), rObj.getReceiptText());
			
			////receiveTab.append("\n" + finalMessage + "\n");
		}
		else
			receiptText.append("\nThis receipt has already been received\n");
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
}
