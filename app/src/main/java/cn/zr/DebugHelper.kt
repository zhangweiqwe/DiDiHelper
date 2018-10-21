package cn.zr

import android.app.Activity
import android.content.Context
import android.os.Process
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.content.pm.PackageManager
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.support.v4.content.ContextCompat.getSystemService


class DebugHelper {
    companion object {
        private const val TAG = "DebugHelper"
        private const val ROOT_PATH = "/data/local2/"
        private const val LIB_SHARE_NAME = "libshare.so"

        private const val TARGET_PACKAGE_NAME = "com.eatbeancar.user"

        fun init() {
            ShellUtil.exec("mkdir $ROOT_PATH\nchmod -R 777 $ROOT_PATH", OnResultListener())
        }

        fun copyAssistFile(context: Context, fileName: String, filePath: String) {


            val inS = context.applicationContext.assets.open("$filePath$fileName")
            val out = FileOutputStream(File("$ROOT_PATH$fileName"))

            val buffer = ByteArray(1024)
            var len = inS.read(buffer)

            while (len != -1) {
                out.write(buffer, 0, len);
                len = inS.read(buffer)//自增
            }
            out.flush()


            inS?.apply {
                close()
            }
            out?.apply {
                close()
            }

        }


        fun initExec(context: Context) {

            ShellUtil.exec("cp ${context.applicationInfo.sourceDir.let {
                it.substring(0, it.lastIndexOf("/") + 1) + "lib/arm64/$LIB_SHARE_NAME"
            }} $ROOT_PATH\n" +
                    "chmod -R 777 $ROOT_PATH", OnResultListener())
        }

        fun exec() {
            ShellUtil.exec(".$ROOT_PATH/zr.so $TARGET_PACKAGE_NAME $ROOT_PATH$LIB_SHARE_NAME", OnResultListener())
        }


        fun check() {
            ShellUtil.exec("pgrep -f $TARGET_PACKAGE_NAME") { it ->
                if (it.result == 0) {
                    it.data.also {
                        if (it.isNotEmpty()) {
                            ShellUtil.exec("cat /proc/${it[0]}/maps", OnResultListener2())
                        }

                    }

                }
            }


        }


        class OnResultListener : ShellUtil.OnResultListener {
            override fun onResult(result: ShellUtil.Result) {
                result.data.also {
                    it.forEach {
                        Log.d(TAG, it)
                    }
                }
            }
        }

        class OnResultListener2 : ShellUtil.OnResultListener {
            override fun onResult(result: ShellUtil.Result) {
                result.data.also {
                    Log.d(TAG, "it.size=${it.size}")
                    it.forEach {
                        if (it.contains(ROOT_PATH)) {
                            Log.d(TAG, it)
                        }
                    }
                }

            }
        }


    }


}