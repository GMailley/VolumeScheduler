package com.yooshsoft.volumescheduler.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{
	public static class EventViewHolder extends RecyclerView.ViewHolder {
		CardView listItem;
		TextView eventStart;
		TextView eventEnd;
		TextView eventVolumes;

		EventViewHolder(View itemView) {
			super(itemView);
			listItem	= (CardView)itemView.findViewById(R.id.list_item);
			eventStart	= (TextView)itemView.findViewById(R.id.event_start);
			eventEnd	= (TextView)itemView.findViewById(R.id.event_end);
			eventVolumes	= (TextView)itemView.findViewById(R.id.event_volumes);
		}
	}

	List<ScheduleEvent> events;

	public EventAdapter(List<ScheduleEvent> events){
		this.events = events;
	}

	@Override
	public int getItemCount() {
		return events.size();
	}

	@Override
	public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sched_event_item, viewGroup, false);
		EventViewHolder evh = new EventViewHolder(v);
		return evh;
	}

	@Override
	public void onBindViewHolder(EventViewHolder personViewHolder, int i) {
		personViewHolder.eventStart.setText(events.get(i).startToString());
		personViewHolder.eventEnd.setText(events.get(i).endToString());
		personViewHolder.eventVolumes.setText(events.get(i).getVolumes().toString());
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	@Override
	public int getItemViewType(int position) {
		int viewType = 0;
		// add here your booleans or switch() to set viewType at your needed
		// I.E if (position == 0) viewType = 1; etc. etc.
		return viewType;
	}

}
