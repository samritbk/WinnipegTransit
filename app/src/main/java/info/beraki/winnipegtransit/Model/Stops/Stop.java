
package info.beraki.winnipegtransit.Model.Stops;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Stop {

    @SerializedName("centre")
    private Centre mCentre;
    @SerializedName("cross-street")
    private CrossStreet mCrossStreet;
    @SerializedName("direction")
    private String mDirection;
    @SerializedName("distances")
    private Distances mDistances;
    @SerializedName("key")
    private Long mKey;
    @SerializedName("name")
    private String mName;
    @SerializedName("number")
    private Long mNumber;
    @SerializedName("side")
    private String mSide;
    @SerializedName("street")
    private Street mStreet;

    public Centre getCentre() {
        return mCentre;
    }

    public void setCentre(Centre centre) {
        mCentre = centre;
    }

    public CrossStreet getCrossStreet() {
        return mCrossStreet;
    }

    public void setCrossStreet(CrossStreet crossStreet) {
        mCrossStreet = crossStreet;
    }

    public String getDirection() {
        return mDirection;
    }

    public void setDirection(String direction) {
        mDirection = direction;
    }

    public Distances getDistances() {
        return mDistances;
    }

    public void setDistances(Distances distances) {
        mDistances = distances;
    }

    public Long getKey() {
        return mKey;
    }

    public void setKey(Long key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Long getNumber() {
        return mNumber;
    }

    public void setNumber(Long number) {
        mNumber = number;
    }

    public String getSide() {
        return mSide;
    }

    public void setSide(String side) {
        mSide = side;
    }

    public Street getStreet() {
        return mStreet;
    }

    public void setStreet(Street street) {
        mStreet = street;
    }

}
