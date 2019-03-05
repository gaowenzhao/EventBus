package com.zhao.libeventbus.bean

import java.lang.reflect.Method

class SubscribeMethod {
    lateinit var method: Method
    lateinit var type:Class<*>
}