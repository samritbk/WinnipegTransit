
package info.beraki.winnipegtransit.Model.Stops;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Distances {

    @SerializedName("direct")
    private String mDirect;

    public String getDirect() {
        return mDirect;
    }

    public void setDirect(String direct) {
        mDirect = direct;
    }

}
