package cn.zr.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final String LOG_TAG = "Util";

    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    public static Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    //截取数字  【读取字符串中第一个连续的字符串，不包含后面不连续的数字】
    public static Integer getStartNumber(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String findStr = matcher.group(0);
            if (findStr != null) {
                try {
                    return Integer.parseInt(findStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    LLog.Companion.d(LOG_TAG, "getStartNumber error" + e.getMessage());
                }
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public static String getDevicesTag(Context context) {
        TelephonyManager telephonyManage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return telephonyManage.getImei();
        } else {
            return telephonyManage.getDeviceId();
        }
    }
}
