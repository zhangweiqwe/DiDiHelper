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

    private void print(String s) {
        XposedBridge.log("XposedInit" + "-->" + s + "<");
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        print(lpparam.packageName);

        if(!BuildConfig.DEBUG){
            new Module().handleLoadPackage(lpparam);
        }else {
            new TestModule().handleLoadPackage(lpparam);
        }



    }


}