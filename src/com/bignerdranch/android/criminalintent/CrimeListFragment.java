package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class CrimeListFragment extends ListFragment {
	
	private ArrayList<Crime> mCrimes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getActivity().setTitle(R.string.crimes_title);
		 //getActivity() is a convenience method provided by list fragment that returns the activity hosting this fragment

		mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();
	}
	
}
