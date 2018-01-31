
package info.beraki.winnipegtransit.Model;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Address {

    @SerializedName("centre")
    private Centre mCentre;
    @SerializedName("key")
    private Long mKey;
    @SerializedName("street")
    private Street mStreet;
    @SerializedName("street-number")
    private Long mStreetNumber;

    public Centre getCentre() {
        return mCentre;
    }

    public void setCentre(Centre centre) {
        mCentre = centre;
    }

    public Long getKey() {
        return mKey;
    }

    public void setKey(Long key) {
        mKey = key;
    }

    public Street getStreet() {
        return mStreet;
    }

    public void setStreet(Street street) {
        mStreet = street;
    }

    public Long getStreetNumber() {
        return mStreetNumber;
    }

    public void setStreetNumber(Long streetNumber) {
        mStreetNumber = streetNumber;
    }

}
