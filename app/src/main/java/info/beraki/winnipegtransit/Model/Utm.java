
package info.beraki.winnipegtransit.Model;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Utm {

    @SerializedName("x")
    private Long mX;
    @SerializedName("y")
    private Long mY;
    @SerializedName("zone")
    private String mZone;

    public Long getX() {
        return mX;
    }

    public void setX(Long x) {
        mX = x;
    }

    public Long getY() {
        return mY;
    }

    public void setY(Long y) {
        mY = y;
    }

    public String getZone() {
        return mZone;
    }

    public void setZone(String zone) {
        mZone = zone;
    }

}
