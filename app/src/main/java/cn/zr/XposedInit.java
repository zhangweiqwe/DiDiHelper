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
        XposedBridge.log("XposedInit" + "-->" + s + "<--");
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        log(loadPackageParam.packageName + " " + loadPackageParam.appInfo + " " + loadPackageParam.appInfo.sourceDir+" "+AndroidAppHelper.currentApplication());


        Context systemContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", loadPackageParam.classLoader), "currentActivityThread"), "getSystemContext");
        SharedPreferences prefs = new RemotePreferences(systemContext, "cn.zr.preferences", "other_preferences");

        log(loadPackageParam.packageName + " " + systemContext.getPackageManager().getPackageInfo(loadPackageParam.packageName, PackageManager.GET_ACTIVITIES).versionCode + "");
        String packageSourceDir;
        if (loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            prefs.edit().putString("sourceDir", packageSourceDir = loadPackageParam.appInfo.sourceDir).apply();
        } else {
            packageSourceDir = prefs.getString("sourceDir", systemContext.getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA).sourceDir);
        }
        log("getString sourceDir" + packageSourceDir);


        PathClassLoader pathClassLoader = new PathClassLoader(packageSourceDir, ClassLoader.getSystemClassLoader());
        Class<?> aClass = Class.forName(Module.class.getCanonicalName(), true, pathClassLoader);
        Method aClassMethod = aClass.getMethod("handleLoadPackage", XC_LoadPackage.LoadPackageParam.class);
        aClassMethod.invoke(aClass.newInstance(), loadPackageParam);
    }


}