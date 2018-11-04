package cn.zr.preferens

import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.text.InputType
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import cn.zr.ConfigManager
import cn.zr.DensityUtils
import cn.zr.PrefsFragment
import cn.zr.R

class DistanceAlertDialog(val context: Context, val preference: Preference, val prefsFragment: PrefsFragment) {

    companion object {
        private const val TAG = "TimeQuantumAlertDialog"
    }

    private lateinit var alertDialog: AlertDialog
    private val configManager = ConfigManager.getInstance()
    private lateinit var editText: EditText
    fun show() {
        AlertDialog.Builder(context!!).setTitle(preference.title).setView(EditText(context).apply {
            when (preference.key) {
                "i_distance_users_key" -> {
                    setText(configManager.iDistanceUsers.toString())
                }
                "users_distance_destination_key" -> {
                    setText(configManager.usersDistanceDestination.toString())
                }
                "farthest_drive_key" -> {
                    setText(configManager.farthestDrive.toString())
                }
            }
            inputType = InputType.TYPE_CLASS_NUMBER
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();
            editText = this
        }).apply {
            setNegativeButton(context!!.resources.getText(R.string.cancel), null).setPositiveButton(context!!.resources.getText(R.string.confirm), null)
        }
                .create().apply {
                    alertDialog = this
                }.show()

        requestInputMethod(alertDialog)
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            editText.text.toString().trim().also {
                if (it.isEmpty()) {
                } else {
                    prefsFragment.preferenceManager.sharedPreferences.edit().apply {

                        when (preference.key) {
                            "i_distance_users_key" -> {
                                putString(preference.key, it.apply { configManager.iDistanceUsers = this.toDouble() })
                                preference.callChangeListener(null)
                            }
                            "users_distance_destination_key" -> {
                                putString(preference.key, it.apply { configManager.usersDistanceDestination = this.toDouble() })
                                preference.callChangeListener(null)
                            }
                            "farthest_drive_key" -> {
                                putString(preference.key, it.apply { configManager.farthestDrive = this.toDouble() })
                                preference.callChangeListener(null)
                            }
                        }

                    }.apply()
                    Toast.makeText(context, context.getString(R.string.save_success), Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                }
            }
        }
    }


    private fun requestInputMethod(dialog: Dialog) {
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

}