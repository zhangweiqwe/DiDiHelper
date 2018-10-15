package cn.zr;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;

import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by mbpeele on 2/24/16.//https://github.com/UMLGenerator/Android-UML-Generator/blob/f26521e9420d4be032c2e4a1a872ac0c945c7c2e/xposed/src/main/java/software/umlgenerator/PackageHooker.java
 */
public class PackageHooker {


    private final XC_LoadPackage.LoadPackageParam loadPackageParam;


    private void print(String s) {
        XposedBridge.log("PackageHooker" + "-->" + s + "<");
    }

    private PackageHooker(XC_LoadPackage.LoadPackageParam param) {
        loadPackageParam = param;
    }

    public static void assist(XC_LoadPackage.LoadPackageParam param) {
        try {
            new PackageHooker(param).hook();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void hook() throws IOException, ClassNotFoundException {
        DexFile dexFile = new DexFile(loadPackageParam.appInfo.sourceDir);
        Enumeration<String> classNames = dexFile.entries();
        while (classNames.hasMoreElements()) {
            String className = classNames.nextElement();
            /*if (className.startsWith("de.robv.android.xposed") || className.startsWith(Module.class.getCanonicalName())) {
                continue;
            }*/
            //print("className-->" + className);
            if (isClassNameValid(className)) {
                Class clazz = Class.forName(className, false, loadPackageParam.classLoader);

                for (Method method : clazz.getDeclaredMethods()) {
                    if (!Modifier.isAbstract(method.getModifiers())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                StringBuilder sb = new StringBuilder();
                                sb.append(param.method.getName() + " " + param.method.getDeclaringClass() + " ");

                                if (param.args != null) {
                                    sb.append(param.args.length + "(");
                                    for (Object obj : param.args) {
                                        sb.append((obj == null ? null : obj.getClass().getCanonicalName()) + " " + getValue(obj) + " ");
                                    }
                                    sb.append(") ");
                                }
                                Object result = param.getResultOrThrowable();
                                sb.append("(" + (result == null ? null : result.getClass().getCanonicalName()) + " " + getValue(result) + ")");
                                print("HOOKED: " + sb.toString());
                            }
                        });
                    }
                }
            }
        }
    }

    public boolean isClassNameValid(String className) {
        return
                className.startsWith(loadPackageParam.packageName)
                        //&& !className.contains("$")
                        &&

                        !className.contains("BuildConfig")
                        && !className.equals(loadPackageParam.packageName + ".R");
    }

    private final String getValue(Object obj) {
        if (obj == null) {
            return "";
        } else if (obj instanceof Boolean) {
            return (Boolean) obj + "";
        } else if (obj instanceof Integer) {
            return (Integer) obj + "";
        } else if (obj instanceof Short) {
            return (Short) obj + "";
        } else if (obj instanceof Float) {
            return (Float) obj + "";
        } else if (obj instanceof Double) {
            return (Double) obj + "";
        } else if (obj instanceof Character) {
            return (Character) obj + "";
        } else if (obj instanceof Long) {
            return (Long) obj + "";
        } else if (obj instanceof Byte) {
            return (Byte) obj + "";
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Object) {
            return "";
        } else {
            return "";
        }
    }
}
