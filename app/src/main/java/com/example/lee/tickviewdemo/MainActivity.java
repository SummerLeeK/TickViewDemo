package com.example.lee.tickviewdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private TickView tickView;
    private int progress = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress += 1;
            tickView.setCurrentProgress(progress);
            handler.sendEmptyMessageDelayed(0, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tickView = (TickView) findViewById(R.id.tick);


        handler.sendEmptyMessageDelayed(0, 100);
    }
}
