package fairrepair.service.fairrepair.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.List;
import java.util.Map;

import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.LoginActivity;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.app.RegisterActivity;
import fairrepair.service.fairrepair.data.local.PrefsHelper;
import fairrepair.service.fairrepair.data.model.Mechanic;
import fairrepair.service.fairrepair.data.model.MechanicDetail;
import fairrepair.service.fairrepair.data.model.SignInResponse;
import fairrepair.service.fairrepair.data.model.UserInfo;
import fairrepair.service.fairrepair.data.remote.FairRepairService;
import fairrepair.service.fairrepair.fragment.MyProfileFragment;
import fairrepair.service.fairrepair.fragment.SupportFragment;
import fairrepair.service.fairrepair.fragment.TermsNConditionDialogFragment;
import fairrepair.service.fairrepair.model.Service;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.DialogFactory;
import fairrepair.service.fairrepair.utils.NetworkUtil;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 11/25/2016.
 */

public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();
    private FairRepairService mApiService;
    private PrefsHelper prefsHelper;
    private Context mContext;
    private RequestCallback mCallback = null;
    private OnOnlineMechCallback onlineMechCallback = null;
    private LocationUpdateCallback mLocationUpdateCallback = null;
    public DataManager(Context context) {
        mContext = context;
        mApiService = FairRepairService.Factory.makeFairRepairService(context);
        prefsHelper = new PrefsHelper(context);
    }

    public interface LocationUpdateCallback{
//        void locationReceived(CustomerLocation location);
        void locationReceived();
    }
    public void setmLocationUpdateCallback(LocationUpdateCallback callback) {
        this.mLocationUpdateCallback = callback;
    }

    public interface RequestCallback{
        void Data(Object data);
    }
    public void setCallback(RequestCallback mCallback){
        this.mCallback = mCallback;
    }

    public interface OnOnlineMechCallback{
        void onlineMechanics(List<Mechanic> onlineMechs);
    }
    public void setOnlineMechCallback(OnOnlineMechCallback callback) {
        onlineMechCallback = callback;
    }
    public void signUp(final Map<String,RequestBody> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.signUp(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();

                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String sessionToken = response.body().getResponseData().getSessionToken();
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.SESSION_TOKEN, sessionToken);
                    prefsHelper.savePref(ApplicationMetadata.USER_ID, userInfo.getId());
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());
                    prefsHelper.savePref(ApplicationMetadata.LOGIN, true);

                    //launch home screen activity
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //login
    public void login(final Map<String, String> loginRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.login(loginRequest);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String sessionToken = response.body().getResponseData().getSessionToken();
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.SESSION_TOKEN, sessionToken);
                    prefsHelper.savePref(ApplicationMetadata.USER_ID, userInfo.getId());
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());
                    prefsHelper.savePref(ApplicationMetadata.LOGIN, true);

                    //launch home screen activity
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error);
            }
        });
    }

    //forgot password
    public void forgotPassword(final Map<String,String> forgotPasswordRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention,R.string.no_connectin).show();
            return;
        }
        final ProgressDialog  progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.forgotPassword(forgotPasswordRequest);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse>call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_password_changed, response.body().getResponseMsg()).show();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext,R.string.title_attention,R.string.msg_server_error);
            }
        });
    }

    //Logout user
    public void logout(final Map<String, String> logoutRequest) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.logout(logoutRequest);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    PrefsHelper prefsHelper = new PrefsHelper(mContext);
                    String deviveToken = prefsHelper.getPref(ApplicationMetadata.DEVICE_TOKEN);
                    prefsHelper.clearAllPref();
                    prefsHelper.savePref(ApplicationMetadata.DEVICE_TOKEN, deviveToken);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error);
            }
        });
    }

    //get static content
    public void getStaticPages(Map<String, String> requestMap, final String type) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.getStaticPages(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    String content = response.body().getResponseData().getStaticContent();

                    if (type.equals(ApplicationMetadata.ABOUT_CUSTOMER)) {
                        //launch about us fragment
                        Fragment newFragment = SupportFragment.newInstance(content);
                        ((MainActivity)mContext).addFragmentToStack(newFragment, "support");
                    } else if (type.equals(ApplicationMetadata.TNC_CUSTOMER)) {
                        //show tnc dialog
                        DialogFragment customerDetailFragment = TermsNConditionDialogFragment.newInstance(content);
                        customerDetailFragment.show(((RegisterActivity)mContext).getSupportFragmentManager(), "terms_n_condition");
                    }
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //get profile of the user
    public void getProfile(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.getProfile(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());

                    Fragment newFragment = MyProfileFragment.newInstance(2);
                    ((MainActivity)mContext).addFragmentToStack(newFragment, "my_profile");
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //edit profile of the user
    public void editProfile(Map<String, RequestBody> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.editProfile(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    UserInfo userInfo = response.body().getResponseData().getUserInfo();
                    prefsHelper.savePref(ApplicationMetadata.USER_NAME, userInfo.getName());
                    prefsHelper.savePref(ApplicationMetadata.USER_EMAIL, userInfo.getEmail());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOBILE, userInfo.getPhoneNo());
                    prefsHelper.savePref(ApplicationMetadata.PASSWORD, userInfo.getPassword());
                    prefsHelper.savePref(ApplicationMetadata.USER_IMAGE, userInfo.getProfilePic());
                    prefsHelper.savePref(ApplicationMetadata.STRIPE_ID, userInfo.getStripeId());
                    prefsHelper.savePref(ApplicationMetadata.USER_LATITUDE, userInfo.getLatitude());
                    prefsHelper.savePref(ApplicationMetadata.USER_LONGITUDE, userInfo.getLongitude());
                    prefsHelper.savePref(ApplicationMetadata.APP_LANGUAGE, userInfo.getLanguage());
                    prefsHelper.savePref(ApplicationMetadata.USER_ADD_DATE, userInfo.getAddDate());
                    prefsHelper.savePref(ApplicationMetadata.USER_MOD_DATE, userInfo.getModDate());

                    Fragment newFragment = MyProfileFragment.newInstance(2);
                    ((MainActivity)mContext).addFragmentToStack(newFragment, "my_profile");
                    ((MainActivity)mContext).loadData();

                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    //reset password
    public void resetPassword(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.resetPassword(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();

                    Intent intent = new Intent(mContext,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // get all services for customer
    public void resetGetAllServices(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.resetGetAllServices(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {

                    List<Service> serviceList = response.body().getResponseData().getServices();
                    mCallback.Data(serviceList);
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // get all services for customer
    public void getOnlineMechs(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        Call<SignInResponse> call = mApiService.getMechByServiceType(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {

                    List<Mechanic> mechanicList = response.body().getResponseData().getOnlineMech();
                    onlineMechCallback.onlineMechanics(mechanicList);

                } else {
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // send request to the mechs
    public void sendRequest(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.getSendRequest(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    mCallback.Data(new Object());
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_success, response.body().getResponseMsg()).show();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Get mechanic detail
    public void getMechanicDetail(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.getMechanicDetail(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    MechanicDetail mechanicDetail = response.body().getResponseData().getMechanicDetail();
                    mCallback.Data(mechanicDetail);
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Add reivew for the mechanic
    public void rateMechanic(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.rateMechanic(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_success, response.body().getResponseMsg()).show();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Accept offer by the client
    public void acceptOffer(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.acceptOffer(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    mCallback.Data(new Object());
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Cancel mechanic request
    public void cancelRequest(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.msg_loading));
        progressDialog.show();
        Call<SignInResponse> call = mApiService.cancelRequest(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    mCallback.Data(new Object());
                    DialogFactory.createSimpleOkSuccessDialog(mContext,R.string.title_success, response.body().getResponseMsg()).show();
                } else {
                    progressDialog.dismiss();
                    DialogFactory.createSimpleOkErrorDialog(mContext, response.body().getResponseMsg()).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }

    // Cancel mechanic request
    public void updateLatLng(Map<String, String> requestMap) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.no_connectin).show();
            return;
        }
        Call<SignInResponse> call = mApiService.updateLatLng(requestMap);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                int status = response.body().getResponseStatus();
                if (status == ApplicationMetadata.SUCCESS_RESPONSE_STATUS) {
                    mLocationUpdateCallback.locationReceived();
                } else {
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                DialogFactory.createSimpleOkErrorDialog(mContext, R.string.title_attention, R.string.msg_server_error).show();
            }
        });
    }
}
