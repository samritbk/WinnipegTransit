
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Bus {

    @SerializedName("bike-rack")
    private String BikeRack;
    @SerializedName("easy-access")
    private String EasyAccess;
    @SerializedName("wifi")
    private String Wifi;

    public String getBikeRack() {
        return BikeRack;
    }

    public void setBikeRack(String bikeRack) {
        BikeRack = bikeRack;
    }

    public String getEasyAccess() {
        return EasyAccess;
    }

    public void setEasyAccess(String easyAccess) {
        EasyAccess = easyAccess;
    }

    public String getWifi() {
        return Wifi;
    }

    public void setWifi(String wifi) {
        Wifi = wifi;
    }

}
