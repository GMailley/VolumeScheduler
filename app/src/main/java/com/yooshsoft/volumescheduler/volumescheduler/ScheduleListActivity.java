package com.yooshsoft.volumescheduler.volumescheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tonicartos.superslim.LayoutManager;
import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.adapters.EventAdapter;
import com.yooshsoft.volumescheduler.sqlite.EventsContract;
import com.yooshsoft.volumescheduler.sqlite.EventTableDbHelper;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

public class ScheduleListActivity extends AppCompatActivity {
	private static final String KEY_HEADER_POSITIONING = "key_header_mode";
	private static final String KEY_MARGINS_FIXED = "key_margins_fixed";

	public static int CREATE_EVENT_CODE = 1;

	protected SQLiteDatabase dbwrite;
	protected SQLiteDatabase dbread;

	protected RecyclerView recyclerView;
	protected TextView addButton;

	protected EventAdapter eventAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		EventTableDbHelper mDbHelper;
		AudioManager audio;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_list);

		mDbHelper = new EventTableDbHelper(this.getApplicationContext());
		this.dbwrite = mDbHelper.getReadableDatabase();
		this.dbread = mDbHelper.getReadableDatabase();

		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		VolumeSettings.setMaxVolumes(
			audio.getStreamMaxVolume(AudioManager.STREAM_RING),
			audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
			audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),
			audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
			AudioManager.RINGER_MODE_NORMAL
		);

		getViews();

		setAddListener();

		init_recycler(savedInstanceState);

//		this.linearLayout = new LinearLayoutManager(getApplicationContext());
//		this.recyclerView.setAdapter(this.eventAdapter);
//		this.recyclerView.setLayoutManager(this.linearLayout);
//
//		recyclerView.setLayoutManager(new LayoutManager(getApplicationContext()));
//		eventAdapter = new EventAdapter(this.getApplicationContext(), mHeaderDisplay);
//		mAdapter.setMarginsFixed(mAreMarginsFixed);
//		mAdapter.setHeaderDisplay(mHeaderDisplay);
//		mViews.setAdapter(mAdapter);
//
//		this.eventAdapter = new EventAdapter(this.events);
//		this.linearLayout = new LinearLayoutManager(getApplicationContext());
//		this.recyclerView.setAdapter(this.eventAdapter);
//		this.recyclerView.setLayoutManager(this.linearLayout);
//
//		recyclerView.setLayoutManager(new LayoutManager(getApplicationContext()));
//		recyclerView.setAdapter(this.eventAdapter);

