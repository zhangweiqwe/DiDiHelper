package cn.zr;


import android.content.Context;
import android.content.pm.PackageManager;

import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class XposedInit implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("XposedInit" + loadPackageParam.packageName + "  -->" + loadPackageParam.appInfo);




        /*

        //通过反射实现热更新
        //final String packageName = Module.class.getPackage().getName();
        final String packageName = BuildConfig.APPLICATION_ID;
        String filePath = String.format("/data/app/%s-%s.apk", packageName, 1);
        if (!new File(filePath).exists()) {
            filePath = String.format("/data/app/%s-%s.apk", packageName, 2);
            if (!new File(filePath).exists()) {
                filePath = String.format("/data/app/%s-%s/base.apk", packageName, 1);
                if (!new File(filePath).exists()) {
                    filePath = String.format("/data/app/%s-%s/base.apk", packageName, 2);
                    if (!new File(filePath).exists()) {
                        XposedBridge.log("Error:在/data/app找不到APK文件" + packageName);
                        return;
                    }
                }
            }
        }
        final PathClassLoader pathClassLoader = new PathClassLoader(filePath, ClassLoader.getSystemClassLoader());
        final Class<?> aClass = Class.forName(packageName + "." + Module.class.getSimpleName(), true, pathClassLoader);
        final Method aClassMethod = aClass.getMethod("handleMyHandleLoadPackage", XC_LoadPackage.LoadPackageParam.class);
        aClassMethod.invoke(aClass.newInstance(), loadPackageParam);

        */

    }


    private void zr(XC_LoadPackage.LoadPackageParam loadPackageParam, String filePath) throws Throwable {
        XposedBridge.log("XposedInit-->zr" + filePath);


        final PathClassLoader pathClassLoader = new PathClassLoader(filePath, ClassLoader.getSystemClassLoader());
        final Class<?> aClass = Class.forName("cn.zr" + "." + Module.class.getSimpleName(), true, pathClassLoader);
        final Method aClassMethod = aClass.getMethod("handleMyHandleLoadPackage", XC_LoadPackage.LoadPackageParam.class);
        aClassMethod.invoke(aClass.newInstance(), loadPackageParam);
    }
}