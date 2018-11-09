package cn.zr.didi

import android.content.Context
import android.support.v7.preference.PreferenceManager
import cn.zr.AccessibilityServiceDiDiBean
import cn.zr.util.ConfigUtil
import cn.zr.config.TimeQuantum
import cn.zr.util.LLog
import java.text.SimpleDateFormat
import java.util.ArrayList

class DidiConfigManager private constructor() {

    lateinit var useCarTime: TimeQuantum
    var useCarTimeSate: String = "0"

    var iDistanceUsers: Float = 0.0f
    var iDistanceUsersSate: String = "0"
    var usersDistanceDestination: Float = 0.0f
    var userDistanceDestinationState: String = "0"

    var theStartingPointKeywords: ArrayList<String>? = null
    var theStartingPointState: String = "0"
    var destinationKeywords: ArrayList<String>? = null
    var destinationState: String = "0"

    /*var farthestDrive: Double = 0.0
    var farthestDriveState: String = "0"*/

    var clickRandomDelay: Int = 20


    var isShowSuspensionWindow: Boolean = false


    fun check(accessibilityServiceDiDiBean: AccessibilityServiceDiDiBean): Boolean {
        if (
                accessibilityServiceDiDiBean.useCarTime == null ||
                accessibilityServiceDiDiBean.iDistanceUsers == null ||
                accessibilityServiceDiDiBean.usersDistanceDestination == null ||
                accessibilityServiceDiDiBean.theStartingPoint == null ||
                accessibilityServiceDiDiBean.destination == null
        ) {
            LLog.d(LOG_TAG, "check error")
            return false
        }



        accessibilityServiceDiDiBean.useCarTime?.apply {
            if (useCarTimeSate != "0") {
                if (!(useCarTime.startTime <= this && this <= useCarTime.endTime)) {
                    return false
                }
            }
        }


        accessibilityServiceDiDiBean.iDistanceUsers?.apply {
            if (iDistanceUsersSate != "0" && iDistanceUsers >= this) {
                return false
            }
        }
        accessibilityServiceDiDiBean.usersDistanceDestination?.apply {
            if (userDistanceDestinationState != "0" && usersDistanceDestination <= this) {
                return false
            }
        }



        accessibilityServiceDiDiBean.theStartingPoint?.apply {
            var contains = false
            theStartingPointKeywords?.forEach {
                if (this.contains(it)) {
                    contains = true
                    return@forEach
                }
            }
            when (theStartingPointState) {
                "2" -> {
                    if (!contains) {
                        return false
                    }
                }
                "1" -> {
                    if (contains) {
                        return false
                    }
                }
            }
        }
        accessibilityServiceDiDiBean.destination?.apply {
            var contains = false
            destinationKeywords?.forEach {
                if (this.contains(it)) {
                    contains = true
                    return@forEach
                }
            }
            when (destinationState) {
                "2" -> {
                    if (!contains) {
                        return false
                    }
                }
                "1" -> {
                    if (contains) {
                        return false
                    }
                }
            }
        }






        return true
    }

    override fun toString(): String {
        return "DidiConfigManager(useCarTime=$useCarTime, useCarTimeSate='$useCarTimeSate', iDistanceUsers=$iDistanceUsers, iDistanceUsersSate='$iDistanceUsersSate', usersDistanceDestination=$usersDistanceDestination, userDistanceDestinationState='$userDistanceDestinationState', theStartingPointKeywords=$theStartingPointKeywords, theStartingPointState='$theStartingPointState', destinationKeywords=$destinationKeywords, destinationState='$destinationState', clickRandomDelay=$clickRandomDelay, isShowSuspensionWindow=$isShowSuspensionWindow)"
    }


    companion object {

        private const val LOG_TAG = "DidiConfigManager"
        private val configManager = DidiConfigManager()


        fun init(context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val simpleDateFormat = SimpleDateFormat(ConfigUtil.SIMPLE_DATA_FORMAT)
            configManager.useCarTime = ConfigUtil.parseTimeQuantum(sharedPreferences.getString("use_car_time_key", "${simpleDateFormat.format(System.currentTimeMillis())}" +
                    "${ConfigUtil.SPLIT_FLAG}" +
                    "${simpleDateFormat.format(System.currentTimeMillis() + 0.3 * 60 * 60 * 1000)}"))!!
            configManager.useCarTimeSate = sharedPreferences.getString("use_car_time_sate_key", configManager.useCarTimeSate)

            configManager.iDistanceUsers = sharedPreferences.getString("i_distance_users_key", configManager.iDistanceUsers.toString()).toFloat()
            configManager.iDistanceUsersSate = sharedPreferences.getString("i_distance_users_sate_key", configManager.iDistanceUsersSate)
            configManager.usersDistanceDestination = sharedPreferences.getString("users_distance_destination_key", configManager.usersDistanceDestination.toString()).toFloat()
            configManager.userDistanceDestinationState = sharedPreferences.getString("user_distance_destination_state_key", configManager.userDistanceDestinationState)

            configManager.theStartingPointKeywords = ConfigUtil.getKeywords(sharedPreferences.getString("the_starting_point_keywords_key", null))
            configManager.theStartingPointState = sharedPreferences.getString("the_starting_point_state_key", "0")
            configManager.destinationKeywords = ConfigUtil.getKeywords(sharedPreferences.getString("destination_keywords_key", null))
            configManager.destinationState = sharedPreferences.getString("destination_state_key", configManager.destinationState)


            /* configManager.farthestDrive = sharedPreferences.getString("farthest_drive_key", "0").toDouble()
             configManager.farthestDriveState = sharedPreferences.getString("farthest_drive_sate_key", "0")*/

            configManager.clickRandomDelay = sharedPreferences.getInt("click_random_delay_key", configManager.clickRandomDelay)


            configManager.isShowSuspensionWindow = sharedPreferences.getBoolean("is_show_suspension_window_key", configManager.isShowSuspensionWindow)

        }

        fun getInstance(): DidiConfigManager {
            return configManager
        }


    }
}