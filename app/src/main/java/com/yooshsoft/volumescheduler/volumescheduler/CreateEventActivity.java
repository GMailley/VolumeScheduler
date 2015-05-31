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
import com.yooshsoft.volumescheduler.structures.EventTime;
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

	protected EventTime input_starttime;
	protected EventTime input_endtime;
	protected int event_id = 0;


	final SlideDayTimeListener startListener = new SlideDayTimeListener() {

		@Override
		public void onDayTimeSet(int day, int hour, int minute) {
			input_starttime = new EventTime(day, hour, minute);
			setStartText();
		}

		@Override
		public void onDayTimeCancel() {
			Toast.makeText(CreateEventActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
		}
	};

	final SlideDayTimeListener endListener = new SlideDayTimeListener() {

		@Override
		public void onDayTimeSet(int day, int hour, int minute) {
			input_endtime = new EventTime(day, hour, minute);
			setEndText();
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

		setStartText();
		setEndText();
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

	private void setStartText() {
		buttonStart.setText(input_starttime.toStringWithDay(false));
	}

	private void setEndText() {
		buttonEnd.setText(input_endtime.toStringWithDay(false));
	}

	private void add_init() {
		Calendar c;

		c = Calendar.getInstance();

		this.input_starttime = new EventTime(c.get(Calendar.DAY_OF_WEEK), 9, 0);

		this.input_endtime = new EventTime(c.get(Calendar.DAY_OF_WEEK), 17, 0);
	}

	private void edit_init() {
		ScheduleEvent event;

		event = this.getIntent().getParcelableExtra(EVENT);

		this.event_id = event.getId();

		this.input_starttime = new EventTime(
			event.getStartDay(),
			event.getStartHour(),
			event.getStartMin()
		);

		this.input_endtime = new EventTime(
			event.getEndDay(),
			event.getEndHour(),
			event.getEndMin()
		);

		this.volumes = event.getVolumes();

		seekBarUpdate(this.volumes);
	}

	public void submitEvent(View v) {
		ScheduleEvent event;
		int vol1, vol2, vol3, vol4;
		boolean is_edit;
		Intent intent;

		is_edit = getIntent().getBooleanExtra(IS_EDIT, false);

		vol1 = this.ringSeek.getProgress();
		vol2 = this.mediaSeek.getProgress();
		vol3 = this.notifSeek.getProgress();
		vol4 = this.sysSeek.getProgress();

		if (silentCheck.isChecked()) {
			vol1 = -1;
		}

		event = new ScheduleEvent(
			input_starttime.getDay(),
			input_starttime.getHour(),
			input_starttime.getMin(),
			input_endtime.getDay(),
			input_endtime.getHour(),
			input_endtime.getMin(),
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

	public void cancelEvent(View v) {
		setResult(RESULT_CANCELED, new Intent());
		finish();
	}

	public void onCheckboxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();

		switch (view.getId()) {
			case R.id.check_silent:
				set_silent(checked);
		}
	}

	private void set_silent(boolean checked) {
		if (checked) {
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

	public void showTimePickerStart(View v) {
		new SlideDayTimePicker.Builder(getSupportFragmentManager())
			.setListener(startListener)
			.setInitialDay(input_starttime.getDay())
			.setInitialHour(input_starttime.getHour())
			.setInitialMinute(input_starttime.getMin())
			.setIs24HourTime(false)
			.build()
			.show();
	}

	public void showTimePickerEnd(View v) {
		new SlideDayTimePicker.Builder(getSupportFragmentManager())
			.setListener(endListener)
			.setInitialDay(input_endtime.getDay())
			.setInitialHour(input_endtime.getHour())
			.setInitialMinute(input_endtime.getMin())
			.setIs24HourTime(false)
			.build()
			.show();
	}
}
