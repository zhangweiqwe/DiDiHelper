package cn.zr

import android.content.Context
import android.provider.Settings
import android.support.v7.preference.PreferenceManager
import cn.zr.config.TimeQuantum
import java.text.SimpleDateFormat
import java.util.ArrayList

class ConfigManager private constructor() {

    lateinit var useCarTime: TimeQuantum
    var useCarTimeSate: String = "0"

    var iDistanceUsers: Long = 0
    var iDistanceUsersSate: String = "0"
    var usersDistanceDestination: Long = 0
    var userDistanceDestinationState: String = "0"

    var theStartingPointKeywords: ArrayList<String>? = null
    var theStartingPointState: String = "0"
    var destinationKeywords: ArrayList<String>? = null
    var destinationState: String = "0"

    var isShowSuspensionWindow: Boolean = false


    companion object {
        private val configManager = ConfigManager()

        fun init(context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val simpleDateFormat = SimpleDateFormat(ConfigUtil.SIMPLE_DATA_FORMAT)
            configManager.useCarTime = ConfigUtil.parseTimeQuantum(sharedPreferences.getString("use_car_time_key", "${simpleDateFormat.format(System.currentTimeMillis())}" +
                    "${ConfigUtil.SPLIT_FLAG}" +
                    "${simpleDateFormat.format(System.currentTimeMillis() + 0.3 * 60 * 60 * 1000)}"))!!
            configManager.useCarTimeSate = sharedPreferences.getString("use_car_time_sate_key", "0")

            configManager.iDistanceUsers = sharedPreferences.getLong("i_distance_users_key", 0)
            configManager.iDistanceUsersSate = sharedPreferences.getString("i_distance_users_sate_key", "0")
            configManager.usersDistanceDestination = sharedPreferences.getLong("users_distance_destination_key", 0)
            configManager.userDistanceDestinationState = sharedPreferences.getString("user_distance_destination_state_key", "0")

            configManager.theStartingPointKeywords = ConfigUtil.getKeywords(sharedPreferences.getString("the_starting_point_keywords_key", null))
            configManager.theStartingPointState = sharedPreferences.getString("the_starting_point_state_key", "0")
            configManager.destinationKeywords = ConfigUtil.getKeywords(sharedPreferences.getString("destination_keywords_key", null))
            configManager.destinationState = sharedPreferences.getString("destination_state_key", "0")

            configManager.isShowSuspensionWindow = sharedPreferences.getBoolean("is_show_suspension_window_key", false)

        }

        fun getInstance(): ConfigManager {
            return configManager
        }


    }
}