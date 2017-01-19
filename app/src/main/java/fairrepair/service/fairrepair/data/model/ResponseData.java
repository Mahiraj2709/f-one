
package fairrepair.service.fairrepair.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fairrepair.service.fairrepair.model.Service;

public class ResponseData {

    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;
    @SerializedName("session_token")
    @Expose
    private String sessionToken;
    @SerializedName("fileuploaderror")
    @Expose
    private Object fileuploaderror;

    @SerializedName("content")
    private String staticContent;

    @SerializedName("services")
    private List<Service> services;

    @SerializedName("allOnlineMechanic")
    private List<Mechanic> onlineMech;

    @SerializedName("mechanic_details")
    private MechanicDetail mechanicDetail;
    /**
     * 
     * @return
     *     The userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 
     * @param userInfo
     *     The user_info
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * 
     * @return
     *     The sessionToken
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * 
     * @param sessionToken
     *     The session_token
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /**
     * 
     * @return
     *     The fileuploaderror
     */
    public Object getFileuploaderror() {
        return fileuploaderror;
    }

    /**
     * 
     * @param fileuploaderror
     *     The fileuploaderror
     */
    public void setFileuploaderror(Object fileuploaderror) {
        this.fileuploaderror = fileuploaderror;
    }

    public String getStaticContent() {
        return staticContent;
    }

    public void setStaticContent(String staticContent) {
        this.staticContent = staticContent;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Mechanic> getOnlineMech() {
        return onlineMech;
    }

    public void setOnlineMech(List<Mechanic> onlineMech) {
        this.onlineMech = onlineMech;
    }

    public MechanicDetail getMechanicDetail() {
        return mechanicDetail;
    }

    public void setMechanicDetail(MechanicDetail mechanicDetail) {
        this.mechanicDetail = mechanicDetail;
    }
}
