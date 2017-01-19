package fairrepair.service.fairrepair.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by admin on 1/6/2017.
 */

public class MyMarkerItem implements ClusterItem {
    private final LatLng mPosition;
    public MyMarkerItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
