package com.orchestr8.android.test;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.GridView;

import com.orchestr8.android.api.AlchemyAPI;
import com.orchestr8.android.api.AlchemyAPI_NamedEntityParams;

public class AlchemyAPI_Android_TestApp extends Activity {
	//public EditText edittext;
	//public static TextView textview;
	public static GridView grid;
	public static Context con;
	/****
	 * 
	 * Put your API Key into the variable below. Can get key from
	 * http://www.alchemyapi.com/api/register.html
	 */
	public static String AlchemyAPI_Key = "0536e9715b92b8f621f7879fa7ef2222786d471e";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		con = this.getApplicationContext();

	//	edittext = (EditText) findViewById(R.id.entry);
		//textview = (TextView) findViewById(R.id.TextView01);
		grid = (GridView) findViewById(R.id.gridView1);
		ArrayList<String> s = new ArrayList();
		grid.setAdapter(new GridItemAdapter(this.getApplicationContext(),s, ""));
		//edittext.setText("http://www.cnn.com/2010/WORLD/meast/12/07/egypt.shark.attack/index.html?hpt=Sbin");
		//edittext.setSingleLine(true);
		//textview.setText("");

		//textview.setMovementMethod(new ScrollingMovementMethod());

		 final SpeechRecognizer speechRecognizer;
		 speechRecognizer = SpeechRecognizer
		 .createSpeechRecognizer(getBaseContext());
		 final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
			intent.putExtra(
					RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
					new Long(1000));
		 MyRecognitionListener speechListner = new MyRecognitionListener();
		 speechRecognizer.setRecognitionListener(speechListner);
		 speechRecognizer.startListening(intent);

//		final Button button = (Button) findViewById(R.id.concept_button);
//		button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				SendAlchemyCall("concept", getApplicationContext());
//			}
//		});
		  final Handler mHandler = new  Handler() {

			     public void handleMessage(Message msg) {
					 speechRecognizer.startListening(intent);

			     }
			 };


		Timer mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				mHandler.handleMessage(null);
			}
		}, 0, 10000);
		
//		final Button entity_button = (Button) findViewById(R.id.entity_button);
//		entity_button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				SendAlchemyCall("entity", getApplicationContext());
//			}	
//		});

//		final Button keyword_button = (Button) findViewById(R.id.keyword_button);
//		keyword_button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				AlchemyAPI_Android_TestApp.this.startService(new Intent(
//						AlchemyAPI_Android_TestApp.this, MyService.class));
//			}
//		});

//		final Button text_button = (Button) findViewById(R.id.text_button);
//		text_button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				SendAlchemyCall("text", getApplicationContext());
//			}
//		});

//		final Button sentiment_button = (Button) findViewById(R.id.sentiment_button);
//		sentiment_button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				SendAlchemyCall("sentiment", getApplicationContext());
//			}
//		});

	}

	public static String SendAlchemyCall(String call, Context c, String toRecognize) {
		Document doc = null;
		AlchemyAPI api = null;
		try {
			api = AlchemyAPI.GetInstanceFromString(AlchemyAPI_Key);
		} catch (IllegalArgumentException ex) {
		//	textview.setText("Error loading AlchemyAPI.  Check that you have a valid AlchemyAPI key set in the AlchemyAPI_Key variable.  Keys available at alchemyapi.com.");
			return "Error";
		}
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		SharedPreferences.Editor edit = prefs.edit();

		//System.out.println("CALLING RECOGNITION ON" + someString);

		try {
			if ("concept".equals(call)) {
				doc = api.TextGetRankedConcepts(toRecognize);
				return ShowDocInTextView(doc, false);
			} else if ("entity".equals(call)) {
				System.out.println("FUCK swag" + call);
				doc = api.TextGetRankedNamedEntities(toRecognize);
				System.out.println("FUCK yolo" + call);
				return ShowDocInTextView(doc, false);
				//return "The method should return this then";
			} else if ("keyword".equals(call)) {
				doc = api.TextGetRankedKeywords(toRecognize);
				return ShowDocInTextView(doc, false);
			} else if ("text".equals(call)) {
				doc = api.URLGetText(toRecognize);
				return ShowDocInTextView(doc, false);
			} else if ("sentiment".equals(call)) {
				AlchemyAPI_NamedEntityParams nep = new AlchemyAPI_NamedEntityParams();
				nep.setSentiment(true);
				doc = api.TextGetRankedNamedEntities(toRecognize, nep);
				return ShowDocInTextView(doc, true);
			}

		} catch (Throwable t) {
			System.out.println("FUCK: " + t);
		}
		return toRecognize;
	}

	private static String ShowDocInTextView(Document doc, boolean showSentiment) {
		System.out.println("FUCK CHECKPOINT 0");
		//textview.setText("");
		if (doc == null) {
			System.out.println("FUCK CHECKPOINT 0.5");

			System.out.println("CALL DOC IS NULL");
			return "";
		}
		System.out.println("FUCK CHECKPOINT 0.75");
		Element root = doc.getDocumentElement();
		NodeList items = root.getElementsByTagName("text");
		System.out.println("FUCK CHECKPOINT 0.99");

		if (showSentiment) {
			NodeList sentiments = root.getElementsByTagName("sentiment");
			for (int i = 0; i < items.getLength(); i++) {
				Node concept = items.item(i);
				String astring = concept.getNodeValue();
				astring = concept.getChildNodes().item(0).getNodeValue();
			//	textview.append("\n" + astring);
				if (i < sentiments.getLength()) {
					Node sentiment = sentiments.item(i);
					Node aNode = sentiment.getChildNodes().item(1);
					Node bNode = aNode.getChildNodes().item(0);
			//		textview.append(" (" + bNode.getNodeValue() + ")");
				}
			}
			return "";
		} else {
			System.out.println("FUCK CHECKPOINT 1");
			String grid_items = "";
			for (int i = 0; i < items.getLength(); i++) {
				Node concept = items.item(i);
				String astring = concept.getNodeValue();
				astring = concept.getChildNodes().item(0).getNodeValue();
				//System.out.println("GridShow " + astring);
				//add the relevant items to memory in sharedpreferences
				if(!grid_items.contains(astring)){
					grid_items+= astring + ";";
				}
				
				//edit.putString("putingrid", grid_items);
				//edit.commit();
				//((GridItemAdapter) grid.getAdapter()).notifyDataSetChanged();
				//grid.setAdapter(new GridItemAdapter(con));
				//textview.append("\n" + astring);
			}
			return grid_items;
		}
	}

}

class MyRecognitionListener implements RecognitionListener {

	@Override
	public void onBeginningOfSpeech() {
		Log.d("leapkh", "onBeginningOfSpeech");
		System.out.println("begin");
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		Log.d("leapkh", "onBufferReceived");
	}

	@Override
	public void onEndOfSpeech() {
		Log.d("leapkh", "onEndOfSpeech");
	}

	@Override
	public void onError(int error) {
		Log.d("leapkh", "onError");
		System.out.println("ERROR" + error);
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		Log.d("leapkh", "onEvent");
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		Log.d("leapkh",
				"onPartialResults"
						+ partialResults
								.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
		System.out
				.println("RESULTS "
						+ partialResults
								.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		Log.d("leapkh", "onReadyForSpeech");
	}

	@Override
	public void onResults(Bundle results) {
		Log.d("leapkh", "onResults");

	}

	@Override
	public void onRmsChanged(float arg0) {

	}
}
