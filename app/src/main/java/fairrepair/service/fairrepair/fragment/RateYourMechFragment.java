package fairrepair.service.fairrepair.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.fragment.home_fragment.HomeFragment;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.DialogFactory;

/**
 * Created by admin on 12/27/2016.
 */

public class RateYourMechFragment extends Fragment {
    private static final String TAG  = RateYourMechFragment.class.getSimpleName();
    @BindView(R.id.et_commentMechanic) EditText et_commentMechanic;
    @BindView(R.id.rating_mech) RatingBar rating_mech;
    @BindView(R.id.image_profile) CircleImageView image_profile;

    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;
    public static RateYourMechFragment newInstance(Bundle args) {
        RateYourMechFragment fragment = new RateYourMechFragment();
        Bundle data = new Bundle();
        data.putBundle("content",args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_rate_your_mech));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rate_mech_fragment,container,false);
        ButterKnife.bind(this,view);

        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());

        Glide.with(this)
                .load(ApplicationMetadata.MECHANIC_IMAGE_BASE_URL + getArguments().getBundle("content").getString(ApplicationMetadata.USER_IMAGE))
                .thumbnail(0.2f)
                .centerCrop()
                .error(R.drawable.ic_profile_photo)
                .into(image_profile);

        return view;
    }

    @OnClick(R.id.btn_submit)
    public void submitRating() {
        if (validRating()) {
//make payment
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
            requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
            requestParams.put(ApplicationMetadata.APP_PROVIDER_ID, getArguments().getBundle("content").getString(ApplicationMetadata.APP_PROVIDER_ID)); // MECH ID
            requestParams.put(ApplicationMetadata.REVIEW, et_commentMechanic.getText().toString());
            requestParams.put(ApplicationMetadata.RATE, rating_mech.getRating()+"");
            dataManager.setCallback(new DataManager.RequestCallback() {
                @Override
                public void Data(Object data) {
                    Fragment fragment = HomeFragment.newInstance(0);
                    ((MainActivity)getActivity()).addFragmentToStack(fragment,"home_fragment");
                }
            });

            dataManager.addReview(requestParams);
        }
    }

    private boolean validRating() {
        if (et_commentMechanic.getText().toString().isEmpty()) {
            DialogFactory.createAlertDialog(getContext(),"Please provide feedback!");
            return false;
        }else if (rating_mech.getRating() <= 0.0f) {
            DialogFactory.createAlertDialog(getContext(),"Please rate your mechanic!");
            return false;
        }
        return true;
    }
}
