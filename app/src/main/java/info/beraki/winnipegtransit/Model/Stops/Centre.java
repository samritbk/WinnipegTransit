package info.beraki.winnipegtransit.Model.Stops;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@SuppressWarnings("unused")
public class Centre implements Serializable {

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
