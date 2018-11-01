package cn.zr;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.util.Date;

import de.robv.android.xposed.XposedBridge;

public class Other {

    static {
        //System.loadLibrary("native-lib");
        //System.loadLibrary("share");
    }

    private static final String TAG = "Other";
    //public native String stringFromJNI();
    public Other() {
    }

    public static File getApk() {
        for (File file : new File("data/app/").listFiles()) {
            if (file.getName().startsWith(BuildConfig.APPLICATION_ID)) {
                return new File(file.getPath(), "base.apk");
            }
        }
        return null;
    }


    public static final void z(Context context){
        Date[] dates = new Date[2];
        dates[0] = new Date();
        dates[1] = new Date();

        Intent intent = new Intent();
        intent.setAction("fdasfsadf");
        //Intent intent = new Intent("android.intent.action.ACCESSIBILITY_MANAGER_SERVICE_HELPER");
//        intent.addCategory("android.intent.category.CHECK_TIME");
        intent.putExtra("startTime", dates[0].getTime());
        intent.putExtra("endTime", dates[1].getTime());
        context.sendBroadcast(intent);
    }
}
