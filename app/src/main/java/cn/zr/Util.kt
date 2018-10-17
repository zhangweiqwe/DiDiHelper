package cn.zr

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager

class Util {
    companion object {


        private fun getKeywords(context: Context): ArrayList<String>? {
            return PreferenceManager.getDefaultSharedPreferences(context).let {
                it.getString("keywords", null)
            }?.let {
                it.split(" ")?.let {
                    ArrayList<String>().apply {

                        for (i in it) {
                            if (i.isNotEmpty()) {
                                add(i)
                            }
                        }
                    }

                }


            }
        }


        fun checkKeywords(context: Context, string: String?): Boolean {
            if (string == null) {
                return false
            }
            getKeywords(context)?.forEach {
                if (string.contains(it)) {
                    return true
                }
            }
            return false
        }


    }
}