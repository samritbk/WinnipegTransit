package info.beraki.winnipegtransit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.UndeclaredThrowableException;
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

public class StopActivity extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar toolbar;
    TextView toolbarTitle;
    SwipeRefreshLayout swipeToRefreshLayout;
    Stop stopData;
    Context context;
    ScheduleAdapter scheduleAdapter;
    RecyclerView scheduleRecyclerLayout;
    StopSchedule stopSchedule;
    GoogleMap gMap;
    LatLng busLatLng;
    List<ScheduledStop> scheduledStopList;
    MenuItem mapToggle;
    SupportMapFragment mapFragment;
    SingleObserver<StopSchedule> singleObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);

        init(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10f);
        }

        Intent intent= this.getIntent();
        if(intent != null) {
            // TODO: They say its better to use Parcelable
            Stop stop = (Stop) intent.getSerializableExtra("data");
            if(stop != null){
                stopData = stop;
                String stopName=stopData.getName();
                toolbarTitle.setText(shortenStopDirection(stopName));
            }
        }

        double LATITUDE= Double.parseDouble(stopData.getCentre().getGeographic().getLatitude());
        double LONGITUDE = Double.parseDouble(stopData.getCentre().getGeographic().getLongitude());
        busLatLng = new LatLng(LATITUDE, LONGITUDE);
        //Toast.makeText(context, LATITUDE+"-"+LONGITUDE, Toast.LENGTH_LONG).show();

        // TODO: I am here now
        if(!isStopSaved(stopData.getNumber(), getString(R.string.prefSavedStop))){
            if (saveStopDetails(stopData.getName(), stopData.getNumber(), "My Work Stop")) {
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "don't be greedy. Fool", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sharedPreferences=getSharedPreferences("winnipegTransit", MODE_PRIVATE);
        String s=sharedPreferences.getString("savedStops", null);


        Toast.makeText(context, sharedPreferences.contains("savedStops")+"-", Toast.LENGTH_SHORT).show();
    }

    public String shortenStopDirection(String stopName){
        stopName = stopName.replace("Northbound", "NB");
        stopName = stopName.replace("Westbound", "WB");
        stopName = stopName.replace("Southbound", "SB");
        stopName = stopName.replace("Eastbound", "EB");

        return stopName;
    }

    private void init(Context context) {
        this.context = context;
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        scheduleRecyclerLayout = findViewById(R.id.scheduleRecyclerLayout);
        swipeToRefreshLayout = findViewById(R.id.swipeToRefresh);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.stop_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.togglemap:
                FrameLayout map = findViewById(R.id.map);
                if(map.getVisibility() == View.VISIBLE)
                    map.setVisibility(View.GONE);
                else
                    map.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                stopData.getNumber(), //TODO: Is dynamic 10542 - MTS
                DataGathering.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        stopScheduleSingle.subscribe(new SingleObserver<StopSchedule>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(StopSchedule stopSchedule) {
                StopSchedule stopScheduleDep=stopSchedule;
                if(stopSchedule != null) {
                    scheduledStopList = getSchedulesByTime(stopScheduleDep);
                    scheduleDataAvailable(stopSchedule, scheduledStopList);
                }
            }

            @Override
            public void onError(Throwable e) {
                Crashlytics.logException(e);
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
        // Log.v("tag",stopSchedule.getRouteSchedules().size()+"");

        Collections.sort(scheduledStops, new ScheduledStopComparator());

        scheduleAdapter = new ScheduleAdapter(stopSchedule, scheduledStops);
        LinearLayoutManager scheduleLayoutManager=new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration=
                new DividerItemDecoration(this,
                        scheduleLayoutManager.getOrientation());
        scheduleRecyclerLayout.setLayoutManager(scheduleLayoutManager);
        scheduleRecyclerLayout.addItemDecoration(dividerItemDecoration);
        scheduleRecyclerLayout.setAdapter(scheduleAdapter);
        scheduleAdapter.notifyDataSetChanged();

    }

    static long getEtaFromTime(String scheduledArrivalEst){

        //String ETAString=null;

        long millis = System.currentTimeMillis();
        long time = getTimeFromString(scheduledArrivalEst);
        long ETA = -1;

        if(time != -1) {
            ETA = (time - millis) / 60000;
        }

        return ETA;
    }


    private static long getTimeFromString(String scheduledArrivalEst) {

        long time = -1;

        @SuppressLint("SimpleDateFormat")
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(scheduledArrivalEst);
        } catch (ParseException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
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

        gMap.addMarker(new MarkerOptions().position(busLatLng).title("Bus stop"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(busLatLng));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        gMap.animateCamera(zoom);
    }


    private boolean isStopSaved(long stopNumber, String prefName){
        JSONArray stopsArray= null;

        SharedPreferences sharedPreferences  = getSharedPreferences("winnipegTransit", MODE_PRIVATE);
        String stopsString= sharedPreferences.getString(prefName, null);
        if(stopsString != null){
            try {
                stopsArray = new JSONArray(stopsString);


                int count= stopsArray.length();

                for(int i=0; i < count; i++){
                    JSONObject stopObject = stopsArray.getJSONObject(i);
                    long savedStopNumber = stopObject.getLong("stop_number");

                    if(savedStopNumber == stopNumber){
                        return true;
                    }
                }

                return false;

            } catch (JSONException e) {
                return false;
            }
        }else {
            return false;
        }
    }

    // Stand alone methods
    private boolean saveStopDetails(String stopName, long stopNumber, String nickName){

        JSONObject stopObject= new JSONObject();
        JSONArray stopsArray=new JSONArray();

        try {
            stopObject.put("stop_number", stopNumber);
            stopObject.put("stop_name", stopName);
            stopObject.put("stop_nickname", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        SharedPreferences sharedPreferences= getSharedPreferences("winnipegTransit", MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor= sharedPreferences.edit();

        if(sharedPreferences.getString(getString(R.string.prefSavedStop), null) == null){

            stopsArray.put(stopObject);
            sharedPreferencesEditor.putString(getString(R.string.prefSavedStop), stopsArray.toString());
            Toast.makeText(context, "null done", Toast.LENGTH_SHORT).show();

            sharedPreferencesEditor.apply();

        }else if(sharedPreferences.getString(getString(R.string.prefSavedStop), null) != null){
            Toast.makeText(context, "not null", Toast.LENGTH_SHORT).show();
            String savedStops=sharedPreferences.getString(getString(R.string.prefSavedStop), null);
            Toast.makeText(context, savedStops, Toast.LENGTH_LONG).show();
            try {
                stopsArray= new JSONArray(savedStops);
                int count= stopsArray.length();
                stopsArray.put(stopObject);
                sharedPreferencesEditor.putString(getString(R.string.prefSavedStop), stopsArray.toString());
                sharedPreferencesEditor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


}
