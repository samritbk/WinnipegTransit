
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Variant {

    @SerializedName("key")
    private String Key;
    @SerializedName("name")
    private String Name;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}
