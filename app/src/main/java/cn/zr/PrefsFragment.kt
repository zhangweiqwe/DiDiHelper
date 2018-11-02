package cn.zr

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v14.preference.PreferenceFragment
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.preference.*
import android.support.v7.preference.internal.AbstractMultiSelectListPreference
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.*
import cn.zr.preferens.DistanceAlertDialog
import cn.zr.preferens.TimeQuantumAlertDialog
import java.text.SimpleDateFormat
import java.util.*

class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val TAG = "PrefsFragment"
        private const val REQUEST_CODE_DRAW_OVERLAYS_PERMISSION = 1000

    }


    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this)
    }


    private lateinit var configManager: ConfigManager;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        switchSuspensionWindow(configManager.isShowSuspensionWindow)
        PreferenceManager.getDefaultSharedPreferences(context).apply {
            registerOnSharedPreferenceChangeListener(this@PrefsFragment)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        configManager = ConfigManager.getInstance();
        setPreferencesFromResource(R.xml.preferences, p1)
        initSate("use_car_time_sate_key")

        initSate("i_distance_users_sate_key")
        initSate("user_distance_destination_state_key")

        initSate("the_starting_point_state_key")
        initSate("destination_state_key")

    }

    private fun initSate(sateKey: String) {
        (findPreference(sateKey) as ListPreference).apply {
            val value = preferenceManager.sharedPreferences.getString(sateKey, "0")
            entryValues.forEachIndexed { index, charSequence ->
                if (charSequence == value) {
                    summary = entries[index]
                    return@forEachIndexed
                }
            }
            setOnPreferenceChangeListener { preference, any ->
                entryValues.forEachIndexed { index, charSequence ->
                    if (charSequence == any) {
                        summary = entries[index]
                        return@forEachIndexed
                    }
                }
                true
            }
        }
    }


    override fun onDisplayPreferenceDialog(preference: Preference) {
        Log.d(TAG, "onDisplayPreferenceDialog")


        preference.also {
            when (it.key) {
                "use_car_time_key" -> {
                    TimeQuantumAlertDialog(context!!, preference, this).show()
                }
                "i_distance_users_key", "users_distance_destination_key" -> {
                    DistanceAlertDialog(context!!, preference, this).show()
                }
                else -> {
                    super.onDisplayPreferenceDialog(preference)
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences, p1: String) {
        Log.d(TAG,"onSharedPreferenceChanged:$p1")
        when (p1) {
            "is_show_suspension_window_key" -> {
                switchSuspensionWindow(p0.getBoolean(p1, false).apply {
                    configManager.isShowSuspensionWindow = this
                })
            }
            "use_car_time_sate_key" -> {
                configManager.useCarTimeSate = p0.getString(p1, "0")
            }

            "i_distance_users_sate_key" -> {
                configManager.iDistanceUsersSate = p0.getString(p1, "0")
            }
            "user_distance_destination_state_key" -> {
                configManager.userDistanceDestinationState = p0.getString(p1, "0")
            }

            "the_starting_point_state_key" -> {
                configManager.theStartingPointState = p0.getString(p1, "0")
            }
            "destination_state_key" -> {
                configManager.destinationState = p0.getString(p1, "0")
            }
        }



    }

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.apply {
            findViewById<View>(16908351)?.also {

                if(it is ViewGroup){
                    if(it.childCount>0){
                        it.getChildAt(0).also {
                            if(it is RecyclerView){
                                //it.setPadding(0, 0, 0, 0)
                            }
                        }

                    }
                }

            }
        }
    }*/

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