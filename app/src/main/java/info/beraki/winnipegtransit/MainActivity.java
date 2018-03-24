package info.beraki.winnipegtransit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import info.beraki.winnipegtransit.Adapter.ScheduleAdapter;
import info.beraki.winnipegtransit.Adapter.StopAdapter;
import info.beraki.winnipegtransit.Model.Stops.StopsData;
import info.beraki.winnipegtransit.View.DataGathering;
import info.beraki.winnipegtransit.View.MainActivityInterface;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        MainActivityInterface,
        View.OnClickListener,
        OnMapReadyCallback {

    private static final int MY_PERMISSIONS_LOCATION = 10;
    RecyclerView recyclerView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static double LONGITUDE = 0;
    public static double LATITUDE = 0;
    public static final int RESOLVED_LOCATION=21;
    StopsData stopsData = new StopsData();
    StopAdapter stopAdapter;
    GoogleMap gMaps;
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    AppBarLayout appbar;
    TextView enableLocation;
    LocationRequest locationRequest;
    String TAG="winnipeg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerLayout);
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        appbar = findViewById(R.id.appbar);
        enableLocation = findViewById(R.id.locationEnable);

        enableLocation.setOnClickListener(this);

        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initializing a location request
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(1000)
                .setInterval(1500);
        getLocationData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getLocationData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.togglemap:
                FrameLayout fragment = findViewById(R.id.map);
                if(fragment.getVisibility() == View.VISIBLE)
                    fragment.setVisibility(View.GONE);
                else
                    fragment.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case MY_PERMISSIONS_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    getLocationData();
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.locationEnable:
                getCurrentLocation(mFusedLocationProviderClient);
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMaps = googleMap;
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.black_map));
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(49.895077, -97.138451);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Winnipeg City"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

    }


    // Relay on location methods ------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void getCurrentLocation(final FusedLocationProviderClient mFusedLocationClient) {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build())
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // All location settings are satisfied. The client can initialize
                        // location requests here.
                        if (!locationSettingsResponse.getLocationSettingsStates().isGpsUsable()) {
                            requestLocationUpdate(mFusedLocationClient, locationRequest);
                        }else if (!locationSettingsResponse.getLocationSettingsStates().isLocationUsable()){
                            requestLocationUpdate(mFusedLocationClient, locationRequest);
                            Toast.makeText(MainActivity.this, "onSuccessTaskGPSnotusable", Toast.LENGTH_SHORT).show();
                        }
                        getLocationData();
                        Toast.makeText(MainActivity.this, "Outside called", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MainActivity.this, RESOLVED_LOCATION);

                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                            Toast.makeText(MainActivity.this, "onSuccessTask22", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case RESOLVED_LOCATION:
                switch (resultCode){
                    case Activity.RESULT_OK:
                        Toast.makeText(this, "RESULT_OK", Toast.LENGTH_SHORT).show();
                        getLocationData();
                        break;
                }
                break;
        }

    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate(FusedLocationProviderClient mFusedLocationClient,
                                       LocationRequest locationRequest) {

        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    @SuppressLint("MissingPermission")
    private void locationDataAvailable() {


        setUpMapAfterLocationData();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.winnipegtransit.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        DataGathering dataGathering= retrofit.create(DataGathering.class);

        Single<StopsData> StopsSingle= dataGathering.getStopsByLoc(
                LATITUDE,
                LONGITUDE,
                250,
                DataGathering.API_KEY)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        StopsSingle.subscribe(new SingleObserver<StopsData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(StopsData stopsData) {
                stopDataAvailable(stopsData);
            }

            @Override
            public void onError(Throwable e) {

            }
        });

    }

    @SuppressLint("MissingPermission")
    private void setUpMapAfterLocationData() {
        LatLng latLng = new LatLng(LATITUDE, LONGITUDE);
        gMaps.setMyLocationEnabled(true);
        gMaps.getUiSettings().setMyLocationButtonEnabled(false);
        gMaps.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        gMaps.animateCamera(zoom);
    }

    // Custom methods after request ------------------------------------------------------------------------
    public void getLocationData() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissionsESZ
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_LOCATION);

        } else {
            //Toast.makeText(this, "You can", Toast.LENGTH_SHORT).show();
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    private void showLocationEnable(TextView enableLocation,int reason){ // 0: lastLocation null 1: lastLocation not null
        switch(reason){
            case 0:
                enableLocation.setText(R.string.locationNotEnabled);
                swipeRefreshLayout.setRefreshing(false);
                break;
            case 1:
                enableLocation.setText(R.string.locationNotEnabledLastLocation);
                break;
        }

        enableLocation.setVisibility(View.VISIBLE);

    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void stopDataAvailable(StopsData value) {
        stopsData = value;
        // TODO: Create a new method for three lines below

                stopAdapter= new StopAdapter(stopsData);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getApplicationContext(),
                        DividerItemDecoration.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setAdapter(stopAdapter);
                stopAdapter.notifyDataSetChanged();
                //TODO: Hide progressbar here
                swipeRefreshLayout.setRefreshing(false);

        //Toast.makeText(this, stopsData.getStops().get(0).getName(), Toast.LENGTH_SHORT).show();

    }

    // Standalone callbacks -----------------------------------------------------------------------------
    OnSuccessListener<Location> onSuccessListener = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
                LONGITUDE = location.getLongitude();
                LATITUDE = location.getLatitude();
                locationDataAvailable();
                Toast.makeText(MainActivity.this, "onSuccessTaskReq", Toast.LENGTH_SHORT).show();
                enableLocation.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(true);
                Toast.makeText(MainActivity.this, "still cont", Toast.LENGTH_SHORT).show();
            } else {
                //TODO: Working on !! Handle LOC data not available
                showLocationEnable(enableLocation, 0);
                //getCurrentLocation(mFusedLocationProviderClient);
                //Toast.makeText(MainActivity.this, "Again", Toast.LENGTH_SHORT).show();
            }
        }
    };

    OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage()+" its me", Toast.LENGTH_SHORT).show();
        }
    };

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            Toast.makeText(MainActivity.this, "datagiven", Toast.LENGTH_SHORT).show();
            LONGITUDE = location.getLongitude();
            LATITUDE = location.getLatitude();

            locationDataAvailable();
            stopLocationUpdates();
        }

    };
}