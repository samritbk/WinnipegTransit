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
import info.beraki.winnipegtransit.Model.Stops.Stop;
import info.beraki.winnipegtransit.Model.Stops.StopsData;
import info.beraki.winnipegtransit.R;
import info.beraki.winnipegtransit.StopActivity;

/**
 * Created by Beraki on 1/30/2018.
 */

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.MyViewHolder>{

    StopsData stopsData;
    View v;
    Context context;

    public StopAdapter(StopsData value){
        this.stopsData=value;
    }




    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView stopName;
        TextView stopNumber;
        RelativeLayout stopLayoutParent;

        public MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            stopName = itemView.findViewById(R.id.stop_name);
            stopNumber = itemView.findViewById(R.id.stop_number);
            stopLayoutParent = itemView.findViewById(R.id.RLstopLayoutParent);



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

        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.stops_recycler_layout, null);

        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Stop aStop= stopsData.getStops().get(position);


        String name=aStop.getName();
        long number=aStop.getNumber();

        holder.stopName.setText(name);
        holder.stopNumber.setText("#"+number);


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStopActivity(context, aStop);
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("tag", "Hi "+stopsData.getStops().size());
        return stopsData.getStops().size();
    }

    public void startStopActivity(Context context, Stop stop) {
        Intent startStopActivity= new Intent(context, StopActivity.class);
        startStopActivity.putExtra("data", stop);
        context.startActivity(startStopActivity);
    }
}