//		SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
//			recyclerView,
//			new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
//				@Override
//				public boolean canDismiss(int position) {
//					return true;
//				}
//
//				@Override
//				public void onDismiss(View view) {
//					// Do what you want when dismiss
//					int id = recyclerView.getChildPosition(view);
//					menu_remove(id);
//				}
//			})
//			.setIsVertical(false)
//			.setItemTouchCallback(
//				new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
//					@Override
//					public void onTouch(int index) {
//						// Do what you want when item be touched
//						menu_edit(index);
//					}
//				})
//			.create();
//		recyclerView.setOnTouchListener(listener);

		select_events();

		registerForContextMenu(recyclerView);
	}

	private void getViews() {
		this.recyclerView = (RecyclerView) findViewById(R.id.list_sched);
		this.addButton = (TextView) findViewById(R.id.list_add);
	}

	private void init_recycler(Bundle savedInstanceState) {
		int mHeaderDisplay;
		boolean mAreMarginsFixed;

		if (savedInstanceState != null) {
			mHeaderDisplay = savedInstanceState
				.getInt(KEY_HEADER_POSITIONING,
					getResources().getInteger(R.integer.default_header_display));
			mAreMarginsFixed = savedInstanceState
				.getBoolean(KEY_MARGINS_FIXED,
					getResources().getBoolean(R.bool.default_margins_fixed));
		} else {
			mHeaderDisplay = getResources().getInteger(R.integer.default_header_display);
			mAreMarginsFixed = getResources().getBoolean(R.bool.default_margins_fixed);
		}

		this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

		eventAdapter = new EventAdapter(getApplicationContext(), mHeaderDisplay);
		eventAdapter.setMarginsFixed(mAreMarginsFixed);
		eventAdapter.setHeaderDisplay(mHeaderDisplay);
		recyclerView.setAdapter(eventAdapter);

		SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
			recyclerView,
			new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
				@Override
				public boolean canDismiss(int position) {
					return true;
				}

				@Override
				public void onDismiss(View view) {
					// Do what you want when dismiss
					int id = recyclerView.getChildPosition(view);
					menu_remove(id);
				}
			})
			.setIsVertical(false)
			.setItemTouchCallback(
				new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
					@Override
					public void onTouch(int index) {
						// Do what you want when item be touched
						menu_edit(index);
					}
				})
			.create();
		recyclerView.setOnTouchListener(listener);
	}

	private void setAddListener() {
		OnClickListener addListen = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ScheduleListActivity.this, CreateEventActivity.class);

				intent.putExtra(CreateEventActivity.IS_EDIT, false);

				startActivityForResult(intent, CREATE_EVENT_CODE);
			}
		};

		this.addButton.setOnClickListener(addListen);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean is_edit;
		ScheduleEvent event;

		if (resultCode == RESULT_CANCELED) {
			return;
		}

		if (resultCode == RESULT_OK) {
			if (requestCode == CREATE_EVENT_CODE) {
				event = data.getParcelableExtra(CreateEventActivity.EVENT);
				is_edit = data.getBooleanExtra(CreateEventActivity.IS_EDIT, false);

				if (is_edit) {
					edit_event(event);
				} else {
					add_event(event);
				}
			}
		}
	}

	public void scheduleAlarm(ScheduleEvent event) {
		Context context;
		AlarmManager alarmMgr;
		Intent intent;
		PendingIntent alarmIntent;
		Calendar calendar;

		context = this.getApplicationContext();
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(context, StartAlarmReceiver.class);
		calendar = Calendar.getInstance();

		intent.putExtra(StartAlarmReceiver.ALARM_EVENT, event);

		alarmIntent = PendingIntent.getBroadcast(context, event.getId(), intent, 0);

		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.DAY_OF_WEEK, event.getStartDay());
		calendar.set(Calendar.HOUR_OF_DAY, event.getStartHour());
		calendar.set(Calendar.MINUTE, event.getStartMin());
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
			AlarmManager.INTERVAL_DAY, alarmIntent);
		Toast.makeText(this, "Alarm Scheduled for " + event.getStartHour() + ":" + (event.getStartMin() < 10 ? "0" + event.getStartMin() : event.getStartMin()), Toast.LENGTH_LONG).show();
	}

	public void cancelAlarm(int event_id) {
		Context context;
		AlarmManager alarmMgr;
		Intent intent;
		PendingIntent alarmIntent;

		context = this.getApplicationContext();
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		intent = new Intent(context, StartAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, event_id, intent, 0);
		alarmMgr.cancel(alarmIntent);

		intent = new Intent(context, EndAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, event_id, intent, 0);
		alarmMgr.cancel(alarmIntent);
	}

	protected void menu_edit(int index) {
		Intent intent;
		ScheduleEvent event;

		intent = new Intent(ScheduleListActivity.this, CreateEventActivity.class);
		event = eventAdapter.getEvent(index);

		intent.putExtra(CreateEventActivity.IS_EDIT, true);
		intent.putExtra(CreateEventActivity.EVENT, event);

		startActivityForResult(intent, CREATE_EVENT_CODE);
	}

	protected void menu_remove(int index) {
		int id = this.eventAdapter.getEvent(index).getId();
		if (!remove_event(id)) {
			Toast toast = Toast.makeText(this.getApplicationContext(), "Failed to delete Event", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		this.eventAdapter.removeEvent(index);
		cancelAlarm(id);
	}

	protected void select_events() {
		String orderBy;
		Cursor c;


		String[] cols = {
			EventsContract.EventColumns.COLUMN_NAME_EVENT_ID,
			EventsContract.EventColumns.COLUMN_NAME_STARTTIME,
			EventsContract.EventColumns.COLUMN_NAME_DURATION,
			EventsContract.EventColumns.COLUMN_NAME_VOLUME_RING,
			EventsContract.EventColumns.COLUMN_NAME_VOLUME_MEDIA,
			EventsContract.EventColumns.COLUMN_NAME_VOLUME_NOTIF,
			EventsContract.EventColumns.COLUMN_NAME_VOLUME_SYS,
			EventsContract.EventColumns.COLUMN_NAME_RING_MODE
		};

		orderBy = EventsContract.EventColumns.COLUMN_NAME_STARTTIME + ", " + EventsContract.EventColumns.COLUMN_NAME_DURATION;

		c = dbread.query(EventsContract.EventColumns.TABLE_NAME, cols, null, null, null, null, orderBy);
		if (!c.moveToFirst()) {
			return;
		}

		do {
			int event_id = c.getInt(0);
			int starttime = c.getInt(1);
			int duration = c.getInt(2);
			int ring = c.getInt(3);
			int media = c.getInt(4);
			int notif = c.getInt(5);
			int sys = c.getInt(6);
			int ring_mode = c.getInt(7);

			this.eventAdapter.addEvent(new ScheduleEvent(event_id, starttime, duration, ring, media, notif, sys, ring_mode));
		} while (c.moveToNext());

		c.close();
	}

	protected void add_event(ScheduleEvent event) {
		int id;
		VolumeSettings vols;
		ContentValues values;

		vols = event.getVolumes();

		values = new ContentValues();
		values.put(EventsContract.EventColumns.COLUMN_NAME_STARTTIME, event.getStarttime());
		values.put(EventsContract.EventColumns.COLUMN_NAME_DURATION, event.getDuration());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_RING, vols.getRingtone());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_MEDIA, vols.getMedia());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_NOTIF, vols.getNotifications());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_SYS, vols.getSystem());

		id = (int) dbread.insertOrThrow(EventsContract.EventColumns.TABLE_NAME, null, values);

		if (id > 0) {
			//successful insert
			event.setId(id);
			this.eventAdapter.addEvent(event);
			scheduleAlarm(event);
		} else {
			//insert failed
			Toast.makeText(this.getApplicationContext(), "Failed to create Event", Toast.LENGTH_SHORT).show();
		}
	}

	protected void edit_event(ScheduleEvent event) {
		ContentValues values;
		String whereClause;
		boolean success;
		VolumeSettings vols;

		vols = event.getVolumes();

		values = new ContentValues();
		whereClause = EventsContract.EventColumns.COLUMN_NAME_EVENT_ID + " = " + event.getId();

		values.put(EventsContract.EventColumns.COLUMN_NAME_STARTTIME, event.getStarttime());
		values.put(EventsContract.EventColumns.COLUMN_NAME_DURATION, event.getDuration());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_RING, vols.getRingtone());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_MEDIA, vols.getMedia());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_NOTIF, vols.getNotifications());
		values.put(EventsContract.EventColumns.COLUMN_NAME_VOLUME_SYS, vols.getSystem());
		values.put(EventsContract.EventColumns.COLUMN_NAME_RING_MODE, vols.getRingMode());

		success = dbwrite.updateWithOnConflict(EventsContract.EventColumns.TABLE_NAME, values, whereClause, null, SQLiteDatabase.CONFLICT_REPLACE) > 0;

		if (success) {
			Log.i("SUCCESS", "true");
			eventAdapter.clearEvents();
			select_events();
			cancelAlarm(event.getId());
			scheduleAlarm(event);
		} else {
			Log.i("SUCCESS", "false");
		}
	}

	protected boolean remove_event(int id) {
		boolean success;
		String where;

		where = EventsContract.EventColumns.COLUMN_NAME_EVENT_ID + " = " + id;
		success = dbwrite.delete(EventsContract.EventColumns.TABLE_NAME, where, null) > 0;

		return success;
	}

}
