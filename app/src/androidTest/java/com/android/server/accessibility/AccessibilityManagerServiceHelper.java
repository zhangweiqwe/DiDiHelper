package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.android.server.accessibility.remotePreferences.RemotePreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccessibilityManagerServiceHelper {
    private static final String LOG_TAG = "AccessibilityManagerServiceHelper";
    private static final String SPLIT_FLAG = "=";
    private static final String SIMPLE_DATA_FORMAT = "yyyy-MM-dd HH:mm";
    private static final boolean DEBUG = true;

    private static final int THE_RESERVED_LEN = 2;


    private Context context;


    //private final String randomPackageName;
    //private String authority;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATA_FORMAT);


    public AccessibilityManagerServiceHelper(Context context) {
        this.context = context;
        //randomPackageName = getRandomPackageName();

        /*if (DEBUG) {
            Log.i(LOG_TAG, "randomPackageName:" + randomPackageName);
        }*/
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


                if (DEBUG) {
                    Log.i(LOG_TAG, "info.packageNames[0]:" + info.packageNames[0]);
                }


                //Log.i(LOG_TAG, "prepareHandle Success. randomPackageName:" + randomPackageName);

                SharedPreferences prefs = new RemotePreferences(context, info.packageNames[0], "main_prefs");
                String key = prefs.getString("key0", null);
                if (DEBUG) {
                    Log.i(LOG_TAG, "key:" + key);
                }
                if (key != null) {
                    String password = "youyou_";
                    String decryptKey = null;
                    try {
                        decryptKey = AESCrypt.decrypt(password, key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (DEBUG) {
                            Log.i(LOG_TAG, "Error while check key:" + e.getMessage());
                        }
                        return services;
                    }

                    if (DEBUG) {
                        Log.i(LOG_TAG, "decryptKey:" + decryptKey);
                    }

                    if (decryptKey != null) {
                        String arr[] = decryptKey.split(SPLIT_FLAG);
                        if (arr != null && arr.length == 3) {
                            long startTime = 0;
                            long endTime = 0;
                            try {
                                startTime = simpleDateFormat.parse(arr[0]).getTime();
                                endTime = simpleDateFormat.parse(arr[1]).getTime();
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (DEBUG) {
                                    Log.i(LOG_TAG, "time cast error:" + e.getMessage());
                                }
                                return services;
                            }
                            long currentTimeMillis = System.currentTimeMillis();
                            if (startTime <= currentTimeMillis && currentTimeMillis <= endTime) {
                                if (arr[2].equals(getDevicesTag(context))) {
                                    List<AccessibilityServiceInfo> prepareHandleData = deepCopy(services);
                                    prepareHandleData.get(i).packageNames = new String[]{info.packageNames[0]};
                                    if (DEBUG) {
                                        Log.i(LOG_TAG, "check Success");
                                    }
                                    return prepareHandleData;
                                } else {
                                    if (DEBUG) {
                                        Log.i(LOG_TAG, "device tag invalid");
                                    }
                                    return services;
                                }
                            } else {
                                if (DEBUG) {
                                    Log.i(LOG_TAG, "time invalid");
                                }
                                return services;
                            }
                        } else {
                            if (DEBUG) {
                                Log.i(LOG_TAG, "decryptKey.split mismatching");
                            }
                        }
                    } else {
                        if (DEBUG) {
                            Log.i(LOG_TAG, "decryptKey split error");
                        }
                        return services;
                    }
                }


            }
        }
        return services;
    }


    private final String getDevicesTag(Context context) {
        TelephonyManager telephonyManage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return telephonyManage.getImei();
        } else {
            return telephonyManage.getDeviceId();
        }
    }

    /*private final String getRandomPackageName() {
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
*/

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
