package fairrepair.service.fairrepair.data.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fairrepair.service.fairrepair.data.model.SignInResponse;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by admin on 11/21/2016.
 */

public interface FairRepairService {
    String ENDPOINT = "http://fairrepair.onsisdev.info/customerapi/";

    @POST("signup")
    @Multipart
    Call<SignInResponse> signUp(@PartMap Map<String, RequestBody> requestMap);

    @POST("login")
    @FormUrlEncoded
    Call<SignInResponse> login(@FieldMap Map<String, String> params);

    @POST("forgotpassword")
    @FormUrlEncoded
    Call<SignInResponse> forgotPassword(@FieldMap Map<String, String> params);

    @POST("logout")
    @FormUrlEncoded
    Call<SignInResponse> logout(@FieldMap Map<String, String> params);

    @POST("staticpages")
    @FormUrlEncoded
    Call<SignInResponse> getStaticPages(@FieldMap Map<String, String> params);

    @POST("getprofile")
    @FormUrlEncoded
    Call<SignInResponse> getProfile(@FieldMap Map<String, String> params);

    @POST("editprofile")
    @Multipart
    Call<SignInResponse> editProfile(@PartMap Map<String, RequestBody> params);

    @POST("changepassword")
    @FormUrlEncoded
    Call<SignInResponse> resetPassword(@FieldMap Map<String, String> params);

    @POST("getservicetype")
    @FormUrlEncoded
    Call<SignInResponse> resetGetAllServices(@FieldMap Map<String, String> params);

    @POST("getallonlinemechanic")
    @FormUrlEncoded
    Call<SignInResponse> getMechForService(@FieldMap Map<String, String> params);

    @POST("getonlinemechanicbyservicetype")
    @FormUrlEncoded
    Call<SignInResponse> getMechByServiceType(@FieldMap Map<String, String> params);

    @POST("sendrequest")
    @FormUrlEncoded
    Call<SignInResponse> getSendRequest(@FieldMap Map<String, String> params);

    @POST("getproviderdetails")
    @FormUrlEncoded
    Call<SignInResponse> getMechanicDetail(@FieldMap Map<String, String> requestMap);

    @POST("acceptoffer")
    @FormUrlEncoded
    Call<SignInResponse> acceptOffer(@FieldMap Map<String, String> requestMap);

    @POST("cancelrequest")
    @FormUrlEncoded
    Call<SignInResponse> cancelRequest(@FieldMap Map<String, String> requestMap);

    @POST("addreview")
    @FormUrlEncoded
    Call<SignInResponse> rateMechanic(@FieldMap Map<String, String> requestMap);

    @POST("updatelatlong")
    @FormUrlEncoded
    Call<SignInResponse> updateLatLng(@FieldMap Map<String, String> requestMap);

    /********
     * Factory class that sets up a new ribot services
     *******/
    class Factory {

        public static FairRepairService makeFairRepairService(Context context) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(new UnauthorisedInterceptor(context))
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FairRepairService.ENDPOINT)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit.create(FairRepairService.class);
        }

    }
}
