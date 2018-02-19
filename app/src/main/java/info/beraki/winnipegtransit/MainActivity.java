package info.beraki.winnipegtransit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import info.beraki.winnipegtransit.Adapter.StopAdapter;
import info.beraki.winnipegtransit.Model.Stops.Stop;
import info.beraki.winnipegtransit.Model.Stops.StopsData;
import info.beraki.winnipegtransit.Model.WTD;
import info.beraki.winnipegtransit.View.DataGathering;
import info.beraki.winnipegtransit.View.MainActivityInterface;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MainActivityInterface, OnMapReadyCallback {

    private static final int MY_PERMISSIONS_LOCATION = 10;
    TextView text;
    Button button;
    RecyclerView recyclerView;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private double LONGITUDE=0;
    private double LATITUDE=0;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        if(LATITUDE != 0 && LONGITUDE != 0){
            Toast.makeText(this, LATITUDE+"-"+LONGITUDE, Toast.LENGTH_LONG).show();
        }

//        button.setOnClickListener(this);

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
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                LONGITUDE=location.getLongitude();
                                LATITUDE=location.getLatitude();
                                locationDataAvailable();
                            }else{
                                //TODO: Handle LOC data not available
                                Toast.makeText(MainActivity.this, "There is no Location data on file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void locationDataAvailable() {
        Toast.makeText(this, LATITUDE+"-"+LONGITUDE, Toast.LENGTH_LONG).show();

        LatLng sydney = new LatLng(LATITUDE, LONGITUDE);
        gMaps.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
        gMaps.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        gMaps.animateCamera(zoom);
        gMaps.addCircle(new CircleOptions().fillColor(R.color.colorPrimary).center(sydney).strokeColor(R.color.colorAccent));

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

//        List<Stop> stops=stopsData.getStops();
//
//        int count=stops.size();
//
//        for(int i=0; i < count; i++){
//            text.append(stops.get(i).getName()+"\n");
//        }

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
//        switch (view.getId()){
//            case R.id.button:
//                getLocationData();
//                break;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getLocationData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMaps = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}