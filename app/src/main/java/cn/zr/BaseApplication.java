package cn.zr;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    private static final List<Activity> list = new ArrayList();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        ConfigManager.Companion.init(this);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "1";
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(getString(R.string.description));
            mNotificationManager.createNotificationChannel(notificationChannel);
        }*/
    }
    public final void add(Activity activity){
        list.add(activity);
    }
    public final void remove(Activity activity){
        list.remove(activity);
    }

    public final void exit() {
        for (Activity activity : list) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }
}
