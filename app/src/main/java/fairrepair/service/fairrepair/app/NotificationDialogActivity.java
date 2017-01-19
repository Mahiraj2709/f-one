package fairrepair.service.fairrepair.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.model.AllMechanic;
import fairrepair.service.fairrepair.utils.ApplicationMetadata;

/**
 * Created by admin on 12/29/2016.
 */

public class NotificationDialogActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        View view = getLayoutInflater().inflate(R.layout.notification_dialog, null);
        TextView tv_notificationMsg = (TextView) view.findViewById(R.id.tv_notificationMsg);
        Button button = (Button) view.findViewById(R.id.btn_dialog_ok);
        AllMechanic allMech = null;
        String notificationData = getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA);
        final int notificationType = getIntent().getIntExtra(ApplicationMetadata.NOTIFICATION_TYPE, -1);
        if (notificationData != null && notificationType == ApplicationMetadata.NOTIFICATION_REQ_ACCEPTED) {
            allMech = new Gson().fromJson(notificationData, AllMechanic.class);
        }
        if (allMech != null)
            tv_notificationMsg.setText("We have found " + allMech.mechanicList.size() + " offers for your request.");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(NotificationDialogActivity.this, MainActivity.class);
                intent.putExtra(ApplicationMetadata.NOTIFICATION_DATA, getIntent().getStringExtra(ApplicationMetadata.NOTIFICATION_DATA));
                intent.putExtra(ApplicationMetadata.NOTIFICATION_TYPE, notificationType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                NotificationDialogActivity.this.finish();
            }
        });
        alertDialog.setView(view);

        alertDialog.show();
    }
}
