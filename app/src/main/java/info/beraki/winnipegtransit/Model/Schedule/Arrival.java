
package info.beraki.winnipegtransit.Model.Schedule;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Arrival {

    @SerializedName("estimated")
    private String Estimated;
    @SerializedName("scheduled")
    private String Scheduled;

    public String getEstimated() {
        return Estimated;
    }

    public void setEstimated(String estimated) {
        Estimated = estimated;
    }

    public String getScheduled() {
        return Scheduled;
    }

    public void setScheduled(String scheduled) {
        Scheduled = scheduled;
    }

}
