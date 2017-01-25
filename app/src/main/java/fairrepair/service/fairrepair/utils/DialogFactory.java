package fairrepair.service.fairrepair.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import fairrepair.service.fairrepair.R;
import fairrepair.service.fairrepair.app.MainActivity;
import fairrepair.service.fairrepair.data.DataManager;
import fairrepair.service.fairrepair.data.local.PrefsHelper;

public class DialogFactory {

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context, context.getString(messageResource));
    }

    public static Dialog createLogoutDialog(final Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_no, null)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager dataManager = new DataManager(context);
                        PrefsHelper prefsHelper = new PrefsHelper(context);
                        Map<String, String> requestParams = new HashMap<>();
                        requestParams.put(ApplicationMetadata.SESSION_TOKEN, (String) prefsHelper.getPref(ApplicationMetadata.SESSION_TOKEN));
                        requestParams.put("language", (String) prefsHelper.getPref(ApplicationMetadata.APP_LANGUAGE));
                        dataManager.logout(requestParams);
                    }
                });
        return alertDialog.create();
    }

    public static Dialog createLogoutDialog(Context context,
                                            @StringRes int titleResource,
                                            @StringRes int messageResource) {

        return createLogoutDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static void createExitDialog(final Context context) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("Do you want to exit?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) context).finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static Dialog createSimpleOkSuccessDialog(Context context, @StringRes int title, String message) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.request_sent_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView tv_message = (TextView) dialogView.findViewById(R.id.tv_message);
        tv_message.setText(message);
        final AlertDialog alertDialog = dialogBuilder.create();
        Button button = (Button) dialogView.findViewById(R.id.btn_dialog_ok);
        button.setText(context.getString(R.string.dialog_action_ok));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        return alertDialog;
    }

    public static void createComingSoonDialog(final Context context) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        // Setting Dialog Title
        alertDialog.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("Coming Soon.");

        alertDialog.setPositiveButton("OK", null);


        alertDialog.show();
    }

    public static void createRequsestSentDialog(Context context, String message) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.request_sent_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        Button button = (Button) dialogView.findViewById(R.id.btn_dialog_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public static void createOffersFoundDialog(Context context, String message) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.offers_found_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        Button button = (Button) dialogView.findViewById(R.id.btn_dialog_view_details);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public static void createOffeAcceptedDialog(final Context context, String message) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.request_sent_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView tv_message = (TextView) dialogView.findViewById(R.id.tv_message);
        tv_message.setText(message);
        final AlertDialog alertDialog = dialogBuilder.create();
        Button button = (Button) dialogView.findViewById(R.id.btn_dialog_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        alertDialog.show();
    }

    public static void createAlertDialog(Context context, String message) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCanceledOnTouchOutside(false);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.notification_dialog, null);
        TextView tv_notificationMsg = (TextView) view.findViewById(R.id.tv_notificationMsg);
        tv_notificationMsg.setText(message);
        Button button = (Button) view.findViewById(R.id.btn_dialog_ok);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(view);

        alertDialog.show();
    }
}
