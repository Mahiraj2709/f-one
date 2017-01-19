package fairrepair.service.fairrepair.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fairrepair.service.fairrepair.FairRepairApplication;
import fairrepair.service.fairrepair.Globals;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.DateUtil;
import fairrepair.service.fairrepair.utils.DialogFactory;

/**
 * Created by admin on 12/19/2016.
 */

public class RequestFormFragment extends Fragment {
    private static final String TAG = RequestFormFragment.class.getSimpleName();
    private MainActivity activity;
    @BindView(R.id.et_truckModel) EditText et_truckModel;
    @BindView(R.id.et_engineManufacture) EditText et_engineManufacture;
    @BindView(R.id.et_vin) EditText et_vin;
    @BindView(R.id.et_describeNeed) EditText et_describeNeed;
    @BindView(R.id.et_trailer) EditText et_trailer;
    @BindView(R.id.rg_services) RadioGroup rg_services;
    private boolean isServiceSelected = false;
    private String serviceProvider = "";
    public static RequestFormFragment newInstance(String args,String locationName) {
        RequestFormFragment fragment = new RequestFormFragment();
        Bundle data = new Bundle();
        data.putString("args",args);
        data.putString(ApplicationMetadata.LOCATION,locationName);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_requset_form));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.request_form_fragment,container,false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);

        rg_services.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                isServiceSelected = true;
                int id = rg_services.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.rb_roadsideService:
                        serviceProvider = "1";
                        break;
                    case R.id.rb_inShopService:
                        serviceProvider = "2";
                        break;
                    case R.id.rb_shopWithTowing:
                        serviceProvider = "3";
                        break;
                }
            }
        });

//        setTestData();
        return view;
    }


    @OnClick(R.id.btn_submit)
    void Submit() {
        if (validForm()) {
            PrefsHelper prefsHelper = new PrefsHelper(getContext());
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
            requestParams.put(ApplicationMetadata.SERVICE_TYPE_ID, getArguments().getString("args"));
            requestParams.put(ApplicationMetadata.MODEL, getString(et_truckModel));
            requestParams.put(ApplicationMetadata.ENGINE_MANUFACTURER, getString(et_engineManufacture));
            requestParams.put(ApplicationMetadata.VIN, getString(et_vin));
            requestParams.put(ApplicationMetadata.NEED, getString(et_describeNeed));
            requestParams.put(ApplicationMetadata.TRAILER, getString(et_trailer));
            requestParams.put(ApplicationMetadata.SERVICE_PROVIDER_ID, serviceProvider);
            requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));

            if (Globals.getUserLatLng() == null) {
                return;
            }
            requestParams.put(ApplicationMetadata.LATITUDE, Globals.getUserLatLng().latitude +"");
            requestParams.put(ApplicationMetadata.LONGITUDE, Globals.getUserLatLng().longitude +"");
            requestParams.put(ApplicationMetadata.LOCATION, getArguments().getString(ApplicationMetadata.LOCATION));
            requestParams.put(ApplicationMetadata.SERVICE_TIME, DateUtil.getCurrentDate());
            Log.i(TAG,DateUtil.getCurrentDate());
            DataManager dataManager = new DataManager(getContext());
            dataManager.setCallback(new DataManager.RequestCallback() {
                @Override
                public void Data(Object data) {
                    //close this fragment

                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction trans = manager.beginTransaction();
                    trans.remove(RequestFormFragment.this);
                    trans.commit();
                    manager.popBackStack();

                }
            });
            dataManager.sendRequest(requestParams);
        }
    }



    private boolean validForm() {
        if (et_truckModel.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_truck_make).show();
            return false;
        } else if (et_engineManufacture.getText().toString().trim().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_invalid_engine_manufacturer).show();
            return false;
        } else if (et_describeNeed.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.valid_msg_empty_need).show();
            return false;
        } else if (et_trailer.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_empty_with_trailer).show();
            return false;
        } else if (!isServiceSelected) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention, R.string.msg_select_service).show();
            return false;
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        FairRepairApplication.getBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public String getString(EditText editText) {
        if (editText != null) {
            return editText.getText().toString();
        }
        return "";
    }

    private void setTestData(){
        et_truckModel.setText("TATA");
        et_engineManufacture.setText("FORD");
        et_vin.setText("89DDE343D");
        et_describeNeed.setText("SPECIAL NEEDS");
        et_trailer.setText("YES");
    }
}
