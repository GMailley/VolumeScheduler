package com.yooshsoft.volumescheduler.volumescheduler;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener
{
	private TimePickedListener mListener;

	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.i("1BUTTON CLICKED", "!!!!");
		// use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onAttach(Activity activity)
	{
		Log.i("2BUTTON CLICKED", "!!!!");
		// when the fragment is initially shown (i.e. attached to the activity), cast the activity to the callback interface type
		super.onAttach(activity);
		try {
			mListener = (TimePickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement " + TimePickedListener.class.getName());
		}
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute)
	{
		// when the time is selected, send it to the activity via its callback interface method
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);

		mListener.onTimePicked(c);
	}

	public static interface TimePickedListener
	{
		public void onTimePicked(Calendar time);
	}
}