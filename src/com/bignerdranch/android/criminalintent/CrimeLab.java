package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {

	private static CrimeLab sCrimeLab;
	private Context mAppContext;

	private ArrayList<Crime> mCrimes;

	private CrimeLab(Context appContext) { 
		mAppContext = appContext;
		mCrimes = new ArrayList<Crime>();
	}

	public void addCrime(Crime crime) {
		mCrimes.add(crime);
	}
	
	public static CrimeLab getInstance(Context c) {

		if (sCrimeLab == null) {
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}

		return sCrimeLab;
	}

	public ArrayList<Crime> getCrimes() {
		return mCrimes;
	}

	public Crime getCrime(UUID id) {
		for (Crime crime : mCrimes) {
			if (crime.getId().equals(id)) { 
				return crime; 
			}
		}
		return null;
	}

}
