package fairrepair.service.fairrepair.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
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

/**
 * Created by admin on 12/27/2016.
 */

public class YourBillFragment extends Fragment {
    @BindView(R.id.tv_totalPrice) TextView tv_totalPrice;
    @BindView(R.id.tv_requestId) TextView tv_requestId;
    @BindView(R.id.tv_serviceName) TextView tv_serviceName;
    @BindView(R.id.tv_servicePrice) TextView tv_servicePrice;
    @BindView(R.id.tv_serviceCharge) TextView tv_serviceCharge;
    @BindView(R.id.tv_tv_serviceChargePrice) TextView tv_tv_serviceChargePrice;
    public static final String PAYPAL_CLIENT_ID = "AStwH7Uui0zunfVSPXE-6JFCKQNyQ5Nxc5MSE7isi96G6VqW6Ajtbc9DjYbW4ExYHC1b-D2Ikl7bh6vK";

    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;

    private PrefsHelper prefsHelper = null;
    private DataManager dataManager = null;

    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    //Payment Amount
    private String paymentAmount;

    public static YourBillFragment newInstance(Bundle args) {
        YourBillFragment fragment = new YourBillFragment();
        Bundle data = new Bundle();
        data.putBundle("content", args);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((TextView) ((MainActivity) getActivity()).findViewById(R.id.tv_toolbarHeader)).setText(getString(R.string.title_your_bill));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_bill_fragment, container, false);
        ButterKnife.bind(this, view);

        Intent intent = new Intent(getContext(), PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        getActivity().startService(intent);
        prefsHelper = new PrefsHelper(getContext());
        dataManager = new DataManager(getContext());
        setView();
        return view;
    }

    private void setView() {
        tv_totalPrice.setText("$"+getArguments().getBundle("content").getString(ApplicationMetadata.BILLING_PRICE));
        tv_requestId.setText("Request ID-"+getArguments().getBundle("content").getString(ApplicationMetadata.REQUEST_ID));
        tv_serviceName.setText(getArguments().getBundle("content").getString(ApplicationMetadata.SERVICE_DETAIL));
        tv_servicePrice.setText("$"+getArguments().getBundle("content").getString(ApplicationMetadata.SERVICE_CHARGE));
        tv_serviceCharge.setText("Service Charge "+getArguments().getBundle("content").getString(ApplicationMetadata.SERVICE_PERCENTAGE)+"%");
        tv_tv_serviceChargePrice.setText("$"+getArguments().getBundle("content").getString(ApplicationMetadata.SERVICE_CHARGE));
    }

    @OnClick(R.id.btn_PayNow)
    public void payNow() {
        getPayment();
    }
    private void getPayment() {
        //Getting the amount from editText
        paymentAmount = getArguments().getBundle("content").getString(ApplicationMetadata.BILLING_PRICE);

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "Fair Repair",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(getActivity(), PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);

                        fairrepair.service.fairrepair.model.paypal.PayPalPayment paymentResponse = new Gson().fromJson(paymentDetails,fairrepair.service.fairrepair.model.paypal.PayPalPayment.class);

                        //make payment
                        Map<String, String> requestParams = new HashMap<>();
                        requestParams.put(ApplicationMetadata.SESSION_TOKEN, prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN,""));
                        requestParams.put(ApplicationMetadata.LANGUAGE, prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE, ""));
                        requestParams.put(ApplicationMetadata.APP_PROVIDER_ID, getArguments().getBundle("content").getString(ApplicationMetadata.APP_PROVIDER_ID)); // MECH ID
                        requestParams.put(ApplicationMetadata.REQUEST_ID, getArguments().getBundle("content").getString(ApplicationMetadata.REQUEST_ID));
                        requestParams.put(ApplicationMetadata.PAYMENT_STATUS, paymentResponse.getResponse().getState());
                        requestParams.put(ApplicationMetadata.TXN_NO, paymentResponse.getResponse().getId());
                        dataManager.setCallback(new DataManager.RequestCallback() {
                            @Override
                            public void Data(Object data) {
                                Fragment fragment = RateYourMechFragment.newInstance(getArguments().getBundle("content"));
                                ((MainActivity)getActivity()).addFragmentToStack(fragment,"home_fragment");
                            }
                        });

                        dataManager.makePayment(requestParams);

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    public class PayPalConfig {

        public static final String PAYPAL_CLIENT_ID = "AStwH7Uui0zunfVSPXE-6JFCKQNyQ5Nxc5MSE7isi96G6VqW6Ajtbc9DjYbW4ExYHC1b-D2Ikl7bh6vK";

    }
}
