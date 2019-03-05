package com.zhao.eventbus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.zhao.libeventbus.EventBus
import com.zhao.libeventbus.annotation.Subscribe
import com.zhao.libeventbus.bean.ThreadMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val Tag : String = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        findViewById<TextView>(R.id.tv_content).setOnClickListener{
            val intent = Intent(this,SecondActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getMessage(msg:EventBean){
        Log.i(Tag,"${msg.name},${msg.phone},thread="+Thread.currentThread().name)
    }
}
