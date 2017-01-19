package fairrepair.service.fairrepair.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import fairrepair.service.fairrepair.FairRepairApplication;
import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.app.NotificationDialogActivity;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;
import fairrepair.service.fairrepair.utils.NotificationUtils;

/**
 * Created by admin on 12/20/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().containsKey("type")) {
            String payLoad = "";
            int notificationType = Integer.parseInt(remoteMessage.getData().get("type"));
            payLoad = new NotificationUtils().getData(remoteMessage.getData());
            Intent intent = null;

            if (FairRepairApplication.isVisible) {
                intent = new Intent(getApplicationContext(), NotificationDialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            switch (notificationType) {
                /*case ApplicationMetadata.NOTIFICATION_NEW_OFFER:
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    break;*/
                case ApplicationMetadata.NOTIFICATION_REQ_ACCEPTED:
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    break;
                case ApplicationMetadata.NOTIFICATION_REQ_COMPLETED:
                    break;
                /*case ApplicationMetadata.NOTIFICATION_SEND_REQ:
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, payLoad);
                    intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                    break;*/
                default:

            }

            //start activity
            if (FairRepairApplication.isVisible) {
                startActivity(intent);
            } else {
                // use System.currentTimeMillis() to have a unique ID for the pending intent
                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

                // build notification
                // the addAction re-use the same intent to keep the example short
                Notification n = new Notification.Builder(this)
                        .setContentTitle("FairRepair")
                        .setContentText("You have one new request")
                        .setSmallIcon(R.drawable.ic_login_logo)
                        //.setColor(getColor(R.color.colorPrimary))
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);
            }
        } else {
            //notification without type
        }
    }
}
