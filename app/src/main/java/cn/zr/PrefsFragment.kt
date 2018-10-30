package cn.zr

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import cn.zr.preferens.TimeQuantumAlertDialog
import java.text.SimpleDateFormat
import java.util.*

class PrefsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val TAG = "PrefsFragment"

    }

    private lateinit var configManager: ConfigManager;


    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        configManager = ConfigManager.getInstance();
        setPreferencesFromResource(R.xml.preferences, p1)

        val use_car_time_key = findPreference("use_car_time_key") as? EditTextPreference
        use_car_time_key?.also {

        }
    }


    override fun onDisplayPreferenceDialog(preference: Preference?) {
        //super.onDisplayPreferenceDialog(preference)
        Log.d(TAG, "onDisplayPreferenceDialog")


        preference?.also {
            when (it.key) {
                "use_car_time_key" -> {
                    TimeQuantumAlertDialog(context!!, preference, this).show()
                }
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
}