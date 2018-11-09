package cn.zr.activity

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.preference.EditTextPreferenceDialogFragmentCompat
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MenuItem
import android.view.accessibility.AccessibilityManager
import android.widget.*
import cn.zr.MyAccessibilityService
import cn.zr.Other
import cn.zr.R
import cn.zr.contentProviderPreference.RemotePreferences
import cn.zr.didi.DidiConfigManager
import cn.zr.util.*
import cn.zr.util.shell.Shell
import java.security.GeneralSecurityException

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.text.SimpleDateFormat

/**
 * adb shell uiautomator dump /data/local/tmp/app.uix
adb shell screencap -p /data/local/tmp/app.png


adb pull /data/local/tmp/app.uix d:/tmp/app.uix
adb pull /data/local/tmp/app.png d:/tmp/app.png
 *
 */
class MainActivity : BaseAppCompatActivity(), AccessibilityManager.AccessibilityStateChangeListener, CompoundButton.OnCheckedChangeListener
        , NavigationView.OnNavigationItemSelectedListener {


    private lateinit var mainSwitch: Switch

    private lateinit var accessibilityManager: AccessibilityManager
    private var isAccessibility: Boolean = false

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION_READ_PHONE_STATE -> {
                grantResults.also {
                    if (!it.isEmpty() && it[0] == PackageManager.PERMISSION_GRANTED) {
                        saveKey()
                    } else {
                        finish()
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        Other().getCallerProcessName(this)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            saveKey()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_CODE_PERMISSION_READ_PHONE_STATE)
            } else {
                finish()
            }
        }


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


        object : AsyncTask<Int, Int, Boolean>() {
            override fun doInBackground(vararg params: Int?): Boolean {
                return Shell.SU.run().isSuccessful//.SU.run().isSuccessful
            }

            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                if (!result) {
                    AlertDialog.Builder(this@MainActivity).setCancelable(false).setMessage(getString(R.string.please_grant_root_authority)).setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                    }.create().show()
                }
            }

        }.execute(null)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_share -> {
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, this@MainActivity.getString(R.string.app_name))
                    type = "text/plain"
                    startActivity(Intent.createChooser(this, resources.getText(R.string.app_name)))
                }
            }
            R.id.nav_send -> {
                LLog.shareLog(this)
            }
            R.id.nav_credit_card -> {
               /* var s:String? = null
                s!!.toInt()*/
               /* try {
                    s!!.toInt()
                }catch (e:Exception){
                   e.printStackTrace()
                }*/
                val prefs = RemotePreferences(this, "cn.zr.preferences", "main_prefs")
                val editText = EditText(this).apply {
                    setText(prefs.getString("key", null))
                }
                val margin = DensityUtils.dp2px(this, 26f)
                val frameLayout = FrameLayout(this).apply {
                    addView(editText.apply { layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply { setMargins(margin, margin, margin, margin) } })
                }
                AlertDialog.Builder(this).setTitle(getString(R.string.poll_code)).setView(frameLayout).setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                    val s0 = editText.text.toString()
                    if (s0.isEmpty()) {
                        return@setPositiveButton
                    }
                    prefs.edit().putString("key", s0).apply()

                    var decryptKey: String? = null
                    try {
                        decryptKey = AESCrypt.decrypt("_kankan", s0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LLog.d(LOG_TAG, e.message)
                    }

                    if (decryptKey != null) {
                        var encryptedMsg: String? = null
                        try {
                            encryptedMsg = AESCrypt.encrypt("youyou_", decryptKey)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            LLog.d(LOG_TAG, e.message)
                        }
                        encryptedMsg?.also {
                            prefs.edit().putString("key0", it).apply()
                        }
                    }


                }.setNeutralButton(getString(R.string.copy_device_id)) { dialog, which ->
                    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip = ClipData.newPlainText(getString(R.string.copy_device_id), Util.getDevicesTag(this))
                    Toast.makeText(this, getString(R.string.copy_success), Toast.LENGTH_SHORT).show()
                }.setNegativeButton(getString(R.string.cancel), null)
                        .create().show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onAccessibilityStateChanged(enabled: Boolean) {
        Log.d(LOG_TAG, "$enabled onAccessibilityStateChanged")
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

    }

    private fun saveKey() {
        val prefs = RemotePreferences(this, "cn.zr.preferences", "main_prefs")
        prefs.getString("key", null)?.also {
            if (it.isNotEmpty()) {
                return
            }
        }
        val message = "2018-11-7 16:09=2019-11-20 16:09=${Util.getDevicesTag(this)}"
        var encryptedMsg:String? = null
        try {
            encryptedMsg  = AESCrypt.encrypt("_kankanRoot", message)
        } catch (e: Exception) {
            e.printStackTrace()
            LLog.d(LOG_TAG, e.message)
        }
        encryptedMsg?.let {
            prefs.edit().putString("key", it).apply()
        }

    }

    private fun isStart(): Boolean {
        accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)?.also {
            for (i in it) {
                if (i.id == "$packageName/.MyAccessibilityService") {
                    Toast.makeText(this,"${i.packageNames.size}\t"+ i.packageNames[0], Toast.LENGTH_SHORT).show()
                    return true
                }
            }
        }
        return false
    }


    companion object {
        private const val LOG_TAG = "MainActivity"
        private const val REQUEST_CODE_PERMISSION_READ_PHONE_STATE = 1001
    }


}
