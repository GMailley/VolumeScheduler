package com.yooshsoft.volumescheduler.structures;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class ScheduleEvent implements Comparable<ScheduleEvent>, Parcelable {
	protected int id;
	protected EventTime starttime;
	protected EventTime endtime;
	protected VolumeSettings volumes;

	public ScheduleEvent(int id, int starttime, int duration, int vol1, int vol2, int vol3, int vol4, int ring_mode) {
		this.id = id;

		this.starttime = new EventTime(starttime);
		this.endtime = new EventTime(starttime, duration);

		volumes = new VolumeSettings(vol1, vol2, vol3, vol4, ring_mode);
	}

	public ScheduleEvent(int startday, int starthour, int startmin, int endday, int endhour, int endmin, VolumeSettings v) {
		this.id = -1;
		this.starttime = new EventTime(startday, starthour, startmin);
		this.endtime = new EventTime(endday, endhour, endmin);
		this.volumes = v;
	}


	public int getStartDay() {
		return this.starttime.getDay();
	}

	public int getEndDay() {
		return this.endtime.getDay();
	}

	public int getStartHour() {
		return this.starttime.getHour();
	}

	public int getEndHour() {
		return this.endtime.getHour();
	}

	public int getStartMin() {
		return this.starttime.getMin();
	}

	public int getEndMin() {
		return this.endtime.getMin();
	}

	public int compareTo(@NonNull ScheduleEvent another) {
		return this.starttime.compareTo(another.starttime);
	}

	public boolean equals(Object object) {
		ScheduleEvent sched;

		if (!(object instanceof ScheduleEvent)) {
			return false;
		}

		sched = (ScheduleEvent) object;

		return starttime.equals(sched.starttime) && this.getDuration() == sched.getDuration();
	}


	public String startToString() {
		return this.starttime.toString();
	}

	public String endToString() {
		return this.endtime.toString();
	}

	public VolumeSettings getVolumes() {
		return this.volumes;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStarttime() {
		return this.starttime.getTime();
	}

	public int getDuration() {
		return this.starttime.duration(this.endtime);
	}


	//Parcelable Stuff
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeInt(this.starttime.getTime());
		dest.writeInt(this.endtime.getTime());
		dest.writeInt(this.volumes.getRingtone());
		dest.writeInt(this.volumes.getMedia());
		dest.writeInt(this.volumes.getNotifications());
		dest.writeInt(this.volumes.getSystem());
		dest.writeInt(this.volumes.getRingMode());
	}

	//Parcel constructor
	public ScheduleEvent(Parcel source) {
		this.id = source.readInt();
		this.starttime = new EventTime(source.readInt());
		this.endtime = new EventTime(source.readInt());
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
