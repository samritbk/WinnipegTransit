
package info.beraki.winnipegtransit.Model.Stops;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@SuppressWarnings("unused")
public class Geographic  implements Serializable {

    @SerializedName("latitude")
    private String mLatitude;
    @SerializedName("longitude")
    private String mLongitude;

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

}
