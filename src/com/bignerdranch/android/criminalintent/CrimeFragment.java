package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {

	public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
	public static final String DIALOG_DATE ="date";
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	
	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				mCrime.setTitle(c.toString());
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Do nothing
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// Do nothing
			}
		});
		
		mDateButton = (Button) v.findViewById(R.id.crime_date);
		mDateButton.setText(mCrime.getDate().toString());
		mDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.show(fm, DIALOG_DATE);
			}
			
		});
		
		
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
			
		});
		
		return v;
	}
	
	
	
}
