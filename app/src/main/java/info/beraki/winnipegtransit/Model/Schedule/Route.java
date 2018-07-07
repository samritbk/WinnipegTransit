
package info.beraki.winnipegtransit.Model.Schedule;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Route {

    @SerializedName("coverage")
    private String Coverage;
    @SerializedName("customer-type")
    private String CustomerType;
    @SerializedName("key")
    private Long Key;
    @SerializedName("name")
    private String Name;
    @SerializedName("number")
    private Long Number;

    public String getCoverage() {
        return Coverage;
    }

    public void setCoverage(String coverage) {
        Coverage = coverage;
    }

    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String customerType) {
        CustomerType = customerType;
    }

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

    public Long getNumber() {
        return Number;
    }

    public void setNumber(Long number) {
        Number = number;
    }

}
