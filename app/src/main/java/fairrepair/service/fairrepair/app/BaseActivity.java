package fairrepair.service.fairrepair.app;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import fairrepair.service.fairrepair.FairRepairApplication;

/**
 * Created by admin on 11/22/2016.
 */

public class BaseActivity extends AppCompatActivity {
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FairRepairApplication.isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FairRepairApplication.isVisible = true;
    }
}
