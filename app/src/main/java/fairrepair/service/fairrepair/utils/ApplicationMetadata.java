package fairrepair.service.fairrepair.utils;

/**
 * Created by admin on 11/22/2016.
 */

public interface ApplicationMetadata {
    float MAP_ZOOM_VALUE = 15.0f;

    String APP_LANGUAGE = "app_language";
    String DEVICE_TOKEN = "device_token";
    int SUCCESS_RESPONSE_STATUS = 1;
    String USER_ID = "id";
    String USER_NAME = "name";
    String USER_EMAIL = "email";
    String USER_MOBILE = "mobile";
    String USER_IMAGE = "profile_pic";
    String LOGIN = "login";
    String LATITUDE = "latitude";

    String LONGITUDE = "longitude";
    String ADDRESS = "address";
    String PASSWORD = "password";
    String PERSONAL_DESC = "personal_desc";
    String SERVICE_TYPE = "service_type";
    String STRIPE_ID = "stripe_id";
    String SESSION_TOKEN = "session_token";
    String STRIPE_TOKEN = "stripe_token";
    String USER_LATITUDE = "user_latitude";
    String USER_LONGITUDE = "user_longitude";
    String USER_ADD_DATE = "user_add_date";
    String USER_MOD_DATE = "user_mod_date";
    String IMAGE_BASE_URL = "http://fairrepair.onsisdev.info/public/media/customer/";
    String MECHANIC_IMAGE_BASE_URL = "http://fairrepair.onsisdev.info/public/media/mechanic/";
    String SERVICE_IMAGE_BASE_URL = "http://fairrepair.onsisdev.info/public/media/servicetype/";
    String LANG_ENGLISH = "en";
    String LANGUAGE = "language" ;
    String PAGE_IDENTIFIER = "page_identifier";
    String ABOUT_CUSTOMER = "aboutusmechanic";
    String TNC_CUSTOMER = "termofservicesmechanic";
    String PRIVACY_POLICY_CUSTOMER = "privecypolicymechanic";
    String SERVICE_TYPE_ID = "service_type_id";
    String MODEL = "model";

    String ENGINE_MANUFACTURER = "engine_manufacturer";
    String NEED = "need";
    String TRAILER = "trailer";
    String SERVICE_PROVIDER_ID = "service_provide";

    String VIN = "vin";
    int PIC_CROP_REQUEST_ID = 342;
    String LOCATION = "location";
    String SERVICE_TIME = "service_time";
    String NOTIFICATION_DATA = "notification_data";
    int SHOW_ALL_MECH = 100;
    int SHOW_MECH_REQUEST = 110;
    String APP_PROVIDER_ID = "app_provider_id";
    String REQUEST_ID = "request_id";
    String OFFER_PRICE = "offer_price";
    String REASON_FOR_CANCEL = "reason_for_cancel";
    int NOTIFICATION_REQ_ACCEPTED = 2;

    int NOTIFICATION_REQ_COMPLETED = 5;
    String NOTIFICATION_TYPE = "notification_type";
    String CUSTOMER_ID = "customer_id";
    int NOTIFICATION_MECH_ARRIVED = 7;
    int NOTIFICATION_REQ_FINISHED = 5;
    String BILLING_PRICE = "billing_price";
    String SERVICE_PERCENTAGE = "service_percentage";
    String SERVICE_CHARGE = "service_charge";
    String SERVICE_DETAIL = "service_detail";
    String PAYMENT_STATUS = "payment_status";
    String TXN_NO = "txn_no";
    String REVIEW = "review";
    String RATE = "rate";
    String FROM_DATE = "from_date";
    String TO_DATE = "to_date";
}
