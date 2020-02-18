package com.dlightindia.dddcapture;

import android.Manifest;
import android.content.Context;
import android.content.RestrictionEntry;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.github.scribejava.core.model.Response;
import android.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class SearchLeadActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, LocationListener  {

    private Button buttonSearchLead;
    private EditText editTextSearchLeadID;
    private TextView textViewSearchLeadIDResults;
    private TextView textViewUpdateInstructions;
    private TextView textViewPhone;
    private ProgressDialog progressDialog;
    private static final String REST_UPDATE_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=183&deploy=1";
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=182&deploy=1";
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private Spinner spinnerLeadStatusUpdate;
    private EditText editTextUpdateComments;
    private Button buttonUpdateLead;
    private String lead_internalid=null;
    private String str_leadid=null;
    private String str_companyname=null;
    private String str_ownername=null;
    private String str_visitLevel=null;
    private String str_leadStatus=null;
    private String str_billingAddress=null;
    private String str_phone=null;
    private String str_lattitude=null;
    private String str_longitude=null;
    private TextView textViewLocation;
    private boolean flagLocationAvailable=false;
    private LocationManager locationManager=null;
    private LocationListener locationListener=null;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    String lattitude,longitude;
    private Boolean mRequestingLocationUpdates;
    String provider;
    private static final int REQUEST_LOCATION = 1;

    @Override
    public boolean isVoiceInteraction() {
        return super.isVoiceInteraction();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lead);
        getSupportActionBar().setTitle("D.light Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewSearchLeadIDResults  = (TextView) findViewById(R.id.textViewLeadSearchResult);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);
       textViewLocation = (TextView) findViewById(R.id.textViewLocation);
       textViewLocation.setOnClickListener(SearchLeadActivity.this);
        textViewLocation.setPaintFlags(textViewLocation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        textViewUpdateInstructions = (TextView) findViewById(R.id.textViewInstructionsUpdate);

        editTextSearchLeadID  = (EditText) findViewById(R.id.editTextSearchLead);
        buttonSearchLead = (Button) findViewById(R.id.searchLead);
        buttonSearchLead.setOnClickListener(SearchLeadActivity.this);
        progressDialog = new ProgressDialog(this);

        spinnerLeadStatusUpdate = (Spinner) findViewById(R.id.spinnerLeadStatusUpdate);
        String[] lead_status={"Select Lead Status","Warm","Hot","Successfully Closed","Declined"};
        ArrayAdapter<String> adapterState = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,lead_status);
        spinnerLeadStatusUpdate.setAdapter(adapterState);


        editTextUpdateComments = (EditText)findViewById(R.id.editTextCommentsUpdate);


        buttonUpdateLead = (Button) findViewById(R.id.buttonUpdateLead);
        buttonUpdateLead.setOnClickListener(SearchLeadActivity.this);
        buttonUpdateLead.setOnFocusChangeListener(this);
        editTextUpdateComments.setOnFocusChangeListener(this);
        spinnerLeadStatusUpdate.setOnFocusChangeListener(this);
        buttonUpdateLead.setVisibility(View.GONE);
        editTextUpdateComments.setVisibility(View.GONE);
        spinnerLeadStatusUpdate.setVisibility(View.GONE);

    }

    public void onClick(View view) {
        //if Search lead is pressed
       // Toast.makeText(this,view.toString(),Toast.LENGTH_LONG).show();
        if(view == buttonSearchLead)
        {
            if (TextUtils.isEmpty(editTextSearchLeadID.getText().toString().trim()))
            {
                Toast.makeText(SearchLeadActivity.this ,"Please enter lead ID",Toast.LENGTH_LONG).show();
            }
            else
            {
                str_longitude = null;
                str_lattitude = null;
                flagLocationAvailable = false;
                searchLead();
            }
        }

        if (view == buttonUpdateLead)
        {
            if (TextUtils.isEmpty(editTextUpdateComments.getText().toString().trim()))
            {
                Toast.makeText(SearchLeadActivity.this,"Please enter details of discussion",Toast.LENGTH_LONG).show();
            }
            else
            {
                updateLead();
            }
        }

        if (view == textViewLocation)
        {
            double lat= Double.valueOf(str_lattitude);
            double lon = Double.valueOf(str_longitude);
            String label = "";
            String uriBegin = "geo:" + lat + "," + lon;
            String query = lat + "," + lon + "(" + label + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    public void searchLead()
    {
        progressDialog.setMessage("Searching Lead! Please wait...");
        progressDialog.show();

        String str_searchLeadID = editTextSearchLeadID.getText().toString().trim();

        final String payLoad = "{\r\n\t\"operation\": \"search\",\r\n\t\"recordtype\": \"lead\",\r\n\r\n\t\"filters\": [{\r\n \t\t\"entityid\": \""+str_searchLeadID+"\",\r\n \t\t}],\r\n \t\"columns\": [{\r\n\t\t\r\n\t\t\"entityid\": \"entityid\",\r\n\t\t\"companyname\":\"companyname\",\r\n\t\t\"custentity_owner_name\":\"custentity_owner_name\",\r\n\t\t\"custentity3\":\"custentity3\",\r\n\t\t\"custentity_lead_status_dealer\":\"custentity_lead_status_dealer\",\r\n\t\t\"custentitycustentity_created_from\":\"custentitycustentity_created_from\",\r\n\t\t\"custentity_dealer_type\":\"custentity_dealer_type\",\r\n\t\t\"billaddress\":\"billaddress\",\r\n\t\t\"phone\":\"phone\",\r\n\t\t\"custentitycust_lattitude\":\"custentitycust_lattitude\",\r\n\t\t\"custentitycust_longitude\":\"custentitycust_longitude\",\r\n\t\t\r\n\t\t\r\n\t}]\r\n\r\n}";

        final CallNetSuiteAPI callNetSuiteAPI = new CallNetSuiteAPI();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    Response response = callNetSuiteAPI.main(payLoad,REST_URL);
                    try {
                        final JSONObject responseJSONObbject = new JSONObject(response.getBody());
                        if (responseJSONObbject != null) {
                            final String JSON_message = responseJSONObbject.getString("Message").trim();

                            Log.d("Sparsh App", "Got it -" + JSON_message);

                            if (JSON_message.equalsIgnoreCase("success"))
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        //textViewSearchLeadIDResults.setText(responseJSONObbject.toString());


                                        showResults(responseJSONObbject);

                                    }
                                });

                            else if (JSON_message.equalsIgnoreCase("lead has not been generated."))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(SearchLeadActivity.this);

                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your lead ID was not found. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //startActivity(new Intent(SearchLeadActivity.this, SearchLeadActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();

                                    }
                                });
                            }

                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("Sparsh App", e.getMessage());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Sparsh App","Exception :"+e.getStackTrace());
                }
            }
        });

        thread.start();

    }


    public void showResults(JSONObject result)
    {
        try {
            if (result != null) {

                JSONObject json= (JSONObject) new JSONTokener(result.toString()).nextValue();

                JSONArray searchObject = json.getJSONArray("search_result");
                   JSONObject jObj = null;
                   JSONObject cObj = null;
                   //JSONArray colObj = null;
                for (int i=0; i<searchObject.length();i++) {
                    jObj = searchObject.getJSONObject(i);
                    lead_internalid = jObj.getString("id");
                    //Toast.makeText(SearchLeadActivity.this,"In show results : " + jObj.toString(),Toast.LENGTH_LONG).show();
                    cObj = jObj.getJSONObject("columns");

                    str_leadid = cObj.getString("entityid");
                    str_companyname = cObj.getString("companyname");
                    if (cObj.has("custentitycust_lattitude"))
                    {
                        str_lattitude = cObj.getString("custentitycust_lattitude");
                    }
                    if (cObj.has("custentitycust_longitude"))
                    {
                        str_longitude = cObj.getString("custentitycust_longitude");
                    }
                    str_ownername = cObj.getString("custentity_owner_name");
                    str_billingAddress = cObj.getString("billaddress");
                    str_phone = cObj.getString("phone");
                    JSONObject leadStatusObject = cObj.getJSONObject("custentity_lead_status_dealer");
                    str_leadStatus = leadStatusObject.getString("name");
                    int visitLevelsDone = 0;
                    if (cObj.has("custentity3")) {
                        JSONObject visitLevelObject = cObj.getJSONObject("custentity3");
                        str_visitLevel = visitLevelObject.getString("name");
                        Log.d("Sparsh App", "Visit Levels -" + str_visitLevel);

                        if (str_visitLevel.toLowerCase().equals("first"))
                            visitLevelsDone = 1;
                        if (str_visitLevel.toLowerCase().equals("second"))
                            visitLevelsDone = 2;
                        if (str_visitLevel.toLowerCase().equals("third"))
                            visitLevelsDone = 3;
                    }

                    String resultString = "";

                    resultString = "Below are the details:\n\nLead ID : " + str_leadid +
                            "\nDealer Name : " + str_companyname +
                            "\nOwner Name : " + str_ownername +
                            "\nVisits so far : " + visitLevelsDone +

                            "\nAddress :" + str_billingAddress;

                    textViewPhone.setText("Phone : " + str_phone);

                    if ((str_lattitude!=null) && (str_longitude!=null)) {
                        flagLocationAvailable = true;
                        textViewLocation.setText("Click here to see location in Map");
                    }
                    else
                    {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            buildAlertMessageNoGps();

                        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            getLocation();
                        }

                    }

                    textViewSearchLeadIDResults.setText(resultString);

                    spinnerLeadStatusUpdate.setSelection(((ArrayAdapter) spinnerLeadStatusUpdate.getAdapter()).getPosition(str_leadStatus));

                    if (spinnerLeadStatusUpdate.getSelectedItem().toString().equals("Successfully Closed")) {
                        textViewUpdateInstructions.setText("Lead has already been converted. This can't be updated.");
                    } else {
                        buttonUpdateLead.setVisibility(View.VISIBLE);
                        editTextUpdateComments.setVisibility(View.VISIBLE);
                        spinnerLeadStatusUpdate.setVisibility(View.VISIBLE);
                        spinnerLeadStatusUpdate.requestFocus();
                        spinnerLeadStatusUpdate.setPrompt("Lead Status");
                        textViewUpdateInstructions.setText("Please update lead status and provide discussion note below:");
                    }
                }

                }

        }
        catch(Exception e)
        {
            //Toast.makeText(SearchLeadActivity.this,"Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
            Log.d("Sparsh App Exception",e.getMessage());
        }
    }


    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed to access location")
                        .setMessage("This app needs permission to access your GPS location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(SearchLeadActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }
            return false;

        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, SearchLeadActivity.this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    void getLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                str_lattitude = String.valueOf(latti);
                str_longitude = String.valueOf(longi);

                textViewLocation.setText("Click here to see your current location in Map");
                //textViewLocation.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                 //       + "\n" + "Longitude = " + longitude + "\nLocation Name : "+getLocationName(latti,longi));

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                str_lattitude = String.valueOf(latti);
                str_longitude = String.valueOf(longi);

                textViewLocation.setText("Click here to see your current location in Map");
                //textViewLocation.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                //       + "\n" + "Longitude = " + longitude + "\nLocation Name : "+getLocationName(latti,longi));


            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                str_lattitude = String.valueOf(latti);
                str_longitude = String.valueOf(longi);

                textViewLocation.setText("Click here to see your current location in Map");
                //textViewLocation.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                //       + "\n" + "Longitude = " + longitude + "\nLocation Name : "+getLocationName(latti,longi));

            }else{

                Toast.makeText(this,"Unable to trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected String getLocationName(double latti, double longi)
    {
        /*
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        String fnialAddress="";//it is Geocoder
        StringBuilder builder = new StringBuilder();
        try {

            List<Address> address = geoCoder.getFromLocation(latti, longi, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            fnialAddress = builder.toString(); //This is the complete address.

        } catch (IOException e) {}
        catch (NullPointerException e) {}
        return fnialAddress;
        */

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        String locality="";
        try {
            addresses = gcd.getFromLocation(latti, longi, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            locality = addresses.get(0).getLocality();
        }
        return locality;


    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        textViewLocation.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(SearchLeadActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }



    public void updateLead()
    {
        //call api and pass on the values of id, comments, lead status
        //visit level should be auto-changed
        //Comments to be added to note&&&&&&&&&&&&&&&&&&&&&&&&&&&*************************UPDATEFROMHERE

        progressDialog.setMessage("Updating Lead! Please wait...");
        progressDialog.show();
        int visitLevel=1;
        int leadStatus=1;
        //check for internal id, lead status, visit level
        Log.d("Sparsh App - Updating","Visit Level - "+str_visitLevel+" Lead Status - "+str_leadStatus);
        if (str_visitLevel!=null)
        {
            if (str_visitLevel.equals("First"))
            {
                visitLevel = 2;
            }
            if (str_visitLevel.equals("Second"))
            {
                visitLevel = 3;
            }
            if (str_visitLevel.equals("Third"))
            {
                visitLevel = 3;
            }
            if (str_visitLevel.equals(""))
            {
                visitLevel=1;
            }

        }


            if (str_leadStatus.equals("Successfully Closed"))
            {

                Toast.makeText(this, "Lead has already been converted. Can't change status now.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }


        else if (lead_internalid==null) {
            Toast.makeText(this, "Lead Internal ID field empty", Toast.LENGTH_LONG).show();

        }
        else if (spinnerLeadStatusUpdate.getSelectedItem().toString().trim()=="Select Lead Status")
        {
            Toast.makeText(this,"Please select lead status",Toast.LENGTH_LONG).show();
        }
        else if (editTextUpdateComments.getText().toString().trim() == "")
            {
                Toast.makeText(this,"Please enter details of discussion",Toast.LENGTH_LONG).show();
            }

        else

        {


            if(spinnerLeadStatusUpdate.getSelectedItem().toString().trim()=="Hot")
                leadStatus = 1;
            if(spinnerLeadStatusUpdate.getSelectedItem().toString().trim()=="Warm")
                leadStatus = 2;
            if(spinnerLeadStatusUpdate.getSelectedItem().toString().trim()=="Successfully Closed")
                leadStatus = 3;
            if(spinnerLeadStatusUpdate.getSelectedItem().toString().trim()=="Declined")
                leadStatus = 4;

            //capture current location



        final String payLoad = "{\r\n\t\"operation\": \"update\",\r\n\t\"recordtype\": \"lead\",\r\n\t\"id\" : \""+
              lead_internalid+"\",\r\n\t\"custentity_lead_status_dealer\": "+leadStatus+",\r\n\t\"custentity3\": "+visitLevel+",\r\n\t\"note\": \""+editTextUpdateComments.getText().toString().replace('\n',' ').trim()+"\",\r\n\t\"notetype\": 9\r\n}";
        final String payLoad1 = "{\r\n\t\"operation\": \"update\",\r\n\t\"recordtype\": \"lead\",\r\n\t\"id\" : \""+
                lead_internalid+"\",\r\n\t\"custentity_lead_status_dealer\": "+leadStatus+",\r\n\t\"custentity3\": "+visitLevel+",\r\n\t\"note\": \""+editTextUpdateComments.getText().toString().replace('\n',' ').trim()+"\",\r\n\t\"notetype\": 9,\r\n\t\"custentitycust_lattitude\":\""+
                str_lattitude+"\",\r\n\t\"custentitycust_longitude\":\""+str_longitude+"\"\r\n}";

        final CallNetSuiteAPI callNetSuiteAPI = new CallNetSuiteAPI();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    Response response=null;
                    if (flagLocationAvailable == true) {
                        response = callNetSuiteAPI.main(payLoad, REST_UPDATE_URL);
                    }
                    if (flagLocationAvailable == false)
                    {
                        response = callNetSuiteAPI.main(payLoad1, REST_UPDATE_URL);
                    }
                    try {
                        final JSONObject responseJSONObbject = new JSONObject(response.getBody());
                        if (responseJSONObbject != null) {
                            final String JSON_message = responseJSONObbject.getString("Message").trim();

                            Log.d("Sparsh App", "Got it -" + JSON_message);

                            if (JSON_message.equalsIgnoreCase("success"))
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        //textViewSearchLeadIDResults.setText(responseJSONObbject.toString());
                                        //showResults(responseJSONObbject);

                                        alertDialogBuilder = new AlertDialog.Builder(SearchLeadActivity.this);


                                        alertDialogBuilder.setTitle("Success");
                                        alertDialogBuilder.setMessage("Your lead was updated successfully.");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(SearchLeadActivity.this, SearchLeadActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });


                            else if (JSON_message.equalsIgnoreCase("lead has not been generated."))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(SearchLeadActivity.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your lead was not updated. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //startActivity(new Intent(SearchLeadActivity.this, SearchLeadActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });
                            }


                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("Sparsh App", e.getMessage());
                        final String err = e.getMessage().toString().trim();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                alertDialogBuilder = new AlertDialog.Builder(SearchLeadActivity.this);


                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(SearchLeadActivity.this, LoginActivity.class));
                                    }
                                });
                                alertDialog = alertDialogBuilder.create();
                                alertDialog.show();


                                //Toast.makeText(AddNewLead.this,"Error in saving lead. Please check your data again!",Toast.LENGTH_LONG).show();

                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Sparsh App","Exception :"+e.getStackTrace());
                    Log.d("Sparsh App", e.getMessage());
                    final String err = e.getMessage().toString().trim();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            alertDialogBuilder = new AlertDialog.Builder(SearchLeadActivity.this);


                            alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(SearchLeadActivity.this, LoginActivity.class));
                                }
                            });
                            alertDialog = alertDialogBuilder.create();
                            alertDialog.show();


                            //Toast.makeText(AddNewLead.this,"Error in saving lead. Please check your data again!",Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });

        thread.start();


    }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus)
        {
            spinnerLeadStatusUpdate.setVisibility(View.VISIBLE);
            editTextUpdateComments.setVisibility(View.VISIBLE);
            buttonUpdateLead.setVisibility(View.VISIBLE);
        }
    }
}
