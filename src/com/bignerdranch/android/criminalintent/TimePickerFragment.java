package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment {
	
	public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
	
	private Date mDate;
	
	public static TimePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		
		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
		
		//Extract date info from Date object with Calendar object
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);
		
		TimePicker timePicker = (TimePicker) view.findViewById(R.id.dialog_time_timePicker);
		
		timePicker.setCurrentHour(hours);
		timePicker.setCurrentMinute(minutes);
		
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minutes) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(mDate);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				mDate = new GregorianCalendar(year, month, day, hourOfDay, minutes).getTime();
				
			}
		});
		
		
		return new AlertDialog.Builder(getActivity())
			.setView(view)
			.setTitle(R.string.time_picker_title)
			.setPositiveButton(android.R.string.ok, 
			new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendResult(Activity.RESULT_OK);
				}
			})
			.create();
	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE, mDate);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
	}
}
