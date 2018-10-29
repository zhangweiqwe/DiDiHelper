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

            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                Log.d(TAG,"AccessibilityEvent.TYPE_ANNOUNCEMENT "+accessibilityEvent.getText());
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.d(TAG, accessibilityEvent.getClassName().toString());

                StringBuilder sb = new StringBuilder();
                sb.append(accessibilityEvent.getClassName().toString());
                sb.append("\n");
                Object z = null;
                AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();


                if (accessibilityNodeInfo != null) {
                    //com.sdu.didi.gsui:id/order_card_fragment_key_info_1 明天 1
                    //com.sdu.didi.gsui:id/order_card_fragment_key_info_3 公里  2
                    //com.sdu.didi.gsui:id/order_card_fragment_key_info_3 公里 3
                    List<AccessibilityNodeInfo> accessibilityNodeInfoParent = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/show_order_fragment"); //aev afx
                    if (accessibilityNodeInfoParent != null && accessibilityNodeInfoParent.size() == 1) {

                        CharSequence arr[][] = new CharSequence[3][2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfo0 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_2");
                        if (accessibilityNodeInfo0 != null && accessibilityNodeInfo0.size() == 3) {
                            for (int i = 0; i < 3; i++) {
                                arr[0][i] = accessibilityNodeInfo0.get(i).getText();
                            }
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo1 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_1");
                        if (accessibilityNodeInfo1 != null && accessibilityNodeInfo1.size() == 1) {
                            arr[0][0] = accessibilityNodeInfo1.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo2 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_3");
                        if (accessibilityNodeInfo2 != null && accessibilityNodeInfo2.size() == 2) {
                            arr[0][1] = accessibilityNodeInfo2.get(1).getText();
                            arr[0][2] = accessibilityNodeInfo2.get(2).getText();
                        }

                        CharSequence arr0[] = new CharSequence[2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfoAreaAddressStart = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/text_order_card_ordinary_area_address_start");
                        if (accessibilityNodeInfoAreaAddressStart != null && accessibilityNodeInfoAreaAddressStart.size() == 1) {
                            arr0[0] = accessibilityNodeInfoAreaAddressStart.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfoAreaAddressEnd = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/text_order_card_ordinary_area_address_end");
                        if (accessibilityNodeInfoAreaAddressEnd != null && accessibilityNodeInfoAreaAddressEnd.size() == 1) {
                            arr0[1] = accessibilityNodeInfoAreaAddressEnd.get(0).getText();
                        }


                        //抢单按钮状态客抢
                        CharSequence bottomBnArr[] = new CharSequence[2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfobottomBn0 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/grab_order_btn");
                        if (accessibilityNodeInfobottomBn0 != null && accessibilityNodeInfobottomBn0.size() == 1) {
                            bottomBnArr[0] = accessibilityNodeInfobottomBn0.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfobottomBn1 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/grab_order_count_down");
                        if (accessibilityNodeInfobottomBn1 != null && accessibilityNodeInfobottomBn1.size() == 1) {
                            bottomBnArr[1] = accessibilityNodeInfobottomBn1.get(0).getText();
                        }

                        sb.append(arr[0][0] + "\t" + arr[0][1]);
                        sb.append(arr[1][0] + "\t" + arr[0][1]);
                        sb.append(arr[2][0] + "\t" + arr[0][1]);
                        sb.append("\n");
                        sb.append(arr0[0]);
                        sb.append(arr0[1]);
                        sb.append(bottomBnArr[0]);
                        sb.append(bottomBnArr[1]);
                    }

                    /*List<AccessibilityNodeInfo> accessibilityNodeInfoCrabBn = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/broad_order_show_order_grab_btn");
                    if (accessibilityNodeInfoCrabBn != null && accessibilityNodeInfoCrabBn.size() == 1) {
                        accessibilityNodeInfoCrabBn.get(0).performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                    }

                    List<AccessibilityNodeInfo> accessibilityNodeInfoExitBn = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/title_view_image");
                    if (accessibilityNodeInfoExitBn != null && accessibilityNodeInfoCrabBn.size() == 1) {
                        accessibilityNodeInfoExitBn.get(0).performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                    }*/
                }


                SuspensionWindow.showMsg(sb.toString());

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
            /*NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(getString(R.string.description));
            mNotificationManager.createNotificationChannel(notificationChannel);*/


            Notification.Builder builder = new Notification.Builder(this, "1");
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
