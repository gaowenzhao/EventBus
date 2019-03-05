package com.zhao.eventbus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.zhao.libeventbus.EventBus
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SecondActivity :AppCompatActivity(){
    val TAG = SecondActivity::class.java.simpleName
    private lateinit var mExecutors: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        mExecutors = Executors.newCachedThreadPool();
        findViewById<Button>(R.id.btn_send).setOnClickListener{
            var msg = EventBean("高文钊","12345678910")
             mExecutors.execute{
                Log.i(TAG,"thread=${Thread.currentThread().name}")
                EventBus.getDefault().post(msg)
            }

        }
    }
}