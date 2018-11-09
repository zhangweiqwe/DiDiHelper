package cn.zr;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import cn.zr.helper.DidiAccessibilityServiceHelper;
import cn.zr.util.LLog;

public class MyAccessibilityService extends AccessibilityService {
    private static final String LOG_TAG = "MyAccessibilityService";

    private DidiAccessibilityServiceHelper didiAccessibilityServiceHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        didiAccessibilityServiceHelper = new DidiAccessibilityServiceHelper(this);
        LLog.Companion.d(LOG_TAG, "onCreate()");
        didiAccessibilityServiceHelper.onCreate();
        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         *//*NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(getString(R.string.description));
            mNotificationManager.createNotificationChannel(notificationChannel);*//*


            Notification.Builder builder = new Notification.Builder(this, "1");
            builder.setContentIntent(PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setTicker(getString(R.string.app_name));
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(getString(R.string.description));
            Notification notification = builder.build();
            startForeground(1, notification);
        }*/
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(LOG_TAG, "onAccessibilityEvent(AccessibilityEvent accessibilityEvent)" );
        didiAccessibilityServiceHelper.onAccessibilityEvent(accessibilityEvent);


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        LLog.Companion.d(LOG_TAG, "onDestroy()");
        didiAccessibilityServiceHelper.onDestroy();
        //stopForeground(true);
    }

    @Override
    public void onInterrupt() {
        LLog.Companion.d(LOG_TAG, "onInterrupt()");
    }







}
