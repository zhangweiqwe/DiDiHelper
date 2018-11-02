package cn.zr.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.zr.BaseApplication

open class BaseAppCompatActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as BaseApplication).add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        (applicationContext as BaseApplication).remove(this)
    }
}