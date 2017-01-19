
package fairrepair.service.fairrepair.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("stripe_id")
    @Expose
    private Object stripeId;
    @SerializedName("profile_pic")
    @Expose
    private Object profilePic;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("add_date")
    @Expose
    private String addDate;
    @SerializedName("mod_date")
    @Expose
    private String modDate;
    @SerializedName("is_deleted")
    @Expose
    private String isDeleted;

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The phoneNo
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * 
     * @param phoneNo
     *     The phone_no
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    /**
     * 
     * @return
     *     The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 
     * @param password
     *     The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 
     * @return
     *     The stripeId
     */
    public Object getStripeId() {
        return stripeId;
    }

    /**
     * 
     * @param stripeId
     *     The stripe_id
     */
    public void setStripeId(Object stripeId) {
        this.stripeId = stripeId;
    }

    /**
     * 
     * @return
     *     The profilePic
     */
    public Object getProfilePic() {
        return profilePic;
    }

    /**
     * 
     * @param profilePic
     *     The profile_pic
     */
    public void setProfilePic(Object profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 
     * @param language
     *     The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 
     * @return
     *     The addDate
     */
    public String getAddDate() {
        return addDate;
    }

    /**
     * 
     * @param addDate
     *     The add_date
     */
    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    /**
     * 
     * @return
     *     The modDate
     */
    public String getModDate() {
        return modDate;
    }

    /**
     * 
     * @param modDate
     *     The mod_date
     */
    public void setModDate(String modDate) {
        this.modDate = modDate;
    }

    /**
     * 
     * @return
     *     The isDeleted
     */
    public String getIsDeleted() {
        return isDeleted;
    }

    /**
     * 
     * @param isDeleted
     *     The is_deleted
     */
    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

}
