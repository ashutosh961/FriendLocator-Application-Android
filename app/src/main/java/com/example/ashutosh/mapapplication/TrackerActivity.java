 package com.example.ashutosh.mapapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Ashutosh on 22-11-2016.
 */

 import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class TrackerActivity extends Activity {
    SharedPreferences BTrack1,YouTrack1;
    List<String> li;
    List<String> li2;
    ListView list,list1;
  //  View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView textView = new TextView(getApplicationContext());
        TextView textView1 = new TextView(getApplicationContext());
        textView.setText("People Who you are Tracking " );

        textView.setTextColor(Color.BLACK);
        textView1.setTextColor(Color.BLACK);
        textView.setTextSize(15);
        textView1.setTextSize(15);

        // final Button show=(Button) findViewById(R.id.button1);
        //  final EditText et=(EditText) findViewById(R.id.editText1);
        list = (ListView) findViewById(R.id.listView1);
        list1 = (ListView) findViewById(R.id.listView3);
        list1.addHeaderView(textView1);
        list.addHeaderView(textView);
        SharedPreferences BTrack1 = getSharedPreferences("BTrack1", Context.MODE_PRIVATE);
        SharedPreferences YouTrack1 = getSharedPreferences("YouTrack1", Context.MODE_PRIVATE);
        Set<String> set = BTrack1.getStringSet("BTrack", null);
        Set<String> set1 = YouTrack1.getStringSet("YouTrack", null);
        List<String> BTrack = new ArrayList<String>(set);
        List<String> YouTrack = new ArrayList<String>(set1);
        li = BTrack;
        li2 = YouTrack;

        System.out.println("Btrack Arraylist:" + BTrack);
        System.out.println("YouTrack Arraylist:" + YouTrack);

        ArrayAdapter<String> adp=new ArrayAdapter<String>
            (getApplicationContext(),R.layout.list,li);
     //   textView1.setText("People You are Tracking");
  //      ArrayAdapter<String> adp1=new ArrayAdapter<String>
    //            (getApplicationContext(), R.layout.list,li2);
        list.setAdapter(adp);
//        list1.setAdapter(adp1);

//        add();
    }
      /*  show.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                li.add(et.getText().toString());
                et.setText(null);

                add();
            }
        });
    }*/

    public void add()
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

      //  getMenuInflater().inflate(R.layout.activity_main, menu);
        return true;
    }
}