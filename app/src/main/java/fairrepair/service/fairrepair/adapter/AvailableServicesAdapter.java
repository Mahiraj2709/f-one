package fairrepair.service.fairrepair.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.fragment.home_fragment.HomeFragment;
import fairrepair.service.fairrepair.model.Service;

import static com.google.android.gms.internal.zzs.TAG;
import static fairrepair.service.fairrepair.utils.ApplicationMetadata.SERVICE_IMAGE_BASE_URL;

/**
 * Created by admin on 11/29/2016.
 */

public class AvailableServicesAdapter extends RecyclerView.Adapter<AvailableServicesAdapter.RibotHolder> {
    private List<Service> mServiceList;
    public static AvailableServicesAdapter.MyClickListerer myClickListerer;
    private int selected_position = 0;
    private Context mContext;
    private HomeFragment homeFragment = null;
    public AvailableServicesAdapter(List<Service> mServiceList,Context mContext,int selected_position,HomeFragment homeFragment) {
        this.mServiceList = mServiceList;
        this.mContext = mContext;
        this.selected_position = selected_position;
        this.homeFragment = homeFragment;
        callApi();
    }

    @Override
    public AvailableServicesAdapter.RibotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.single_home_service, parent, false);
        return new AvailableServicesAdapter.RibotHolder(view);
    }

    @Override
    public void onBindViewHolder(final AvailableServicesAdapter.RibotHolder holder, final int position) {
        Service service = mServiceList.get(position);
        holder.tv_serviceName.setText(service.getServiceName());

        Log.i(TAG,selected_position + " "+ position);
        if (selected_position == position) {
            Glide.with(mContext)
                    .load(SERVICE_IMAGE_BASE_URL+service.getServiceSelectedImage())
                    .crossFade()
                    .into(holder.iv_serviceImage);
        } else {
            Glide.with(mContext).
                    load(SERVICE_IMAGE_BASE_URL+service.getServiceImage())
                    .crossFade()
                    .into(holder.iv_serviceImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListerer.onItemClick(position,mServiceList.get(position).getId()+"", v);
                notifyItemChanged(selected_position);
                if (selected_position == position) {
                    //show detail
                } else {
//load the data again
                    callApi();
                }
                selected_position = position;
                notifyItemChanged(selected_position);
            }
        });
    }

    public interface MyClickListerer {
        void onItemClick(int position, String serviceId, View view);
    }

    public void setItemClickListener(AvailableServicesAdapter.MyClickListerer myClickListerer) {
        this.myClickListerer = myClickListerer;
    }

    @Override
    public int getItemCount() {
        return mServiceList.size();
    }

    public void setTeamMembers(List<Service> list) {
        mServiceList = list;
    }

    class RibotHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_serviceName)
        public TextView tv_serviceName;

        @BindView(R.id.iv_serviceImage)
        public ImageView iv_serviceImage;

        public RibotHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


        }
    }

    private void callApi() {
        /*PrefsHelper prefsHelper = new PrefsHelper(mContext);
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
        requestParams.put(ApplicationMetadata.SERVICE_TYPE, mServiceList.get(selected_position).getId()+"");

        if (Globals.getUserLatLng() == null) {
            return;
        }
        requestParams.put(ApplicationMetadata.LATITUDE, Globals.getUserLatLng().latitude +"");
        requestParams.put(ApplicationMetadata.LONGITUDE, Globals.getUserLatLng().longitude +"");
        DataManager dataManager = new DataManager(mContext);
        dataManager.setCallback(new DataManager.RequestCallback() {
            @Override
            public void Data(Object data) {
                homeFragment.updateMapForOnlineMech((ArrayList<Mechanic>)data);
            }
        });
        dataManager.getOnlineMechs(requestParams);*/
    }
}