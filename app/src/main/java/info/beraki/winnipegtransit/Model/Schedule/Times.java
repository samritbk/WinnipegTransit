
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Times {

    @SerializedName("arrival")
    private info.beraki.winnipegtransit.Model.Schedule.Arrival Arrival;
    @SerializedName("departure")
    private info.beraki.winnipegtransit.Model.Schedule.Departure Departure;

    public info.beraki.winnipegtransit.Model.Schedule.Arrival getArrival() {
        return Arrival;
    }

    public void setArrival(info.beraki.winnipegtransit.Model.Schedule.Arrival arrival) {
        Arrival = arrival;
    }

    public info.beraki.winnipegtransit.Model.Schedule.Departure getDeparture() {
        return Departure;
    }

    public void setDeparture(info.beraki.winnipegtransit.Model.Schedule.Departure departure) {
        Departure = departure;
    }

}
