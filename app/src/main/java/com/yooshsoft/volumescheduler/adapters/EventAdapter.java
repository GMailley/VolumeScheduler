package com.yooshsoft.volumescheduler.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;
import com.yooshsoft.volumescheduler.R;
import com.yooshsoft.volumescheduler.structures.ScheduleEvent;
import com.yooshsoft.volumescheduler.structures.VolumeSettings;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder>{

	private static final int VIEW_TYPE_HEADER = 0x01;

	private static final int VIEW_TYPE_CONTENT = 0x00;

	private static final int LINEAR = 0;

	private final ArrayList<LineItem> mItems;

	private int mHeaderDisplay;

	private boolean mMarginsFixed;

	private final Context mContext;

	public EventAdapter(Context context, int headerMode) {
		mContext = context;

		mHeaderDisplay = headerMode;

		mItems = new ArrayList<>();
	}

	public void addEvent(ScheduleEvent event) {
		int sectionManager = LINEAR;

		int sectionStart = 0;
		boolean added = false;
		int i;

		if (this.mItems.size() < 1) {
			this.mItems.add(new LineItem(event.getStartDay(), true, sectionManager, 0));
			this.mItems.add(new LineItem(event, false, sectionManager, 0));
			notifyDataSetChanged();
			return;
		}

		for (i = 0; i < this.mItems.size(); i++) {
			LineItem item = this.mItems.get(i);

			if (item.isHeader) {
				sectionStart = i;
				item.sectionFirstPosition = sectionStart;
				if (i>=1 && event.getStartDay() < item.day) {
					LineItem prev = this.mItems.get(i-1);
					if (event.getStartDay() > prev.event.getStartDay()) {
						this.mItems.add(i, new LineItem(event.getStartDay(), true, sectionManager, sectionStart));
						this.mItems.add(i + 1, new LineItem(event, false, sectionManager, sectionStart));
						added = true;
						break;
					} else {
						this.mItems.add(i, new LineItem(event, false, sectionManager, prev.sectionFirstPosition));
						added = true;
						break;
					}
				} else {
					continue;
				}
			}

			item.sectionFirstPosition = sectionStart;

			if (event.compareTo(item.event) < 0) {
				this.mItems.add(i, new LineItem(event, false, sectionManager, sectionStart));
				added = true;
				break;
			}
		}

		for (; i < this.mItems.size(); i++) {
			LineItem item = this.mItems.get(i);

			if (item.isHeader) {
				sectionStart = i;
			}

			item.sectionFirstPosition = sectionStart;
		}

		if (!added) {
			if (event.getStartDay() == mItems.get(mItems.size() - 1).event.getStartDay()) {
				this.mItems.add(new LineItem(event, false, sectionManager, sectionStart));
			} else {
				sectionStart = this.mItems.size();
				this.mItems.add(new LineItem(event.getStartDay(), true, sectionManager, sectionStart));
				this.mItems.add(new LineItem(event, false, sectionManager, sectionStart));
			}
		}

		notifyDataSetChanged();
	}

	public void removeEvent(int index)
	{
		if(this.mItems.size() == 0) {
			return;
		} else if(this.mItems.size() == 2) {
			this.clearEvents();
		} else if(index >= this.mItems.size()) {
			if(this.mItems.get(index-1).isHeader) {
				this.mItems.remove(index-1);
				this.mItems.remove(index-1);
			} else {
				this.mItems.remove(index);
			}
		} else {
			if(this.mItems.get(index-1).isHeader && this.mItems.get(index+1).isHeader) {
				this.mItems.remove(index-1);
				this.mItems.remove(index-1);
			} else {
				this.mItems.remove(index);
			}
		}
	}

	public void clearEvents()
	{
		this.mItems.clear();
	}

	public ScheduleEvent getEvent(int index)
	{
		return this.mItems.get(index).event;
	}

	public boolean isItemHeader(int position) {
		return mItems.get(position).isHeader;
	}

	public String itemToString(int position) {
		return "Hello there";
	}

	@Override
	public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == VIEW_TYPE_HEADER) {
			view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.header_item, parent, false);
		} else {
			view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.sched_event_item, parent, false);
		}
		return new EventViewHolder(view);
	}

	@Override
	public void onBindViewHolder(EventViewHolder holder, int position) {
		final LineItem item = mItems.get(position);
		final View itemView = holder.itemView;

		if (item.isHeader) {
			holder.bindItem(item.day);
		} else {
			holder.bindItem(item.event);
		}

		final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
		// Overrides xml attrs, could use different layouts too.
		if (item.isHeader) {
			lp.headerDisplay = mHeaderDisplay;
			if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
				lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
			} else {
				lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
			}

			lp.headerEndMarginIsAuto = !mMarginsFixed;
			lp.headerStartMarginIsAuto = !mMarginsFixed;
		}
		lp.setSlm(item.sectionManager == LINEAR ? LinearSLM.ID : GridSLM.ID);
		lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
		lp.setFirstPosition(item.sectionFirstPosition);
		itemView.setLayoutParams(lp);
	}

	@Override
	public int getItemViewType(int position) {
		return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void setHeaderDisplay(int headerDisplay) {
		mHeaderDisplay = headerDisplay;
		notifyHeaderChanges();
	}

	public void setMarginsFixed(boolean marginsFixed) {
		mMarginsFixed = marginsFixed;
		notifyHeaderChanges();
	}

	private void notifyHeaderChanges() {
		for (int i = 0; i < mItems.size(); i++) {
			LineItem item = mItems.get(i);
			if (item.isHeader) {
				notifyItemChanged(i);
			}
		}
	}

	private static class LineItem {

		public int sectionManager;

		public int sectionFirstPosition;

		public boolean isHeader;

		public int day;
		public ScheduleEvent event;

		public LineItem(int day, boolean isHeader, int sectionManager,
				int sectionFirstPosition) {
			this.isHeader = isHeader;
			this.day = day;
			this.event = null;
			this.sectionManager = sectionManager;
			this.sectionFirstPosition = sectionFirstPosition;
		}

		public LineItem(ScheduleEvent event, boolean isHeader, int sectionManager,
				int sectionFirstPosition) {
			this.isHeader = isHeader;
			this.day = 0;
			this.event = event;
			this.sectionManager = sectionManager;
			this.sectionFirstPosition = sectionFirstPosition;
		}
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

}
