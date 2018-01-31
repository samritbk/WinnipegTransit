
package info.beraki.winnipegtransit.Model;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Street {

    @SerializedName("key")
    private Long mKey;
    @SerializedName("name")
    private String mName;
    @SerializedName("type")
    private String mType;

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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
