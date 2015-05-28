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
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yooshsoft.volumeschedule.R;
import com.yooshsoft.volumeschedule.adapters.EventAdapter;
import com.yooshsoft.volumeschedule.adapters.ScheduleAdapter;
import com.yooshsoft.volumeschedule.sqlite.EventsContract;
import com.yooshsoft.volumeschedule.sqlite.EventTableDbHelper;
import com.yooshsoft.volumeschedule.structures.ScheduleEvent;
import com.yooshsoft.volumeschedule.structures.VolumeSettings;

import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

public class ScheduleListActivity extends AppCompatActivity {
	public static int CREATE_EVENT_CODE = 1;

	protected SQLiteDatabase dbwrite;
	protected SQLiteDatabase dbread;

	protected RecyclerView recyclerView;
	protected TextView addButton;

	protected List<ScheduleEvent> events;

	protected EventAdapter eventAdapter;
	protected LinearLayoutManager linearLayout;

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

		this.events = new ArrayList<>();

		this.eventAdapter = new EventAdapter(this.events);
		this.linearLayout = new LinearLayoutManager(getApplicationContext());
		this.recyclerView.setAdapter(this.eventAdapter);
		this.recyclerView.setLayoutManager(this.linearLayout);

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

		select_events();

		registerForContextMenu(recyclerView);
	}

	private void getViews() {
		this.recyclerView = (RecyclerView) findViewById(R.id.list_sched);
		this.addButton = (TextView) findViewById(R.id.list_add);
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

				this.eventAdapter.notifyDataSetChanged();
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == recyclerView.getId()) {
			super.onCreateContextMenu(menu, v, menuInfo);
			MenuInflater m = getMenuInflater();
			m.inflate(R.menu.event_item, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		int index;

		info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		index = info.position;

		switch (item.getItemId()) {
			case R.id.schedule_menu_edit:
				menu_edit(index);
				return true;
			case R.id.schedule_menu_remove:
				menu_remove(index);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	protected void menu_edit(int index) {
		Intent intent;
		ScheduleEvent event;

		intent = new Intent(ScheduleListActivity.this, CreateEventActivity.class);
		event = events.get(index);

		intent.putExtra(CreateEventActivity.IS_EDIT, true);
		intent.putExtra(CreateEventActivity.EVENT, event);

		startActivityForResult(intent, CREATE_EVENT_CODE);
	}

	protected void menu_remove(int index) {
		int id = this.events.get(index).getId();
		if(!remove_event(id)) {
			Toast toast = Toast.makeText(this.getApplicationContext(), "Failed to delete Event", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		this.events.remove(index);
		this.eventAdapter.notifyDataSetChanged();
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

			this.events.add(new ScheduleEvent(event_id, starttime, duration, ring, media, notif, sys, ring_mode));
		} while (c.moveToNext());

		this.eventAdapter.notifyDataSetChanged();

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
			this.events.add(event);
			Collections.sort(events);
			this.eventAdapter.notifyDataSetChanged();
			scheduleAlarm(event);
		} else {
			//insert failed
			Toast.makeText(this.getApplicationContext(), "Failed to create Event", Toast.LENGTH_SHORT).show();
		}
	}

	protected void edit_event(ScheduleEvent event)
	{
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

		if(success) {
			Log.i("SUCCESS", "true");
			this.events.clear();
			select_events();
			cancelAlarm(event.getId());
			scheduleAlarm(event);
		} else {
			Log.i("SUCCESS", "false");
		}
	}

	protected boolean remove_event(int id)
	{
		boolean success;
		String where;

		where = EventsContract.EventColumns.COLUMN_NAME_EVENT_ID + " = " + id;
		success = dbwrite.delete(EventsContract.EventColumns.TABLE_NAME, where, null) > 0;

		return success;
	}

}
