package io.appium.android.apis.nfc;

import io.appium.android.apis.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

/**
 * An example of how to use the NFC foreground NDEF push APIs.
 */
public class ForegroundNdefPush extends Activity {
    private NfcAdapter mAdapter;
    private TextView mText;
    private NdefMessage mMessage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Initialize NFC adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // Create an NDEF message with a URL
        mMessage = new NdefMessage(NdefRecord.createUri("http://www.android.com"));

        // Set the content view and initialize the TextView
        setContentView(R.layout.foreground_dispatch);
        mText = findViewById(R.id.text);

        if (mAdapter != null) {
            // Show a message for Android 10+ where Android Beam is deprecated
            mText.setText("NFC Beam (Android Beam) is deprecated on this device.\nPlease use an alternative method.");
        } else {
            // NFC is not supported on the device
            mText.setText("This phone is not NFC enabled.");
        }
    }
}