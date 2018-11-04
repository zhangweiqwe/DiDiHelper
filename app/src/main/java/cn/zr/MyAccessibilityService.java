package cn.zr;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import cn.zr.activity.MainActivity;

public class MyAccessibilityService extends AccessibilityService {
    private static final String LOG_TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(LOG_TAG, "onAccessibilityEvent" + accessibilityEvent.getEventType());

        if (!isTheValidTime) {
            return;
        }

        switch (accessibilityEvent.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;

            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                Log.d(LOG_TAG, "AccessibilityEvent.TYPE_ANNOUNCEMENT " + accessibilityEvent.getText());
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.d(LOG_TAG, accessibilityEvent.getClassName().toString());

                StringBuilder sb = new StringBuilder();
                sb.append(accessibilityEvent.getClassName().toString());
                sb.append("\n");
                final AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();


                if (accessibilityNodeInfo != null) {
                    /*com.sdu.didi.gsui:id/show_order_fragment


                    com.sdu.didi.gsui:id/title_view_image

                    com.sdu.didi.gsui:id/order_card_fragment_key_info_1
                    com.sdu.didi.gsui:id/order_card_fragment_key_info_2

                    com.sdu.didi.gsui:id/order_card_fragment_key_info_2
                    com.sdu.didi.gsui:id/order_card_fragment_key_info_3

                    com.sdu.didi.gsui:id/order_card_fragment_key_info_2
                    com.sdu.didi.gsui:id/order_card_fragment_key_info_3



                    com.sdu.didi.gsui:id/text_order_card_ordinary_area_address_start
                    com.sdu.didi.gsui:id/main_order_map_sliding_drawer



                    com.sdu.didi.gsui:id/grab_order_bg
                    com.sdu.didi.gsui:id/grab_order_btn
                    com.sdu.didi.gsui:id/grab_order_count_down

                     */

                    List<AccessibilityNodeInfo> accessibilityNodeInfoParent = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/show_order_fragment"); //aev afx
                    if (accessibilityNodeInfoParent != null && accessibilityNodeInfoParent.size() == 1) {
                        CharSequence arr[][] = new CharSequence[3][2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfo0 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_2");
                        Log.d(LOG_TAG, "" + accessibilityNodeInfo0 == null ? "null" : accessibilityNodeInfo0.size() + "");
                        if (accessibilityNodeInfo0 != null && accessibilityNodeInfo0.size() == 3) {
                            arr[0][1] = accessibilityNodeInfo0.get(0).getText();
                            arr[1][0] = accessibilityNodeInfo0.get(1).getText();
                            arr[2][0] = accessibilityNodeInfo0.get(2).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo1 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_1");
                        Log.d(LOG_TAG, "" + accessibilityNodeInfo1 == null ? "null" : accessibilityNodeInfo1.size() + "");
                        if (accessibilityNodeInfo1 != null && accessibilityNodeInfo1.size() == 1) {
                            arr[0][0] = accessibilityNodeInfo1.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo2 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_3");
                        Log.d(LOG_TAG, "" + accessibilityNodeInfo2 == null ? "null" : accessibilityNodeInfo2.size() + "");
                        if (accessibilityNodeInfo2 != null && accessibilityNodeInfo2.size() == 2) {
                            arr[1][1] = accessibilityNodeInfo2.get(0).getText();
                            arr[2][1] = accessibilityNodeInfo2.get(1).getText();
                        }
                        StringBuilder sb1 = new StringBuilder();
                        for (int i = 0; i < arr.length; i++) {
                            for (int z = 0; z < arr[i].length; z++) {
                                sb1.append(arr[i][z]);
                                sb1.append("\t");
                            }
                            sb1.append("\n");
                        }
                        Log.d(LOG_TAG, sb1.toString());


                        CharSequence[] arr1 = new CharSequence[2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfo10 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/text_order_card_ordinary_area_address_start");
                        Log.d(LOG_TAG, "" + accessibilityNodeInfo10 == null ? "null" : accessibilityNodeInfo10.size() + " accessibilityNodeInfo10");
                        if (accessibilityNodeInfo10 != null && accessibilityNodeInfo10.size() == 1) {
                            arr1[0] = accessibilityNodeInfo10.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo11 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/text_order_card_ordinary_area_address_end");
                        Log.d(LOG_TAG, "" + accessibilityNodeInfo11 == null ? "null" : accessibilityNodeInfo11.size() + " accessibilityNodeInfo11");
                        if (accessibilityNodeInfo11 != null && accessibilityNodeInfo11.size() == 1) {
                            arr1[1] = accessibilityNodeInfo11.get(0).getText();
                        }
                        StringBuilder sb2 = new StringBuilder();
                        for (int i = 0; i < arr1.length; i++) {
                            sb2.append(arr1[i] + "\t");
                            sb2.append("\n");
                        }
                        Log.d(LOG_TAG, sb2.toString());


                        CharSequence[] arr2 = new CharSequence[2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfo20 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/grab_order_btn");
                        Log.d(LOG_TAG, "" + accessibilityNodeInfo20 == null ? "null" : accessibilityNodeInfo20.size() + " accessibilityNodeInfo20");
                        if (accessibilityNodeInfo20 != null && accessibilityNodeInfo20.size() == 1) {
                            arr2[0] = accessibilityNodeInfo20.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo21 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/grab_order_count_down");
                        Log.d(LOG_TAG, "" + accessibilityNodeInfo21 == null ? "null" : accessibilityNodeInfo21.size() + " accessibilityNodeInfo21");
                        if (accessibilityNodeInfo21 != null && accessibilityNodeInfo21.size() == 1) {
                            arr2[1] = accessibilityNodeInfo21.get(0).getText();
                        }
                        StringBuilder sb3 = new StringBuilder();
                        for (int i = 0; i < arr2.length; i++) {
                            sb3.append(arr2[i] + "\t");
                            sb3.append("\n");
                        }
                        /*
                        10s秒
                        3s后开始接单
                        1s后自动关闭


                         1s后开始接单


                         订单已被抢
                        1s后自动关闭
                         */
                        Log.d(LOG_TAG, sb3.toString());


                        //closeView(accessibilityNodeInfo);


                    }

                }


                SuspensionWindow.showMsg(sb.toString());
                Log.d(LOG_TAG, sb.toString());

                break;

        }
    }


    private boolean grabOrder(AccessibilityNodeInfo accessibilityNodeInfo) {
        List<AccessibilityNodeInfo> accessibilityNodeInfos = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/grab_order_bg");
        if (accessibilityNodeInfos != null && accessibilityNodeInfos.size() == 1) {
            return accessibilityNodeInfos.get(0).performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
        }
        return false;
    }

    private boolean closeView(AccessibilityNodeInfo accessibilityNodeInfo) {
        List<AccessibilityNodeInfo> accessibilityNodeInfos = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/title_view_image");
        if (accessibilityNodeInfos != null && accessibilityNodeInfos.size() == 1) {
            return accessibilityNodeInfos.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return false;
    }

    @Override
    public void onInterrupt() {
        Log.d(LOG_TAG, "onInterrupt()");
    }

    private boolean isTheValidTime = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate()");
        new CheckUtil(this, new CheckUtil.OnCheckTimeResultListener() {
            @Override
            public void onResult(boolean b) {
                isTheValidTime = b;
            }
        }).checkTime();
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
    public void onDestroy() {
        super.onDestroy();
        //stopForeground(true);
    }


}
