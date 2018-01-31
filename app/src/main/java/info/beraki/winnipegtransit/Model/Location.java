
package info.beraki.winnipegtransit.Model;

import java.util.List;
import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Location {

    @SerializedName("address")
    private Address mAddress;
    @SerializedName("categories")
    private List<String> mCategories;
    @SerializedName("key")
    private Long mKey;
    @SerializedName("name")
    private String mName;
    @SerializedName("type")
    private String mType;

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public List<String> getCategories() {
        return mCategories;
    }

    public void setCategories(List<String> categories) {
        mCategories = categories;
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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
