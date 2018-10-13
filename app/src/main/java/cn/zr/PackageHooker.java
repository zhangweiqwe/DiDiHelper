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


    private void log(String s) {
        XposedBridge.log("PackageHooker" + "-->" + s + "<--");
    }

    public PackageHooker(XC_LoadPackage.LoadPackageParam param) {
        loadPackageParam = param;
        try {
            hook();
        } catch (IOException | ClassNotFoundException e) {
            log(e.getMessage());
        }
    }

    public void hook() throws IOException, ClassNotFoundException {
        DexFile dexFile = new DexFile(loadPackageParam.appInfo.sourceDir);
        Enumeration<String> classNames = dexFile.entries();
        while (classNames.hasMoreElements()) {
            String className = classNames.nextElement();

            if (isClassNameValid(className)) {
                Class clazz = Class.forName(className, false, loadPackageParam.classLoader);

                for (Method method: clazz.getDeclaredMethods()) {
                    if (!Modifier.isAbstract(method.getModifiers())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                log("HOOKED: " + param.method.getName());
                            }
                        });
                    }
                }
            }
        }
    }

    public boolean isClassNameValid(String className) {
        return className.startsWith(loadPackageParam.packageName)
                && !className.contains("$")
                && !className.contains("BuildConfig")
                && !className.equals(loadPackageParam.packageName + ".R");
    }
}
