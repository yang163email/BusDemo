package com.yan.lsn24_eventbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yan.lsn24_eventbus.bus.DNBus;
import com.yan.lsn24_eventbus.bus.Subscribe;

/**
 * @author : yan
 * @date : 2018/4/25 17:20
 * @description : 测试兼容性
 */
public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG1 = "SecondActivity1";
    private static final String TAG2 = "SecondActivity2";
    private static final String TAG3 = "SecondActivity3";
    private TextView mTvContent1;
    private TextView mTvContent2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DNBus.getInstance().register(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_send2).setOnClickListener(this);
        mTvContent1 = findViewById(R.id.tv_content);
        mTvContent2 = findViewById(R.id.tv_content2);

        TextView tvHead = findViewById(R.id.tv_head);
        tvHead.setText(getClass().getSimpleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                DNBus.getInstance().post(TAG1, 13, "zhangsan");
                DNBus.getInstance().post(TAG2, 14, "lisi");
                break;
            case R.id.btn_send2:
                DNBus.getInstance().post(TAG3);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DNBus.getInstance().unregister(this);
    }

    @Subscribe(tag = {TAG1, TAG2})
    public void event1(int age, String name) {
        CharSequence oldText = mTvContent1.getText();
        mTvContent1.setText(oldText + "--age: " + age + ", name: " + name);
    }

    @Subscribe(tag = {TAG3})
    private void event3() {
        mTvContent2.setText("事件2来了");
    }

}
