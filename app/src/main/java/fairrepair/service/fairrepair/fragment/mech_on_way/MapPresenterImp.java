package fairrepair.service.fairrepair.fragment.mech_on_way;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fairrepair.service.fairrepair.Globals;
import fairrepair.service.fairrepair.app.BaseActivity;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.data.model.MechanicDetail;
import fairrepair.service.fairrepair.fragment.home_fragment.HomeFragment;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;


/**
 * Created by admin on 12/27/2016.
 */

public class MapPresenterImp implements MapPresenter, LocationListener,CancelRequestDialogFragment.SubmitReqCallBack {
    private static final String TAG = MapPresenterImp.class.getSimpleName();
    private static final int CALL_PERMISSIONS_REQUEST = 231;
    private MechOnWayMapView view;
    private MechanicDetail mechanicDetail = null;
    private FragmentActivity activity = null;
    private Context context = null;
    private String[] permissions = {Manifest.permission.CALL_PHONE};
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;

    public MapPresenterImp(MechOnWayMapView view, FragmentActivity fragmentActivity, Context context) {
        if (view == null) throw new NullPointerException("view can not be NULL");
        if (fragmentActivity == null)
            throw new NullPointerException("AppCompactActivity can not be NULL");
        if (context == null) throw new NullPointerException("context can not be NULL");

        this.view = view;
        activity = fragmentActivity;
        this.context = context;
        prefsHelper = new PrefsHelper(context);
        dataManager = new DataManager(context);
        /*this.googleLocationApiManager = new GoogleLocationApiManager(fragmentActivity, context);
        this.googleLocationApiManager.setLocationCallback(this);

        this.geofencingManager = new GeofencingManager(this.googleLocationApiManager, context);
        this.geofencingManager.setmGeofenceCallback(this);*/

        this.view.generateMap();
    }

    @Override
    public void setMechanicDetail(MechanicDetail mechanicDetail) {
        this.mechanicDetail = mechanicDetail;
        this.view.setMechanicDetails(this.mechanicDetail);
    }

    @Override
    public void connectToLocationService() {

    }

    @Override
    public void disconnectFromLocationService() {

    }

    @Override
    public void getCompanyProfile() {

    }

    @Override
    public void onMapReady() {
        //add path between mech and customer
        List<LatLng> latLngList = new ArrayList<>();
        latLngList.add(new LatLng(Double.parseDouble(mechanicDetail.getLatitude()), Double.parseDouble(mechanicDetail.getLongitude())));
        latLngList.add( new LatLng(Globals.getUserLatLng().latitude,  Globals.getUserLatLng().longitude));
        //List of all the lat lng to show
        view.drawPolylines(latLngList);
    }

    @Override
    public void callMechanic() {
        if (((BaseActivity)activity).hasPermission(permissions[0])) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mechanicDetail.getPhoneNo()));
            activity.startActivity(intent);
        } else {
            ((BaseActivity)activity).requestPermissionsSafely(permissions,CALL_PERMISSIONS_REQUEST);
        }
   }

    @Override
    public void cancelRequest() {
        CancelRequestDialogFragment cancelRequestFragment = CancelRequestDialogFragment.newInstance("no content");
        cancelRequestFragment.setSubmitReqCallback(this);
        cancelRequestFragment.show(activity.getSupportFragmentManager(), "cancel_request");
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void cancelRequest(String message) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.APP_PROVIDER_ID, mechanicDetail.getId()); // MECH ID
        requestParams.put(ApplicationMetadata.REQUEST_ID, "7"); // CUSTOMER ID
        requestParams.put(ApplicationMetadata.REASON_FOR_CANCEL, message);
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                Fragment fragment = HomeFragment.newInstance(0);
                ((MainActivity)activity).addFragmentToStack(fragment,"home_fragment");
            }
        });

        dataManager.cancelRequest(requestParams);
    }
}
