package fairrepair.service.fairrepair.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fairrepair.service.fairrepair.FairRepairApplication;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.adapter.ServiceHistoryAdapter;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.model.ServiceHisrotyResponse;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.DateUtil;

/**
 * Created by admin on 11/22/2016.
 */

public class ServiceHistoryFragment extends Fragment {
    private MainActivity activity;
    @BindView(R.id.tv_fromDate) TextView tv_fromDate;
    @BindView(R.id.tv_toDate) TextView tv_toDate;
    @BindView(R.id.rv_servicesView) RecyclerView rv_servicesView;
    private ServiceHistoryAdapter mServiceAdapter;
    private List<ServiceHisrotyResponse.ServiceHistory> mServiceList;
    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;

    public static ServiceHistoryFragment newInstance(int args) {
        ServiceHistoryFragment fragment = new ServiceHistoryFragment();
        Bundle data = new Bundle();
        data.putInt("args",args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) (getActivity());
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_service_history));
        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());
        //make request
        getAllSercviceHistory();
    }

    private void getAllSercviceHistory() {
//make payment
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN,""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.FROM_DATE,DateUtil.getCurrentDate() ); // MECH ID
        requestParams.put(ApplicationMetadata.TO_DATE, DateUtil.getCurrentDate());
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                //all service history
            }
        });

        dataManager.allrequesthistory(requestParams);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_history_fragment,container,false);
        ButterKnife.bind(this, view);
        FairRepairApplication.getBus().register(this);
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        mServiceList = getTestData();
        mServiceAdapter  = new ServiceHistoryAdapter(mServiceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_servicesView.setLayoutManager(mLayoutManager);
        rv_servicesView.setItemAnimator(new DefaultItemAnimator());
        rv_servicesView.setAdapter(mServiceAdapter);
        mServiceAdapter.setItemClickListener(new ServiceHistoryAdapter.MyClickListerer() {
            @Override
            public void onItemClick(int position, View view) {
                Fragment fragment = ServiceHistoryDetailFragment.newInstance(1);
                activity.addFragmentToStack(fragment,"service_detail_fragment");
                FairRepairApplication.getBus().post(mServiceList.get(position));
            }
        });
    }

    @OnClick(R.id.rl_fromDate)
    void selectFromDate() {
        DialogFragment datePicker = DatePickerFragment.getInstance(0);
        datePicker.show(getActivity().getSupportFragmentManager(), "from_date");
    }
    @OnClick(R.id.rl_toDate)
    void selectToDate() {
        DialogFragment datePicker = DatePickerFragment.getInstance(1);
        datePicker.show(getActivity().getSupportFragmentManager(), "to_date");
    }

    @Subscribe
    public void getSelectedDate(DatePickerFragment.DateData dateData) {
        if (dateData.type == 0) {
            tv_fromDate.setText(dateData.date);
        }
        if (dateData.type == 1) {
            tv_toDate.setText(dateData.date);

        }
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

    private List<ServiceHisrotyResponse.ServiceHistory> getTestData() {
        List<ServiceHisrotyResponse.ServiceHistory> serviceHistoryList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ServiceHisrotyResponse.ServiceHistory serviceHistory = new ServiceHisrotyResponse(). new ServiceHistory();
            serviceHistory.serviceName = "wheel change "+ i;
            serviceHistory.serviceId = "Request ID - "+ i;
            serviceHistory.serviceDate = i+"-11-2016";
            serviceHistory.servicePrice = "$"+ i+(i*i);
            serviceHistoryList.add(serviceHistory);
        }
        return serviceHistoryList;
    }
}
