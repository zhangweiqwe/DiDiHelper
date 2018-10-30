package cn.zr.preferens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import cn.zr.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class TimeQuantumAlertDialog(val context: Context, val preference: Preference, val prefsFragment: PrefsFragment) {

    companion object {
        const val SIMPLE_DATA_FORMAT_1 = "yyyy-MM-dd"
        const val SIMPLE_DATA_FORMAT_2 = "HH:mm"
        private const val TAG = "TimeQuantumAlertDialog"
    }

    private lateinit var startTimeTV: TextView
    private lateinit var startTime1TV: TextView
    private lateinit var endTimeTV: TextView
    private lateinit var endTime1TV: TextView
    private val configManager = ConfigManager.getInstance()
    fun show() {
        val timeQuantum = configManager.useCarTime
        val simpleDateFormat1 = SimpleDateFormat(SIMPLE_DATA_FORMAT_1)
        val simpleDateFormat2 = SimpleDateFormat(SIMPLE_DATA_FORMAT_2)
        val calendar = Calendar.getInstance().apply {
            time = timeQuantum.startTime
        }

        val calendar1 = Calendar.getInstance().apply {
            time = timeQuantum.endTime
        }


        //preferenceManager.sharedPreferences.getString("use_car_time_key", "2018-12-10 12:00" + ConfigUtil.SPLIT_FLAG + "2019-12-10 12:00")
        AlertDialog.Builder(context!!).setTitle(preference.title).setView(LayoutInflater.from(context).inflate(R.layout.view_time_quantum_select, null).apply {

            startTimeTV = findViewById<TextView>(R.id.startTimeTV);
            startTime1TV = findViewById<TextView>(R.id.startTime1TV);
            endTimeTV = findViewById<TextView>(R.id.endTimeTV);
            endTime1TV = findViewById<TextView>(R.id.endTime1TV);


            startTimeTV.also {
                it.text = simpleDateFormat1.format(timeQuantum.startTime)
                it.setOnClickListener {
                    DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        (it as TextView).also {
                            it.text = "$year-${month + 1}-$dayOfMonth"
                        }
                    }, calendar.get(Calendar.YEAR)
                            , calendar.get(Calendar.MONTH)
                            , calendar.get(Calendar.DAY_OF_MONTH)).apply {
                    }.show()
                }

            }
            startTime1TV.also {
                it.text = simpleDateFormat2.format(timeQuantum.startTime)
                it.setOnClickListener {
                    TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        (it as TextView).also {
                            it.text = "$hourOfDay:$minute"
                        }
                    }
                            , calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                }

            }


            endTimeTV.also {
                it.text = simpleDateFormat1.format(timeQuantum.endTime)
                it.setOnClickListener {
                    DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        (it as TextView).also {
                            it.text = "$year-${month + 1}-$dayOfMonth"
                        }
                    }, calendar1.get(Calendar.YEAR)
                            , calendar1.get(Calendar.MONTH)
                            , calendar1.get(Calendar.DAY_OF_MONTH)).apply {
                        //datePicker.minDate = simpleDateFormat1.parse(startTimeTV.text.toString()).time
                    }.show()
                }
            }
            endTime1TV.also {
                it.text = simpleDateFormat2.format(timeQuantum.endTime)
                it.setOnClickListener {
                    TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        (it as TextView).also {
                            it.text = "$hourOfDay:$minute"
                        }
                    }
                            , calendar1.get(Calendar.HOUR_OF_DAY), calendar1.get(Calendar.MINUTE), true).show()
                }
            }
        })
                .setNegativeButton(context!!.resources.getText(R.string.cancel), null).setPositiveButton(context!!.resources.getText(R.string.confirm), { dialog, which ->

                    val s = "${startTimeTV.text} ${startTime1TV.text}=${endTimeTV.text} ${endTime1TV.text}"
                    ConfigUtil.parseTimeQuantum(s).also {
                        if (it != null) {

                            if (it.startTime > it.endTime) {
                                Toast.makeText(context, context.getString(R.string.the_start_time_cannot_be_greater_than_the_end_time), Toast.LENGTH_SHORT).show()
                            } else {
                                configManager.useCarTime = it
                                prefsFragment.preferenceManager.sharedPreferences.edit().apply {
                                    putString(preference.key, s)
                                }.apply()
                                Toast.makeText(context, context.getString(R.string.save_success), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                }).show()
    }
}