package com.dlightindia.dddcapture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.Activity;


import android.widget.TextView;
import android.os.Bundle;

import org.json.JSONException;

public class UpdateLead extends AppCompatActivity {




    TextView textView;
    //listview object
    ListView listView;

    //the hero list where we will store all the hero objects after parsing json
    List<Hero> heroList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_lead);

//initializing listview and hero list
        listView = (ListView) findViewById(R.id.listView);
        heroList = new ArrayList<>();

        textView = (TextView) findViewById(R.id.textViewUpdateLead);
        //this method will fetch and parse the data
        //loadHeroList();

        textView.setText("Coming Soon");
        PostNSwithLeadData();


    }

    private void PostNSwithLeadData()
    {




    }


    private void loadHeroList() {


    }


}
