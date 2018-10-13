package cn.zr;


import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import java.lang.reflect.Method;

import cn.zr.contentProviderPreference.RemotePreferenceAccessException;
import cn.zr.contentProviderPreference.RemotePreferences;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class XposedInit implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

    }

    private void log(String s) {
        XposedBridge.log("Xposed" + "-->" + s + "<--");
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        log(loadPackageParam.packageName + " " + loadPackageParam.appInfo + " " + loadPackageParam.appInfo.sourceDir);


        Context systemContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", loadPackageParam.classLoader), "currentActivityThread"), "getSystemContext");
        SharedPreferences prefs = new RemotePreferences(systemContext, "cn.zr.preferences", "other_preferences");


        String packageSourceDir = null;
        if (loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            //Context context = (Context) AndroidAppHelper.currentApplication();
            //final int versionCheck = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
            try {
                String value = loadPackageParam.appInfo.sourceDir;//systemContext.getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA).sourceDir;
                prefs.edit().putString("packageSourceDir", value).apply();
                packageSourceDir = value;
                log("putString packageSourceDir=" + value);
            } catch (RemotePreferenceAccessException e) {
                log("RemotePreferenceAccessException=" + e.getMessage());
            }
        } else {
            packageSourceDir = prefs.getString("packageSourceDir", systemContext.getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA).sourceDir);
        }


        log("getString packageSourceDir=" + packageSourceDir);


        PathClassLoader pathClassLoader = new PathClassLoader(packageSourceDir, ClassLoader.getSystemClassLoader());
        Class<?> aClass = Class.forName(BuildConfig.APPLICATION_ID + "." + Module.class.getSimpleName(), true, pathClassLoader);
        Method aClassMethod = aClass.getMethod("handleLoadPackage", XC_LoadPackage.LoadPackageParam.class);

        try {
            aClassMethod.invoke(aClass.newInstance(), loadPackageParam);
        } catch (Exception e) {
            log(e.getMessage());
        }

    }


}