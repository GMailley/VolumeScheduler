package com.yooshsoft.volumescheduler.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

public class ScheduleAdapter extends ArrayAdapter<ScheduleEvent> {
	protected ArrayList<Object> data;

	public ScheduleAdapter(Context context, int layoutResourceId, List<ScheduleEvent> events) {
		super(context, layoutResourceId, events);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ScheduleEvent event;
		VolumeSettings volumes;
		TextView start, end, info;

		// Get the data item for this position
		event = getItem(position);
		volumes = event.getVolumes();

		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.sched_event_item, parent, false);
		}

		// Lookup view for data population
		start = (TextView) convertView.findViewById(R.id.event_start);
		end = (TextView) convertView.findViewById(R.id.event_end);
		info = (TextView) convertView.findViewById(R.id.event_volumes);

		convertView.setTag(R.integer.view_tag_id, event.getId());

		// Populate the data into the template view using the data object
		start.setText(event.startToString());
		end.setText(event.endToString());
		info.setText(volumes.toString());

		// Return the completed view to render on screen
		if (position % 2 == 0) {
			convertView.setBackgroundColor(Color.parseColor("#333333"));
		} else {
			convertView.setBackgroundColor(Color.parseColor("#494949"));
		}

		return convertView;
	}
}