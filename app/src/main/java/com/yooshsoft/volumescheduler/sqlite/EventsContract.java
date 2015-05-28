package com.yooshsoft.volumescheduler.sqlite;

import android.provider.BaseColumns;

public final class EventsContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public EventsContract() {}

	/* Inner class that defines the table contents */
	public static abstract class EventColumns implements BaseColumns {
		public static final String TABLE_NAME = "event";
		public static final String COLUMN_NAME_EVENT_ID = "eventid";
		public static final String COLUMN_NAME_STARTTIME = "starttime";
		public static final String COLUMN_NAME_DURATION = "duration";
		public static final String COLUMN_NAME_VOLUME_RING = "volume_ring";
		public static final String COLUMN_NAME_VOLUME_MEDIA = "volume_media";
		public static final String COLUMN_NAME_VOLUME_NOTIF = "volume_notif";
		public static final String COLUMN_NAME_VOLUME_SYS = "volume_sys";
		public static final String COLUMN_NAME_RING_MODE = "ring_mode";
	}

	private static final String TINYINT_TYPE = " TINYINT";
	private static final String SMALLINT_TYPE = " SMALLINT";
	private static final String COMMA_SEP = ", ";

	protected static final String SQL_CREATE_EVENTS =
		"CREATE TABLE IF NOT EXISTS " + EventColumns.TABLE_NAME + " (" +
			EventColumns.COLUMN_NAME_EVENT_ID + " INTEGER PRIMARY KEY, " +
			EventColumns.COLUMN_NAME_STARTTIME + SMALLINT_TYPE + COMMA_SEP +
			EventColumns.COLUMN_NAME_DURATION + SMALLINT_TYPE + COMMA_SEP +
			EventColumns.COLUMN_NAME_VOLUME_RING + TINYINT_TYPE + COMMA_SEP +
			EventColumns.COLUMN_NAME_VOLUME_MEDIA + TINYINT_TYPE + COMMA_SEP +
			EventColumns.COLUMN_NAME_VOLUME_NOTIF + TINYINT_TYPE + COMMA_SEP +
			EventColumns.COLUMN_NAME_VOLUME_SYS + TINYINT_TYPE + COMMA_SEP +
			EventColumns.COLUMN_NAME_RING_MODE + TINYINT_TYPE + COMMA_SEP +
			"CONSTRAINT uc_StartUnique UNIQUE (" +
			EventColumns.COLUMN_NAME_STARTTIME + COMMA_SEP +
			EventColumns.COLUMN_NAME_DURATION +
			")" +
		" )";

	protected static final String SQL_DELETE_EVENTS =
		"DROP TABLE IF EXISTS " + EventColumns.TABLE_NAME;

}