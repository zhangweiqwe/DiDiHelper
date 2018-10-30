package cn.zr

import android.content.Context
import android.provider.Settings
import android.support.v7.preference.PreferenceManager
import cn.zr.config.TimeQuantum
import java.text.SimpleDateFormat

class ConfigManager private constructor() {

    lateinit var useCarTime: TimeQuantum
    var iDistanceUsers: Long = 0
    var usersDistanceDestination: Long = 0
    lateinit var theStartingPointKeywords: Array<String>
    lateinit var destinationKeywords: Array<String>

    var isShowSuspensionWindow: Boolean = true


    companion object {
        private val configManager = ConfigManager()

        fun init(context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val simpleDateFormat = SimpleDateFormat(ConfigUtil.SIMPLE_DATA_FORMAT)
            configManager.useCarTime = ConfigUtil.parseTimeQuantum(sharedPreferences.getString("use_car_time_key", "${simpleDateFormat.format(System.currentTimeMillis())}" +
                    "${ConfigUtil.SPLIT_FLAG}" +
                    "${simpleDateFormat.format(System.currentTimeMillis() + 24 * 60 * 60 * 1000)}"))

        }

        fun getInstance(): ConfigManager {
            return configManager
        }


    }
}