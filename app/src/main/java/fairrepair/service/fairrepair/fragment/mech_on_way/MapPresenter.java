package fairrepair.service.fairrepair.fragment.mech_on_way;

import fairrepair.service.fairrepair.data.model.MechanicDetail;

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
}
