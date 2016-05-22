package com.example.sander.sunshine.service;


import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;

public class SunshineService extends IntentService {

    public SunshineService(){
        super("SunshineService");
    }
    private ArrayAdapter<String> mForecastAdapter;
    public static final String LOCATION_QUERY_EXTRA = "lqe";





    @Override
    protected void onHandleIntent(Intent intent) {


    }

    static public class AlarmReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            Intent sendintent=new Intent(context,SunshineService.class);
            sendintent.putExtra(LOCATION_QUERY_EXTRA,intent.getStringExtra(LOCATION_QUERY_EXTRA));
            context.startService(sendintent);


        }
    }
}

