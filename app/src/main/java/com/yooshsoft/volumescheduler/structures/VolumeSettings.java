package com.yooshsoft.volumescheduler.structures;

import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;

public class VolumeSettings implements Parcelable{
	protected static VolumeSettings MAX_VOLUMES;

	protected int ringtone;
	protected int media;
	protected int notifications;
	protected int system;
	protected int ring_mode;

	public VolumeSettings(int ringtone, int media, int notifications, int system, int ring_mode) {
		this.ringtone = ringtone;
		this.media = media;
		this.notifications = notifications;
		this.system = system;
		this.ring_mode = ring_mode;
	}

	public VolumeSettings(int ringtone, int media, int notifications, int system) {
		if(ringtone >= 0) {
			this.ringtone = ringtone;
			this.media = media;
			this.notifications = notifications;
			this.system = system;
			this.ring_mode = AudioManager.RINGER_MODE_NORMAL;
		} else {
			this.ringtone = 0;
			this.media = media;
			this.notifications = 0;
			this.system = 0;
			this.ring_mode = AudioManager.RINGER_MODE_SILENT;
		}
	}

	public void setRingtone(int vol) {
		this.ringtone = vol;
	}

	public void setMedia(int vol) {
		this.media = vol;
	}

	public void setNotifications(int vol) {
		this.notifications = vol;
	}

	public void setSystem(int vol) {
		this.system = vol;
	}

	public void setRingMode(int ring_mode) {
		this.ring_mode = ring_mode;
	}

	public int getRingtone() {
		return this.ringtone;
	}

	public int getMedia() {
		return this.media;
	}

	public int getNotifications() {
		return this.notifications;
	}

	public int getSystem() {
		return this.system;
	}

	public int getRingMode() {
		return this.ring_mode;
	}

	public String toString() {
		String str;

		int ring_percent = (int) (((float) this.getRingtone() / (float) MAX_VOLUMES.getRingtone()) * 100.0);
		int media_percent = (int) (((float) this.getMedia() / (float) MAX_VOLUMES.getMedia()) * 100.0);
		int notif_percent = (int) (((float) this.getNotifications() / (float) MAX_VOLUMES.getNotifications()) * 100.0);
		int system_percent = (int) (((float) this.getSystem() / (float) MAX_VOLUMES.getSystem()) * 100.0);

		str = "R " + ring_percent + "% M " + media_percent + "% N " + notif_percent + "% S " + system_percent + "%";

		return str;
	}

	public static void setMaxVolumes(int ring, int media, int notif, int sys, int vibrate) {
		MAX_VOLUMES = new VolumeSettings(ring, media, notif, sys, vibrate);
	}

//	public static VolumeSettings getMaxVolumes()
//	{
//		return MAX_VOLUMES;
//	}


	//Parcelable Stuff

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.ringtone);
		dest.writeInt(this.media);
		dest.writeInt(this.notifications);
		dest.writeInt(this.system);
		dest.writeInt(this.ring_mode);
	}

	//Parcel constructor
	public VolumeSettings(Parcel source)
	{
		this.ringtone = source.readInt();
		this.media = source.readInt();
		this.notifications = source.readInt();
		this.system = source.readInt();
		this.ring_mode = source.readInt();
	}

	public static final Creator<VolumeSettings> CREATOR = new Creator<VolumeSettings>() {
		@Override
		public VolumeSettings[] newArray(int size) {
			return new VolumeSettings[size];
		}

		@Override
		public VolumeSettings createFromParcel(Parcel source) {
			return new VolumeSettings(source);
		}
	};
}
