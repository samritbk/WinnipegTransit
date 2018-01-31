
package info.beraki.winnipegtransit.Model;

import java.util.List;
import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class WTD {

    @SerializedName("locations")
    private List<Location> mLocations;
    @SerializedName("query-time")
    private String mQueryTime;

    public List<Location> getLocations() {
        return mLocations;
    }

    public void setLocations(List<Location> locations) {
        mLocations = locations;
    }

    public String getQueryTime() {
        return mQueryTime;
    }

    public void setQueryTime(String queryTime) {
        mQueryTime = queryTime;
    }

}
