package edu.training.droidbountyhunter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class NetServices extends AsyncTask<String, Void, Object> {
	private static final String URL_WS1 = "http://201.168.207.119/droidBHServices/fujitivos.aspx";
	private static final String URL_WS2 = "http://201.168.207.119/droidBHServices/atrapados.aspx";
	private OnTaskCompleted listener;
	private Exception exception;
	public Activity oActRef;

	public NetServices(OnTaskCompleted listener) {
		exception = null;
		this.listener = listener;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e)  {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String connectGet(String url) {
		HttpClient httpclient = new DefaultHttpClient();
		String sRes = "";

		HttpGet httpget = new HttpGet(url);
		HttpResponse response;

		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity. getContent();
				String result = convertStreamToString(instream);
				sRes = result;
				instream.close();
			}
		} catch (Exception e) {
			Log.w("[CHECK]", e.toString());
		}
		return (sRes);
	}

	public static String connectPost(String url, String udid) throws UnsupportedEncodingException {
		HttpClient httpclient = new DefaultHttpClient();
		String sRes = "";
		HttpPost httppost = new HttpPost(url);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("udid", udid));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		HttpResponse response;
		try {
			response = httpclient.execute(httppost);
			Log.w("[CHECK]", response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				Log.w("[CHECK]", result);
				instream.close();
			}
		} catch (Exception e) {
			Log.w("[CHECK]", e.toString());
		}
		return (sRes);
	}

	protected Object doInBackground(String...urls) {
		Object x = null;
		String sResp = "";
		if (urls[0] == "Fugitivos") {
			try {
				sResp = NetServices.connectGet(URL_WS1);
				String[] aFugs = null;
				JSONArray jaData = new JSONArray(sResp);
				aFugs = new String[jaData.length()];
				for (int i = 0; i < jaData.length(); ++i) {
					JSONObject joFug = jaData.getJSONObject(i);
					aFugs[i] = joFug.getString("name");
				}
				if (aFugs != null) {
					for (int i = 0; i < aFugs.length; i++) {
						MainActivity.oDB.InsertFugitivo(aFugs[i]);
					}
				}
				x = aFugs;
			} catch (Exception e) {
				exception = e;
			}
		}
		if (urls[0] == "Atrapar") {
			try {
				sResp = NetServices.connectPost(URL_WS2, urls[1]);
				String sMsg = "";
				JSONObject jaData = new JSONObject(sResp);
				sMsg = jaData.getString("Mensaje");
			} catch (Exception e) {
				exception = e;
			}
		}
		return (x);
	}

	protected void onPostExecute(Object feed) {
		if (exception == null) {
			listener.onTaskCompleted(feed);
		}
		else {
			listener.onTaskError(exception.toString());
		}
	}
}