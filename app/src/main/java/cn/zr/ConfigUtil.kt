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
        const val SPLIT_FLAG_BLANK_SPACE = " "
        const val SIMPLE_DATA_FORMAT = "yyyy-MM-dd HH:mm"


        fun getKeywords(string: String?): ArrayList<String>? {

            string?.trim()?.also {
                if (it.contains(SPLIT_FLAG_BLANK_SPACE)) {
                    it.split(SPLIT_FLAG_BLANK_SPACE).let {
                        return ArrayList<String>().apply {
                            for (i in it) {
                                i.trim().also {
                                    if (it.isNotEmpty()) {
                                        add(it)
                                    }
                                }

                            }
                        }

                    }
                } else {
                    if (it.isNotEmpty()) {
                        return arrayListOf(it)
                    }
                }

            }
            return null
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