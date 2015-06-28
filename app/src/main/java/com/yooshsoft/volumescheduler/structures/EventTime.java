package com.yooshsoft.volumescheduler.structures;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Garrett on 5/31/2015.
 */
public class EventTime implements Comparable<EventTime>, Parcelable{
	protected int time;

	public EventTime(int day, int hour, int min)
	{
		time = calctime(day, hour, min);
	}

	public EventTime(int time)
	{
		this.time = time;
	}

	public EventTime(int time, int duration)
	{
		this.time = time + duration;

		if(this.time >= 11520) {
			this.time -= 10080;
		}
	}

	public int getTime()
	{
		return this.time;
	}

	public int getDay() {
		int temp, min, hour, day;

		temp = this.time;
		min = temp % 60;
		temp -= min;

		hour = (temp / 60) % 24;
		temp -= hour * 60;

		day = temp / (60 * 24);

		return day;
	}

	public void setDay(int day)
	{
		int hour, min;

		hour = getHour();
		min = getMin();

		this.time = calctime(day, hour, min);
	}

	public int getHour()
	{
		int temp, min, hour;

		temp = this.time;
		min = temp % 60;
		temp -= min;

		hour = (temp / 60) % 24;

		return hour;
	}

	public void setHour(int hour)
	{
		int day, min;

		day = getDay();
		min = getMin();

		this.time = calctime(day, hour, min);
	}

	public int getMin()
	{
		return this.time % 60;
	}

	public void setMin(int min)
	{
		int day, hour;

		day = getDay();
		hour = getHour();

		this.time = calctime(day, hour, min);
	}

	public String toString() {
		int min, hour;
		String str = "";
		boolean pm = false;

		hour = getHour();
		min = getMin();

		if (hour >= 12) {
			hour -= 12;
			pm = true;
		}

		if(hour == 0) {
			hour = 12;
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

	public String toStringWithDay(boolean shortened)
	{
		String str = "";

		if(shortened) {
			str += dayStringShort(getDay());
		} else {
			str += dayString(getDay());
		}

		str += " " + toString();

		return str;
	}

	public int duration(EventTime endTime)
	{
		if(endTime.getTime() < this.time) {
			return (endTime.getTime() + 10080) - this.time;
		} else {
			return endTime.getTime() - this.time;
		}
	}

	public int compareTo(EventTime eventTime)
	{
		if(this.getDay() < eventTime.getDay()) {
			return -1;
		} else if(this.getDay() > eventTime.getDay()) {
			return 1;
		}

		if(this.getHour() < eventTime.getHour()) {
			return -1;
		} else if(this.getHour() > eventTime.getHour()) {
			return 1;
		}

		if(this.getMin() < eventTime.getMin()) {
			return -1;
		} else if(this.getMin() > eventTime.getMin()) {
			return 1;
		}

		return 0;
	}

	public boolean equals(Object o)
	{
		EventTime otherTime;

		if(!(o instanceof EventTime)) {
			return false;
		}

		otherTime = (EventTime) o;

		return this.time == otherTime.time;
	}

	public static String dayString(int day) {
		switch (day) {
			case 1:
				return "Sunday";
			case 2:
				return "Monday";
			case 3:
				return "Tuesday";
			case 4:
				return "Wednesday";
			case 5:
				return "Thursday";
			case 6:
				return "Friday";
			case 7:
				return "Saturday";
		}

		return null;
	}

	public static String dayStringShort(int day) {
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

	//Parcelable Stuff
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.time);
	}

	//Parcel constructor
	public EventTime(Parcel source) {
		this.time = source.readInt();
	}

	public static final Creator<EventTime> CREATOR = new Creator<EventTime>() {
		@Override
		public EventTime[] newArray(int size) {
			return new EventTime[size];
		}

		@Override
		public EventTime createFromParcel(Parcel source) {
			return new EventTime(source);
		}
	};


	private int calctime(int day, int hour, int min)
	{
		int ret;

		ret = day * 60 * 24;
		ret += hour * 60;
		ret += min;

		return ret;
	}
}
