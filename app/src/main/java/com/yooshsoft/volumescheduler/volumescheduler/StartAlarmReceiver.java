package com.yooshsoft.volumescheduler.volumescheduler;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import java.util.Calendar;

public class StartAlarmReceiver extends BroadcastReceiver {

	public static final String ALARM_EVENT = "alarm_event";

	@Override
	public void onReceive(Context context, Intent intent) {
		ScheduleEvent event;

		event = intent.getParcelableExtra(ALARM_EVENT);

		send_notification(context, event);
		schedule_alarm(context, event);
		setVolumes(context, event.getVolumes());
	}

	private void send_notification(Context context, ScheduleEvent event)
	{
		Calendar c;
		int hour, min, ampm;
		int mNotificationId;
		CharSequence text;
		NotificationCompat.Builder mBuilder;
		NotificationManager mNotifyMgr;

		c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR);
		min = c.get(Calendar.MINUTE);
		ampm = c.get(Calendar.AM_PM);

		text = "Start Alarm! At " + hour + ":" + (min < 10 ? "0" + min : min) + (ampm == 0 ? "AM" : "PM") + " ID: " + event.getId();

		mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("My notification")
				.setContentText(text);

		mNotificationId = event.getId();
		mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
	}

	private void schedule_alarm(Context context, ScheduleEvent event)
	{
		AlarmManager alarmMgr;
		Intent intent;
		AudioManager audio;
		VolumeSettings vols;
		PendingIntent alarmIntent;
		Calendar calendar;

		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(context, EndAlarmReceiver.class);
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		calendar = Calendar.getInstance();

		vols = new VolumeSettings(
			audio.getStreamVolume(AudioManager.STREAM_RING),
			audio.getStreamVolume(AudioManager.STREAM_MUSIC),
			audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION),
			audio.getStreamVolume(AudioManager.STREAM_SYSTEM),
			audio.getRingerMode()
		);

		intent.putExtra(EndAlarmReceiver.EVENT_ID, event.getId());
		intent.putExtra(EndAlarmReceiver.ALARM_VOLUMES, vols);
		alarmIntent = PendingIntent.getBroadcast(context, event.getId(), intent, 0);

		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.DAY_OF_WEEK, event.getEndDay());
		calendar.set(Calendar.HOUR_OF_DAY, event.getEndHour());
		calendar.set(Calendar.MINUTE, event.getEndMin());
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
	}

	protected void setVolumes(Context context, VolumeSettings vols) {
		AudioManager audio;

 		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		audio.setStreamVolume(AudioManager.STREAM_RING, vols.getRingtone(), 0);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, vols.getMedia(), 0);
		audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, vols.getNotifications(), 0);
		audio.setStreamVolume(AudioManager.STREAM_SYSTEM, vols.getSystem(), 0);
		audio.setRingerMode(vols.getRingMode());
	}

}