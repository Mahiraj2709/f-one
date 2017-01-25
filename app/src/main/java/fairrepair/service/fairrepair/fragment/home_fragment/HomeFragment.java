package fairrepair.service.fairrepair.fragment.home_fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fairrepair.service.fairrepair.FairRepairApplication;
import fairrepair.service.fairrepair.Globals;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.adapter.AvailableServicesAdapter;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.model.Mechanic;
import fairrepair.service.fairrepair.model.AllMechanic;
import fairrepair.service.fairrepair.model.Service;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.DialogFactory;
import fairrepair.service.fairrepair.utils.LocationUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by admin on 11/22/2016.
 */

public class HomeFragment extends Fragment implements OnMapReadyCallback, HomeView, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private static final int REQUEST_PERMISSION_ACCESS_LOCATION = 12;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static View view;
    private GoogleMap map;
    private HomePresenter presenter = null;
    @BindView(R.id.tv_locationName)
    TextView tv_locationName;
    @BindView(R.id.iv_currentLocation)
    ImageView iv_currentLocation;
    @BindView(R.id.tv_place_marker)
    ImageView tv_place_marker;
    @BindView(R.id.tv_sendRequest)
    TextView tv_sendRequest;
    @BindView(R.id.ll_bottomBar)
    LinearLayout ll_bottomBar;
    @BindView(R.id.pb_addressLoading)
    ProgressBar pb_addressLoading;
    @BindView(R.id.rv_servicesView)
    RecyclerView rv_servicesView;
    @BindView(R.id.circleProgress)
    FrameLayout circleProgress;
    @BindView(R.id.ll_searchLocation)
    LinearLayout ll_searchLocation;
    @BindView(R.id.donut_progress)
    DonutProgress donut_progress;
    @BindView(R.id.tv_totalTime)
    TextView tv_totalTime;
    private AvailableServicesAdapter mServiceAdapter;
    private MainActivity activity;

    private int mapType = ApplicationMetadata.SHOW_ALL_MECH;

    public static HomeFragment newInstance(int args) {
        HomeFragment fragment = new HomeFragment();
        Bundle data = new Bundle();
        data.putInt("args", args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        } else {
            FairRepairApplication.getBus().register(this);
        }
        try {
            view = inflater.inflate(R.layout.content_main, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        donut_progress.setUnfinishedStrokeColor(getResources().getColor(R.color.colorPrimary));
        donut_progress.setFinishedStrokeColor(getResources().getColor(R.color.colorLineSeperator));
        donut_progress.setUnfinishedStrokeWidth(10.0f);
        donut_progress.setFinishedStrokeWidth(10.0f);
        donut_progress.setTextColor(getResources().getColor(R.color.lightGrey));
        tv_totalTime.setText("60");
        rv_servicesView.setLayoutManager(mLayoutManager);
        rv_servicesView.setItemAnimator(new DefaultItemAnimator());
        presenter = new HomePresenterImp(this, this, getContext());
        presenter.initialSetup();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @OnClick(R.id.ll_searchLocation)
    public void launchSearchLocation() {
        presenter.searchLocation();
    }

    @OnClick(R.id.iv_currentLocation)
    public void moveToMyLocation() {
        presenter.moveToCurrentLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnMarkerClickListener(this);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker arg0) {
                return null;

            }

            @Override
            public View getInfoWindow(Marker marker) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
                LatLng latLng = marker.getPosition();
                String[] markerValues = marker.getTitle().split(":");
                ImageView[] stars = {
                        (ImageView) v.findViewById(R.id.iv_star_one),
                        (ImageView) v.findViewById(R.id.iv_star_two),
                        (ImageView) v.findViewById(R.id.iv_star_three),
                        (ImageView) v.findViewById(R.id.iv_star_four),
                        (ImageView) v.findViewById(R.id.iv_star_five),
                };

                int starCount = Integer.parseInt(markerValues[0]);
                for (int i = 0; i < starCount; i++) {
                    stars[i].setImageResource(R.drawable.ic_star_yellow);
                }
                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_price);
                // Setting the longitude
                tvLng.setText("Price: $" + markerValues[1]);
                // Returning the view containing InfoWindow contents
                return v;
            }
        });
        map.setOnInfoWindowClickListener(this);
        presenter.onMapReady();
        //set map type
        presenter.setMapType(mapType);

        String getAccountsPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (activity.hasPermission(getAccountsPermission)) {
            //show myLocation Button here
            presenter.enableCurrentLocation();
        } else {
            activity.requestPermissionsSafely(new String[]{getAccountsPermission},
                    REQUEST_PERMISSION_ACCESS_LOCATION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place: " + place.getName());
                //set the text view with this location
                presenter.setSearchPlace(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

            switch (requestCode) {
                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case RESULT_OK:
                            Log.e("Settings", "Result OK");
                            Toast.makeText(getActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
                            //startLocationUpdates();
                            break;
                        case RESULT_CANCELED:
                            Log.e("Settings", "Result Cancel");
                            Toast.makeText(getActivity(), "GPS is disabaled in your device", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.enableCurrentLocation();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(getActivity(),
                            R.string.title_permissions,
                            R.string.permission_not_accepted_access_location).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @OnClick(R.id.tv_sendRequest)
    public void sendRequest() {
        //check if the service list is there

        presenter.openSendRequest();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void updateMapForOnlineMech(ArrayList<Mechanic> onlineMechs) {
        //mapFragment.showOnlineMechanicOnMap(onlineMechs);
    }

    @Override
    public void generateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void setMapType(int mapType) {
        //map type
    }

    @Override
    public void setCurrentLocation(LatLng currentLocation) {
        if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    @Override
    public void changeAddress(final String address) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_locationName.setText(address);
            }
        });
    }

    //when map is moved hide all view
    @Override
    public void hideViews() {
        ll_bottomBar.animate().translationY(200);
        tv_sendRequest.animate().translationY(200);
        iv_currentLocation.animate().translationY(200);
    }

    //when map is idle show all view
    @Override
    public void showViews() {
        ll_bottomBar.animate().translationY(0);
        tv_sendRequest.animate().translationY(0);
        iv_currentLocation.animate().translationY(0);
    }

    @Override
    public void showAddressLoadingProgressBar() {
        pb_addressLoading.setVisibility(View.VISIBLE);
        tv_place_marker.setVisibility(View.GONE);
    }

    @Override
    public void hideAddressLoadingProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb_addressLoading.setVisibility(View.GONE);
                tv_place_marker.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void initialSetup() {
        LocationUtils locationUtils = new LocationUtils(getActivity());
        locationUtils.showSettingDialog();

        //receiveNotification(testAcceptedUser());
        String notificationData = getActivity().getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA);
        int notificationType = getActivity().getIntent().getIntExtra(ApplicationMetadata.NOTIFICATION_TYPE, -1);
        if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_REQ_ACCEPTED) {
            presenter.setRequestAcceptedMech(new Gson().fromJson(notificationData, AllMechanic.class));
            mapType = ApplicationMetadata.SHOW_MECH_REQUEST;
            //setMapType(mapType);
        } else {

        }
    }

    @Override
    public void searchLocation() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void enableMapCurrentLocation() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        iv_currentLocation.setVisibility(View.VISIBLE);
    }

    @Override
    public void showOnlineMechanics(List<Mechanic> onlineManics) {
        //show mechanic on the map
        if (map != null) {
            map.clear();

            //add inner circle
            map.addCircle(new CircleOptions()
                    .center(Globals.getUserLatLng())
                    .radius(300)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            //add outer circle
            map.addCircle(new CircleOptions()
                    .center(Globals.getUserLatLng())
                    .radius(600)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            for (Mechanic mech : onlineManics) {
                try {
                    LatLng latLng = new LatLng(Double.parseDouble(mech.getLat()), Double.parseDouble(mech.getLng()));
                    MarkerOptions marker = new MarkerOptions().position(latLng).title(
                            (mech.getAvgRating() != null ? mech.getAvgRating() : "0") + ":" + mech.getHourlyServiceCharges());
                    // Changing marker icon
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon));
                    map.addMarker(marker);
                } catch (NumberFormatException e) {
                    //the parseDouble failed and you need to handle it here
                    Log.e(TAG, e.toString());
                }
            }
        }
    }

    @Override
    public void showAcceptedMechanics(AllMechanic allMechanic) {

        if (map != null) {
            map.clear();
        }
        List<LatLng> latLngs = new ArrayList<>();
        for (AllMechanic.Mechanic mech : allMechanic.mechanicList) {
            Log.i(TAG, "" + allMechanic.mechanicList.size());
            LatLng mechLatLng = new LatLng(Double.parseDouble(mech.latitude), Double.parseDouble(mech.longitude));
            latLngs.add(mechLatLng);
            if (map != null) {
                //add inner circle
                map.addMarker(new MarkerOptions().position(mechLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon)).title(((mech.avg_rate != null) ? mech.avg_rate : "0") + ":" + mech.offer_price + ":" + mech.app_provider_id +":"+allMechanic.request_id));
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });
            }
        }

        LatLng centerLatLng = LocationUtils.computeCentroid(latLngs);
        map.addCircle(new CircleOptions()
                .center(centerLatLng)
                .radius(300)
                .strokeWidth(0f)
                .fillColor(getResources().getColor(R.color.colorMapCircle)));

        //add outer circle
        map.addCircle(new CircleOptions()
                .center(centerLatLng)
                .radius(600)
                .strokeWidth(0f)
                .fillColor(getResources().getColor(R.color.colorMapCircle)));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, ApplicationMetadata.MAP_ZOOM_VALUE));
    }

    @Override
    public void setServices(List<Service> serviceList) {

        Log.i(TAG, serviceList.size() + "");
        mServiceAdapter = new AvailableServicesAdapter(serviceList, getContext(), 0, HomeFragment.this);
        mServiceAdapter.setItemClickListener(new AvailableServicesAdapter.MyClickListerer() {

            @Override
            public void onItemClick(int position, String serviceId, View view) {
                presenter.setSelectedServiceId(serviceId);
                presenter.getOnlineMechanics();
            }
        });
        rv_servicesView.setAdapter(mServiceAdapter);
    }

    @Override
    public void showTimer(int time) {
        ObjectAnimator anim = ObjectAnimator.ofInt(donut_progress, "progress",time * 60);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(time * 60 * 1000);
        anim.start();
        ll_searchLocation.setVisibility(View.GONE);
        circleProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSentRequestTime() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ll_searchLocation.setVisibility(View.VISIBLE);
                circleProgress.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void updateSentRequestTime(final int time) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_totalTime.setText(time + "");
            }
        });
    }

    @Override
    public void onCameraIdle() {
        presenter.onCameraIdle(map.getCameraPosition().target);
    }


    @Override
    public void onCameraMoveStarted(int i) {
        presenter.onCameraMove();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void enableSendRequest() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_sendRequest.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void disableSendRequest() {
        tv_sendRequest.setVisibility(View.GONE);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        presenter.infoWindowClicked(marker);
    }

    //receive notification from the customer
    @Subscribe
    public void receiveNotification(AllMechanic allMechanic) {
        //request has been accepted
        DialogFactory.createAlertDialog(getContext(), allMechanic.message);
        presenter.setRequestAcceptedMech(allMechanic);
        mapType = ApplicationMetadata.SHOW_MECH_REQUEST;/**/
        presenter.setMapType(mapType);
    }

    private AllMechanic testAcceptedUser() {
        AllMechanic allMechanic = new AllMechanic();
        allMechanic.total_offer = "1";
        allMechanic.type = "2";
        allMechanic.request_id = "3";
        allMechanic.message = "We found 1 offers for your request";

        List<AllMechanic.Mechanic> mechanicList = new ArrayList<>();
        AllMechanic.Mechanic mechanic = new AllMechanic().new Mechanic();
        mechanic.app_provider_id = "1";
        mechanic.latitude = "28.5410496";
        mechanic.avg_rate = "4";
        mechanic.offer_price = "55.00";
        mechanic.longitude = "77.3985685";
        mechanicList.add(mechanic);

        allMechanic.mechanicList = mechanicList;
        return allMechanic;
    }
}
