package com.yooshsoft.volumescheduler.sqlite;

import android.provider.BaseColumns;

public final class EventDayContract
{
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public EventDayContract() {}

	/* Inner class that defines the table contents */
	public static abstract class EventDayColumns implements BaseColumns {
		public static final String TABLE_NAME = "eventDay";
		public static final String COLUMN_NAME_EVENT_ID = "event_id";
		public static final String COLUMN_NAME_DAY = "day";
	}

	protected static final String SQL_CREATE_EVENTS =
		"CREATE TABLE IF NOT EXISTS " + EventDayColumns.TABLE_NAME + " (" +
			EventDayColumns.COLUMN_NAME_EVENT_ID + " INTEGER, " +
			EventDayColumns.COLUMN_NAME_DAY	+ " INTEGER, " +
			"PRIMARY KEY (" + EventDayColumns.COLUMN_NAME_EVENT_ID +", "+ EventDayColumns.COLUMN_NAME_DAY + ")" +
			")" +
		" )";

	protected static final String SQL_DELETE_EVENTS =
		"DROP TABLE IF EXISTS " + EventDayColumns.TABLE_NAME;

}