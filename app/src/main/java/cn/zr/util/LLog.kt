package cn.zr.util

import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.view.Window
import android.widget.ProgressBar
import cn.zr.R
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import android.widget.Toast
import cn.zr.CrashHandler
import java.lang.StringBuilder


class LLog {
    companion object {
        public const val SIMPLE_DATA_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒"
        private const val SHARE_LOG_TAG = "SHARE_LOG_TAG"
        private val DATA = ArrayList<String>()
        private val HANDLER = Handler(Looper.getMainLooper()) {
            when (it.what) {
                1000 -> {
                    DATA.add(it.obj as String)
                }
            }
            true
        }

        fun d(tag: String, string: String?) {
            StringBuilder().apply {
                append("\n")
                append(SimpleDateFormat(SIMPLE_DATA_FORMAT).format(Date()) + "\n")
                append("$tag $string\n")
                append("\n")

                HANDLER.sendMessage(Message.obtain().let {
                    it.what = 1000
                    it.obj = this.toString()
                    it
                })
            }
            Log.d(tag, "" + string)
        }

        fun saveRuntimeLog(context: Context):File?{
            synchronized(DATA){
                if (DATA.isEmpty()) {
                    DATA.add(context.getString(R.string.app_name)+"\n")
                }
                val sb = StringBuilder()
                DATA.forEachIndexed { index, s ->
                    sb.append(s)
                }
                sb.append("end"+"\n")
                return saveLog(context, CrashHandler.LOG_FILE_NAME, sb.toString().toByteArray(),false)
            }

            return null

        }

        fun saveLog(context: Context, name: String, byteArray: ByteArray,append:Boolean = false): File? {
            val file = File(context.filesDir, "$name").apply {
                LLog.d(SHARE_LOG_TAG, absolutePath)
            }
            val out = FileOutputStream(file,append);
            try {
                out.write(byteArray)
                out.flush()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            } finally {
                if (out != null) {
                    try {
                        out.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return file
        }

        fun shareLog(context: Context) {

            val files = ArrayList<Uri>()
            File(context.filesDir, CrashHandler.LOG_FILE_NAME)?.also {
                files.add(FileProvider.getUriForFile(context, "cn.zr.fileProvider", it))
            }
            if(files.isEmpty()){
                return
            }
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)//发送多个文件
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            intent.type = "*/*"//多个文件格式
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)//Intent.EXTRA_STREAM同于传输文件流
            context.startActivity(intent)


        }
    }
}