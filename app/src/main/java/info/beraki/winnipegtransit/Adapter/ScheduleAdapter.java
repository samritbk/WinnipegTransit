package info.beraki.winnipegtransit.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import info.beraki.winnipegtransit.Model.Schedule.ScheduledStop;
import info.beraki.winnipegtransit.Model.Schedule.StopSchedule;
import info.beraki.winnipegtransit.Model.Stops.Stop;
import info.beraki.winnipegtransit.R;
import info.beraki.winnipegtransit.StopActivity;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.google.gson.internal.bind.util.ISO8601Utils.format;

/**
 * Created by Beraki on 1/30/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder>{

    StopSchedule stopSchedule;
    View v;
    Context context;
    List<ScheduledStop> scheduledStopsList;

    public ScheduleAdapter(StopSchedule value, List<ScheduledStop> scheduledStopsList){
        this.stopSchedule=value;
        this.scheduledStopsList = scheduledStopsList;
        Log.w("tah", scheduledStopsList.size()+"");
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView busName;
        TextView busNumber;
        TextView schedule;
        TextView variant;

        public MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            busName = itemView.findViewById(R.id.bus_name);
            schedule = itemView.findViewById(R.id.schedule);
            variant = itemView.findViewById(R.id.variant);
            busNumber = itemView.findViewById(R.id.bus_number);
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

        //TODO: Good idea to make a method for these ones
        ScheduledStop scheduledStop=scheduledStopsList.get(position);
        String busName= scheduledStop.getVariant().getName();
        String scheduledArrivalEst=scheduledStop.getTimes().getArrival().getEstimated();
        String busVarientKey=scheduledStop.getVariant().getKey();
        String busNo = getBusNoFromKey(busVarientKey);
        //String variant = scheduledStop;
        holder.busNumber.setText(busNo);
        holder.busName.setText(busName);

        String ETA=getETAMins(scheduledArrivalEst);
        holder.schedule.setText(ETA+"");;
    }

    private long getEtaFromTime(String scheduledArrivalEst){

        //String ETAString=null;

        long millis = System.currentTimeMillis();
        long time = getTimeFromString(scheduledArrivalEst);
        long ETA = -1;

        if(time != -1) {
            ETA = (time - millis) / 60000;
        }

        return ETA;
    }


    private long getTimeFromString(String scheduledArrivalEst) {

        long time = -1;

        @SuppressLint("SimpleDateFormat")
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(scheduledArrivalEst);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            time = date.getTime();
        }

        return time;
    }

    //TODO: INPROGRESS What is this thing doing here
    private String getETAMins(String scheduledArrivalEst) {

        String ETAString = null;

        @SuppressLint("SimpleDateFormat")
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(scheduledArrivalEst);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date!= null) {
            long time = date.getTime();

            //Log.w("tah", String.valueOf(time));

            long millis = System.currentTimeMillis();

            long ETA = (time - millis) / 60000;

            if(ETA < 1){
                ETAString = "Due";
            }else if(ETA > 25){


                Date d = new Date(time);
                SimpleDateFormat formatter= new SimpleDateFormat("HH:mm");
                ETAString = formatter.format(d);

            }else{
                ETAString = ETA+"";
            }
        }

        return ETAString;

    }

    @Override
    public int getItemCount() {
        return scheduledStopsList.size();
    }

    public void startStopActivity(Context context, Stop stop) {
        Intent startStopActivity= new Intent(context, StopActivity.class);
        startStopActivity.putExtra("data", stop);
        context.startActivity(startStopActivity);
    }

    public String getBusNoFromKey(String busVarientKey){
        String[] busExploaded = busVarientKey.split("-");
        String busNo = busExploaded[0];

        return busNo;
    }

}
