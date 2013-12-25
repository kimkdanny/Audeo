package com.orchestr8.android.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lymbix.LymbixClient;

public class MainScreen extends Activity implements OnClickListener {

	protected static final int RESULT_SPEECH = 1;
	ArrayList<String> conceptStrings = new ArrayList();

	private ImageButton btnSpeak, history;
	// private TextView txtText, sentiment;
	public GridView grid;
	public String original_text, concepts, sentimental;
	public ArrayList<String> urls = new ArrayList<String>();
	public List<String> keyterms;
	public ImageView logo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_screen);
		// txtText = (TextView) findViewById(R.id.text);
		// sentiment = (TextView) findViewById(R.id.sentiment);
		grid = (GridView) findViewById(R.id.gridView1);
		btnSpeak = (ImageButton) findViewById(R.id.read);
		logo = (ImageView) findViewById(R.id.logo);
		btnSpeak.setOnClickListener(this);
		grid.setVisibility(View.INVISIBLE);
		history = (ImageButton) findViewById(R.id.history);

		history.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (concepts != null && sentimental != null) {
					Intent intent = new Intent(MainScreen.this,
							SharedPrefs.class);
					System.out.println("FUCK SAVING " + sentimental + " "
							+ concepts);
					intent.putExtra("emotion", sentimental);
					intent.putExtra("item", concepts);
					startActivity(intent);
				}
			}

		});

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String url = "http://simple.wikipedia.org/wiki/"
						+ keyterms.get(position);
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}

		});

	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// sentiment.setText(msg.obj.toString());
		}
	};
	Thread t1 = new Thread() {
		@Override
		public void run() {
			System.out.println("FUCK thread started");
			LymbixClient lymbix = null;
			try {
				lymbix = new LymbixClient(
						"f7a64f0321fe0215a100053af36b0695c62647ba");
				final String emotion = lymbix.tonalize(original_text, null).DominantCategory;
				System.out.println("FUCK emotion: " + emotion);
				Handler refresh = new Handler(Looper.getMainLooper());
				refresh.post(new Runnable() {
					public void run() {
						// sentiment.setText(emotion);

					}
				});
				// t1.stop();

			} catch (Exception e3) {
				System.out.println("FUCK" + e3);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case RESULT_SPEECH: {
				if (resultCode == RESULT_OK && null != data) {
	
					ArrayList<String> text = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					System.out.println(data);
					original_text = text.get(0);
					System.out.println("FUCK ORIGINAL " + original_text);
	
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs
							.add(new BasicNameValuePair("text", original_text));
					nameValuePairs.add(new BasicNameValuePair("locale", "en"));
					nameValuePairs.add(new BasicNameValuePair("format", "json"));
					nameValuePairs.add(new BasicNameValuePair("token",
							"38aa5e2d8c1f4e8cb650b1b4101d35b6"));
	
					new JSONClass().execute(nameValuePairs);
				}
			}
		}
	}

	class JSONClass extends AsyncTask<ArrayList, Void, Void> {

		@Override
		protected Void doInBackground(ArrayList... arg0) {
			// TODO Auto-generated method stub
			InputStream is = null;
			String result = "";
			// http post
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://api.reegle.info/service/extract");
				httppost.setEntity(new UrlEncodedFormEntity(arg0[0]));
				HttpResponse response = httpclient.execute(httppost);
				InputStream stream = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stream));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				String jsonData = sb.toString();

				JSONObject json = new JSONObject(jsonData);
				JSONArray concepts = json.getJSONArray("concepts");
				for (int i = 0; i < concepts.length(); i++) {
					JSONObject eachConcept = concepts.getJSONObject(i);
					conceptStrings.add(eachConcept.getString("prefLabel"));
				}

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			// txtText.setText("Purified:"
			// + purified);

			new Thread() {
				@Override
				public void run() {
					System.out.println("FUCK thread started");
					LymbixClient lymbix = null;
					try {
						lymbix = new LymbixClient(
								"f7a64f0321fe0215a100053af36b0695c62647ba");
						final String emotion = lymbix.tonalize(original_text,
								null).DominantCategory;
						sentimental = emotion;
						System.out.println("FUCK emotion: " + emotion);
						Handler refresh = new Handler(Looper.getMainLooper());
						refresh.post(new Runnable() {
							public void run() {
								// sentiment.setText(emotion);
								grid.setAdapter(new GridItemAdapter(
										getApplicationContext(),
										conceptStrings, emotion));
								grid.setVisibility(View.VISIBLE);
								logo.setVisibility(View.GONE);

							}
						});

					} catch (Exception e3) {
						System.out.println("FUCK" + e3);
					}
				}
			}.start();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
		intent.putExtra(
				RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
				new Long(3000));

		try {
			startActivityForResult(intent, RESULT_SPEECH);
			// txtText.setText("");
		} catch (ActivityNotFoundException a) {
			Toast t = Toast.makeText(getApplicationContext(),
					"Opps! Your device doesn't support Speech to Text",
					Toast.LENGTH_SHORT);
			t.show();
		}
	}

}
