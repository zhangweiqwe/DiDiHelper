package cn.zr

import cn.zr.util.ConfigUtil
import java.text.SimpleDateFormat
import java.util.*

class AccessibilityServiceDiDiBean(var useCarTime: Date?=null, var iDistanceUsers: Float?=null, var usersDistanceDestination: Float?=null,
                                   var theStartingPoint: CharSequence?=null, val destination: CharSequence?=null){

    override fun toString(): String {
        return "AccessibilityServiceDiDiBean(useCarTime=${SimpleDateFormat(ConfigUtil.SIMPLE_DATA_FORMAT).format(useCarTime)}, iDistanceUsers=$iDistanceUsers, usersDistanceDestination=$usersDistanceDestination, theStartingPoint=$theStartingPoint, destination=$destination)"
    }
}

