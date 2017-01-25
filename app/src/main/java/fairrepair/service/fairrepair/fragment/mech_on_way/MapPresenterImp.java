package fairrepair.service.fairrepair.fragment.mech_on_way;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import fairrepair.service.fairrepair.Globals;
import fairrepair.service.fairrepair.app.BaseActivity;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.data.model.MechanicDetail;
import fairrepair.service.fairrepair.fragment.home_fragment.HomeFragment;
import fairrepair.service.fairrepair.model.NotificationData;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;


/**
 * Created by admin on 12/27/2016.
 */

public class MapPresenterImp implements MapPresenter, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,CancelRequestDialogFragment.SubmitReqCallBack {
    private static final String TAG = MapPresenterImp.class.getSimpleName();
    private static final int CALL_PERMISSIONS_REQUEST = 231;
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private String mLastUpdateTime;
    private MechOnWayMapView view;
    private MechanicDetail mechanicDetail = null;
    private FragmentActivity activity = null;
    private Context context = null;
    private String[] permissions = {Manifest.permission.CALL_PHONE};
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    private Bundle data = null;
    Map<String,String> locationRequest = new HashMap<>();
    private LatLng mLatLng = null;
    private Timer timer = new Timer();

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
        buildGoogleApiClient();
        this.view.generateMap();
    }

    @Override
    public void setMechanicDetail(MechanicDetail mechanicDetail) {
        this.mechanicDetail = mechanicDetail;
        this.view.setMechanicDetails(this.mechanicDetail);
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
    public void connectToLocationService() {

    }

    @Override
    public void disconnectFromLocationService() {

    }

    protected synchronized void buildGoogleApiClient() {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
        //got the the current location for the first time
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void connectToGoogleApiClient() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.getLatitude() + " longitude " + location.getLongitude());
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.i(TAG, "last update time for location is" + mLastUpdateTime);

        //move map to the current location
        //moveToLatLng();
        LatLng mechCurrentLoc = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mLatLng = mechCurrentLoc;

        /*if(customerLatLng != null)
            view.setMap(mechCurrentLoc,customerLatLng);*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void cancelRequest(String message) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.APP_PROVIDER_ID, data.getString(ApplicationMetadata.APP_PROVIDER_ID)); // MECH ID
        requestParams.put(ApplicationMetadata.REQUEST_ID, data.getString(ApplicationMetadata.REQUEST_ID));
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

    private void upldateLocation(String serviceProviderId) {
        locationRequest.clear();
        locationRequest.put(ApplicationMetadata.CUSTOMER_ID, serviceProviderId);
        locationRequest.put(ApplicationMetadata.SESSION_TOKEN,prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        if (mLatLng == null) {
            return;
        }
        locationRequest.put(ApplicationMetadata.LATITUDE,mLatLng.latitude+"");
        locationRequest.put(ApplicationMetadata.LONGITUDE,mLatLng.longitude+"");
        locationRequest.put(ApplicationMetadata.LANGUAGE,"en");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //call update location every 5 minutes
                dataManager.updateLatLng(locationRequest);
                dataManager.setmLocationUpdateCallback(new DataManager.LocationUpdateCallback() {
                    @Override
                    public void locationReceived() {
                        /*customerLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        List<LatLng> latLngList = new ArrayList<>();
                        latLngList.add(myLatLng);
                        latLngList.add(customerLatLng);

                        if (myLatLng != null && customerLatLng != null) {
                            view.drawPolylines(latLngList);
                        }*/
                    }
                });

            }
        }, 0, 2 * 60  * 1000);

//        }, 0, 5 * 60 * 1000);
    }

    @Override
    public void mechanicFinishedTask(NotificationData data) {
        //mechanic has finished work

    }

    @Override
    public void mechanicArrived(NotificationData data) {

    }

    @Override
    public void setBundleData(Bundle mech_data) {
        this.data = mech_data;
    }
}

