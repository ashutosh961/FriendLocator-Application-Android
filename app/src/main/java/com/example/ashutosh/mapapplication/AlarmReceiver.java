package com.example.ashutosh.mapapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ashutosh on 01-09-2016.
 */
public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent Service1=new Intent(context,AlarmService.class);
        context.startService(Service1);
    }
}
