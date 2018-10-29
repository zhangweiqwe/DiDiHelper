package cn.zr;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by lanxiaobin on 2018/1/29.
 * https://api.xposed.info/reference/android/app/AndroidAppHelper.html
 */

public class Module implements IXposedHookLoadPackage {

    private void print(String s) {
        XposedBridge.log("Module" + "-->" + s + "<");
    }

    private final List<AccessibilityServiceInfo> prepare(List<AccessibilityServiceInfo> services) {
        if (services == null || services.isEmpty() || !(services instanceof ArrayList)) {
            return services;
        }
        List<AccessibilityServiceInfo> infos = (ArrayList<AccessibilityServiceInfo>) (((ArrayList<AccessibilityServiceInfo>) services).clone());
        for (int i = 0; i < services.size(); i++) {
            AccessibilityServiceInfo info = services.get(i);
            if (info.packageNames != null && info.packageNames.length > 1) {
                if (info.packageNames[0].equals("zzr") && info.packageNames[1].equals("com.sdu.didi.gui") && info.getSettingsActivityName().equals("cn.zr.MainActivity")) {
                    info.packageNames[1] = "kankan";
                    return infos;
                }
            }
        }
        return services;
    }

    /**
     * 入口，通过反射调用
     *
     * @param loadPackageParam
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) {

            XC_MethodHook callback = new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult("zr");
                    Toast.makeText((Context) param.thisObject, "demo", Toast.LENGTH_SHORT).show();
                }
            };
            findAndHookMethod(MainActivity.class, "getResult", callback);

        }

    }


}
