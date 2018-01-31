package info.beraki.winnipegtransit.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import info.beraki.winnipegtransit.Model.Stops.StopsData;
import info.beraki.winnipegtransit.R;

/**
 * Created by Beraki on 1/30/2018.
 */

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.MyViewHolder>{

    List<StopsData> stops;

    public StopAdapter(List<StopsData> stopsData){
        this.stops=stopsData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView stopName;


        public MyViewHolder(View itemView) {
            super(itemView);

            stopName = itemView.findViewById(R.id.stop_name);

        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout, null);

        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StopsData stopsData= stops.get(position);

        String name=stopsData.getStops().get(position).getName();
        holder.stopName.setText(name);

    }

    @Override
    public int getItemCount() {

        return stops.size();

    }
}
