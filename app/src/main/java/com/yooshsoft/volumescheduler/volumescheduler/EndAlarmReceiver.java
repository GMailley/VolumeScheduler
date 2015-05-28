package com.yooshsoft.volumescheduler.volumescheduler;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import java.util.Calendar;

public class EndAlarmReceiver extends BroadcastReceiver {

	public static final String EVENT_ID = "event_id";
	public static final String ALARM_VOLUMES = "alarm_volumes";

	@Override
	public void onReceive(Context context, Intent intent) {
		int event_id;
		VolumeSettings vols;

		event_id = intent.getIntExtra(EVENT_ID, 0);
		vols = intent.getParcelableExtra(ALARM_VOLUMES);

		send_notification(context, event_id);
		setVolumes(context, vols);
	}

	private void send_notification(Context context, int event_id)
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

		text = "End Alarm! At " + hour + ":" + (min < 10 ? "0" + min : min) + (ampm == 0 ? "AM" : "PM") + " ID: " + event_id;

		mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle("My notification")
			.setContentText(text);

		mNotificationId = event_id;
		mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
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