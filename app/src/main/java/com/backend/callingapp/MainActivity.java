package com.backend.callingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.backend.callingapp.jingleExtension.ice.elements.BridgeSession;
import com.backend.callingapp.jingleExtension.ice.providers.BridgeSessionProvider;

import org.jivesoftware.smack.provider.ProviderManager;

public class MainActivity extends AppCompatActivity {

    public String TAG = MainActivity.class.getSimpleName();
    String mRoomName = "PakistanZindabad";

    SmackConnection mSmackConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProviderManager.addExtensionProvider(BridgeSession.ELEMENT, BridgeSession.NAMESPACE, new BridgeSessionProvider());


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
