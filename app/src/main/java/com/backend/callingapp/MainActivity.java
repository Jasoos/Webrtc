package com.backend.callingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public String TAG = MainActivity.class.getSimpleName();
    String mRoomName = "Pakistan";

    SmackConnection mSmackConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                mSmackConnection = new SmackConnection(mRoomName);
                mSmackConnection.connect();
            }
        });
        thread.start();
    }
}
