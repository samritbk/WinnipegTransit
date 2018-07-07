
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Utm {

    @SerializedName("x")
    private Long X;
    @SerializedName("y")
    private Long Y;
    @SerializedName("zone")
    private String Zone;

    public Long getX() {
        return X;
    }

    public void setX(Long x) {
        X = x;
    }

    public Long getY() {
        return Y;
    }

    public void setY(Long y) {
        Y = y;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
    }

}
