package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

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
		View view = inflater.inflate(R.layout.fragment_crimelist, container, false);	

		mAddCrimeButton = (Button) view.findViewById(R.id.add_new_crime_button);
		mAddCrimeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addNewCrime();
			}
		});

		//Displays a hint that tells the user to click the new crime button at the top or in the middle of the screen.
		//The spannable object allows one to insert a bitmap image in between strings of text.
		String hint1 = getActivity().getResources().getString(R.string.no_crimes_hint);
		String hint2 = getActivity().getResources().getString(R.string.no_crimes_hint2);
		String spannableLocation = " ";
		String finalHint = hint1 + spannableLocation + hint2;

		TextView emptyListTextView = (TextView) view.findViewById(R.id.empty_list_hint_textview);
		SpannableStringBuilder ssb = new SpannableStringBuilder(finalHint);
		Bitmap addCrimeIcon = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_menu_add);
		ssb.setSpan(new ImageSpan(getActivity(), addCrimeIcon), hint1.length(), finalHint.indexOf(hint2), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		emptyListTextView.setText(ssb, BufferType.SPANNABLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}

		ListView listView = (ListView) view.findViewById(android.R.id.list);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			//Use floating context menus on Froyo and Gingerbread
			registerForContextMenu(listView); //Registers context menu to the list view	
		} else {
			//Use contextual action bar on Honeycomb and Higher
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

				@Override
				public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
					//Required but not used in this implementation.
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode arg0) {
					// TODO Auto-generated method stub

				}

				//ActionMode.Callback methods
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.crime_list_item_context, menu);

					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_crime:
						CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
						CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							if (getListView().isItemChecked(i)) {
								crimeLab.deleteCrime(adapter.getItem(i));
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();

						return true;
					default: 
						return false;

					}
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position,
						long id, boolean checked) {
					// Required but not used in this implementation.

				}
			});
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
		Crime crime = adapter.getItem(position);

		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.getInstance(getActivity()).deleteCrime(crime);
			adapter.notifyDataSetChanged();
			return true;
		}

		return super.onContextItemSelected(item);
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
