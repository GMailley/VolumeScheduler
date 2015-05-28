package com.yooshsoft.volumescheduler.volumescheduler;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.jjobes.slidedaytimepicker.SlideDayTimeListener;
import com.github.jjobes.slidedaytimepicker.SlideDayTimePicker;
import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

	public static final String IS_EDIT = "IS_EDIT";
	public static final String EVENT = "EVENT";

	protected Button buttonStart;
	protected Button buttonEnd;

	protected SeekBar ringSeek;
	protected SeekBar mediaSeek;
	protected SeekBar notifSeek;
	protected SeekBar sysSeek;

	protected CheckBox silentCheck;

	protected VolumeSettings volumes;

	protected Button set;
	protected Button cancel;

	protected int input_startday = 2;
	protected int input_starthour = 9;
	protected int input_startmin = 0;
	protected int input_endday = 2;
	protected int input_endhour = 17;
	protected int input_endmin = 0;
	protected int event_id = 0;


	final SlideDayTimeListener startListener = new SlideDayTimeListener() {

		@Override
		public void onDayTimeSet(int day, int hour, int minute) {
			setStartText(day, hour, minute);
			input_startday = day;
			input_starthour = hour;
			input_startmin = minute;
			Toast.makeText(getApplicationContext(), "" + day, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onDayTimeCancel() {
			Toast.makeText(CreateEventActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
		}
	};

	final SlideDayTimeListener endListener = new SlideDayTimeListener() {

		@Override
		public void onDayTimeSet(int day, int hour, int minute) {
			setEndText(day, hour, minute);
			input_endday = day;
			input_endhour = hour;
			input_endmin = minute;
		}

		@Override
		public void onDayTimeCancel() {
			Toast.makeText(CreateEventActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		AudioManager audio;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volume);

		getViews();

		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		this.ringSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_RING) + 1);
		this.mediaSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		this.notifSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
		this.sysSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));

		setSeekListeners();

		this.volumes = new VolumeSettings(audio.getStreamVolume(AudioManager.STREAM_RING), audio.getStreamVolume(AudioManager.STREAM_MUSIC),
			audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION), audio.getStreamVolume(AudioManager.STREAM_SYSTEM), audio.getRingerMode());

		seekBarUpdate(this.volumes);

		if (this.getIntent().getBooleanExtra(IS_EDIT, false)) {
			edit_init();
		} else {
			add_init();
		}

		setStartText(this.input_startday, this.input_starthour, this.input_startmin);
		setEndText(this.input_endday, this.input_endhour, this.input_endmin);
	}

	protected void seekBarUpdate(VolumeSettings vols) {
		this.ringSeek.setProgress(vols.getRingtone());
		this.mediaSeek.setProgress(vols.getMedia());
		this.notifSeek.setProgress(vols.getNotifications());
		this.sysSeek.setProgress(vols.getSystem());
	}

	private void getViews() {
		this.buttonStart = (Button) findViewById(R.id.button_start);
		this.buttonEnd = (Button) findViewById(R.id.button_end);

		this.ringSeek = (SeekBar) findViewById(R.id.seek_ringtone);
		this.mediaSeek = (SeekBar) findViewById(R.id.seek_media);
		this.notifSeek = (SeekBar) findViewById(R.id.seek_notifications);
		this.sysSeek = (SeekBar) findViewById(R.id.seek_system);

		this.silentCheck = (CheckBox) findViewById(R.id.check_silent);

		this.set = (Button) findViewById(R.id.button_set);
		this.cancel = (Button) findViewById(R.id.button_cancel);
	}

	private void setSeekListeners() {
		this.ringSeek.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();

				volumes.setRingtone(ringSeek.getProgress());
				seekBarUpdate(volumes);

				return false;
			}
		});

		this.mediaSeek.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();

				volumes.setMedia(mediaSeek.getProgress());

				return false;
			}
		});

		this.notifSeek.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();

				volumes.setNotifications(notifSeek.getProgress());

				return false;
			}
		});

		this.sysSeek.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();

				volumes.setSystem(sysSeek.getProgress());

				return false;
			}
		});
	}

	private void setStartText(int day, int hour, int min) {
		String str;

		str = daytostring(day) + " " + timetostring(hour, min);

		buttonStart.setText(str);
	}

	private void setEndText(int day, int hour, int min) {
		String str;

		str = daytostring(day) + " " + timetostring(hour, min);

		buttonEnd.setText(str);
	}

	private String daytostring(int day) {
		switch (day) {
			case 1:
				return "Sunday";
			case 2:
				return "Monday";
			case 3:
				return "Tuesday";
			case 4:
				return "Wednesday";
			case 5:
				return "Thursday";
			case 6:
				return "Friday";
			case 7:
				return "Saturday";
		}

		return null;
	}

	private String timetostring(int hour, int min) {
		String str;
		boolean pm;

		str = "";
		pm = false;

		if (hour >= 12) {
			hour -= 12;
			pm = true;
		}

		str += hour + ":";

		if (min >= 10) {
			str += min + " ";
		} else {
			str += "0" + min + " ";
		}

		if (pm) {
			str += "PM";
		} else {
			str += "AM";
		}

		return str;
	}

	private void add_init() {
		Calendar c;

		c = Calendar.getInstance();

		this.input_startday = c.get(Calendar.DAY_OF_WEEK);
		this.input_starthour = c.get(Calendar.HOUR_OF_DAY);
		this.input_startmin = c.get(Calendar.MINUTE) + 1;

		this.input_endday = this.input_startday;
		if(this.input_startmin + 20 < 60) {
			this.input_endhour = this.input_starthour;
			this.input_endmin = this.input_startmin + 20;
		} else {
			this.input_endhour = this.input_starthour + 1;
			this.input_endmin = 20 - (60 - this.input_startmin);
		}
	}

	private void edit_init() {
		ScheduleEvent event;

		event = this.getIntent().getParcelableExtra(EVENT);

		this.event_id = event.getId();

		this.input_startday = event.getStartDay();
		this.input_starthour = event.getStartHour();
		this.input_startmin = event.getStartMin();

		this.input_endday = event.getEndDay();
		this.input_endhour = event.getEndHour();
		this.input_endmin = event.getEndMin();

		this.volumes = event.getVolumes();

		seekBarUpdate(this.volumes);
	}

	public void submitEvent(View v)
	{
		ScheduleEvent event;
		int vol1, vol2, vol3, vol4;
		boolean is_edit;
		Intent intent;

		is_edit = getIntent().getBooleanExtra(IS_EDIT, false);

		vol1 = this.ringSeek.getProgress();
		vol2 = this.mediaSeek.getProgress();
		vol3 = this.notifSeek.getProgress();
		vol4 = this.sysSeek.getProgress();

		if(silentCheck.isChecked()) {
			vol1 = -1;
		}

		event = new ScheduleEvent(
			input_startday,
			input_starthour,
			input_startmin,
			input_endday,
			input_endhour,
			input_endmin,
			new VolumeSettings(
				vol1,
				vol2,
				vol3,
				vol4
			)
		);

		event.setId(this.event_id);

		intent = new Intent();
		intent.putExtra(IS_EDIT, is_edit);
		intent.putExtra(EVENT, event);

		setResult(RESULT_OK, intent);
		finish();
	}

	public void cancelEvent(View v)
	{
		setResult(RESULT_CANCELED, new Intent());
		finish();
	}

	public void onCheckboxClicked(View view)
	{
		boolean checked = ((CheckBox) view).isChecked();

		switch(view.getId()) {
			case R.id.check_silent:
				set_silent(checked);
		}
	}

	private void set_silent(boolean checked)
	{
		if(checked) {
			seekBarUpdate(new VolumeSettings(0, 0, 0, 0));
			ringSeek.setEnabled(false);
			mediaSeek.setEnabled(false);
			notifSeek.setEnabled(false);
			sysSeek.setEnabled(false);
		} else {
			seekBarUpdate(this.volumes);
			ringSeek.setEnabled(true);
			mediaSeek.setEnabled(true);
			notifSeek.setEnabled(true);
			sysSeek.setEnabled(true);
		}
	}

	public void showTimePickerStart(View v)
	{
		new SlideDayTimePicker.Builder(getSupportFragmentManager())
			.setListener(startListener)
			.setInitialDay(input_startday)
			.setInitialHour(input_starthour)
			.setInitialMinute(input_startmin)
			.build()
			.show();
	}

	public void showTimePickerEnd(View v)
	{
		new SlideDayTimePicker.Builder(getSupportFragmentManager())
			.setListener(endListener)
			.setInitialDay(input_endday)
			.setInitialHour(input_endhour)
			.setInitialMinute(input_endmin)
			.build()
			.show();
	}
}
