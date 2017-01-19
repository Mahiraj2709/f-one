package fairrepair.service.fairrepair.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 12/12/2016.
 */

public class Mechanic {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("hourly_service_charge")
    @Expose
    private String hourlyServiceCharges;
    @SerializedName("latitude")
    @Expose
    private String Lat;
    @SerializedName("longitude")
    @Expose
    private String lng;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("login_status")
    @Expose
    private String loginStatus;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("average_rate")
    @Expose
    private String avgRating;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHourlyServiceCharges() {
        return hourlyServiceCharges;
    }

    public void setHourlyServiceCharges(String hourlyServiceCharges) {
        this.hourlyServiceCharges = hourlyServiceCharges;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }
}
