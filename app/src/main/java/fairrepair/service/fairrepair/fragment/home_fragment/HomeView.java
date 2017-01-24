package fairrepair.service.fairrepair.fragment.home_fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import fairrepair.service.fairrepair.data.model.Mechanic;
import fairrepair.service.fairrepair.model.AllMechanic;
import fairrepair.service.fairrepair.model.Service;

/**
 * Created by admin on 1/3/2017.
 */

public interface HomeView {
    void generateMap();

    //Map type are two, 1)Show All online mechs 2)show all mech accepted request
    void setMapType(int mapType);
    void changeAddress(String address);

    void hideViews();
    void showViews();
    void showAddressLoadingProgressBar();
    void hideAddressLoadingProgressBar();
    void setCurrentLocation(LatLng currentLocation);
    void enableSendRequest();
    void disableSendRequest();

    void initialSetup();
    void searchLocation();
    void enableMapCurrentLocation();

    void showOnlineMechanics(List<Mechanic> onlineManics);
    void showAcceptedMechanics(AllMechanic allMechanic);
    void setServices(List<Service> serviceList);

    void showTimer(int time);

    void hideSentRequestTime();
    void updateSentRequestTime(int time);
}
