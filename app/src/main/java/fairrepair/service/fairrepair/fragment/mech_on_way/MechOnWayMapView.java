package fairrepair.service.fairrepair.fragment.mech_on_way;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import fairrepair.service.fairrepair.data.model.MechanicDetail;
import fairrepair.service.fairrepair.model.NotificationData;

/**
 * Created by admin on 12/27/2016.
 */

public interface MechOnWayMapView {
    void updateLocationOnMap(Location location);
    void generateMap();
    void setMechanicDetails(MechanicDetail mechachicDetails);
    void callMechanic();
    void drawPolylines(List<LatLng> centerLatLng);

    void mechArrived(NotificationData data);
}
