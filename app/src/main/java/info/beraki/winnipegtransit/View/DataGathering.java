package info.beraki.winnipegtransit.View;

import info.beraki.winnipegtransit.Model.Stops.StopsData;
import info.beraki.winnipegtransit.Model.WTD;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Beraki on 1/29/2018.
 */

public interface DataGathering {


        String API_KEY="Kg6sJ2UfTKaIoyc8oSx";

        //String UNITS= "METRIC";
        //?distance=50&x=633861&y=5525798

        @GET("/v2/locations.json")
        Single<WTD> getWinnipegTransitData(
                @Query("lat") double latitude,
                @Query("lon") double longitude,
                @Query("api-key") String API_KEY);

        @GET("/v2/stops.json")
        Single<StopsData> getStopsByLoc(
                @Query("lat") double latitude,
                @Query("lon") double longitude,
                @Query("distance") int distance,
                @Query("api-key") String API_KEY);

}
