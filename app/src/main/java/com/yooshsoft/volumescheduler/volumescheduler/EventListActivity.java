package com.yooshsoft.volumescheduler.volumescheduler;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.adapters.EventCard;
import com.yooshsoft.volumescheduler.sqlite.EventTableDbHelper;
import com.yooshsoft.volumescheduler.sqlite.EventContract;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class EventListActivity extends AppCompatActivity
{
	public static int CREATE_EVENT_CODE = 1;
	public static int EDIT_EVENT_CODE = 2;

	//Views
	protected TextView mAddEvent;
	protected CardRecyclerView mRecycler;

	//Adapters
	protected CardArrayRecyclerViewAdapter mRecyclerAdapter;

	//Listeners
	protected Card.OnSwipeListener mSwipeListener;
	protected Card.OnCardClickListener mEditClickListener;

	//Database Connections
	protected SQLiteDatabase dbwrite;

	//Data
	protected List<Card> mCards;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);

		this.mCards = new ArrayList<>();

		init_all();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ScheduleEvent event;
		ArrayList<ScheduleEvent> events;

		if (resultCode == RESULT_CANCELED) {
			return;
		}

		if (resultCode == RESULT_OK) {
			if (requestCode == CREATE_EVENT_CODE)
			{
				events = data.getParcelableArrayListExtra(com.yooshsoft.volumescheduler.volumescheduler.CreateEventActivity.EVENTS);

				for(int i=0; i<events.size(); i++)
				{
					addEvent(events.get(i));
				}
			}
			else if (requestCode == EDIT_EVENT_CODE)
			{
				event = data.getParcelableExtra(com.yooshsoft.volumescheduler.volumescheduler.EditEventActivity.EVENT);
				editEvent(event);
			}
		}
	}

	private void init_all()
	{
		init_views();
		init_dbs();
		init_adapters();
		init_listeners();
		init_volumes();
		init_data();
	}

	private void init_views()
	{
		this.mAddEvent = (TextView) findViewById(R.id.list_add);

		this.mRecycler = (CardRecyclerView) findViewById(R.id.list_sched);
		this.mRecycler.setHasFixedSize(false);
		this.mRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
	}

	private void init_dbs()
	{
		EventTableDbHelper mDbHelper;

		mDbHelper = new EventTableDbHelper(this.getApplicationContext());
		this.dbwrite = mDbHelper.getReadableDatabase();
	}

	//Needs init_views
	private void init_adapters()
	{
		this.mRecyclerAdapter = new CardArrayRecyclerViewAdapter(getApplicationContext(), this.mCards);
		this.mRecycler.setAdapter(mRecyclerAdapter);
	}

	//Needs init_views
	private void init_listeners()
	{
		this.mSwipeListener = new Card.OnSwipeListener()
		{
			@Override
			public void onSwipe(Card card) {
				removeEvent((EventCard) card);
				mRecyclerAdapter.notifyDataSetChanged();
			}
		};

		View.OnClickListener addListen = new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventListActivity.this, com.yooshsoft.volumescheduler.volumescheduler.CreateEventActivity.class);

				startActivityForResult(intent, CREATE_EVENT_CODE);

				collapseCards();
			}
		};

		this.mAddEvent.setOnClickListener(addListen);

		this.mEditClickListener = new Card.OnCardClickListener()
		{
			@Override
			public void onClick(Card card, View view) {
				Intent intent;

				intent = new Intent(EventListActivity.this, com.yooshsoft.volumescheduler.volumescheduler.EditEventActivity.class);
				intent.putExtra(com.yooshsoft.volumescheduler.volumescheduler.EditEventActivity.EVENT, ((EventCard) card).getEvent());

				startActivityForResult(intent, EDIT_EVENT_CODE);

				collapseCards();

			}
		};
	}

	private void init_volumes()
	{
		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		VolumeSettings.setMaxVolumes(
			audio.getStreamMaxVolume(AudioManager.STREAM_RING),
			audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
			audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),
			audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
			AudioManager.RINGER_MODE_NORMAL
		);
	}

	private void init_data()
	{
		String orderBy;
		Cursor c;
		EventTableDbHelper mDbHelper;
		SQLiteDatabase db;

		mDbHelper = new EventTableDbHelper(this.getApplicationContext());

		db = mDbHelper.getReadableDatabase();

		String[] cols = {
			EventContract.EventColumns.COLUMN_NAME_EVENT_ID,
			EventContract.EventColumns.COLUMN_NAME_STARTTIME,
			EventContract.EventColumns.COLUMN_NAME_DURATION,
			EventContract.EventColumns.COLUMN_NAME_VOLUME_RING,
			EventContract.EventColumns.COLUMN_NAME_VOLUME_MEDIA,
			EventContract.EventColumns.COLUMN_NAME_VOLUME_NOTIF,
			EventContract.EventColumns.COLUMN_NAME_VOLUME_SYS,
			EventContract.EventColumns.COLUMN_NAME_RING_MODE
		};

		orderBy = EventContract.EventColumns.COLUMN_NAME_STARTTIME + ", " + EventContract.EventColumns.COLUMN_NAME_DURATION;

		c = db.query(EventContract.EventColumns.TABLE_NAME, cols, null, null, null, null, orderBy);
		if (!c.moveToFirst()) {
			return;
		}

		do {
			ScheduleEvent newEvent;
			EventCard newCard;

			int event_id = c.getInt(0);
			int starttime = c.getInt(1);
			int duration = c.getInt(2);
			int ring = c.getInt(3);
			int media = c.getInt(4);
			int notif = c.getInt(5);
			int sys = c.getInt(6);
			int ring_mode = c.getInt(7);

			newEvent = new ScheduleEvent(event_id, starttime, duration, ring, media, notif, sys, ring_mode);
			newCard = new EventCard(getApplicationContext(), newEvent);
			newCard.setOnSwipeListener(this.mSwipeListener);
			newCard.setOnClickListener(this.mEditClickListener);

			this.mCards.add(newCard);
		} while (c.moveToNext());

		c.close();
	}

	protected void addEvent(ScheduleEvent event) {
		int id;
		VolumeSettings vols;
		ContentValues values;

		vols = event.getVolumes();

		values = new ContentValues();
		values.put(EventContract.EventColumns.COLUMN_NAME_STARTTIME, event.getStarttime());
		values.put(EventContract.EventColumns.COLUMN_NAME_DURATION, event.getDuration());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_RING, vols.getRingtone());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_MEDIA, vols.getMedia());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_NOTIF, vols.getNotifications());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_SYS, vols.getSystem());

		id = (int) dbwrite.insertOrThrow(EventContract.EventColumns.TABLE_NAME, null, values);

		if (id > 0) {
			EventCard newCard;

			//successful insert
			event.setId(id);
			newCard = new EventCard(getApplicationContext(), event);
			newCard.setOnSwipeListener(this.mSwipeListener);
			newCard.setOnClickListener(this.mEditClickListener);

			sortedInsertCard(newCard);
			scheduleAlarm(event);
			this.mRecyclerAdapter.notifyDataSetChanged();
		} else {
			//insert failed
			Toast.makeText(this.getApplicationContext(), "Failed to create Event", Toast.LENGTH_SHORT).show();
		}
	}

	public void editEvent(ScheduleEvent event)
	{
		ContentValues values;
		String whereClause;
		boolean success;
		VolumeSettings vols;

		vols = event.getVolumes();

		values = new ContentValues();
		whereClause = EventContract.EventColumns.COLUMN_NAME_EVENT_ID + " = " + event.getId();

		values.put(EventContract.EventColumns.COLUMN_NAME_STARTTIME, event.getStarttime());
		values.put(EventContract.EventColumns.COLUMN_NAME_DURATION, event.getDuration());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_RING, vols.getRingtone());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_MEDIA, vols.getMedia());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_NOTIF, vols.getNotifications());
		values.put(EventContract.EventColumns.COLUMN_NAME_VOLUME_SYS, vols.getSystem());
		values.put(EventContract.EventColumns.COLUMN_NAME_RING_MODE, vols.getRingMode());

		success = dbwrite.updateWithOnConflict(EventContract.EventColumns.TABLE_NAME, values, whereClause, null, SQLiteDatabase.CONFLICT_REPLACE) > 0;

		if (success) {
			EventCard newCard = new EventCard(getApplicationContext(), event);
			newCard.setOnSwipeListener(this.mSwipeListener);
			newCard.setOnClickListener(this.mEditClickListener);

			Log.i("SUCCESS", "true");
			removeCard(event);
			cancelAlarm(event.getId());

			sortedInsertCard(newCard);
			scheduleAlarm(event);
		} else {
			Log.i("SUCCESS", "false");
		}
	}

	protected void removeEvent(EventCard card)
	{
		String where;

		where = EventContract.EventColumns.COLUMN_NAME_EVENT_ID + " = " + card.getEventId();

		// Successfully removed from DB
		if (dbwrite.delete(EventContract.EventColumns.TABLE_NAME, where, null) > 0) {
			cancelAlarm(card.getEventId());
			mCards.remove(card);
			mRecyclerAdapter.notifyDataSetChanged();
		}
		//failed to remove
		else {
			Toast toast = Toast.makeText(this.getApplicationContext(), "Failed to delete Event", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void scheduleAlarm(ScheduleEvent event)
	{
//		Context context;
//		AlarmManager alarmMgr;
//		Intent intent;
//		PendingIntent alarmIntent;
//		Calendar calendar;
//
//		context = this.getApplicationContext();
//		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		intent = new Intent(context, com.yooshsoft.volumescheduler.volumescheduler.StartAlarmReceiver.class);
//		calendar = Calendar.getInstance();
//
//		intent.putExtra(com.yooshsoft.volumescheduler.volumescheduler.StartAlarmReceiver.ALARM_EVENT, event);
//
//		alarmIntent = PendingIntent.getBroadcast(context, event.getId(), intent, 0);
//
//		calendar.setTimeInMillis(System.currentTimeMillis());
//		calendar.set(Calendar.DAY_OF_WEEK, event.getStartDay());
//		calendar.set(Calendar.HOUR_OF_DAY, event.getStartHour());
//		calendar.set(Calendar.MINUTE, event.getStartMin());
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
//
//		alarmMgr.setRepeating(
//			AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//			AlarmManager.INTERVAL_DAY, alarmIntent
//		);
//		Toast.makeText(this, "Alarm Scheduled for " + event.startToString(), Toast.LENGTH_LONG).show();
	}

	private void cancelAlarm(int event_id)
	{
//		Context context;
//		AlarmManager alarmMgr;
//		Intent intent;
//		PendingIntent alarmIntent;
//
//		context = this.getApplicationContext();
//		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//		intent = new Intent(context, com.yooshsoft.volumescheduler.volumescheduler.StartAlarmReceiver.class);
//		alarmIntent = PendingIntent.getBroadcast(context, event_id, intent, 0);
//		alarmMgr.cancel(alarmIntent);
//
//		intent = new Intent(context, com.yooshsoft.volumescheduler.volumescheduler.EndAlarmReceiver.class);
//		alarmIntent = PendingIntent.getBroadcast(context, event_id, intent, 0);
//		alarmMgr.cancel(alarmIntent);
	}

	private void sortedInsertCard(EventCard card)
	{
		for (int i = 0; i < this.mCards.size(); i++) {
			if (card.compareTo((EventCard) this.mCards.get(i)) < 0) {
				this.mCards.add(i, card);
				return;
			}
		}

		this.mCards.add(card);
	}

	private void removeCard(ScheduleEvent event)
	{
		EventCard card = new EventCard(getApplicationContext(), event);
		for (int i = 0; i < this.mCards.size(); i++) {
			if (card.compareTo((EventCard) this.mCards.get(i)) == 0) {
				this.mCards.remove(i);
				return;
			}
		}
	}

	private void collapseCards()
	{
		for(int i=0; i<this.mCards.size(); i++)
		{
			this.mCards.get(i).doCollapse();
		}
	}
}