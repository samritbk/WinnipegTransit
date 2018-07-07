
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Stop {

    @SerializedName("centre")
    private info.beraki.winnipegtransit.Model.Schedule.Centre Centre;
    @SerializedName("cross-street")
    private info.beraki.winnipegtransit.Model.Schedule.CrossStreet CrossStreet;
    @SerializedName("direction")
    private String Direction;
    @SerializedName("key")
    private Long Key;
    @SerializedName("name")
    private String Name;
    @SerializedName("number")
    private Long Number;
    @SerializedName("side")
    private String Side;
    @SerializedName("street")
    private info.beraki.winnipegtransit.Model.Schedule.Street Street;

    public info.beraki.winnipegtransit.Model.Schedule.Centre getCentre() {
        return Centre;
    }

    public void setCentre(info.beraki.winnipegtransit.Model.Schedule.Centre centre) {
        Centre = centre;
    }

    public info.beraki.winnipegtransit.Model.Schedule.CrossStreet getCrossStreet() {
        return CrossStreet;
    }

    public void setCrossStreet(info.beraki.winnipegtransit.Model.Schedule.CrossStreet crossStreet) {
        CrossStreet = crossStreet;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        Direction = direction;
    }

    public Long getKey() {
        return Key;
    }

    public void setKey(Long key) {
        Key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Long getNumber() {
        return Number;
    }

    public void setNumber(Long number) {
        Number = number;
    }

    public String getSide() {
        return Side;
    }

    public void setSide(String side) {
        Side = side;
    }

    public info.beraki.winnipegtransit.Model.Schedule.Street getStreet() {
        return Street;
    }

    public void setStreet(info.beraki.winnipegtransit.Model.Schedule.Street street) {
        Street = street;
    }

}
