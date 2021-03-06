package com.orchestr8.android.test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class MyService extends Service {
	protected AudioManager mAudioManager;
	protected SpeechRecognizer mSpeechRecognizer;
	protected Intent mSpeechRecognizerIntent;
	protected final Messenger mServerMessenger = new Messenger(
			new IncomingHandler(this));

	protected boolean mIsListening;
	protected volatile boolean mIsCountDownOn;

	static final int MSG_RECOGNIZER_START_LISTENING = 1;
	static final int MSG_RECOGNIZER_CANCEL = 2;

	@Override
	public void onCreate() {
		super.onCreate();	
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener(
				this.getApplicationContext()));
		mSpeechRecognizerIntent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		mSpeechRecognizerIntent.putExtra(
				RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		mSpeechRecognizerIntent.putExtra(
				RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		IncomingHandler handle = new IncomingHandler(this);
		Message msg = new Message();
		msg.what = MSG_RECOGNIZER_START_LISTENING;
		handle.sendMessage(msg);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mAudioManager.setStreamMute(1, false);
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener(
				this.getApplicationContext()));
		mSpeechRecognizerIntent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		mSpeechRecognizerIntent.putExtra(
				RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		mSpeechRecognizerIntent.putExtra(
				RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		//mSpeechRecongizerIntent.putExtra()
		mIsListening = false;
		IncomingHandler handle = new IncomingHandler(this);
		Message msg = new Message();
		msg.what = MSG_RECOGNIZER_START_LISTENING;
		handle.sendMessage(msg);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	protected static class IncomingHandler extends Handler {
		private final WeakReference<MyService> mtarget;

		IncomingHandler(MyService target) {
			mtarget = new WeakReference<MyService>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			final MyService target = mtarget.get();

			switch (msg.what) {
			case MSG_RECOGNIZER_START_LISTENING:
				//System.out.println("LISTENING: " + target.mIsListening);

				if (Build.VERSION.SDK_INT >= 16) {
					System.out.println("YES UP");
					// turn off beep sound
					target.mAudioManager.setStreamMute(
							AudioManager.STREAM_SYSTEM, true);
				}
				if (!target.mIsListening) {
					target.mSpeechRecognizer
							.startListening(target.mSpeechRecognizerIntent);
					target.mIsListening = true;
					//Log.d(TAG, "message start listening"); //$NON-NLS-1$
				}
				break;

			case MSG_RECOGNIZER_CANCEL:
				target.mSpeechRecognizer.cancel();
				target.mIsListening = false;
				//Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
				break;
			}
		}
	}

	// Count down timer for Jelly Bean work around
	protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000) {

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFinish() {
			mIsCountDownOn = false;
			Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
			try {
				mServerMessenger.send(message);
				message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
				mServerMessenger.send(message);
			} catch (RemoteException e) {

			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mIsCountDownOn) {
			mNoSpeechCountDown.cancel();
		}
		if (mSpeechRecognizer != null) {
			mSpeechRecognizer.destroy();
		}
	}

	protected class SpeechRecognitionListener implements RecognitionListener {
		Context con;

		public SpeechRecognitionListener(Context c) {
			con = c;
		}

		@Override
		public void onBeginningOfSpeech() {
			// speech input will be processed, so there is no need for count
			// down anymore
			if (mIsCountDownOn) {
				mIsCountDownOn = false;
				mNoSpeechCountDown.cancel();
			}
			//Log.d(TAG, "onBeginingOfSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onBufferReceived(byte[] buffer) {

		}

		@Override
		public void onEndOfSpeech() {
			//Log.d(TAG, "onEndOfSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onError(int error) {
			if (mIsCountDownOn) {
				mIsCountDownOn = false;
				mNoSpeechCountDown.cancel();
			}
			mIsListening = false;
			Message message = Message.obtain(null,
					MSG_RECOGNIZER_START_LISTENING);
			try {
				mServerMessenger.send(message);
			} catch (RemoteException e) {

			}
			//Log.d(TAG, "error = " + error); //$NON-NLS-1$
		}

		@Override
		public void onEvent(int eventType, Bundle params) {

		}

		@Override
		public void onPartialResults(Bundle partialResults) {

			ArrayList<String> partial_results = partialResults
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			System.out.println("RESULTS : " + partial_results);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(con);
			SharedPreferences.Editor edit = prefs.edit();
			String store = prefs.getString("spoken", "");
			for (String s : partial_results) {
				//if (!store.contains(s)) {
				System.out.println("PUTTING IN RESULTS STORE " +  s);
					store += s + " ";
				//}
			}

			edit.putString("spoken", store);
			edit.commit();

			AlchemyAPI_Android_TestApp.SendAlchemyCall("entity", con, "");
		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			if (Build.VERSION.SDK_INT >= 16) {
				mIsCountDownOn = true;
				mNoSpeechCountDown.start();
				mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
			}
			Log.d("KSHITIJ", "onReadyForSpeech"); //$NON-NLS-1$
		}

		@Override
		public void onResults(Bundle results) {
			//Log.d(TAG, "onResults"); //$NON-NLS-1$

		}

		@Override
		public void onRmsChanged(float rmsdB) {

		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}