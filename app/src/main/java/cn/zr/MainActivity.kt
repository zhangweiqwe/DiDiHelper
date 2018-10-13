package cn.zr

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import cn.zr.contentProviderPreference.RemotePreferences
import java.io.File


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, AccessibilityManager.AccessibilityStateChangeListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var remotePreferences: RemotePreferences
    private fun initPreferences() {
        remotePreferences = RemotePreferences(this@MainActivity, "cn.zr.preferences", "other_preferences")
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            registerOnSharedPreferenceChangeListener(this@MainActivity)
        }.edit().apply {
            "switch_preference".also {
                putBoolean(it, remotePreferences.getBoolean(it, false))
            }
        }.apply()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        remotePreferences.edit().also {
            when (key) {
                "switch_preference" -> {
                    switchSuspensionWindow(sharedPreferences.getBoolean(key, false))
                    it.putBoolean(key, sharedPreferences.getBoolean(key, false))
                }
            }

        }.apply()
    }

    private fun switchSuspensionWindow(b: Boolean) {
        if (b) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(applicationContext)) {
                    startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), REQUEST_CODE_DRAW_OVERLAYS_PERMISSION)
                } else {
                    showSuspensionWindow()
                }
            } else {
                showSuspensionWindow()
            }
        } else {
            dismissSuspensionWindow()
        }
    }

    private lateinit var mainSwitch: Switch

    private lateinit var accessibilityManager: AccessibilityManager
    private var isAccessibility: Boolean = false


    private fun getResult(): String {
        return "zrd"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getResult()
        Log.d(TAG, "onCreate")
        //Other().stringFromJNI()



        /*if (!File("zr/z").exists()) {
            ShellUtil.execCommand("mkdir zr\ncd zr\ntouch z\ncd .. \nchmod -R 777 zr", true)

        }*/


        accessibilityManager = (getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).apply {
            addAccessibilityStateChangeListener(this@MainActivity)
        }

        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            customView = Switch(this@MainActivity).apply {
                mainSwitch = this
                isAccessibility = isStart().apply {
                    isChecked = this
                }
                setOnCheckedChangeListener(this@MainActivity)
            }
        }


        initPreferences()
        switchSuspensionWindow(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("switch_preference", false))

    }


    override fun onAccessibilityStateChanged(enabled: Boolean) {
        Log.d(TAG, "$enabled onAccessibilityStateChanged")
        isAccessibility = enabled
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }


    override fun onResume() {
        super.onResume()
        mainSwitch.apply {
            setOnCheckedChangeListener(null)
            isChecked = isAccessibility
            setOnCheckedChangeListener(this@MainActivity)
        }
    }

    private fun isStart(): Boolean {
        Log.d(TAG, "isStart")
        for (i in accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)) {
            if (i.id == "$packageName/.MyAccessibilityService") {
                return true
            }
        }
        return false
    }


    private fun showSuspensionWindow() {
        if (suspensionWindow == null) {
            suspensionWindow = SuspensionWindow(this@MainActivity)
        }
        suspensionWindow!!.show()
    }

    private fun dismissSuspensionWindow() {
        if (suspensionWindow != null) {
            suspensionWindow!!.hide()
            suspensionWindow = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_DRAW_OVERLAYS_PERMISSION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(applicationContext)) {
                    Toast.makeText(this, getString(R.string.no_suspension_windows_permission), Toast.LENGTH_SHORT).show()
                } else {
                    showSuspensionWindow()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_DRAW_OVERLAYS_PERMISSION = 1000

        private var suspensionWindow: SuspensionWindow? = null
    }


}
