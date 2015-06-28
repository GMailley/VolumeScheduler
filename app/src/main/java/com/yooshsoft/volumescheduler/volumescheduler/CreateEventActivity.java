package com.yooshsoft.volumescheduler.volumescheduler;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.EventTime;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity
{
	public static final String EVENTS = "EVENTS";

	protected static final int START_TIME_CODE = 1;
	protected static final int END_TIME_CODE = 2;
	protected static final String TIMEPICKER_HOUR = "START_HOUR";
	protected static final String TIMEPICKER_MIN = "START_MIN";

	//Views
	protected TextView vStart;
	protected TextView vEnd;
	protected Button vSet;
	protected Button vCancel;

	protected SeekBar vRing;
	protected SeekBar vMedia;
	protected SeekBar vNotif;
	protected SeekBar vSystem;

	protected CheckBox vSilent;

	protected TextView vSun;
	protected TextView vMon;
	protected TextView vTue;
	protected TextView vWed;
	protected TextView vThu;
	protected TextView vFri;
	protected TextView vSat;

	//Data
	protected EventTime dStart;
	protected EventTime dEnd;
	protected int dRing;
	protected int dMedia;
	protected int dNotif;
	protected int dSystem;
	protected int dRingMode;
	protected boolean[] dDays = {false, false, false, false, false, false, false};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volume);

		init_all();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int hour;
		int min;

		if (resultCode == RESULT_CANCELED) {
			return;
		}

		if (resultCode == RESULT_OK) {
			if (requestCode == START_TIME_CODE) {
				hour = data.getIntExtra(TIMEPICKER_HOUR, 9);
				min = data.getIntExtra(TIMEPICKER_MIN, 0);
				this.dStart = new EventTime(1, hour, min);
				this.vStart.setText(this.dStart.toString());
			} else if (requestCode == END_TIME_CODE) {
				hour = data.getIntExtra(TIMEPICKER_HOUR, 17);
				min = data.getIntExtra(TIMEPICKER_MIN, 0);
				this.dEnd = new EventTime(1, hour, min);
				this.vEnd.setText(this.dEnd.toString());
			}
		}
	}

	private void init_all()
	{
		init_views();
		init_data();
		init_volumes();
		init_listeners();
	}

	private void init_views()
	{
		this.vStart = (TextView) findViewById(R.id.button_start);
		this.vEnd = (TextView) findViewById(R.id.button_end);
		this.vSet = (Button) findViewById(R.id.button_set);
		this.vCancel = (Button) findViewById(R.id.button_cancel);

		this.vRing = (SeekBar) findViewById(R.id.seek_ringtone);
		this.vMedia = (SeekBar) findViewById(R.id.seek_media);
		this.vNotif = (SeekBar) findViewById(R.id.seek_notif);
		this.vSystem = (SeekBar) findViewById(R.id.seek_system);

		this.vSilent = (CheckBox) findViewById(R.id.check_silent);

		this.vSun = (TextView) findViewById(R.id.button_sun);
		this.vMon = (TextView) findViewById(R.id.button_mon);
		this.vTue = (TextView) findViewById(R.id.button_tue);
		this.vWed = (TextView) findViewById(R.id.button_wed);
		this.vThu = (TextView) findViewById(R.id.button_thu);
		this.vFri = (TextView) findViewById(R.id.button_fri);
		this.vSat = (TextView) findViewById(R.id.button_sat);
	}

	private void init_data()
	{
		Calendar c;

		this.dRing = 0;
		this.dMedia = 0;
		this.dNotif = 0;
		this.dSystem = 0;
		this.dRingMode = 0;

		c = Calendar.getInstance();
		this.dStart = new EventTime(c.get(Calendar.DAY_OF_WEEK), 9, 0);
		this.dEnd = new EventTime(c.get(Calendar.DAY_OF_WEEK), 17, 0);
	}

	//Needs init_views
	private void init_volumes()
	{
		AudioManager audio;

		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		this.vRing.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_RING));
		this.vMedia.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		this.vNotif.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
		this.vSystem.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));

		this.dRing = audio.getStreamVolume(AudioManager.STREAM_RING);
		this.dMedia = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		this.dNotif = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		this.dSystem = audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
		this.dRingMode = audio.getRingerMode();

		this.vRing.setProgress(this.dRing);
		this.vMedia.setProgress(this.dMedia);
		this.vNotif.setProgress(this.dNotif);
		this.vSystem.setProgress(this.dSystem);
	}

	//Needs init_views
	private void init_listeners() {
		this.vStart.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					showStartTimePicker(v);
				}
			}
		);

		this.vEnd.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					showEndTimePicker(v);
				}
			}
		);

		this.vRing.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					v.performClick();
					dRing = vRing.getProgress();
					return false;
				}
			}
		);

		this.vMedia.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					v.performClick();
					dMedia = vMedia.getProgress();
					return false;
				}
			}
		);

		this.vNotif.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					v.performClick();
					dNotif = vNotif.getProgress();
					return false;
				}
			}
		);

		this.vSystem.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					v.performClick();
					dSystem = vSystem.getProgress();
					return false;
				}
			}
		);

		this.vSilent.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					if (vSilent.isChecked()) {
						vRing.setProgress(0);
						vMedia.setProgress(0);
						vNotif.setProgress(0);
						vSystem.setProgress(0);

						vRing.setEnabled(false);
						vMedia.setEnabled(false);
						vNotif.setEnabled(false);
						vSystem.setEnabled(false);

						dRingMode = AudioManager.RINGER_MODE_SILENT;
					} else {
						vRing.setProgress(dRing);
						vMedia.setProgress(dMedia);
						vNotif.setProgress(dNotif);
						vSystem.setProgress(dSystem);

						vRing.setEnabled(true);
						vMedia.setEnabled(true);
						vNotif.setEnabled(true);
						vSystem.setEnabled(true);

						dRingMode = AudioManager.RINGER_MODE_NORMAL;
					}
				}
			}
		);

		this.vSet.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					int vol1, vol2, vol3, vol4;
					Intent intent;
					ArrayList<ScheduleEvent> events = new ArrayList<>();

					vol1 = vRing.getProgress();
					vol2 = vMedia.getProgress();
					vol3 = vNotif.getProgress();
					vol4 = vSystem.getProgress();

					if (vSilent.isChecked()) {
						vol1 = -1;
					}

					for(int i=0; i<dDays.length; i++)
					{
						int end = i;
						if(dStart.compareTo(dEnd) > 0)
						{
							end++;
							if(end == 8)
							{
								end = 1;
							}
						}
						if(dDays[i])
						{
							events.add( new ScheduleEvent(
								i+1,
								dStart.getHour(),
								dStart.getMin(),
								end,
								dEnd.getHour(),
								dEnd.getMin(),
								new VolumeSettings(
									vol1,
									vol2,
									vol3,
									vol4
								)
							));
						}
					}

					if(events.size() <= 0)
					{
						Toast.makeText(getApplicationContext(), "At least one day must be chosen", Toast.LENGTH_SHORT).show();
					}
					else
					{
						intent = new Intent();
						intent.putExtra(EVENTS, events);

						setResult(RESULT_OK, intent);
						finish();
					}
				}
			}
		);

		this.vCancel.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			}
		);

		this.vSun.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					dDays[0] = !dDays[0];
					if(dDays[0])
					{
						SpannableString content = new SpannableString(vSun.getText());
						content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
						vSun.setText(content);
						Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
						vSun.setText(vSun.getText().toString());
					}
				}
			}
		);

		this.vMon.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					dDays[1] = !dDays[1];
					if(dDays[1])
					{
						SpannableString content = new SpannableString(vMon.getText());
						content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
						vMon.setText(content);
						Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
						vMon.setText(vMon.getText().toString());
					}
				}
			}
		);

		this.vTue.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					dDays[2] = !dDays[2];
					if(dDays[2])
					{
						SpannableString content = new SpannableString(vTue.getText());
						content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
						vTue.setText(content);
						Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
						vTue.setText(vTue.getText().toString());
					}
				}
			}
		);

		this.vWed.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					dDays[3] = !dDays[3];
					if(dDays[3])
					{
						SpannableString content = new SpannableString(vWed.getText());
						content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
						vWed.setText(content);
						Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
						vWed.setText(vWed.getText().toString());
					}
				}
			}
		);

		this.vThu.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					dDays[4] = !dDays[4];
					if(dDays[4])
					{
						SpannableString content = new SpannableString(vThu.getText());
						content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
						vThu.setText(content);
						Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
						vThu.setText(vThu.getText().toString());
					}
				}
			}
		);

		this.vFri.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					dDays[5] = !dDays[5];
					if(dDays[5])
					{
						SpannableString content = new SpannableString(vFri.getText());
						content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
						vFri.setText(content);
						Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
						vFri.setText(vFri.getText().toString());
					}
				}
			}
		);

		this.vSat.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v) {
					dDays[6] = !dDays[6];
					if(dDays[6])
					{
						SpannableString content = new SpannableString(vSat.getText());
						content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
						vSat.setText(content);
						Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_SHORT).show();
						vSat.setText(vSat.getText().toString());
					}
				}
			}
		);
	}

	/**
	 * Time Picker Fragment
	 */
	public static class StartTimePickerFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			int hour = 9;
			int minute = 0;

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(
				getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity())
			);
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Intent intent;

			intent = new Intent();
			intent.putExtra(TIMEPICKER_HOUR, hourOfDay);
			intent.putExtra(TIMEPICKER_MIN, minute);

			getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
		}
	}

	public static class EndTimePickerFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener
	{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			int hour = 17;
			int minute = 0;

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(
				getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity())
			);
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Intent intent;

			intent = new Intent();
			intent.putExtra(TIMEPICKER_HOUR, hourOfDay);
			intent.putExtra(TIMEPICKER_MIN, minute);

			getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);


		}
	}

	public void showStartTimePicker(View v) {
		DialogFragment newFragment = new StartTimePickerFragment();
		newFragment.setTargetFragment(newFragment, START_TIME_CODE);
		newFragment.show(getFragmentManager(), "startPicker");
	}

	public void showEndTimePicker(View v) {
		DialogFragment newFragment = new EndTimePickerFragment();
		newFragment.setTargetFragment(newFragment, END_TIME_CODE);
		newFragment.show(getFragmentManager(), "endPicker");
	}
}