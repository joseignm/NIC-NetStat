package cl.niclabs.netstatapp.ui;

import java.util.ArrayList;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;

import cl.niclabs.netstatapp.R;
import cl.niclabs.netstatapp.core.Connection;
import cl.niclabs.netstatapp.core.NetStat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_layout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case R.id.menu_update:
	        	if(!isDeviceOnline()) {
	        		Toast.makeText(this, getString(R.string.no_net), Toast.LENGTH_LONG).show();
	        		return true;
	        	}
	        	getUsername();
	        	NetStat ns = new NetStat();
	        	ArrayList<Connection> connections = ns.getConnections(this);
	        	ConnectListAdapter adapter = new ConnectListAdapter(this, connections);
	        	ListView nslist = (ListView)findViewById(R.id.nslist);
	        	nslist.setAdapter(adapter);
	        	Context context = MainActivity.this;
	        	nslist.setOnItemClickListener(new StatusClickListener(context, connections));
	            return true;
		default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * Google OAuth 2.0 code
	 * Here goes the code related to Google APIs and OAuth
	 */
	
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
	
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/plus.me";
	
	private String user_email;
	
	private void pickUserAccount() {
		String[] accountTypes = new String[]{"com.google"};
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
			// Receiving a result from the AccountPicker
			if (resultCode == RESULT_OK) {
				user_email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				// With the account name acquired, go get the auth token
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				// The account picker dialog closed without selecting an account.
				// Notify users that they must pick an account to proceed.
				Toast.makeText(this, getString(R.string.pick_account), Toast.LENGTH_LONG).show();
			}
		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
				requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			handleAuthorizeResult(resultCode, data);
			return;
        }
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void handleAuthorizeResult(int resultCode, Intent data) {
		if (data == null) {
			show(getString(R.string.unk_err));
			return;
		}
		if (resultCode == RESULT_OK) {
			Log.i("NSAPP", "Retrying");
			new GetUsernameTask(this, user_email, SCOPE).execute();
			return;
		}
		if (resultCode == RESULT_CANCELED) {
			show(getString(R.string.auth_rejected));
			return;
		}
		show(getString(R.string.unk_err));
    }
	
	private void show(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void getUsername() {
		if (user_email == null) {
			pickUserAccount();
		} else {
			new GetUsernameTask(this, user_email, SCOPE).execute();
		}
	}

	private boolean isDeviceOnline() {
    	ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    		return true;
    	}
    	return false;
	}

	public void handleException(final UserRecoverableAuthException e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent intent = ((UserRecoverableAuthException)e).getIntent();
                startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
			}
		});
	}
}