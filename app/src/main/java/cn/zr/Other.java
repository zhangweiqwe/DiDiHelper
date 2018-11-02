package cn.zr;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
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

    public String getCallerProcessName(Context context) {
        int uid = Binder.getCallingUid();
        String callingApp = context.getPackageManager().getNameForUid(uid);
        Log.d(TAG, "callingApp: " + callingApp);
        if (callingApp != null) {
            return callingApp;
        }
        return "";
    }


    public static final String getRandomPackageName() {
        String packageName = "com.";

        int ra0 = (int) (Math.random() * 11 + 1);
        int ra1 = (int) (Math.random() * 6);

        for (int i = 0; i < ra0; i++) {
            packageName += (char) (Math.random() * 26 + 'a');
        }
        packageName += ".";
        for (int i = 0; i < ra0; i++) {
            packageName += (char) (Math.random() * 26 + 'a');
        }

        return packageName;
    }

}
