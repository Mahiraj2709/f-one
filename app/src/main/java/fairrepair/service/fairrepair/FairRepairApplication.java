package fairrepair.service.fairrepair;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by admin on 11/21/2016.
 */

public class FairRepairApplication extends Application {

    public static Bus bus = null;
    public static boolean isVisible = false;

    public static Bus getBus() {
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.ANY);
        }
        return bus;
    }
}
