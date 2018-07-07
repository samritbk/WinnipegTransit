
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class CrossStreet {

    @SerializedName("key")
    private Long Key;
    @SerializedName("name")
    private String Name;
    @SerializedName("type")
    private String Type;

    public Long getKey() {
        return Key;
    }

    public void setKey(Long key) {
        Key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

}
