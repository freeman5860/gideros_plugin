package com.freeman.samsungplugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent serviceIntent = new Intent("com.sec.android.iap.service.iapService");
		Boolean flag = getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty();
		if(flag){
			// the IAP is not installed
			Toast.makeText(this, "the IAP is not installed", Toast.LENGTH_LONG).show();
		}else{
			// the IAP is installed
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
