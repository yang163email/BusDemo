package com.yan.lsn24_eventbus.bus

import java.lang.reflect.Method

/**
 *  @author      : yan
 *  @date        : 2018/4/25 12:42
 *  @description : todo
 */
data class SubscribeMethod(
        val tag: String,
        val method: Method,
        val parameterClass: Array<Class<*>>)