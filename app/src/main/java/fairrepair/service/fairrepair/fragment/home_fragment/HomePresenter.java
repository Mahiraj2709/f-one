package fairrepair.service.fairrepair.fragment.home_fragment;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import fairrepair.service.fairrepair.model.AllMechanic;

/**
 * Created by admin on 1/3/2017.
 */

public interface HomePresenter {
    void onMapReady();
    void setMapType(int mapType);

    void initialSetup();
    void searchLocation();

    void setSearchPlace(Place place);
    void enableCurrentLocation();

    void openSendRequest();
    void onCameraMove();
    void onCameraIdle(LatLng centerLatLng);
    void getOnlineMechanics();
    void moveToCurrentLocation();
    void setSelectedServiceId(String serviceId);
    void setRequestAcceptedMech(AllMechanic mechanic);

    void connectToGoogleApiClient();
    void onResume();
    void onStop();
    void onPause();

    void infoWindowClicked(Marker marker);
}
