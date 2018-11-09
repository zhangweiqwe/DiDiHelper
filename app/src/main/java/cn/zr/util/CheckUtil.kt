package cn.zr.util

import android.content.Context
import android.os.Handler
import android.os.Message
import android.widget.Toast
import cn.zr.R
import cn.zr.contentProviderPreference.RemotePreferences
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.security.GeneralSecurityException
import java.text.SimpleDateFormat
import java.util.*

class CheckUtil(val context: Context, onCheckTimeResultListener: OnCheckTimeResultListener) {
    companion object {
        private const val LOG_TAG = "CheckUtil"
        private const val SPLIT_FLAG = "="

    }

    private val handlerThread = android.os.HandlerThread("CheckUtil")

    private val handlerCallback = Handler.Callback {

        when (it.what) {
            1000 -> {
                Toast.makeText(context, context.getString(R.string.validation_failed_please_try_again_after_you_open_the_network), Toast.LENGTH_SHORT).show()
                onCheckTimeResultListener.onResult(false)
            }
            1001 -> {
                Toast.makeText(context, context.getString(R.string.valid_time) + "\t" + it.obj as String?, Toast.LENGTH_SHORT).show()
                onCheckTimeResultListener.onResult(false)
            }
            1002 -> {
                Toast.makeText(context, context.getString(R.string.authentication_is_successful), Toast.LENGTH_SHORT).show()
                onCheckTimeResultListener.onResult(true)
            }
            1003 -> {
                Toast.makeText(context, context.getString(R.string.please_enter_the_registration_code), Toast.LENGTH_SHORT).show()
                onCheckTimeResultListener.onResult(false)
            }
            1004 -> {
                //(context.applicationContext as BaseApplication).exit()
                Toast.makeText(context, context.getString(R.string.authentication_is_failure), Toast.LENGTH_SHORT).show()
            }
        }
        true
    }
    private val handler = Handler(handlerThread.apply {
        start()
    }.looper, handlerCallback)
    private val simpleDateFormat = SimpleDateFormat(ConfigUtil.SIMPLE_DATA_FORMAT)


    interface OnCheckTimeResultListener {
        fun onResult(b: Boolean)
    }


    fun checkTime() {
        val prefs = RemotePreferences(context, "cn.zr.preferences", "main_prefs")
        val key = prefs.getString("key", null)

        if (key != null) {
            var decryptKey: String? = null
            try {
                decryptKey = AESCrypt.decrypt("_kankan", key)
            } catch (e: Exception) {
                e.printStackTrace()
                LLog.d(LOG_TAG,e.message)
            }
            if (decryptKey != null) {
                val arr = decryptKey.split(SPLIT_FLAG)
                if (arr != null && arr.size > 2 && arr[2] == Util.getDevicesTag(context)) {

                    var startDate: Date? = null
                    var endDate: Date? = null
                    try {
                        startDate = simpleDateFormat.parse(arr[0])
                        endDate = simpleDateFormat.parse(arr[1])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (startDate != null && endDate != null) {
                        val httpsURLConnection = URL("https://www.baidu.com").openConnection()
                        Thread {
                            try {
                                httpsURLConnection.connect()
                                httpsURLConnection.date.also {
                                    if (startDate.time <= it && it <= endDate.time) {
                                        handler.sendEmptyMessage(1002)
                                    } else {
                                        handler.sendMessage(Message.obtain().apply {
                                            what = 1001
                                            obj = decryptKey
                                        })
                                    }

                                }
                            } catch (e: IOException) {
                                //e.printStackTrace()
                                handler.sendEmptyMessage(1000)
                            }

                        }.start()
                    } else {
                        handler.sendEmptyMessage(1004)
                    }

                }

            } else {
                handler.sendEmptyMessage(1004)
            }
        } else {
            handler.sendEmptyMessage(1003)
        }


    }
}