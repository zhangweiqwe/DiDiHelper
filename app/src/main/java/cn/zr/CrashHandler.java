package cn.zr;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zr.util.ConfigUtil;
import cn.zr.util.LLog;

/**
 * Create by ChenHao on 2018/6/299:30
 * use : 应用异常处理类
 * 使用方式： 在Application 中初始化  CrashHandler.getInstance().init(this);
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String LOG_TAG = "CrashHandler";
    public static final String LOG_FILE_NAME = "CrashHandlerLog.txt";

    private static CrashHandler sInstance = new CrashHandler();
    private UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;


    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        LLog.Companion.d(LOG_TAG, ex + "   " + ex.getMessage());
        if (mDefaultCrashHandler != null) {
            if(ex!=null){
                LLog.Companion.saveRuntimeLog(mContext);
                LLog.Companion.saveLog(mContext,LOG_FILE_NAME,(new SimpleDateFormat(LLog.SIMPLE_DATA_FORMAT).format(new Date())+"\n"+collectExceptionInfo(ex)+"\n\n\n\n\n\n\n\n").getBytes(),true);
            }
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }

    }


    /**
     * 获取捕获异常的信息
     */
    private String collectExceptionInfo(Throwable ex) {
        Writer mWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mWriter);
        ex.printStackTrace(mPrintWriter);
        //ex.printStackTrace();
        Throwable mThrowable = ex.getCause();
        // 迭代栈队列把所有的异常信息写入writer中
        while (mThrowable != null) {
            mThrowable.printStackTrace(mPrintWriter);
            // 换行 每个个异常栈之间换行
            mPrintWriter.append("\r\n");
            mThrowable = mThrowable.getCause();
        }
        // 记得关闭
        mPrintWriter.close();
        return mWriter.toString();
    }


}

