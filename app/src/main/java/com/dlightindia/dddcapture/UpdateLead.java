package com.dlightindia.dddcapture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.*;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.scribejava.core.oauth.OAuthService;
import com.github.scribejava.core.services.TimestampServiceImpl;
import com.google.firebase.auth.OAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
        PostNSwithLeadData();


    }

    private void PostNSwithLeadData()
    {




    }


    private void loadHeroList() {


    }


}
