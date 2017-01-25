package fairrepair.service.fairrepair.fragment.mech_on_way;

import android.os.Bundle;

import fairrepair.service.fairrepair.data.model.MechanicDetail;
import fairrepair.service.fairrepair.model.NotificationData;

/**
 * Created by admin on 12/27/2016.
 */

public interface MapPresenter {
    void setMechanicDetail(MechanicDetail mechanicDetail);
    void connectToLocationService();
    void disconnectFromLocationService();
    void getCompanyProfile();
    void onMapReady();
    void callMechanic();

    void connectToGoogleApiClient();
    void onResume();
    void onStop();
    void onPause();

    void cancelRequest();

    void mechanicFinishedTask(NotificationData data);
    void mechanicArrived(NotificationData data);

    void setBundleData(Bundle mech_data);
}
