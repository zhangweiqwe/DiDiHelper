package cn.zr.helper;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.zr.AccessibilityServiceDiDiBean;
import cn.zr.SuspensionWindow;
import cn.zr.didi.DidiConfigManager;
import cn.zr.didi.DidiOrderState;
import cn.zr.util.CheckUtil;
import cn.zr.util.LLog;
import cn.zr.util.Util;
import cn.zr.util.shell.CommandResult;
import cn.zr.util.shell.Shell;
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
public class DidiAccessibilityServiceHelper {
    private AccessibilityService accessibilityService;
    private ExecutorService executorService = Executors.newCachedThreadPool();


    public DidiAccessibilityServiceHelper( AccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    private static final String LOG_TAG = "DidiAccessibilityServ";

    private DidiConfigManager didiConfigManager = DidiConfigManager.Companion.getInstance();

    private boolean isTheValidTime = false;

    public void onCreate() {
        new CheckUtil(accessibilityService, new CheckUtil.OnCheckTimeResultListener() {
            @Override
            public void onResult(boolean b) {
                isTheValidTime = b;
                if(isTheValidTime){
                    SuspensionWindow.showMsg(grabOrderCount + ":" + closeOrderCount);
                }
            }
        }).checkTime();
    }
    private int grabOrderCount = 0;
    private int closeOrderCount = 0;

    public final void onAccessibilityEvent(AccessibilityEvent accessibilityEvent){

        Log.d(LOG_TAG,""+accessibilityEvent.getEventType());
        if (!isTheValidTime) {
            return;
        }

        switch (accessibilityEvent.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                StringBuilder sb = new StringBuilder();
                sb.append(accessibilityEvent.getClassName().toString());
                sb.append("\n");
                AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
                if (accessibilityNodeInfo != null) {

                    Log.d(LOG_TAG, "accessibilityService.getRootInActiveWindow().hashCode():"+accessibilityNodeInfo.hashCode());

                    List<AccessibilityNodeInfo> accessibilityNodeInfoParent = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/show_order_fragment"); //aev afx
                    if (accessibilityNodeInfoParent != null && accessibilityNodeInfoParent.size() == 1) {
                        CharSequence arr[][] = new CharSequence[3][2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfo0 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_2");
                        //Log.d(LOG_TAG, "" + accessibilityNodeInfo0 == null ? "null" : accessibilityNodeInfo0.size() + "");
                        if (accessibilityNodeInfo0 != null && accessibilityNodeInfo0.size() == 3) {
                            arr[0][1] = accessibilityNodeInfo0.get(0).getText();
                            arr[1][0] = accessibilityNodeInfo0.get(1).getText();
                            arr[2][0] = accessibilityNodeInfo0.get(2).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo1 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_1");
                        //Log.d(LOG_TAG, "" + accessibilityNodeInfo1 == null ? "null" : accessibilityNodeInfo1.size() + "");
                        if (accessibilityNodeInfo1 != null && accessibilityNodeInfo1.size() == 1) {
                            arr[0][0] = accessibilityNodeInfo1.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo2 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/order_card_fragment_key_info_3");
                        //Log.d(LOG_TAG, "" + accessibilityNodeInfo2 == null ? "null" : accessibilityNodeInfo2.size() + "");
                        if (accessibilityNodeInfo2 != null && accessibilityNodeInfo2.size() == 2) {
                            arr[1][1] = accessibilityNodeInfo2.get(0).getText();
                            arr[2][1] = accessibilityNodeInfo2.get(1).getText();
                        }
                        for (int i = 0; i < arr.length; i++) {
                            for (int z = 0; z < arr[i].length; z++) {
                                sb.append(arr[i][z]);
                                sb.append("\t");
                            }
                            sb.append("\n");
                        }


                        CharSequence[] arr1 = new CharSequence[2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfo10 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/text_order_card_ordinary_area_address_start");
                        //Log.d(LOG_TAG, "" + accessibilityNodeInfo10 == null ? "null" : accessibilityNodeInfo10.size() + " accessibilityNodeInfo10");
                        if (accessibilityNodeInfo10 != null && accessibilityNodeInfo10.size() == 1) {
                            arr1[0] = accessibilityNodeInfo10.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo11 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/text_order_card_ordinary_area_address_end");
                        if (accessibilityNodeInfo11 != null && accessibilityNodeInfo11.size() == 1) {
                            arr1[1] = accessibilityNodeInfo11.get(0).getText();
                        }
                        for (int i = 0; i < arr1.length; i++) {
                            sb.append(arr1[i] + "\t");
                            sb.append("\n");
                        }


                        CharSequence[] arr2 = new CharSequence[2];
                        List<AccessibilityNodeInfo> accessibilityNodeInfo20 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/grab_order_btn");
                        if (accessibilityNodeInfo20 != null && accessibilityNodeInfo20.size() == 1) {
                            arr2[0] = accessibilityNodeInfo20.get(0).getText();
                        }
                        List<AccessibilityNodeInfo> accessibilityNodeInfo21 = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.sdu.didi.gsui:id/grab_order_count_down");
                        if (accessibilityNodeInfo21 != null && accessibilityNodeInfo21.size() == 1) {
                            arr2[1] = accessibilityNodeInfo21.get(0).getText();
                        }
                        for (int i = 0; i < arr2.length; i++) {
                            sb.append(arr2[i] + "\t");
                            sb.append("\n");
                        }


                        DidiOrderState didiOrderState = checkState(arr2);
                        sb.append("didiOrderState:" + didiOrderState.name() + "\n");

                        AccessibilityServiceDiDiBean accessibilityServiceDiDiBean = new AccessibilityServiceDiDiBean(pareDate(arr[0]), pareDistance(arr[1]), pareDistance(arr[2]), arr1[0], arr1[1]);

                        sb.append(accessibilityServiceDiDiBean.toString()+"\n");
                        switch (didiOrderState) {
                            case OK:
                                if (didiConfigManager.check(accessibilityServiceDiDiBean)) {
                                    sb.append("try grabOrder" + "\n");
                                    grabOrder(accessibilityNodeInfo);
                                } else {
                                    sb.append("try closeView" + "\n");
                                    closeView(accessibilityNodeInfo);
                                }
                                break;
                            case CONTINUE:
                                if (!didiConfigManager.check(accessibilityServiceDiDiBean)) {
                                    sb.append("try closeView" + "\n");
                                    closeView(accessibilityNodeInfo);
                                }
                                break;
                            case INVALID:
                                sb.append("try closeView" + "\n");
                                closeView(accessibilityNodeInfo);
                                break;
                        }


                    }

                }

                sb.append(didiConfigManager.toString() + "\n");
                LLog.Companion.d(LOG_TAG, sb.toString());
                break;
        }

        SuspensionWindow.showMsg(grabOrderCount + ":" + closeOrderCount);
    }


    private final Float pareDistance(CharSequence[] arr) {
        if (arr == null || arr.length != 2) {
            return null;
        }

        Float f = null;
        try {
            f = Float.parseFloat(arr[0].toString());
        } catch (Exception e) {
            e.printStackTrace();
            LLog.Companion.d(LOG_TAG, "pareDistance error" + e.getMessage());
            return null;
        }
        String s1 = arr[1].toString();

        if (s1.contains("公里")) {
            return f;
        } else if (s1.contains("米")) {
            return f / 1000;
        }

        return null;
    }

    private final Date pareDate(CharSequence[] arr) {
        if (arr == null || arr.length != 2) {
            return null;
        }
        String s0 = null;
        String s1 = null;

        if (arr[0] == null) {
            if (arr[1] != null) {
                s1 = arr[1].toString();
                if (s1.contains("分钟后")) {
                    Date date = Util.getStartTime();
                    Integer i = Util.getStartNumber(s1);
                    if (i != null) {
                        date.setTime(date.getTime() + i * 60 * 1000);
                        return date;
                    }
                }
            }
        } else {
            s0 = arr[0].toString();
            if (arr[1] != null) {
                s1 = arr[1].toString();

                Date date = Util.getStartTime();

                if (s0.equals("今天")) {

                } else if (s0.equals("明天")) {
                    date.setTime(date.getTime() + 24 * 60 * 60 * 1000);
                } else if (s0.equals("后天")) {
                    date.setTime(date.getTime() + 2 * 24 * 60 * 60 * 1000);
                }

                String flag = ":";
                if (s1.contains(flag)) {
                    String[] strArr = s1.split(flag);
                    if (strArr != null && strArr.length == 2) {
                        try {
                            date.setTime(date.getTime() + Integer.parseInt(strArr[0]) * 60 * 60 * 1000 + Integer.parseInt(strArr[1]) * 60 * 1000);
                            return date;
                        } catch (Exception e) {
                            e.printStackTrace();
                            LLog.Companion.d(LOG_TAG, "pareDate error" + e.getMessage());
                        }
                    } else {
                        return null;
                    }


                }
            }
        }


        return null;
    }

    /*
                       10s秒
                       3s后开始接单
                       1s后自动关闭


                        1s后开始接单


                        订单已被抢
                       1s后自动关闭
                        */
    private final DidiOrderState checkState(CharSequence[] arr) {
        if (arr == null || arr.length != 2) {
            return DidiOrderState.CONTINUE;
        }
        if (arr[0] != null) {
            if (arr[0].equals("订单已被抢")) {
                return DidiOrderState.INVALID;
            } else if (arr[0].equals("抢单")) {
                return DidiOrderState.OK;
            }
        } else {
            if (arr[1].toString().endsWith("后开始接单")) {
                return DidiOrderState.CONTINUE;
            }
        }
        return DidiOrderState.CONTINUE;
    }


    private final void grabOrder(AccessibilityNodeInfo accessibilityNodeInfo) {
        grabOrderCount++;
        String id = "com.sdu.didi.gsui:id/grab_order_bg";
        List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() == 1) {
            final Rect rect = new Rect();
            accessibilityNodeInfoList.get(0).getBoundsInScreen(rect);
            final int random = (int) (Math.random() * didiConfigManager.getClickRandomDelay());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(random);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int x = rect.right = rect.left;
                    int y = rect.bottom - rect.top;
                    final int x0 = rect.left+(int)(Math.random()*x+1);
                    final int y0 = rect.top+(int)(Math.random()*y+1);

                   /* final int x0 = rect.left;
                    final int y0 = rect.top;
                    final int x1 = rect.right;
                    final int y1 = rect.bottom;
                    String command = "input swipe " + x0 + " " + y0 + " " + x1 + " " + y1;*/


                    String command = "input tap " + x0 + " " + y0;
                    CommandResult commandResult = Shell.SU.run(command);
                    LLog.Companion.d(LOG_TAG, "grabOrder random=" + random + "\n" + command + "\n" + commandResult.isSuccessful() + " " + commandResult.exitCode);

                }
            });
        } else {
            LLog.Companion.d(LOG_TAG, "find " + id + " error");
        }


    }

    private final void closeView(AccessibilityNodeInfo accessibilityNodeInfo) {
        closeOrderCount++;
        String id = "com.sdu.didi.gsui:id/title_view_image";
        List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() == 1) {
            final Rect rect = new Rect();
            accessibilityNodeInfoList.get(0).getBoundsInScreen(rect);
            final int random = (int) (Math.random() * didiConfigManager.getClickRandomDelay());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(random);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int x = rect.right = rect.left;
                    int y = rect.bottom - rect.top;
                    final int x0 = rect.left + (int) (Math.random() * x + 1);
                    final int y0 = rect.top + (int) (Math.random() * y + 1);
                    String command = "input tap " + x0 + " " + y0;
                    CommandResult commandResult = Shell.SU.run(command);
                    LLog.Companion.d(LOG_TAG, "closeView random=" + random + "\n" + command + "\n" + commandResult.isSuccessful() + " " + commandResult.exitCode);
                }
            });
        } else {
            LLog.Companion.d(LOG_TAG, "find " + id + " error");
        }
    }

    public final void onDestroy() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }
}
