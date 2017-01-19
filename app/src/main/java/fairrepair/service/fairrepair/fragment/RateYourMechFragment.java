package fairrepair.service.fairrepair.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.MainActivity;

/**
 * Created by admin on 12/27/2016.
 */

public class RateYourMechFragment extends Fragment {
    private static final String TAG  = RateYourMechFragment.class.getSimpleName();

    @BindView(R.id.tv_supportText)
    TextView tv_supportText;
    public static SupportFragment newInstance(String args) {
        SupportFragment fragment = new SupportFragment();
        Bundle data = new Bundle();
        data.putString("content",args);
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
        String content = getArguments().getString("content");
        tv_supportText.setText(Html.fromHtml(content));
        return view;
    }
}