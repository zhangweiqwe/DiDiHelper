package cn.zr;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by lanxiaobin on 2018/1/29.
 * https://api.xposed.info/reference/android/app/AndroidAppHelper.html
 */

public class Module extends XposedInit {

    private void print(String s) {
        XposedBridge.log("Module" + "-->" + s + "<");
    }

    /**
     * 入口，通过反射调用
     *
     * @param loadPackageParam
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals("com.sdu.didi.gsui")) {
            PackageHooker.assist(loadPackageParam);
        }

        if (loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) {

            XC_MethodHook callback = new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult("zr");
                    Toast.makeText((Context) param.thisObject, "demo", Toast.LENGTH_SHORT).show();
                }
            };
            findAndHookMethod("cn.zr.MainActivity", loadPackageParam.classLoader, "getResult", callback);

            XC_MethodHook callback3 = new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    print("afterHookedMethod");

                    if (param.args != null && param.args.length > 0 && param.args[0] instanceof String) {
                        print("->>" + (String) param.args[0]);
                    }
                }

            };
            XposedBridge.hookAllMethods(Canvas.class, "drawText", callback3);
        }
    }


}
