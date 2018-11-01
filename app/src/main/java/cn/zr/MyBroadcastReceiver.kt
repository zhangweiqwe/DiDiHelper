package cn.zr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private const val LOG_TAG = "MyBroadcastReceiver"
    }


    override fun onReceive(context: Context, intent: Intent) {
        val startTime = intent.getLongExtra("startTime", 0)
        val endTime = intent.getLongExtra("endTime", 0)

        Log.d(LOG_TAG, "$startTime $endTime")
        if (startTime == 0L || endTime == 0L) {
            System.exit(0)
        }
    }
}