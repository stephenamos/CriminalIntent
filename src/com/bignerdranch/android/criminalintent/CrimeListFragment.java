package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {

	private ArrayList<Crime> mCrimes;

	private static final String TAG = "CrimeListFragment";
	private boolean mSubtitleVisible;
	private Button mAddCrimeButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); //Lets the FragmentManager know that CrimeListFragment needs to receive options menu callbacks.
		setRetainInstance(true); //retain fragment data in between rotations
		mSubtitleVisible = false;
		
		getActivity().setTitle(R.string.crimes_title);
		//getActivity() is a convenience method provided by list fragment that returns the activity hosting this fragment

		mCrimes = CrimeLab.getInstance(getActivity()).getCrimes();

		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter); //ListFragment convenience method that sets an adapter of the implicit ListView managed by CrimeListFragment
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		if (mCrimes.size() == 0) {
			view = inflater.inflate(R.layout.fragment_crimelist, container, false);	

			mAddCrimeButton = (Button) view.findViewById(R.id.empty_list_button);
			mAddCrimeButton.setVisibility(View.VISIBLE);
			mAddCrimeButton.setEnabled(true);
			
			mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					addNewCrime();
				}
			});
			
		} else {
//			view = super.onCreateView(inflater, container, savedInstanceState);
			view = inflater.inflate(R.layout.fragment_crimelist, container, false);	
			
			mAddCrimeButton = (Button) view.findViewById(R.id.empty_list_button);
			mAddCrimeButton.setVisibility(View.GONE);
			mAddCrimeButton.setEnabled(false);
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);

		//Start CrimePagerActivity with this crime
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());

		startActivity(i);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);

	}

	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { //menu items
		switch (item.getItemId()) {
			case R.id.menu_item_new_crime:
				addNewCrime();
				return true;
			case R.id.menu_item_show_subtitle:
				if (getActivity().getActionBar().getSubtitle() == null) {
					getActivity().getActionBar().setSubtitle(R.string.subtitle);	
					item.setTitle(R.string.hide_subtitle);
					mSubtitleVisible = true;
				} else {
					getActivity().getActionBar().setSubtitle(null);
					item.setTitle(R.string.show_subtitle);
					mSubtitleVisible = false;
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class CrimeAdapter extends ArrayAdapter<Crime> {

		public CrimeAdapter(ArrayList<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//If we weren't given a view, inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
			}

			//Configure the view for this Crime
			Crime c = getItem(position);

			TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
			titleTextView.setText(c.getTitle());

			TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
			dateTextView.setText(c.getDate().toString());

			CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.isSolved());

			return convertView;
		}
	}
	
	private void addNewCrime() {
		Crime crime = new Crime();
		CrimeLab.getInstance(getActivity()).addCrime(crime);
		
		Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
		intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
		
		startActivityForResult(intent, 0);
	}
}
