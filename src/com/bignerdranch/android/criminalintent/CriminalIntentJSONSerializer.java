package com.bignerdranch.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CriminalIntentJSONSerializer {
	
	private Context mContext;
	private String mFilename;
	
	public CriminalIntentJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}
	
	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
		//Build an array in JSON
		JSONArray array = new JSONArray();
		
		for (Crime crime : crimes) {
			array.put(crime.toJSON());
		}
		
		//Write the file to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public void saveCrimesExternal(ArrayList<Crime> crimes) throws JSONException, IOException {
		//Build an array in JSON
		JSONArray array = new JSONArray();
		
		for (Crime crime : crimes) {
			array.put(crime.toJSON());
		}
		
		//Write the file to disk
		Writer writer = null;
		try {
			String root = Environment.getExternalStorageDirectory().toString();
			if (!root.isEmpty() && root != null) {
				//This will get the SD Card directory and create a folder in it.
				File sdCard = Environment.getExternalStorageDirectory();
				File directory = new File(sdCard.getAbsolutePath() + "/CriminalIntent");
				directory.mkdirs();

				//Now create the file in the above directory and write the contents into it
				File file = new File(directory, mFilename);
				
				Log.d("jsonserializer", "Wrote file using path: " + directory.getAbsolutePath().toString());
				
				FileOutputStream out = new FileOutputStream(file);
				writer = new OutputStreamWriter(out);
				writer.write(array.toString());
			}
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
		ArrayList<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			//Open and read the file into a StringBuilder
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				//Line breaks are omitted and irrelevant
				jsonString.append(line);
			}
			//Parse the JSON Using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			//Build the array of crimes from JSONObjects
			for (int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			//Ignore this one; it happens when starting fresh
		} finally {
			if (reader != null) reader.close();
		}
		return crimes;
	}
	
	public ArrayList<Crime> loadCrimesExternal() throws IOException, JSONException {
		String root = Environment.getExternalStorageDirectory().toString();
		if (!root.isEmpty() && root != null) {
			ArrayList<Crime> crimes = new ArrayList<Crime>();
			BufferedReader reader = null;
			try {
				//This will get the SD Card directory and create a folder in it.
				File sdCard = Environment.getExternalStorageDirectory();
				File directory = new File(sdCard.getAbsolutePath() + "/CriminalIntent");
				directory.mkdirs();
				
				Log.d("jsonserializer", "Read file using path: " + directory.getAbsolutePath().toString());
				
				//Now create the file in the above directory and write the contents into it
				File file = new File(directory, mFilename);
				FileInputStream in = new FileInputStream(file);
				reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder jsonString = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					//Line breaks are omitted and irrelevant
					jsonString.append(line);
				}
				//Parse the JSON Using JSONTokener
				JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
				
				//Build the array of crimes from JSONObjects
				for (int i = 0; i < array.length(); i++) {
					crimes.add(new Crime(array.getJSONObject(i)));
				}
			} catch (FileNotFoundException e) {
				//Ignore this one; it happens when starting fresh
			} finally {
				if (reader != null) reader.close();
			}
			return crimes;
		}
		return null;
	}
	
}
