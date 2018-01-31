
package info.beraki.winnipegtransit.Model.Stops;

import java.util.List;
import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class StopsData {

    @SerializedName("query-time")
    private String mQueryTime;
    @SerializedName("stops")
    private List<Stop> mStops;

    public String getQueryTime() {
        return mQueryTime;
    }

    public void setQueryTime(String queryTime) {
        mQueryTime = queryTime;
    }

    public List<Stop> getStops() {
        return mStops;
    }

    public void setStops(List<Stop> stops) {
        mStops = stops;
    }

}
