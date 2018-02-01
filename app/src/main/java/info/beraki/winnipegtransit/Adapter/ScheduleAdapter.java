package info.beraki.winnipegtransit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;

import info.beraki.winnipegtransit.MainActivity;
import info.beraki.winnipegtransit.Model.Schedule.Schedule;
import info.beraki.winnipegtransit.Model.Schedule.StopSchedule;
import info.beraki.winnipegtransit.Model.Stops.Stop;
import info.beraki.winnipegtransit.Model.Stops.StopsData;
import info.beraki.winnipegtransit.R;
import info.beraki.winnipegtransit.StopActivity;

/**
 * Created by Beraki on 1/30/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder>{

    StopSchedule stopSchedule;
    View v;
    Context context;

    public ScheduleAdapter(StopSchedule value){
        this.stopSchedule=value;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView schedule;

        public MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            schedule = itemView.findViewById(R.id.schedule);

        }

    }

//    //TODO: Is this really the best way
//    @Override
//    public void onClick(View view) {
//
//            Log.w("tag", "My Ni99a");
//
//    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_recycler_layout, null);

        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ScheduleAdapter.MyViewHolder holder, int position) {
//        final StopSchedule aStopSchedule= stopSchedule;
//
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startStopActivity(context, aStop);
//            }
//        });


    }

    @Override
    public int getItemCount() {
        if(stopSchedule == null){
            Log.e("tag", "SOS in Adapter");
        }
        return stopSchedule.getRouteSchedules().size();
    }

    public void startStopActivity(Context context, Stop stop) {
        Intent startStopActivity= new Intent(context, StopActivity.class);
        startStopActivity.putExtra("data", stop);
        context.startActivity(startStopActivity);
    }
}
