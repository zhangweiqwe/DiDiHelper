package cn.zr.didi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.SeekBarPreference
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import cn.zr.util.ConfigUtil
import cn.zr.R
import cn.zr.SuspensionWindow
import cn.zr.preferens.TimeQuantumAlertDialog
import cn.zr.util.DensityUtils
import cn.zr.util.LLog
import com.takisoft.fix.support.v7.preference.EditTextPreference
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import java.lang.StringBuilder
import java.text.SimpleDateFormat

class DidiPrefsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val LOG_TAG = "DidiPrefsFragment"
        private const val REQUEST_CODE_DRAW_OVERLAYS_PERMISSION = 1000
    }

    private val didiConfigManager = DidiConfigManager.getInstance();
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        findPreference("use_car_time_key").apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.useCarTime.also {
                    val simpleDateFormat = SimpleDateFormat(ConfigUtil.SIMPLE_DATA_FORMAT)
                    summary = "${simpleDateFormat.format(it.startTime)} ${getString(R.string.to)} ${simpleDateFormat.format(it.endTime)}"
                }
                true
            }
            callChangeListener(null)
        }
        (findPreference("use_car_time_sate_key") as ListPreference).apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.useCarTimeSate = any as String
                findPreference("use_car_time_key").isEnabled = any != "0"
                entryValues.forEachIndexed { index, charSequence ->
                    if (charSequence == any) {
                        summary = entries[index]
                        return@forEachIndexed
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, didiConfigManager.useCarTimeSate))
        }





        (findPreference("i_distance_users_key") as EditTextPreference).apply {
            //editText.inputType
            setOnPreferenceChangeListener { preference, any ->
                (any as String).also {
                    if (it.isNotEmpty()) {
                        didiConfigManager.iDistanceUsers = it.toFloat()
                        summary = it + getString(R.string.kilometer)
                    } else {
                        didiConfigManager.iDistanceUsers = 0f
                        summary = 0f.toString() + getString(R.string.kilometer)
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, didiConfigManager.iDistanceUsers.toString()))
        }
        (findPreference("i_distance_users_sate_key") as ListPreference).apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.iDistanceUsersSate = any as String
                findPreference("i_distance_users_key").isEnabled = any != "0"
                entryValues.forEachIndexed { index, charSequence ->
                    if (charSequence == any) {
                        summary = entries[index]
                        return@forEachIndexed
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, didiConfigManager.iDistanceUsersSate))
        }
        (findPreference("users_distance_destination_key") as EditTextPreference).apply {
            //editText.inputType
            setOnPreferenceChangeListener { preference, any ->
                (any as String).also {
                    if (it.isNotEmpty()) {
                        didiConfigManager.usersDistanceDestination = it.toFloat()
                        summary = it + getString(R.string.kilometer)
                    } else {
                        didiConfigManager.usersDistanceDestination = 0f
                        summary = 0f.toString() + getString(R.string.kilometer)
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, didiConfigManager.usersDistanceDestination.toString()))
        }
        (findPreference("user_distance_destination_state_key") as ListPreference).apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.userDistanceDestinationState = any as String
                findPreference("users_distance_destination_key").isEnabled = any != "0"
                entryValues.forEachIndexed { index, charSequence ->
                    if (charSequence == any) {
                        summary = entries[index]
                        return@forEachIndexed
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, didiConfigManager.userDistanceDestinationState))
        }





        findPreference("the_starting_point_keywords_key").apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.theStartingPointKeywords = ConfigUtil.getKeywords(any as? String)
                didiConfigManager.theStartingPointKeywords.also {
                    summary = if (it == null || it.isEmpty()) {
                        null
                    } else {
                        val sb = StringBuilder()
                        it.forEach {
                            sb.append(it + "\t")
                        }
                        sb.toString()
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, null))
        }
        (findPreference("the_starting_point_state_key") as ListPreference).apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.theStartingPointState = any as String
                findPreference("the_starting_point_keywords_key").isEnabled = any != "0"
                entryValues.forEachIndexed { index, charSequence ->
                    if (charSequence == any) {
                        summary = entries[index]
                        return@forEachIndexed
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, didiConfigManager.theStartingPointState))
        }
        findPreference("destination_keywords_key").apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.destinationKeywords = ConfigUtil.getKeywords(any as? String)

                didiConfigManager.destinationKeywords.also {
                    summary = if (it == null || it.isEmpty()) {
                        null
                    } else {
                        val sb = StringBuilder()
                        it.forEach {
                            sb.append(it + "\t")
                        }
                        sb.toString()
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, null))
        }
        (findPreference("destination_state_key") as ListPreference).apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.destinationState = any as String
                findPreference("destination_keywords_key").isEnabled = any != "0"
                entryValues.forEachIndexed { index, charSequence ->
                    if (charSequence == any) {
                        summary = entries[index]
                        return@forEachIndexed
                    }
                }
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getString(key, didiConfigManager.destinationState))
        }



        (findPreference("click_random_delay_key") as SeekBarPreference).apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.clickRandomDelay = (any as Int)
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getInt(key, didiConfigManager.clickRandomDelay))
        }

        (findPreference("is_show_suspension_window_key") as CheckBoxPreference).apply {
            setOnPreferenceChangeListener { preference, any ->
                didiConfigManager.isShowSuspensionWindow = any as Boolean
                switchSuspensionWindow(didiConfigManager.isShowSuspensionWindow)
                true
            }
            callChangeListener(preferenceManager.sharedPreferences.getBoolean(key, didiConfigManager.isShowSuspensionWindow))
        }
    }

    override fun onCreatePreferencesFix(p0: Bundle?, p1: String?) {
        Log.d(LOG_TAG, "onCreatePreferencesFix")
        //override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        setPreferencesFromResource(R.xml.preferences_didi, p1)
    }


    override fun onDisplayPreferenceDialog(preference: Preference) {
        preference.also {
            when (it.key) {
                "use_car_time_key" -> {
                    TimeQuantumAlertDialog(context!!, preference, this).show()
                }
                else -> {
                    super.onDisplayPreferenceDialog(preference)
                }
            }
        }
    }


    private fun switchSuspensionWindow(b: Boolean) {
        if (b) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context!!.packageName}")), REQUEST_CODE_DRAW_OVERLAYS_PERMISSION)
                } else {
                    SuspensionWindow.showSuspensionWindow(context)
                }
            } else {
                SuspensionWindow.showSuspensionWindow(context)
            }
        } else {
            SuspensionWindow.dismissSuspensionWindow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_DRAW_OVERLAYS_PERMISSION ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(context)) {
                        Toast.makeText(context, getString(R.string.no_suspension_windows_permission), Toast.LENGTH_SHORT).show()
                    } else {
                        SuspensionWindow.showSuspensionWindow(context)
                    }
                }
        }
    }
}