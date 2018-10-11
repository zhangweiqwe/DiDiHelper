package cn.zr;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by lanxiaobin on 2018/1/29.
 */

public class Module {

    private static final String TAG = "Module";

    private void hook_method(String className, ClassLoader classLoader, String methodName,
                             Object... parameterTypesAndCallback) {
        try {
            findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    /**
     * 入口，通过反射调用
     *
     * @param loadPackageParam
     */
    public void handleMyHandleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {


        XposedBridge.log("XposedInit" + loadPackageParam.packageName+"  -->2"+loadPackageParam.appInfo);


        /*try  {
            Class<?>  ContextClass  =  findClass("android.content.ContextWrapper",  loadPackageParam.classLoader);
            findAndHookMethod(ContextClass,  "getApplicationContext",  new  XC_MethodHook()  {
                @Override
                protected  void  afterHookedMethod(MethodHookParam  param)  throws  Throwable  {
                    super.afterHookedMethod(param);
                    XposedBridge.log("XposedInit-->得到上下文"+loadPackageParam.packageName+((Context)  param.getResult()).getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID,PackageManager.GET_META_DATA).sourceDir);
                }
            });
        }  catch  (Throwable  t)  {
            XposedBridge.log("XposedInit-->获取上下文出错");
            XposedBridge.log(t);
        }*/




        if (loadPackageParam.packageName.equals("cn.zr")) {





            hook_method("cn.zr.MainActivity", loadPackageParam.classLoader, "getResult", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("已启动，不需要重启手机ldsa");
                    Toast.makeText((Context) param.thisObject, "你在看什么xposeddemo", Toast.LENGTH_SHORT).show();
                }
            });






        }


       /* XC_MethodHook callback1 = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {

                param.args[0] = true;

            }

        };

        Class<?> clazz = null;
        try {
            clazz = Class.forName("android.support.v7.app.AlertDialog.Builder");
        } catch (ClassNotFoundException e) {


        }

        if (clazz != null) {


            XposedBridge.hookAllMethods(clazz, "setCancelable", callback1);

            *//*XposedBridge.hookAllMethods(clazz, "setOnCancelListener",
                    XC_MethodHook1);*//*

        } else {


        }


        XposedBridge.hookAllMethods(Dialog.class, "setCancelable", callback1);*/



       // hook();

    }


    public void hook(){


     /*   XposedHelpers.findAndHookMethod(AlertDialog.Builder.class,
                "setCancelable", Integer.TYPE,
                new XC_MethodReplacement() {

                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param)
                            throws Throwable {
                        Object color = XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                        if (color instanceof Boolean) {
                            Boolean new_name = (Boolean) color;
                            return true;
                        }
                        return null ;
                    }
                });*/



        XC_MethodHook callback=new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param)
                    throws Throwable {
                param.args[0]=true;


                Toast.makeText((Context) param.thisObject, "fdsfsdfsf", Toast.LENGTH_SHORT).show();

            }

        };

        XposedBridge.hookAllMethods(AlertDialog.Builder.class, "setCancelable", callback);


       // return  XposedBridge.invokeOriginalMethod(methodHookParam.method,  methodHookParam.thisObject,  methodHookParam.args);



        XC_MethodHook callback1=new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param)
                    throws Throwable {


                param.args[0]=true;



            }


        };

        XposedBridge.hookAllMethods(AppCompatActivity.class, "setFinishOnTouchOutside", callback1);




    }

}
