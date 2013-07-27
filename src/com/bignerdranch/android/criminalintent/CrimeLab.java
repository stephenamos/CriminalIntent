package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {

	private static CrimeLab sCrimeLab;
	private Context mAppContext;

	private ArrayList<Crime> mCrimes;

	private CrimeLab(Context appContext) { 
		mAppContext = appContext;
		mCrimes = new ArrayList<Crime>();

		//Populate list of crimes with 100 arbitrarily created crimes where every other crime is solved
		for (int i = 0; i < 100; i++) {
			Crime c = new Crime();
			c.setTitle("Crime #" + i);
			c.setSolved(i % 2 == 0); //Every other crime
			mCrimes.add(c);
		}
	}

	public static CrimeLab getInstance(Context c) {

		if (sCrimeLab != null) {
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
