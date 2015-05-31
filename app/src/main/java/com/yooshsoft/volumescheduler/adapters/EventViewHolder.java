package com.yooshsoft.volumescheduler.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;

/**
 * Simple view holder for a single text view.
 */
class EventViewHolder extends RecyclerView.ViewHolder {

	private TextView mTextView;

	private TextView eventStart;
	private TextView eventEnd;
	private TextView eventVolumes;

	EventViewHolder(View view) {
		super(view);

		mTextView = (TextView) view.findViewById(R.id.text);

		eventStart = (TextView) view.findViewById(R.id.event_start);
		eventEnd = (TextView) view.findViewById(R.id.event_end);
		eventVolumes = (TextView) view.findViewById(R.id.event_volumes);
	}

	public void bindItem(int day) {
		mTextView.setText(daystring(day));
	}

	public void bindItem(ScheduleEvent event) {
		eventStart.setText(event.startToString());
		eventEnd.setText(event.endToString());
		eventVolumes.setText(event.getVolumes().toString());
	}

	@Override
	public String toString() {
		return mTextView.getText().toString();
	}

	private String daystring(int day) {
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
}
