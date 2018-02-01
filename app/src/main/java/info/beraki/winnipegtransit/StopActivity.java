package info.beraki.winnipegtransit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import info.beraki.winnipegtransit.Adapter.ScheduleAdapter;
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

public class StopActivity extends AppCompatActivity {

    Stop stopData;
    Context context;
    StopSchedule stopSchedule;
    ScheduleAdapter scheduleAdapter;
    RecyclerView scheduleRecyclerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        context = this;

        Intent intent= this.getIntent();
        if(intent != null) {
            // TODO: They say its better to use Parcelable
            Stop stop = (Stop) intent.getSerializableExtra("data");

            if(stop != null){
                stopData = stop;
            }
        }
        getNessesary(context);

        scheduleRecyclerLayout = findViewById(R.id.scheduleRecyclerLayout);





        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://api.winnipegtransit.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        DataGathering dataGathering=retrofit.create(DataGathering.class);
        Single<StopSchedule> scheduleSingle= dataGathering
                .getScheduledBusesByStop(
                        stopData.getNumber(),
                        DataGathering.API_KEY)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        scheduleSingle.subscribe(new SingleObserver<StopSchedule>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(StopSchedule stopSchedule) {
                scheduleDataAvailable(stopSchedule);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void scheduleDataAvailable(StopSchedule stopSchedule) {
        this.stopSchedule = stopSchedule;
        scheduleAdapter = new ScheduleAdapter(stopSchedule);
        scheduleRecyclerLayout.setAdapter(scheduleAdapter);
        scheduleRecyclerLayout.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        scheduleAdapter.notifyDataSetChanged();

    }


    private void getNessesary(Context context) {



    }

}
