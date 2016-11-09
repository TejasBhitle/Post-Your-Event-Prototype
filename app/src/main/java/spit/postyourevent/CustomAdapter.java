package spit.postyourevent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import spit.postyourevent.Database.EventData;

/**
 * Created by DELL on 13/10/2016.
 */

public class CustomAdapter extends  RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    Context context;
    ArrayList<EventData> arrayList;
    int lastPosition = -1;

    public CustomAdapter(Context context, ArrayList<EventData> arrayList) {
        this.arrayList= arrayList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView eventNameTextView,timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = (TextView)itemView.findViewById(R.id.custom_row_eventname);
            timeTextView = (TextView)itemView.findViewById(R.id.custom_row_time);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View customView = inflater.inflate(R.layout.single_entry,parent,false);
        ViewHolder viewHolder = new ViewHolder(customView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventData eventData = arrayList.get(position);
        holder.timeTextView.setText(eventData.geteventTime());
        holder.eventNameTextView.setText(eventData.getName());

        if(position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition= position;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }



}