
package info.beraki.winnipegtransit.Model;


import com.google.gson.annotations.SerializedName;
@SuppressWarnings("unused")
public class Centre {

    @SerializedName("geographic")
    private Geographic mGeographic;
    @SerializedName("utm")
    private Utm mUtm;

    public Geographic getGeographic() {
        return mGeographic;
    }

    public void setGeographic(Geographic geographic) {
        mGeographic = geographic;
    }

    public Utm getUtm() {
        return mUtm;
    }

    public void setUtm(Utm utm) {
        mUtm = utm;
    }

}
