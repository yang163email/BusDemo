package com.yan.lsn24_eventbus.bus

/**
 *  @author      : yan
 *  @date        : 2018/4/25 12:25
 *  @description : todo
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val tag: Array<String>)