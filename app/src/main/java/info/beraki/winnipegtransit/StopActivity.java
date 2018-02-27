package info.beraki.winnipegtransit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import info.beraki.winnipegtransit.Adapter.ScheduleAdapter;
import info.beraki.winnipegtransit.Model.Schedule.RouteSchedule;
import info.beraki.winnipegtransit.Model.Schedule.ScheduledStop;
import info.beraki.winnipegtransit.Model.Schedule.StopSchedule;
import info.beraki.winnipegtransit.Model.Stops.Stop;
import info.beraki.winnipegtransit.View.DataGathering;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StopActivity extends AppCompatActivity implements OnMapReadyCallback{

    Toolbar toolbar;
    TextView toolbarTitle;
    SwipeRefreshLayout swipeToRefreshLayout;
    Stop stopData;
    Context context;
    ScheduleAdapter scheduleAdapter;
    RecyclerView scheduleRecyclerLayout;
    StopSchedule stopSchedule;
    Fragment mapFragment;
    GoogleMap gMap;
    LatLng sydney;
    List<ScheduledStop> scheduledStopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        context = this;

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        setSupportActionBar(toolbar);



        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        Intent intent= this.getIntent();
        if(intent != null) {
            // TODO: They say its better to use Parcelable
            Stop stop = (Stop) intent.getSerializableExtra("data");

            if(stop != null){
                stopData = stop;
                //Log.v("tag",stopData.getName());
                //if(getSupportActionBar() != null)
                    toolbarTitle.setText(stopData.getName());
            }
        }


        scheduleRecyclerLayout = findViewById(R.id.scheduleRecyclerLayout);
        swipeToRefreshLayout = findViewById(R.id.swipeToRefresh);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://api.winnipegtransit.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        DataGathering dataGathering=retrofit.create(DataGathering.class);

        Single<StopSchedule> stopScheduleSingle= dataGathering.getScheduledBusesByStop(
                stopData.getNumber(), //TODO: Is dynamic
                DataGathering.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        stopScheduleSingle.subscribe(new SingleObserver<StopSchedule>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(StopSchedule stopSchedule) {

                Toast.makeText(
                        context,
                        stopSchedule.getStopSchedule().getRouteSchedules().size()+"",
                        Toast.LENGTH_LONG)
                        .show();

                scheduledStopList=getSchedulesByTime(stopSchedule);
                scheduleDataAvailable(stopSchedule, scheduledStopList);
                double LATITUDE= Double.parseDouble(stopSchedule.getStopSchedule().getStop().getCentre().getGeographic().getLatitude());
                double LONGITUDE = Double.parseDouble(stopSchedule.getStopSchedule().getStop().getCentre().getGeographic().getLongitude());
                sydney = new LatLng(LATITUDE, LONGITUDE);
                Toast.makeText(context, LATITUDE+"-"+LONGITUDE, Toast.LENGTH_LONG).show();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(StopActivity.this);
                setSupportActionBar(toolbar);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }


    private List<ScheduledStop> getSchedulesByTime(StopSchedule stopSchedule) {
        List<ScheduledStop> scheduledStopList= new ArrayList<>();
        StopSchedule gotStopSchedules;

        gotStopSchedules= stopSchedule.getStopSchedule();

        int countRoutes=gotStopSchedules.getRouteSchedules().size();

        for(int i=0; i < countRoutes; i++){
            RouteSchedule routeSchedule=gotStopSchedules.getRouteSchedules().get(i);

            int countSchedules = routeSchedule.getScheduledStops().size();

            for (int j=0; j < countSchedules; j++){
                ScheduledStop scheduledStop=routeSchedule.getScheduledStops().get(j);
                scheduledStopList.add(scheduledStop);
            }

        }

        return scheduledStopList;
    }

    private void scheduleDataAvailable(StopSchedule stopSchedule, List<ScheduledStop> scheduledStops) {
        this.stopSchedule = stopSchedule.getStopSchedule();
//        Log.v("tag",stopSchedule.getRouteSchedules().size()+"");

        Collections.sort(scheduledStops, new ScheduledStopComparator());

        scheduleAdapter = new ScheduleAdapter(stopSchedule, scheduledStops);
        scheduleRecyclerLayout.setAdapter(scheduleAdapter);
        LinearLayoutManager scheduleLayoutManager=new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration=
                new DividerItemDecoration(this,
                        scheduleLayoutManager.getOrientation());
        scheduleRecyclerLayout.setLayoutManager(scheduleLayoutManager);
        scheduleRecyclerLayout.addItemDecoration(dividerItemDecoration);
        scheduleAdapter.notifyDataSetChanged();

    }
    class ScheduledStopComparator implements Comparator<ScheduledStop>{

        @Override
        public int compare(ScheduledStop scheduledStop1, ScheduledStop scheduledStop2) {
            long scheduledArrivalEst1=getEtaFromTime(scheduledStop1.getTimes().getArrival().getEstimated());
            long scheduledArrivalEst2=getEtaFromTime(scheduledStop2.getTimes().getArrival().getEstimated());

            if(scheduledArrivalEst1 > scheduledArrivalEst2){
                return 1;
            }else if (scheduledArrivalEst1 < scheduledArrivalEst2){
                return -1;
            }

            return 0;
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.black_map));
        gMap.addMarker(new MarkerOptions().position(sydney).title("Your Location").anchor(0.5f, 0.5f));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        gMap.animateCamera(zoom);

//        LatLng userLoc=new LatLng(MainActivity.LATITUDE, MainActivity.LONGITUDE);
//        GoogleDirection.withServerKey(String.valueOf(R.string.google_play_API_key))
//                .from(userLoc)
//                .to(sydney)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//
//                    }
//                });
    }

}
