package com.backend.callingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public String TAG = MainActivity.class.getSimpleName();
    String mRoomName = "muzammil";
    ConvoServicesInterface.PreBindResult mPreBindData = null;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private Callback<ConvoServicesInterface.PreBindResult> preBindResponseReceiver = new Callback<ConvoServicesInterface.PreBindResult>() {
        @Override
        public void onResponse(Call<ConvoServicesInterface.PreBindResult> call, Response<ConvoServicesInterface.PreBindResult> response) {
            MainActivity.this.mPreBindData = response.body();
            getConferenceMapping();
        }

        @Override
        public void onFailure(Call<ConvoServicesInterface.PreBindResult> call, Throwable t) {
            Log.d(TAG, "onFailure: ");
            //call.enqueue(preBindResponseReceiver);
        }
    };

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://meet.jit.si/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ConvoServicesInterface service = retrofit.create(ConvoServicesInterface.class);

    Retrofit retrofitApi = new Retrofit.Builder()
            .baseUrl("https://api.jitsi.net/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    ConvoServicesInterface serviceApi = retrofitApi.create(ConvoServicesInterface.class);
    private ConvoServicesInterface.MapperResponse mMapperResponse = null;

    SmackConnection mSmackConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Call<ConvoServicesInterface.PreBindResult> call = service.getPreBindForRoom(mRoomName);
        call.enqueue(preBindResponseReceiver);
    }

    private void getConferenceMapping() {
        Call<ConvoServicesInterface.MapperResponse> call = serviceApi.getConferenceMapper(mRoomName + "@conference.meet.jit.si");
        call.enqueue(new Callback<ConvoServicesInterface.MapperResponse>() {
            @Override
            public void onResponse(Call<ConvoServicesInterface.MapperResponse> call, Response<ConvoServicesInterface.MapperResponse> response) {
                Log.d(TAG, "onResponse: ");
                MainActivity.this.mMapperResponse = response.body();
                xmppConnection();
            }

            @Override
            public void onFailure(Call<ConvoServicesInterface.MapperResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    public void xmppConnection(){

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                mSmackConnection = new SmackConnection(mPreBindData,mMapperResponse);
                mSmackConnection.connect(mRoomName);
            }
        });
        thread.start();
    }
}
