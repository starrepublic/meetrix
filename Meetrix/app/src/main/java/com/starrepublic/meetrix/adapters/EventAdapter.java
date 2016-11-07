package com.starrepublic.meetrix.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.calendar.model.Event;
import com.starrepublic.meetrix.R;

import java.util.List;

/**
 * Created by richard on 2016-10-28.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> items;
    private LayoutInflater inflator;


    public EventAdapter(Context context) {
        inflator = LayoutInflater.from(context);
    }

    public void setItems(List<Event> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(inflator.inflate(R.layout.view_event, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items==null?0:items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
