package fairrepair.service.fairrepair.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.DialogFactory;

/**
 * Created by admin on 11/22/2016.
 */

public class ResetPasswordFragment extends Fragment {
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_confirmPasswd) EditText et_confirmPasswd;
    public static ResetPasswordFragment newInstance(int args) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle data = new Bundle();
        data.putInt("args",args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((TextView)((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_reset_password));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_password_fragment,container,false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_reset)
    void resetPassword() {
        if (validFields()) {
            PrefsHelper prefsHelper = new PrefsHelper(getContext());
            Map<String,String> requestParams = new HashMap<>();
            requestParams.put(ApplicationMetadata.PASSWORD,et_password.getText().toString());
            requestParams.put(ApplicationMetadata.SESSION_TOKEN,prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN, ""));
            requestParams.put(ApplicationMetadata.LANGUAGE,prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));

            DataManager dataManager = new DataManager(getContext());
            dataManager.resetPassword(requestParams);
        }
    }

    private boolean validFields() {
        if (et_password.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(), R.string.title_attention,R.string.valid_msg_empty_password).show();
            return false;
        } else if (et_password.getText().toString().length() < 6) {
            DialogFactory.createSimpleOkErrorDialog(getContext(),R.string.title_attention,R.string.msg_password_lenght).show();
            return false;
        } else if (et_confirmPasswd.getText().toString().isEmpty()) {
            DialogFactory.createSimpleOkErrorDialog(getContext(),R.string.title_attention,R.string.msg_empty_confirm_password).show();
            return false;
        }  else if (!et_confirmPasswd.getText().toString().equals(et_password.getText().toString())) {
            DialogFactory.createSimpleOkErrorDialog(getContext(),R.string.title_attention,R.string.password_not_match).show();
            return false;
        }
        return true;
    }
}
