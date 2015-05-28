package com.yooshsoft.volumescheduler.structures;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class ScheduleEvent implements Comparable<ScheduleEvent>, Parcelable {
	protected int id;
	protected int starttime;
	protected int endtime;
	protected VolumeSettings volumes;

	public ScheduleEvent(int id, int starttime, int duration, int vol1, int vol2, int vol3, int vol4, int ring_mode)
	{
		this.id = id;
		this.starttime = starttime;
		this.endtime = starttime + duration;

		if(this.endtime > 10080) {
			this.endtime -= 10080;
		}

		volumes = new VolumeSettings(vol1, vol2, vol3, vol4, ring_mode);
	}

	public ScheduleEvent(int startday, int starthour, int startmin, int endday, int endhour, int endmin, VolumeSettings v) {
		this.id = -1;
		this.starttime = calctime(startday, starthour, startmin);
		this.endtime = calctime(endday, endhour, endmin);
		this.volumes = v;
	}


	public int getStartDay() {
		return getday(this.starttime);
	}

	public int getEndDay() {
		return getday(this.endtime);
	}

	public int getStartHour() {
		int time, min, hour;

		time = starttime;
		min = time % 60;
		time -= min;

		hour = (time / 60) % 24;

		return hour;
	}

	public int getEndHour() {
		int time, min, hour;

		time = this.endtime;
		min = time % 60;
		time -= min;

		hour = (time / 60) % 24;

		return hour;
	}

	public int getStartMin() {
		return this.starttime % 60;
	}

	public int getEndMin() {
		return this.endtime % 60;
	}

	public int compareTo(@NonNull ScheduleEvent another) {
		return this.starttime - another.starttime;
	}

	public boolean equals(Object object) {
		ScheduleEvent sched;

		if (!(object instanceof ScheduleEvent)) {
			return false;
		}

		sched = (ScheduleEvent) object;

		return starttime == sched.starttime && this.getDuration()==sched.getDuration();
	}


	public String startToString() {
		int day, hour, min;
		String str;

		day = getStartDay();
		hour = getStartHour();
		min = getStartMin();

		str = daytostring(day);
		str += " ";
		str += timetostring(hour, min);

		return str;
	}

	public String endToString() {
		int day, hour, min;
		String str;

		day = getEndDay();
		hour = getEndHour();
		min = getEndMin();

		str = daytostring(day);
		str += " ";
		str += timetostring(hour, min);

		return str;
	}

	public VolumeSettings getVolumes() {
		return this.volumes;
	}

	public int getId() { return this.id; }

	public void setId(int id) { this.id = id; }

	public int getStarttime() { return this.starttime; }

	public int getDuration()
	{
		if(this.endtime < this.starttime) {
			return (this.endtime + 10080) - this.starttime;
		} else {
			return this.endtime - this.starttime;
		}
	}


	// PRIVATE HELPER METHODS

	private int calctime(int day, int hour, int min) {
		int time;

		time = day * 60 * 24;
		time += hour * 60;
		time += min;

		return time;
	}

	private int getday(int time) {
		int min, hour, day;

		min = time % 60;
		time -= min;

		hour = (time / 60) % 24;
		time -= hour * 60;

		day = time / (60 * 24);

		return day;
	}

	private String daytostring(int day) {
		switch (day) {
			case 1:
				return "Sun";
			case 2:
				return "Mon";
			case 3:
				return "Tue";
			case 4:
				return "Wed";
			case 5:
				return "Thu";
			case 6:
				return "Fri";
			case 7:
				return "Sat";
		}

		return null;
	}

	private String timetostring(int hour, int min) {
		String str = "";
		boolean pm = false;

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

	//Parcelable Stuff

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeInt(this.starttime);
		dest.writeInt(this.endtime);
		dest.writeInt(this.volumes.getRingtone());
		dest.writeInt(this.volumes.getMedia());
		dest.writeInt(this.volumes.getNotifications());
		dest.writeInt(this.volumes.getSystem());
		dest.writeInt(this.volumes.getRingMode());
	}

	//Parcel constructor
	public ScheduleEvent(Parcel source)
	{
		this.id = source.readInt();
		this.starttime = source.readInt();
		this.endtime = source.readInt();
		this.volumes = new VolumeSettings(
			source.readInt(),
			source.readInt(),
			source.readInt(),
			source.readInt(),
			source.readInt()
		);
	}

	public static final Creator<ScheduleEvent> CREATOR = new Creator<ScheduleEvent>() {
		@Override
		public ScheduleEvent[] newArray(int size) {
			return new ScheduleEvent[size];
		}

		@Override
		public ScheduleEvent createFromParcel(Parcel source) {
			return new ScheduleEvent(source);
		}
	};
}
