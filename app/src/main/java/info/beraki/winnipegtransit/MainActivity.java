package info.beraki.winnipegtransit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
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
    StopsData stopsData = new StopsData();
    StopAdapter stopAdapter;
    GoogleMap gMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //text = findViewById(R.id.text);
        //button = findViewById(R.id.button);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (LATITUDE != 0 && LONGITUDE != 0) {
            Toast.makeText(this, LATITUDE + "-" + LONGITUDE, Toast.LENGTH_LONG).show();
        }

        recyclerView = findViewById(R.id.recyclerLayout);


    }


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

            return;
        } else {
            //Toast.makeText(this, "You can", Toast.LENGTH_SHORT).show();
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    OnSuccessListener<Location> onSuccessListener = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
                LONGITUDE = location.getLongitude();
                LATITUDE = location.getLatitude();
                locationDataAvailable();
            } else {
                //TODO: Working on !! Handle LOC data not available
                Toast.makeText(MainActivity.this, "There is no Location data on file", Toast.LENGTH_SHORT).show();
                getCurrentLocation(mFusedLocationProviderClient);
            }
        }
    };

    OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    @SuppressLint("MissingPermission")
    private void getCurrentLocation(final FusedLocationProviderClient mFusedLocationClient) {

        // Initializing a location request
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(1000)
                .setInterval(1500);


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
                            Log.e("winnipeg", "should come 2");
                            requestLocationUpdate(mFusedLocationClient, locationRequest);
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("winnipeg", "should come 1");
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MainActivity.this,
                                        21);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        }
                    }
                });
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();

            LONGITUDE = location.getLongitude();
            LATITUDE = location.getLatitude();

            locationDataAvailable();
            stopLocationUpdates();
        }

    };

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate(FusedLocationProviderClient mFusedLocationClient, LocationRequest locationRequest) {
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);


    }

    @SuppressLint("MissingPermission")
    private void locationDataAvailable() {


        setUpMap();


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

    private void stopDataAvailable(StopsData value) {
        stopsData = value;
        // TODO: Create a new method for three lines below

                stopAdapter= new StopAdapter(stopsData);
                recyclerView.setAdapter(stopAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(getApplicationContext(), linearLayoutManager.getOrientation());
                recyclerView.addItemDecoration(dividerItemDecoration);
                stopAdapter.notifyDataSetChanged();

        Toast.makeText(this, stopsData.getStops().get(0).getName(), Toast.LENGTH_SHORT).show();

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        getLocationData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void setUpMap() {
        LatLng latLng = new LatLng(LATITUDE, LONGITUDE);
        gMaps.setMyLocationEnabled(true);
        gMaps.getUiSettings().setMyLocationButtonEnabled(false);
        gMaps.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        gMaps.animateCamera(zoom);
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
}