package fairrepair.service.fairrepair.fragment.home_fragment;

import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fairrepair.service.fairrepair.Globals;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.data.model.Mechanic;
import fairrepair.service.fairrepair.model.AllMechanic;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;


/**
 * Created by MAHIRAJ on 3/11/2016.
 */
public class MapFragment extends SupportMapFragment  {
    private static final String TAG = MapFragment.class.getSimpleName();
    private GoogleMap mMap;
    //send LatLng to the calling class
    private LatLngListener latLngListener = null;

    private LatLng mapCenterLatLng = null;
    private boolean setMapForFirstTime = false;
    private AllMechanic mechanics = null;
    private int MAP_TYPE = 110;



    public void setMapType(int type) {
        MAP_TYPE = type;
    }


    public interface LatLngListener {
        void sendLatLng(LatLng latLng);
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public void onPause() {
        super.onPause();

    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //Toast.makeText(getContext(), "camera idle", Toast.LENGTH_SHORT).show();
                if (mMap != null) {

                    mapCenterLatLng = mMap.getCameraPosition().target;

                    getAddressFromLatLong(mapCenterLatLng);

                    Globals.setUserLatLng(mapCenterLatLng);

                    if (MAP_TYPE == ApplicationMetadata.SHOW_MECH_REQUEST) {
                        //don't request when mech request is there
                        return;
                    }
                    Log.i(TAG,MAP_TYPE+"");
                    PrefsHelper prefsHelper = new PrefsHelper(getContext());
                    Map<String, String> requestParams = new HashMap<>();
                    requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
                    requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
                    requestParams.put(ApplicationMetadata.SERVICE_TYPE, "2");

                    if (Globals.getUserLatLng() == null) {
                        return;
                    }
                    requestParams.put(ApplicationMetadata.LATITUDE, Globals.getUserLatLng().latitude +"");
                    requestParams.put(ApplicationMetadata.LONGITUDE, Globals.getUserLatLng().longitude +"");
                    DataManager dataManager = new DataManager(getContext());
                    dataManager.setCallback(new DataManager.RequestCallback() {
                        @Override
                        public void Data(Object data) {

                            showOnlineMechanicOnMap((ArrayList<Mechanic>)data);
                        }
                    });
                    dataManager.getOnlineMechs(requestParams);
                }
            }
        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
//                mapOperationListener.hideViews();
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();
                String[] markerValues = arg0.getTitle().split(":");

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_price);


                // Setting the longitude
                tvLng.setText("Price: $"+markerValues[1]);

