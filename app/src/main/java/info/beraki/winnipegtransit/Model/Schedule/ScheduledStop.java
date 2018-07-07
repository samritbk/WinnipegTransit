
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class ScheduledStop {

    @SerializedName("bus")
    private info.beraki.winnipegtransit.Model.Schedule.Bus Bus;
    @SerializedName("key")
    private String Key;
    @SerializedName("times")
    private info.beraki.winnipegtransit.Model.Schedule.Times Times;
    @SerializedName("variant")
    private info.beraki.winnipegtransit.Model.Schedule.Variant Variant;

    public info.beraki.winnipegtransit.Model.Schedule.Bus getBus() {
        return Bus;
    }

    public void setBus(info.beraki.winnipegtransit.Model.Schedule.Bus bus) {
        Bus = bus;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public info.beraki.winnipegtransit.Model.Schedule.Times getTimes() {
        return Times;
    }

    public void setTimes(info.beraki.winnipegtransit.Model.Schedule.Times times) {
        Times = times;
    }

    public info.beraki.winnipegtransit.Model.Schedule.Variant getVariant() {
        return Variant;
    }

    public void setVariant(info.beraki.winnipegtransit.Model.Schedule.Variant variant) {
        Variant = variant;
    }

}
