
package info.beraki.winnipegtransit.Model.Schedule;

import java.util.List;
import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class RouteSchedule {

    @SerializedName("route")
    private info.beraki.winnipegtransit.Model.Schedule.Route Route;
    @SerializedName("scheduled-stops")
    private List<ScheduledStop> ScheduledStops;

    public info.beraki.winnipegtransit.Model.Schedule.Route getRoute() {
        return Route;
    }

    public void setRoute(info.beraki.winnipegtransit.Model.Schedule.Route route) {
        Route = route;
    }

    public List<ScheduledStop> getScheduledStops() {
        return ScheduledStops;
    }

    public void setScheduledStops(List<ScheduledStop> scheduledStops) {
        ScheduledStops = scheduledStops;
    }

}
