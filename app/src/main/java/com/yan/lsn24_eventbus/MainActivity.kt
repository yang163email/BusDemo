package com.yan.lsn24_eventbus

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.yan.lsn24_eventbus.bus.DNBus
import com.yan.lsn24_eventbus.bus.Subscribe
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG1 = "MainActivity1"
        private const val TAG2 = "MainActivity2"
        private const val TAG3 = "MainActivity3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DNBus.instance.register(this)

        tv_head.text = javaClass.simpleName
        btn_send.setOnClickListener(this)
        btn_send2.setOnClickListener(this)
        btn_enter.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_send -> {
                DNBus.instance.post(TAG1, 12, "hello")
                DNBus.instance.post(TAG2, 13, "zhangsan")
            }
            btn_send2 -> DNBus.instance.post(TAG3)
            btn_enter -> {
                startActivity(Intent(this, SecondActivity::class.java))
            }
        }
    }

    @Subscribe(tag = [TAG1, TAG2])
    private fun receiveEvent(age: Int, name: String) {
        tv_content.text = "${tv_content.text} -- age: $age, name: $name"
    }

    @Subscribe(tag = [TAG3])
    fun event2() {
        tv_content2.text = "事件2来了"
    }

    override fun onDestroy() {
        super.onDestroy()
        DNBus.instance.unregister(this)
    }
}
