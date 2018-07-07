
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Centre {

    @SerializedName("geographic")
    private info.beraki.winnipegtransit.Model.Schedule.Geographic Geographic;
    @SerializedName("utm")
    private info.beraki.winnipegtransit.Model.Schedule.Utm Utm;

    public info.beraki.winnipegtransit.Model.Schedule.Geographic getGeographic() {
        return Geographic;
    }

    public void setGeographic(info.beraki.winnipegtransit.Model.Schedule.Geographic geographic) {
        Geographic = geographic;
    }

    public info.beraki.winnipegtransit.Model.Schedule.Utm getUtm() {
        return Utm;
    }

    public void setUtm(info.beraki.winnipegtransit.Model.Schedule.Utm utm) {
        Utm = utm;
    }

}
