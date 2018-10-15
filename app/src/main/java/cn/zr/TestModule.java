package cn.zr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import java.lang.reflect.Method;

import cn.zr.contentProviderPreference.RemotePreferences;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/*

adb install C:\Users\Administrator\Desktop\backup\release\app-release.apk
adb push C:\Users\Administrator\Desktop\backup\release\app-release.apk sdcard/Download/
 */
public class TestModule implements IXposedHookLoadPackage {
    private void print(String s) {
        XposedBridge.log("TestModule" + "-->" + s + "<");
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Context systemContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader), "currentActivityThread"), "getSystemContext");
        SharedPreferences prefs = new RemotePreferences(systemContext, "cn.zr.preferences", "other_preferences");

        print(lpparam.packageName + " " + systemContext.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_ACTIVITIES).versionCode + "");
        String packageSourceDir;
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            prefs.edit().putString("sourceDir", packageSourceDir = lpparam.appInfo.sourceDir).apply();
        } else {
            packageSourceDir = prefs.getString("sourceDir", systemContext.getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA).sourceDir);
        }
        print("getString sourceDir" + packageSourceDir);


        PathClassLoader pathClassLoader = new PathClassLoader(packageSourceDir, ClassLoader.getSystemClassLoader());
        Class<?> aClass = Class.forName(Module.class.getCanonicalName(), true, pathClassLoader);
        Method aClassMethod = aClass.getMethod("handleLoadPackage", XC_LoadPackage.LoadPackageParam.class);
        aClassMethod.invoke(aClass.newInstance(), lpparam);
    }
}
