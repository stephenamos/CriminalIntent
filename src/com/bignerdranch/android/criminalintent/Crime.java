package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_PHOTO = "photo";
	
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private Photo mPhoto;
	
	private boolean mSolved;
	
	public Crime() {
		//Generate unique identifier
		mId = UUID.randomUUID();
		mDate = new Date();
	}
	
	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		mTitle = json.getString(JSON_TITLE);
		mDate = new Date(json.getLong(JSON_DATE));
		mSolved = json.getBoolean(JSON_SOLVED);
		if (json.has(JSON_PHOTO)) {
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		if (mPhoto != null) {
			json.put(JSON_PHOTO, mPhoto.toJSON());
		}
		
		return json;
	}
	
	public UUID getId() {
		return mId;
	}

	public Date getDate() {
		return mDate;
	}
	
	public String getTitle() {
		return mTitle;
	}

	public Photo getPhoto() {
		return mPhoto;
	}
	
	public void setDate(Date date) {
		mDate = date;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
	
	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}

	public boolean isSolved() {
		return mSolved;
	}
	
	/**
	 * The default implementation of ArrayAdapter<t>.getView(...) relies on toString()
	 * (See CrimeListFragment class)
	 */
	@Override
	public String toString() {
		return mTitle;
	}

}
