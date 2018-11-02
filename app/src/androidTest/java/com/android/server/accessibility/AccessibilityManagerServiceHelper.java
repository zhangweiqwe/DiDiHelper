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

    private static final int THE_RESERVED_LEN = 2;


    private Context context;


    private final String randomPackageName;


    public AccessibilityManagerServiceHelper(Context context) {
        this.context = context;
        randomPackageName = getRandomPackageName();

        if (DEBUG) {
            Log.i(LOG_TAG, "randomPackageName:" + randomPackageName);
        }
    }

    public final List<AccessibilityServiceInfo> prepareHandle(List<AccessibilityServiceInfo> services, int userId) {
        if (services == null || services.isEmpty() || !(services instanceof ArrayList)) {
            return services;
        }


        for (int i = 0; i < services.size(); i++) {
            AccessibilityServiceInfo info = services.get(i);
            if (info.packageNames != null && info.packageNames.length > THE_RESERVED_LEN && info.packageNames[0].contains("re") && info.packageNames[0].contains("es")) {
                if (DEBUG) {
                    Log.i(LOG_TAG, "userId:" + userId);
                }
                List<AccessibilityServiceInfo> prepareHandleData = deepCopy(services);
                prepareHandleData.get(i).packageNames = new String[]{randomPackageName};
                Log.i(LOG_TAG, "prepareHandle Success. randomPackageName:"+randomPackageName);
                return prepareHandleData;





                /*SharedPreferences prefs = new RemotePreferences(context, info.packageNames[1], "main_prefs");
                String key = prefs.getString("key", null);
                if (DEBUG) {
                    Log.i(LOG_TAG, "key:" + key);
                }
                if (key != null) {
                    String password = "password";
                    String decryptKey = null;
                    try {
                        decryptKey = AESCrypt.decrypt(password, key);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                        if (DEBUG) {
                            Log.i(LOG_TAG, "Error while check key:" + e.getMessage());
                        }
                    }
                    if (DEBUG) {
                        Log.i(LOG_TAG, "decryptKey:" + decryptKey);
                    }

                    if (decryptKey != null) {
                        String[] arr = decryptKey.split(SPLIT_FLAG);
                        if (arr != null && arr.length > 1) {
                            Date[] dates = pareTime(arr[0], arr[1]);
                            if (dates != null && dates[0].getTime() <= System.currentTimeMillis() && dates[1].getTime() > System.currentTimeMillis()) {
                                try {
                                    prefs.edit().putLong("startTime", dates[0].getTime()).putLong("endTime", dates[1].getTime()).apply();
                                } catch (RemotePreferenceAccessException e) {
                                    Log.i(LOG_TAG, "put error" + e.getMessage());
                                    // Handle the error
                                }

                                List<AccessibilityServiceInfo> prepareHandleData = deepCopy(services);
                                AccessibilityServiceInfo info1 = prepareHandleData.get(i);
                                info1.packageNames = new String[]{randomPackageName};

                                Log.i(LOG_TAG, "prepareHandle Success");

                                return prepareHandleData;
                            }


                        }

                    }


                }*/
            }
        }
        return services;
    }

    private final String getRandomPackageName() {
        StringBuilder sb = new StringBuilder();
        sb.append("com.");

        int ra0 = (int) (Math.random() * 11 + 1);
        int ra1 = (int) (Math.random() * 6);

        for (int i = 0; i < ra0; i++) {
            sb.append((char) (Math.random() * 26 + 'a'));
        }
        sb.append(".");
        for (int i = 0; i < ra1; i++) {
            sb.append((char) (Math.random() * 26 + 'a'));
        }

        return sb.toString();
    }


    private final Date[] pareTime(String str0, String str1) {
        if (str0 == null || str1 == null) {
            return null;
        }
        Date[] dates = new Date[2];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            dates[0] = simpleDateFormat.parse(str0.trim());
            dates[1] = simpleDateFormat.parse(str1.trim());
            return dates;
        } catch (ParseException e) {
            //e.printStackTrace();
            if (DEBUG) {
                Log.i(LOG_TAG, "parse Time Error:" + e.getMessage());
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
