package com.yooshsoft.volumescheduler.adapters;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Garrett on 6/13/2015.
 */
public class EventExpandCard extends CardExpand
{
	VolumeSettings mVolumes;

	public EventExpandCard(Context context, VolumeSettings volumes) {
		super(context, R.layout.event_expand);

		this.mVolumes = volumes;
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {

		if (view == null) return;

		//Retrieve TextView elements
		SeekBar ring = (SeekBar) view.findViewById(R.id.expand_ring);
		SeekBar media = (SeekBar) view.findViewById(R.id.expand_media);
		SeekBar notif = (SeekBar) view.findViewById(R.id.expand_notifications);
		SeekBar system = (SeekBar) view.findViewById(R.id.expand_system);

		ring.setMax(VolumeSettings.getMaxRing());
		media.setMax(VolumeSettings.getMaxMedia());
		notif.setMax(VolumeSettings.getMaxNotifications());
		system.setMax(VolumeSettings.getMaxSystem());

		ring.setProgress(this.mVolumes.getRingtone());
		media.setProgress(this.mVolumes.getMedia());
		notif.setProgress(this.mVolumes.getNotifications());
		system.setProgress(this.mVolumes.getSystem());

		ring.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			}
		);

		media.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			}
		);

		notif.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			}
		);

		system.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			}
		);

		parent.setBackgroundColor(mContext.getResources().
				getColor(R.color.expand_background));
	}
}
