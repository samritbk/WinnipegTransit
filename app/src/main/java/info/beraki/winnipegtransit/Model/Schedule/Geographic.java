
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Geographic {

    @SerializedName("latitude")
    private String Latitude;
    @SerializedName("longitude")
    private String Longitude;

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

}
