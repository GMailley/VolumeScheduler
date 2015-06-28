package com.yooshsoft.volumescheduler.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.EventTime;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

public class EventCard extends Card implements Comparable<EventCard>
{
	protected TextView mStart;
	protected TextView mEnd;

	protected ScheduleEvent mEvent;

	public EventCard(Context context, ScheduleEvent event) {
		super(context, R.layout.event_card);
		this.mEvent = event;
		init();
	}

	private void init()
	{
		CardHeader header = new CardHeader(getContext());
		header.setButtonExpandVisible(true);
		header.setTitle(EventTime.dayString(this.mEvent.getStartDay()));
		addCardHeader(header);

		EventExpandCard expand = new EventExpandCard(getContext(), this.mEvent.getVolumes());
		addCardExpand(expand);

		//Set clickListener
		setOnClickListener(
			new OnCardClickListener()
			{
				@Override
				public void onClick(Card card, View view) {
					Toast.makeText(getContext(), "Click Listener card", Toast.LENGTH_LONG).show();
				}
			}
		);
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		this.mStart = (TextView) parent.findViewById(R.id.card_start);
		this.mEnd = (TextView) parent.findViewById(R.id.card_end);

		this.mStart.setText(this.mEvent.startToString());
		this.mEnd.setText(this.mEvent.endToString());
		setSwipeable(true);
	}

	public ScheduleEvent getEvent()
	{
		return this.mEvent;
	}

	@Override
	public int compareTo(EventCard card)
	{
		return this.mEvent.compareTo(card.mEvent);
	}

	public int getEventId()
	{
		return this.mEvent.getId();
	}
}