                // Returning the view containing InfoWindow contents
                return v;

            }

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
               /* Fragment fragment = CompanyInformationFragment.newInstance(""+1);
                ((MainActivity)getActivity()).addFragmentToStack(fragment,"company_information");*/
            }
        });
        mMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
                    boolean doNotMoveCameraToCenterMarker = true;
                    public boolean onMarkerClick(Marker marker) {
                        //Do whatever you need to do here ....
                        marker.showInfoWindow();
                        return doNotMoveCameraToCenterMarker;
                    }
                });
        if (MAP_TYPE == ApplicationMetadata.SHOW_MECH_REQUEST) {
            //showAllMechRequstOnMap(mechanics);
            showAllMechRequstOnMap(testData());

        }
        Log.i(TAG, "map is ready");
    }



    private void initCamera(Location location) {

        if (location == null) {
            return;
        }
    }



    private void moveToLatLng() {
        if (mMap != null) {
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), ApplicationMetadata.MAP_ZOOM_VALUE));
        }
    }

    public void moveToLatLng(LatLng latLng, String placeName) {
        if (mMap != null) {
            mMap.clear();
            //add inner circle
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(150)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            //add outer circle
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(300)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            mMap.addMarker(new MarkerOptions().position(latLng));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ApplicationMetadata.MAP_ZOOM_VALUE));
        }

    }

    private String getAddressFromLatLong(final LatLng latLng) {
        String addressFinal = "";
        //show address loading progress bar
//        mapOperationListener.showAddressLoadingProgressBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(getActivity());
                String address = "";
                String state = "", city = "", pincode = "";
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
                    state = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getLocality();
                    city = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAdminArea();
                    pincode = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getPostalCode();
                    Log.i("ADDRESS", address + "---" + state + "-----" + city + "=====" + pincode);
                    //addressFinal = address + ","+state;
//                    mapOperationListener.changeAddress(address + ","+ state);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException es) {
                    Log.e(TAG,es.toString());
                }finally {
//                    mapOperationListener.hideAddressLoadingProgressBar();
                }
            }
        }).start();
        return addressFinal;
    }
    //this method set the current position on the map in the edit text box of the activity

    private void setLatLangToCurrentPosition(LatLng latLng) {
        //mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

    }

    public void currenLocation() {
        // Clears all the existing markers
        /*if (mLocation != null) {
            //pass the lat lng to the calling class
            latLngListener.sendLatLng(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
        }
        mMap.clear();
        if (mLocation == null) {
            Toast.makeText(getActivity(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Getting a place from the places list

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);

        // Locate the first location

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        //this method set the current location on map to lat and long edit text in activity
        setLatLangToCurrentPosition(latLng);*/
    }

    //listener for the latlng
    public void setLatLngListener(LatLngListener listener) {
        this.latLngListener = listener;
    }

    @Subscribe
    public void showOnlineMechanicOnMap(ArrayList<Mechanic> onlineMechanics) {

    }

    public void setAllMechanic(AllMechanic mechanics) {
        this.mechanics = mechanics;
    }
    public void showAllMechRequstOnMap(AllMechanic mechanics) {

        this.mechanics = mechanics;

        /*if (mechanics == null) {
            if (mLocation != null && mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()), ApplicationMetadata.MAP_ZOOM_VALUE));
            }
            return;
        }
        if (mMap != null) {
            mMap.clear();

        }
        List<LatLng> latLngs = new ArrayList<>();
        for (AllMechanic.Mechanic mech:mechanics.mechanicList) {
            Log.i(TAG, ""+mechanics.mechanicList.size());
            LatLng mechLatLng = new LatLng(Double.parseDouble(mech.latitude), Double.parseDouble(mech.longitude));
            latLngs.add(mechLatLng);
            if (mMap != null) {
                //add inner circle
                mMap.addMarker(new MarkerOptions().position(mechLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon)).title("Test name:"+mech.avg_rate));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return false;
                    }
                });
            }
        }

        LatLng centerLatLng = LocationUtils.computeCentroid(latLngs);
        mMap.addCircle(new CircleOptions()
                    .center(centerLatLng)
                    .radius(300)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));

            //add outer circle
            mMap.addCircle(new CircleOptions()
                    .center(centerLatLng)
                    .radius(600)
                    .strokeWidth(0f)
                    .fillColor(getResources().getColor(R.color.colorMapCircle)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, ApplicationMetadata.MAP_ZOOM_VALUE));*/
    }

    //test data for the notification
    private AllMechanic testData() {
        AllMechanic allMechanic = new AllMechanic();
        allMechanic.total_offer = 10+"";
        allMechanic.request_id= 7+"";
        allMechanic.type = 2+"";
        allMechanic.message = "We found 10 offers for your request";
        allMechanic.mechanicList = new ArrayList<>();
        for(int i = 0; i< 10; i++) {
            AllMechanic.Mechanic singleMech = new AllMechanic().new Mechanic();
            singleMech.app_provider_id = i+"";
            singleMech.avg_rate = (i/2)+"";
            singleMech.offer_price = (i)+""+i;
            singleMech.offer_id = (i)+"";
            singleMech.latitude = "28.54"+i;
            singleMech.longitude = "77.39"+i;
            allMechanic.mechanicList.add(singleMech);
        }
        return allMechanic;
    }
}