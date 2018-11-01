package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.server.accessibility.remotePreferences.RemotePreferenceAccessException;
import com.android.server.accessibility.remotePreferences.RemotePreferences;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccessibilityManagerServiceHelper {
    private static final String LOG_TAG = "AccessibilityManagerServiceHelper";
    private static final boolean DEBUG = true;

    private static final String SPLIT_FLAG = "---";
    private static final String SPLIT_FLAG_A = "=";


    private Context context;

    public AccessibilityManagerServiceHelper(Context context) {
        this.context = context;
    }

    public final List<AccessibilityServiceInfo> prepareHandle(List<AccessibilityServiceInfo> services) {
        if (services == null || services.isEmpty() || !(services instanceof ArrayList)) {
            return services;
        }

        for (int i = 0; i < services.size(); i++) {
            AccessibilityServiceInfo info = services.get(i);

            final int packageNamesMiniLen = 2;
            if (info.packageNames != null && info.packageNames.length > packageNamesMiniLen) {
                if (info.packageNames[0].equals("cn.a")) {


                    SharedPreferences prefs = new RemotePreferences(context, info.packageNames[1], "main_prefs");
                    String key = prefs.getString("key", null);
                    if (DEBUG) {
                        Log.i(LOG_TAG, "key:" + key);
                    }
                    if (key != null) {

                        String password = "password";
                        String encryptedMsg = key;
                        String decryptKey = null;
                        try {
                            decryptKey = AESCrypt.decrypt(password, encryptedMsg);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                            if (DEBUG) {
                                Log.i(LOG_TAG, "Error while check key" + e.getMessage());
                            }
                        }
                        if (DEBUG) {
                            Log.i(LOG_TAG, "decryptKey:" + decryptKey);
                        }

                        if (decryptKey != null) {
                            if (decryptKey.contains(SPLIT_FLAG)) {
                                String[] arr = decryptKey.split(SPLIT_FLAG);
                                if (arr.length > 2) {

                                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                                    String tag = null;

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        tag = tm.getImei();
                                    }
                                    if (DEBUG) {
                                        Log.i(LOG_TAG, "tag:" + tag);
                                    }

                                    if (tag != null && arr[0].trim().equals(tag)) {
                                        Date[] dates = pareTime(arr[1].trim());
                                        if (dates != null) {
                                            if (dates[0].getTime() < System.currentTimeMillis() && dates[1].getTime() > System.currentTimeMillis()) {
                                                /*Intent intent = new Intent("android.intent.action.ACCESSIBILITY_MANAGER_SERVICE_HELPER");
                                                intent.addCategory("android.intent.category.CHECK_TIME");
                                                intent.putExtra("startTime", dates[0].getTime());
                                                intent.putExtra("endTime", dates[1].getTime());
                                                context.sendBroadcast(intent);*/


                                                try {
                                                    prefs.edit().putLong("startTime", dates[0].getTime()).putLong("endTime", dates[1].getTime()).apply();
                                                } catch (RemotePreferenceAccessException e) {
                                                    Log.i(LOG_TAG, "put error" + e.getMessage());
                                                    // Handle the error
                                                }

                                                List<AccessibilityServiceInfo> prepareHandleData = deepCopy(services);
                                                AccessibilityServiceInfo info1 = prepareHandleData.get(i);

                                                String simplePackageName = arr[2];
                                                for (int z = packageNamesMiniLen; z < info1.packageNames.length - 2; z++) {
                                                    info1.packageNames[z] = simplePackageName + (char) z;
                                                }
                                                if (DEBUG) {
                                                    Log.i(LOG_TAG, "prepareHandleData Success");
                                                }
                                                return prepareHandleData;
                                            }
                                        }


                                    }

                                }
                            }

                        }
                    }


                }
            }
        }
        return services;
    }

    private final Date[] pareTime(String string) {
        if (string == null || !string.contains(SPLIT_FLAG_A)) {
            return null;
        }
        String[] arr = string.split(SPLIT_FLAG_A);
        if (arr.length > 1) {
            Date[] dates = new Date[2];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                dates[0] = simpleDateFormat.parse(arr[0].trim());
                dates[1] = simpleDateFormat.parse(arr[1].trim());
                return dates;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    private final List<AccessibilityServiceInfo> deepCopy(List<AccessibilityServiceInfo> src) {
        if (src == null || src.isEmpty()) {
            return src;
        }
        List<AccessibilityServiceInfo> students = new ArrayList<>();
        for (int i = 0; i < src.size(); i++) {
            students.add((AccessibilityServiceInfo) copy(src.get(i)));
        }
        return students;
    }


    private final <T> T copy(android.os.Parcelable input) {
        android.os.Parcel parcel = null;
        try {
            parcel = android.os.Parcel.obtain();
            parcel.writeParcelable(input, 0);
            parcel.setDataPosition(0);
            return parcel.readParcelable(input.getClass().getClassLoader());
        } finally {
            parcel.recycle();
        }
    }
}
