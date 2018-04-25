package com.yan.lsn24_eventbus.bus

import android.util.Log

/**
 *  @author      : yan
 *  @date        : 2018/4/25 12:34
 *  @description : 事件总线入口类
 */
class DNBus private constructor() {

    protected val TAG = javaClass.simpleName

    companion object {

        @JvmStatic
        val instance by lazy { DNBus() }
    }

    /**
     * 对应对象的所有订阅函数，从tag-->函数，最小单位是tag
     * key：订阅者（函数）所在类对象，value：[订阅的标签、订阅者(Method)、(Method)参数]
     */
    private val methodCache = hashMapOf<Class<*>, List<SubscribeMethod>>()

    /**
     * 订阅集合
     * 发送事件的时候，通过key（标签）查找所有对应的订阅者
     * key：订阅的标签，value：[订阅者(函数所在的对象)、[订阅的标签、订阅者(Method)、(Method)参数]]
     */
    private val subscribeCache = hashMapOf<String, MutableList<Subscription>>()

    /**
     * 对应对象中所有需要回调的标签，方便注销
     * key：订阅者（函数）所在类对象，value：该类中所有的订阅标签
     */
    private val registerCache = hashMapOf<Class<*>, MutableList<String>>()

    /**
     * 注册
     */
    fun register(subscriber: Any) {
        val subscribeClass = subscriber.javaClass
        //找出类中所有被 Subscribe 修饰的函数
        //将其Method、Tag、执行函数需要的参数类型数组缓存
        val subscribeMethods = findSubscribe(subscribeClass)

        //为了方便注销
        var tags = registerCache[subscribeClass]
        if (tags == null) tags = arrayListOf()

        //加入注册集合    key：标签  value：对应标签的所有函数
        for (subscribeMethod in subscribeMethods) {
            val tag = subscribeMethod.tag
            if (tag !in tags) tags.add(tag)

            var subscriptions = subscribeCache[tag]
            if (subscriptions == null) {
                subscriptions = arrayListOf()
                subscribeCache[tag] = subscriptions
            }
            val newSubscription = Subscription(subscriber, subscribeMethod)
            subscriptions.add(newSubscription)
        }

        registerCache[subscribeClass] = tags
    }

    /**
     * 找到被Subscribe注解的函数，并记录缓存
     */
    private fun findSubscribe(subscribeClass: Class<Any>): List<SubscribeMethod> {
        //先在缓存中查找
        var subscribeMethods = methodCache[subscribeClass]

        if (subscribeMethods != null) return subscribeMethods

        //缓存中没有
        subscribeMethods = arrayListOf()
        //获取methods
        val methods = subscribeClass.declaredMethods
        for (method in methods) {
            val subscribe = method.getAnnotation(Subscribe::class.java)
            if (subscribe != null) {
                //获取注解参数tag
                val tags = subscribe.tag
                //获取方法参数类型
                val parameterTypes = method.parameterTypes
                for (tag in tags) {
                    //设置权限
                    method.isAccessible = true
                    subscribeMethods.add(SubscribeMethod(tag, method, parameterTypes))
                }
            }
        }
        methodCache[subscribeClass] = subscribeMethods

        return subscribeMethods
    }

    /**
     * 发送事件。
     * 匹配规则：首先匹配tag，然后是查找被Subscribe注解修饰的函数，参数个数、依次的类型必须与[params]相同才能匹配
     */
    fun post(tag: String, vararg params: Any) {
        val subscriptions = subscribeCache[tag]
        if (subscriptions == null) return

        outer@for (subscription in subscriptions) {
            //组装参数，执行函数
            val subscribeMethod = subscription.subscribeMethod
            val parameterClass = subscribeMethod.parameterClass
            if (parameterClass.size != params.size) {
                Log.d(TAG, "post(): 没找着参数匹配的, 继续找。。。")
                continue
            }
            //检查是否一一匹配
            inner@for (i in 0 until params.size) {
                val clazz = parameterClass[i]
                val javaObjectType = params[i]::class.javaObjectType
                val javaPrimitiveType = params[i]::class.javaPrimitiveType
                if (clazz != javaObjectType && clazz != javaPrimitiveType) {
                    //参数结构不匹配
                    Log.d(TAG, "post(): 参数结构不匹配")
                    break@outer
                }
            }

            try {
                subscribeMethod.method.invoke(subscription.subscribe, *params)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 反注册，防止内存泄漏
     * 先查找注册集合，获得该对象所有的订阅标签
     * 查找订阅集合，删除对应对象上的订阅者
     */
    fun unregister(subscriber: Any) {
        //找到改对象中所有的订阅标签
        val tags = registerCache.get(subscriber.javaClass)
        if (tags == null) return

        for (tag in tags) {
            //根据标签查找记录
            val subscriptions = subscribeCache.get(tag)
            if (subscriptions == null) return

            val iterator = subscriptions.iterator()
            while (iterator.hasNext()) {
                val subscription = iterator.next()
                //对象是同一个，则删除
                if (subscription.subscribe == subscriber) {

                    Log.d(TAG, "unregister(): 找到对象，移除反注册")
                    iterator.remove()
                }
            }
        }
    }
}