package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
	public static final String DIALOG_DATE = "date";
	public static final String DIALOG_TIME = "time";

	private static final int REQUEST_DATE = 0;

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private Button mTimeButton;
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
		setHasOptionsMenu(true); //Tells this fragment it has an options menu (Or in this case, an up button in the action bar)
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);

		//If API11 or higher, allow the home icon to act as an "Up" button
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) { //Check if there is no parent activity (If there isn't, don't display the "Up" button/caret)
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);	
			}
		}

		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				mCrime.setTitle(c.toString());	
			}

			@Override
			public void beforeTextChanged(CharSequence c, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// Do nothing
			}
		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		updateButtonData();
		mDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}

		});

		mTimeButton = (Button) v.findViewById(R.id.crime_time);
		mTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openTimePickerDialog();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) { 
			openTimePickerDialog();
		}
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateButtonData();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityIntent(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
				return true;
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.getInstance(getActivity()).saveCrimes();
	}

	private void updateButtonData() {
		mDateButton.setText(mCrime.getDate().toString());
	}

	private void openTimePickerDialog() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
		dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
		dialog.show(fm, DIALOG_TIME);

	}
	
}
