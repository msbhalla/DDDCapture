package com.dlightindia.dddcapture;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.scribejava.core.model.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MarketWorking extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private EditText salesRepEmail;
    private EditText salesRepID;
    private EditText dealerName;
    private EditText editGeo;
     private EditText dealerCity;
     private EditText detailsDiscussion;
     private EditText editDate;
    private EditText edit_mobile;
     private Spinner dealerState;
     private FirebaseAuth firebaseAuth;
    private TextView textViewLocation;
    private Button buttonSaveLead;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private LocationManager locationManager=null;
    boolean resumeone_time=true;
    String location_name="";
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";
    //location

    String lattitude,longitude;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    // protected LocationManager locationManager;
    //protected LocationListener locationListener;
    protected Context context;
    String provider;
    //protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_working);
        getSupportActionBar().setTitle("D.light Market Visit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ButterKnife.bind(this);

        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        //Title of the form

        //Employee email
        salesRepEmail  = (EditText) findViewById(R.id.editTextSalesRepEmail);
        editGeo  = (EditText) findViewById(R.id.editGeo);

        //Employee ID
        salesRepID = (EditText) findViewById(R.id.editTextSalesRepID);
        editDate = (EditText) findViewById(R.id.editDate);
        edit_mobile = (EditText) findViewById(R.id.edit_mobile);

        buttonSaveLead = (Button) findViewById(R.id.buttonSaveLead);
        buttonSaveLead.setOnClickListener(MarketWorking.this);

        //State
        dealerState = (Spinner) findViewById(R.id.spinnerState);
        String[] states_of_india = {"Select State","Arunachal Pradesh","Assam","Bihar","Haryana",
                "Jammu and Kashmir","Jharkhand","Karnataka","Maharashtra","Manipur","Meghalaya",
                "Mizoram","Nagaland","Odisha","Rajasthan","Sikkim","Tamil Nadu","Tripura","Uttaranchal","Uttar Pradesh",
                "West Bengal"};
        ArrayAdapter<String> adapterState = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,states_of_india);
        dealerState.setAdapter(adapterState);

        //Owner name
        dealerName = (EditText) findViewById(R.id.editTextDealerName);

        //City
        dealerCity = (EditText) findViewById(R.id.editTextCityName);

        //Discussion details
        detailsDiscussion = (EditText) findViewById(R.id.editTextDetails);

        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        salesRepEmail.setText(user.getEmail());
        salesRepEmail.setEnabled(false);
        salesRepID.setText(user.getDisplayName());


        progressDialog = new ProgressDialog(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

       /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }*/
        //startLocationUpdates();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        String currentDateandTime = sdf.format(new Date());
        editDate.setText(currentDateandTime);
        editDate.setEnabled(false);
        editGeo.setEnabled(false);


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                textViewLocation.setText("Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude + "\nLocation Name : "+getLocationName(latti,longi));

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                textViewLocation.setText("Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude+ "\nLocation Name : "+getLocationName(latti,longi));


            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                textViewLocation.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude + "\nLocation Name : "+getLocationName(latti,longi));

            }else{
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                 intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                 startActivity(intent);
                Toast.makeText(this,"Unable to trace your location, Please click on your current location",Toast.LENGTH_SHORT).show();

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

        try {
            addresses = gcd.getFromLocation(latti, longi, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            location_name = addresses.get(0).getLocality();
        }
        return location_name;

    }
 private void fun_fatchLocation(){
     locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         if(resumeone_time)
             buildAlertMessageNoGps();
     } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         getLocation();
     }
 }
    @Override
    protected void onResume() {
        super.onResume();
        fun_fatchLocation();

     }

    protected void buildAlertMessageNoGps() {
        resumeone_time=false;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        resumeone_time=true;

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
        Toast.makeText(MarketWorking.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    public void onClick(View view) {
        //if SaveLead is pressed
        if(view == buttonSaveLead){

            if (TextUtils.isEmpty(salesRepEmail.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter your email id",Toast.LENGTH_LONG).show();

            }
           /* else if (TextUtils.isEmpty(salesRepID.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter your employee id",Toast.LENGTH_LONG).show();
            }*/


            else if (TextUtils.isEmpty(dealerName.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter proprietor/owner's name",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(edit_mobile.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter Mobile Number",Toast.LENGTH_LONG).show();
            }
            else if (edit_mobile.getText().toString().trim().length()>10)
            {
                Toast.makeText(this,"Please enter correct Mobile Number",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(dealerCity.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's city",Toast.LENGTH_LONG).show();
            }

            else if (dealerState.getSelectedItem().toString().trim()=="Select State")
            {
                Toast.makeText(this,"Please select state",Toast.LENGTH_LONG).show();
            }

            else if (dealerState.getSelectedItem().toString().trim()=="Select Visit Level")
            {
                Toast.makeText(this,"Please select visit level",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(detailsDiscussion.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter details of discussion",Toast.LENGTH_LONG).show();
            }
             else {
                if(lattitude!=null&&longitude!=null)
                saveLead();
               else{
                   fun_fatchLocation();
                   Toast.makeText(this,getResources().getString(R.string.gps_check),Toast.LENGTH_LONG).show();
                }
                 //closing activity

                //starting login activity
//            startActivity(new Intent(this, LoginActivity.class));
            }
        }
    }

    public void saveLead()
    {
        progressDialog.setMessage(" Please wait...");
        progressDialog.show();

        String str_salesRepEmail = salesRepEmail.getText().toString().trim();
        Log.d("Sparsh App","Sales Rep Email : "+str_salesRepEmail);
       // String str_salesRepID = salesRepID.getText().toString().trim();


        String str_dealerName = dealerName.getText().toString().trim();
        String mobile_str = edit_mobile.getText().toString().trim();

        String str_dealerCity = dealerCity.getText().toString().trim();
        String str_dealerState = dealerState.getSelectedItem().toString();


        final String payLoad = "{\r\n    \t\"operation\": \"create\",\r\n    \t\"recordtype\": \"marketingapp\",\r\n    \t\"mobile\": \""+mobile_str+"\",\r\n   \t\"date\": \""+editDate.getText().toString()+"\",\r\n    \t\"dealername\": \""+str_dealerName+"\",\r\n    \t\"email\":\""+salesRepEmail.getText().toString()+"\",\r\n   \t\"remarks\": \""+detailsDiscussion.getText().toString()+"\",\r\n  \t\"notetype\": 9,\r\n    \t\"locationname\":\""+location_name+
                "\",\r\n        \t\t\"city\": \""+str_dealerCity+
                "\",\r\n    \t\t\"country\": \"India\",\r\n    \t\t\"state\": \""+str_dealerState+"\",\r\n    \t\t\"custrecord_market_working_long_app\":\""+
                longitude+"\",\r\n    \t\t\"custrecord_address_latitude_app\":\""+lattitude+"\",\r\n    \t\t\"zip\": \""+""+"\"\r\n  }";

        final CallNetSuiteAPI callNetSuiteAPI = new CallNetSuiteAPI();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    Response response = callNetSuiteAPI.main(payLoad,REST_URL);
                    try {
                        JSONObject responseJSONObbject = new JSONObject(response.getBody());
                        if (responseJSONObbject != null) {
                            final String JSON_message = responseJSONObbject.getString("Message").trim();

                            Log.d("Sparsh App", "Got it -" + JSON_message);
                            final String JSON_recordid = responseJSONObbject.getString("recordid").trim();

                            Log.d("Sparsh App", "Lead ID -" + JSON_recordid);

                            if (JSON_message.equalsIgnoreCase("success"))
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(MarketWorking.this);


                                        alertDialogBuilder.setTitle(" Saved");
                                        alertDialogBuilder.setMessage("Saved successfully with \nID - "+JSON_recordid+".");
                                        //alertDialog.setIcon(R.drawable.logo);
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(MarketWorking.this, LoginActivity.class));
                                                finish();
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                        //Toast.makeText(AddNewLead.this,"Lead saved successfully! ID is "+JSON_recordid,Toast.LENGTH_LONG).show();

                                    }
                                });


                            else if (JSON_message.equalsIgnoreCase("failed"))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(MarketWorking.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("has not been saved. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(MarketWorking.this, LoginActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();


                                        Toast.makeText(MarketWorking.this,"Error in saving Please check your data again!",Toast.LENGTH_LONG).show();

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
                                alertDialogBuilder = new AlertDialog.Builder(MarketWorking.this);


                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(MarketWorking.this, LoginActivity.class));
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
                    final String err1 = e.getMessage().toString().trim();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            alertDialogBuilder = new AlertDialog.Builder(MarketWorking.this);


                            alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage(" has not been saved. Error: "+err1);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MarketWorking.this, LoginActivity.class));
                                }
                            });
                            alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    });
                }
            }
        });

        thread.start();



    }



}
