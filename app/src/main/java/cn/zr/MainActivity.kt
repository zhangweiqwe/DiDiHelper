package cn.zr

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityManager
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import cn.zr.contentProviderPreference.RemotePreferenceAccessException
import cn.zr.contentProviderPreference.RemotePreferences
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.reflect.Modifier
import java.security.GeneralSecurityException


/**
 * adb shell uiautomator dump /data/local/tmp/app.uix
adb shell screencap -p /data/local/tmp/app.png


adb pull /data/local/tmp/app.uix d:/tmp/app.uix
adb pull /data/local/tmp/app.png d:/tmp/app.png
 *
 */
class MainActivity : AppCompatActivity(), AccessibilityManager.AccessibilityStateChangeListener, CompoundButton.OnCheckedChangeListener {


    private lateinit var mainSwitch: Switch

    private lateinit var accessibilityManager: AccessibilityManager
    private var isAccessibility: Boolean = false

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION_READ_PHONE_STATE -> {
                grantResults.also {
                    if (!it.isEmpty() && it[0] == PackageManager.PERMISSION_GRANTED) {
                        saveTag()
                    } else {
                        finish()
                    }
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun saveTag() {
        val prefs = RemotePreferences(this, "cn.zr.preferences", "main_prefs")
        prefs.getString("key", null).also {
            if (it != null && it.isNotEmpty()) {
                return
            }
        }
        val telephonyManager = (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val password = "password"
            val message = "${telephonyManager.imei}---2018-7-10 12:00=2018-12-22 12:00---cn.mm"
            try {
                val encryptedMsg = AESCrypt.encrypt(password, message)
                prefs.edit().putString("key", encryptedMsg).apply()
            } catch (e: GeneralSecurityException) {
                e.printStackTrace()
                //handle error
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            saveTag()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE_PERMISSION_READ_PHONE_STATE)
            } else {
                finish()
            }
        }



        Log.d(TAG, "onCreate")

        //Other().equals("")
        /*DebugHelper.init()
        DebugHelper.copyAssistFile(this, "zr.so", "arm64-v8a/")
        //DebugHelper.runAssistFile(this,"share.so","arm64-v8a/")
        //DebugHelper.copyAssistFile(this, "test.so", "arm64-v8a/")

        DebugHelper.initExec(this)
        DebugHelper.exec()

        bn.setOnClickListener {
            DebugHelper.exec()
        }

        bn0.setOnClickListener {
            DebugHelper.check()
        }*/
        //dx  --dex --output jar.dex dex.jar

        accessibilityManager = (getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).apply {
            addAccessibilityStateChangeListener(this@MainActivity)
        }


        Switch(this@MainActivity).apply {
            mainSwitch = this
            isAccessibility = isStart().apply {
                isChecked = this
            }
            setOnCheckedChangeListener(this@MainActivity)
        }
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            customView = mainSwitch
        }


    }


    override fun onAccessibilityStateChanged(enabled: Boolean) {
        Log.d(TAG, "$enabled onAccessibilityStateChanged")
        isAccessibility = enabled
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }


    override fun onResume() {
        super.onResume()
        mainSwitch.apply {
            setOnCheckedChangeListener(null)
            isChecked = isAccessibility
            setOnCheckedChangeListener(this@MainActivity)
        }
        isStart()


    }

    private fun isStart(): Boolean {
        Log.d(TAG, "isStart")
        accessibilityManager.apply {
            Log.d(TAG, "-->" + this.hashCode())
        }.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)?.also {
            for (i in it) {
                Log.d(TAG, "-->")
                Log.d(TAG, "-->" + i.id + "  " + i.packageNames[0])
                if (i.id == "$packageName/.MyAccessibilityService") {
                    Toast.makeText(this@MainActivity, i.packageNames[2], Toast.LENGTH_SHORT).show()
                    return true
                }

            }

        }

        return false
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_PERMISSION_READ_PHONE_STATE = 1001
    }


}
