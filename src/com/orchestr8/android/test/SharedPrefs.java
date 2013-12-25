package com.orchestr8.android.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SharedPrefs extends ListActivity {
	String[] times;
	String[] concepts;
	String concept;
	String emotion;
	int index = 0;
	Date today;
	ArrayList<String> items = new ArrayList();


	SharedPreferences data;
	SharedPreferences.Editor editor;
	public static String key = "MySharedData";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Date rightNow = new Date();
		today = new Date();

		this.getListView().setBackgroundResource(R.drawable.history_back);
		this.getListView().setCacheColorHint((Color.TRANSPARENT));

		try{
		data = getSharedPreferences(key, 0);
		index = data.getInt("index", 0);
		times = new String[index + 1];
		for (int i = 0; i < times.length; i++)
			times[i] = data.getString("today" + i, "Last Conversation");

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, times));
		}
		catch(Exception f){
			System.out.println("FUCK YOU MOTHER FUCKER");

		}
		
		try{
			
			Intent receive = getIntent();
			String feeling = receive.getStringExtra("emotion");
			String items = receive.getStringExtra("item");
			data = getSharedPreferences(key, 0);
			index = data.getInt("index", 0);
			
			data = getSharedPreferences(key, 0);
			editor = data.edit();
			editor.putString("feeling" + index, feeling);
			editor.putString("concept" + index, items);
			editor.putString("today" + index, today.toString());
			editor.putInt("index", index+1);
			editor.commit();
			((ArrayAdapter<String>)getListAdapter()).notifyDataSetChanged();
			
		}
		catch(Exception e){
			System.out.println("SIGH WHAT THE FUCK");
		}
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		data = getSharedPreferences(key, 0);
		index = position;
		emotion = data.getString("feeling" + index, "None");
		concept = data.getString("concept" + index, "None");
		
		Intent main = new Intent(this, MainScreen.class);
		main.putExtra("emotion", emotion);
		main.putExtra("items",concept);
		main.putExtra("fromhistory" ,true);
		System.out.println("FUCK EMOTION" + emotion);
		System.out.println("FUCK ITEMS " + concept);
		startActivity(main);
	}

}
