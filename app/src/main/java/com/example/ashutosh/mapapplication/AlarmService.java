package com.example.ashutosh.mapapplication;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.firebase.client.Firebase;

import java.security.Provider;

/**
 * Created by Ashutosh on 01-09-2016.
 */
public class AlarmService extends Service {
    private Context context;
    private boolean isRunning;
    Thread backgroundThread;
    private static final String FIREBASE_URL3="https://mapapplication-141609.firebaseio.com/Notifications";
    private  Firebase  firebaseref3;
    public NotificationManager mManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


@Override
    public  void onCreate(){
    this.context=this;
    this.isRunning=false;
    this.backgroundThread=new Thread(mytask);
    System.out.println("Connecting Firebase\n");
    firebaseref3=new Firebase(FIREBASE_URL3);

}
  private Runnable  mytask=new Runnable()
  {
   public void run(){



   }


  };
    @Override

    public void onDestroy() {
        this.isRunning = false;
    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;



        
    }







}