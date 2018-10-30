package cn.zr

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import cn.zr.config.TimeQuantum
import java.lang.Exception
import java.text.SimpleDateFormat

class ConfigUtil {
    companion object {

        const val SPLIT_FLAG = "="
        const val SIMPLE_DATA_FORMAT = "yyyy-MM-dd HH:mm"


        private fun getKeywords(context: Context): ArrayList<String>? {
            return PreferenceManager.getDefaultSharedPreferences(context).let {
                it.getString("keywords", null)
            }?.let {
                it.split(" ")?.let {
                    ArrayList<String>().apply {

                        for (i in it) {
                            if (i.isNotEmpty()) {
                                add(i)
                            }
                        }
                    }

                }


            }
        }


        fun checkKeywords(context: Context, string: String?): Boolean {
            if (string == null) {
                return false
            }
            getKeywords(context)?.forEach {
                if (string.contains(it)) {
                    return true
                }
            }
            return false
        }

        fun parseTimeQuantum(timeQuantumStr: String): TimeQuantum? {
            timeQuantumStr.split(SPLIT_FLAG).also {
                if (it.size > 1) {
                    val simpleDateFormat = SimpleDateFormat(SIMPLE_DATA_FORMAT)
                    try {
                        val startTime = simpleDateFormat.parse(it[0])
                        val endTime = simpleDateFormat.parse(it[1])
                        return TimeQuantum(startTime, endTime)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LogManager.log(e.message)
                    }
                }
            }
            return null
        }

    }
}