package fairrepair.service.fairrepair.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import fairrepair.service.fairrepair.FairRepairApplication;
import fairrepair.service.fairrepair.Globals;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.data.model.MechanicDetail;
import fairrepair.service.fairrepair.fragment.mech_on_way.MechOnTheWayFragment;
import fairrepair.service.fairrepair.model.AllMechanic;
import fairrepair.service.fairrepair.model.Service;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.LocationUtils;

import static fairrepair.service.fairrepair.R.id.image_profile;

/**
 * Created by admin on 12/19/2016.
 */

public class CompanyInformationFragment extends Fragment {
    private static final String TAG = CompanyInformationFragment.class.getSimpleName();
    @BindView(image_profile) CircleImageView imageProfile;
    @BindView(R.id.tv_companyName) TextView tv_companyName;
    @BindView(R.id.tv_distance) TextView tv_distance;
    @BindView(R.id.tv_address) TextView tv_address;
    @BindView(R.id.tv_mobileNo) TextView tv_mobileNo;
    @BindView(R.id.tv_offerPrice) TextView tv_offerPrice;
    @BindView(R.id.tv_desc) TextView tv_desc;
    @BindView(R.id.rating_company) RatingBar rating_company;
    @BindView(R.id.btn_acceptOffer) Button btn_acceptOffer;
    @BindView(R.id.ll_services) LinearLayout ll_services;
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    private MainActivity activity;
    private String mechId;
    private MechanicDetail mechanicDetail = null;
    private AllMechanic.Mechanic mechanic = null;
    public static CompanyInformationFragment newInstance(String args, AllMechanic.Mechanic mechanic) {
        CompanyInformationFragment fragment = new CompanyInformationFragment();
        Bundle data = new Bundle();
        data.putString("args",args);
        data.putSerializable("mechanic",mechanic);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_company_information));
        mechId = getArguments().getString("args");
        mechanic = (AllMechanic.Mechanic)getArguments().getSerializable("mechanic");
        activity = (MainActivity) (getActivity());

        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());

        Log.i(TAG,"above");
        //request Data for the mech
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.APP_PROVIDER_ID, mechId); // MECH ID
        requestParams.put(ApplicationMetadata.REQUEST_ID, prefsHelper.getPref(ApplicationMetadata.USER_ID,"")); // CUSTOMER ID

        if (Globals.getUserLatLng() == null) {
            return;
        }
        Log.i(TAG,"below");
        requestParams.put(ApplicationMetadata.LATITUDE, Globals.getUserLatLng().latitude +"");
        requestParams.put(ApplicationMetadata.LONGITUDE, Globals.getUserLatLng().longitude +"");
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                mechanicDetail = (MechanicDetail)data;
                setAllView();
            }
        });

        dataManager.getMechanicDetail(requestParams);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.company_info_fragment,container,false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);
        return view;
    }

    //set all the details of the mechanic
    private void setAllView() {
        tv_companyName.setText(mechanicDetail.getName());
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        tv_distance.setText(
                df.format(LocationUtils.calculateDistance(
                        new LatLng(Double.parseDouble(mechanic.latitude),Double.parseDouble(mechanic.longitude)),
                        Globals.getUserLatLng()
                ))+ "mi \n away");

        Glide.with(this)
                .load(ApplicationMetadata.MECHANIC_IMAGE_BASE_URL + mechanicDetail.getProfilePic())
                .thumbnail(0.2f)
                .centerCrop()
                .error(R.drawable.ic_profile_photo)
                .into(imageProfile);

        rating_company.setRating((mechanic.avg_rate != null)? Float.parseFloat(mechanic.avg_rate):0);
        tv_desc.setText(mechanicDetail.getDesc());
        tv_address.setText(mechanicDetail.getAddress());
        tv_mobileNo.setText(mechanicDetail.getPhoneNo());
        tv_offerPrice.setText("Offer Price $"+ ((mechanic.offer_price != null)?mechanic.offer_price:"na"));

        for (Service service: mechanicDetail.getServiceList()) {
            View serviceView = getActivity().getLayoutInflater().inflate(R.layout.mech_service_name,null);
            TextView tv_serviceName = (TextView) serviceView.findViewById(R.id.tv_serviceName);
            tv_serviceName.setText(service.getServiceName());
            ll_services.addView(serviceView);
        }
    }

    @OnClick(R.id.btn_acceptOffer)
    public void acceptOffer() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.APP_PROVIDER_ID, mechId); // MECH ID
        requestParams.put(ApplicationMetadata.REQUEST_ID, prefsHelper.getPref(ApplicationMetadata.USER_ID,"")); // CUSTOMER ID
        requestParams.put(ApplicationMetadata.OFFER_PRICE, mechanic.offer_price);
        if (Globals.getUserLatLng() == null) {
            return;
        }
        Log.i(TAG,"below");
        requestParams.put(ApplicationMetadata.LATITUDE, Globals.getUserLatLng().latitude +"");
        requestParams.put(ApplicationMetadata.LONGITUDE, Globals.getUserLatLng().longitude +"");
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                Fragment fragment = MechOnTheWayFragment.newInstance(0,mechanicDetail);
                ((MainActivity)getActivity()).addFragmentToStack(fragment,"mech_on_way");
            }
        });

        dataManager.acceptOffer(requestParams);
    }
}
