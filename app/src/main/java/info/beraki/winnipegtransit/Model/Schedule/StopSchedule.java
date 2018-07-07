
package info.beraki.winnipegtransit.Model.Schedule;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class StopSchedule {

    @SerializedName("query-time")
    private String QueryTime;
    @SerializedName("route-schedules")
    private List<RouteSchedule> RouteSchedules;
    @SerializedName("stop")
    private info.beraki.winnipegtransit.Model.Schedule.Stop Stop;
    @SerializedName("stop-schedule")
    private StopSchedule StopSchedule;

    public String getQueryTime() {
        return QueryTime;
    }

    public void setQueryTime(String queryTime) {
        QueryTime = queryTime;
    }

    public List<RouteSchedule> getRouteSchedules() {
        return RouteSchedules;
    }

    public void setRouteSchedules(List<RouteSchedule> routeSchedules) {
        RouteSchedules = routeSchedules;
    }

    public info.beraki.winnipegtransit.Model.Schedule.Stop getStop() {
        return Stop;
    }

    public void setStop(info.beraki.winnipegtransit.Model.Schedule.Stop stop) {
        Stop = stop;
    }

    public StopSchedule getStopSchedule() {
        return StopSchedule;
    }

    public void setStopSchedule(StopSchedule stopSchedule) {
        StopSchedule = stopSchedule;
    }

}
