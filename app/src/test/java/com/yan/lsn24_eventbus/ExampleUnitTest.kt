package com.yan.lsn24_eventbus

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        val a: Int = 1


        val javaObjectType = a::class.javaObjectType
        val javaPrimitiveType = a::class.javaPrimitiveType

        val isJavaObj = String::class.java.isInstance(a)
        val isJavaPri = String::class.java.isInstance(a)

        println("isJavaObj: $isJavaObj, isJavaPri: $isJavaPri")
    }
}
