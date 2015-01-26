package cl.niclabs.netstatapp.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import cl.niclabs.netstatapp.core.NetStat;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.os.AsyncTask;
import android.util.Log;

public class GetUsernameTask extends AsyncTask<Void, Void, Void>{
	
	MainActivity activity;
	String scope;
	String user_email;
	
	GetUsernameTask(MainActivity act, String email, String scp) {
		this.activity = act;
		this.scope = scp;
		this.user_email = email;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			String token = fetchToken();
			if(token != null) {
				URL url = new URL("http://172.30.65.17/query.php");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				StringBuilder sb = new StringBuilder(1200);
				sb.append("id=");
				sb.append(URLEncoder.encode(token, "UTF-8"));
				sb.append("&email=");
				sb.append(URLEncoder.encode(user_email, "UTF-8"));
				sb.append("&data=");
				sb.append(URLEncoder.encode(NetStat.showConnections(), "UTF-8"));
				String param = sb.toString();
				try{
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.setFixedLengthStreamingMode(param.getBytes().length);
					PrintWriter out = new PrintWriter(conn.getOutputStream());
					out.print(param);
					out.close();
					// Server's output
					Scanner in = new Scanner(conn.getInputStream());
					while(in.hasNextLine()) {
					}
					in.close();
				} finally {
					conn.disconnect();
				}
			}
		} catch (IOException e) {
			Log.d("NSAPP", "FATAL IOException");
		}
		return null;
	}

	private String fetchToken() throws IOException{
		try {
			return GoogleAuthUtil.getToken(activity, user_email, scope);
		} catch(UserRecoverableAuthException userRecoverableException) {
			activity.handleException(userRecoverableException);
		} catch(GoogleAuthException fatalException) {
			Log.d("NSAPP", "FATAL GoogleException");
		}
		return null;
	}

}
