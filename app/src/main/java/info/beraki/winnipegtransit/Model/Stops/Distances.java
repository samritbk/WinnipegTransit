
package info.beraki.winnipegtransit.Model.Stops;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@SuppressWarnings("unused")
public class Distances implements Serializable {

    @SerializedName("direct")
    private String mDirect;

    public String getDirect() {
        return mDirect;
    }

    public void setDirect(String direct) {
        mDirect = direct;
    }

}
