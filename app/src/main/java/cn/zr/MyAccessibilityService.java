package cn.zr;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent" + accessibilityEvent.getEventType());

        switch (accessibilityEvent.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();

                if (accessibilityNodeInfo != null) {
                    List<AccessibilityNodeInfo> accessibilityNodeIfs = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ki"); //aev afx

                    int i = 0;
                    for (AccessibilityNodeInfo a : accessibilityNodeIfs) {
                        i++;
                        CharSequence charSequence = a.getText();
                        if (charSequence != null) {
                            Log.d(TAG, charSequence + "<--" + i);
                        } else {
                            a.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                        }
                        //Log.d(TAG, "a" + a.performAction(AccessibilityNodeInfo.ACTION_CLICK));
                    }


                }

                break;

        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "1";
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(getString(R.string.description));
            mNotificationManager.createNotificationChannel(notificationChannel);


            Notification.Builder builder = new Notification.Builder(this, channelId);
            builder.setContentIntent(PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setTicker(getString(R.string.app_name));
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(getString(R.string.description));
            Notification notification = builder.build();
            startForeground(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }


}
