package cn.zr;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Zr {
    private void print(String s) {
        XposedBridge.log("XposedInit" + "-->" + s + "<");
    }
    public static void assist(){
        new Zr().zr();
    }
    private void zr(){
        XposedHelpers.findAndHookMethod(AccessibilityManager.class, "getEnabledAccessibilityServiceList", Integer.TYPE, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object obj = param.getResult();
                if (obj != null) {
                    List<AccessibilityServiceInfo> list = (List<AccessibilityServiceInfo>) obj;
                    for (int i = 0; i < list.size(); i++) {
                        StringBuilder sb = new StringBuilder();

                        String[] arr = list.get(i).packageNames;
                        for (int z = 0; z < arr.length; z++) {
                            sb.append(arr[z] + "\t");
                            StringBuilder sb0 = new StringBuilder();
                            sb0.append("com");
                            sb0.append("sdu");
                            sb0.append("didi");
                            sb0.append("gsui");
                            if (arr[z].equals(sb0.toString())) {
                                arr[z] = "com.google.check";
                            }
                        }

                        print("emptyList" + sb.toString());
                    }
                }
                param.setResult(obj);

            }


        });
    }

}
