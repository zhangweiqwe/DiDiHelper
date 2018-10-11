package cn.zr;

import android.app.Application;
import android.util.Log;

public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate()");
    }
}
