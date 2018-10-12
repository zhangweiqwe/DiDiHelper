package cn.zr;

import android.util.Log;

import java.io.File;

import de.robv.android.xposed.XposedBridge;

public class Other {
    private static final String TAG = "Other";

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
}
