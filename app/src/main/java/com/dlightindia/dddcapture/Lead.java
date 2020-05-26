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
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.scribejava.core.model.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Lead extends AppCompatActivity implements View.OnClickListener, LocationListener {
     private EditText salesRepEmail;
    private EditText editTextSalesRepNAME;
     private EditText dealerShopName;
    private EditText dealerName;
     private EditText dealerPhoneNo;
    private EditText dealerCity;
    private EditText dealerAddress1;
    private EditText dealerAddress2;
     private EditText detailsDiscussion;
     private Spinner spinnerlead_cat;
    private Spinner salesRepRegion;
    private Spinner dealerState;
      private FirebaseAuth firebaseAuth;
    private TextView textViewLocation;
    private Button buttonSaveLead;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private LocationManager locationManager=null;
    private LinearLayout lin_product;
    private TextInputLayout input_shop;
    boolean resumeone_time=true;
     private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";
    //location
    String lattitude,longitude;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    // protected LocationManager locationManager;
    //protected LocationListener locationListener;textViewLocation
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    //protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lead);
        getSupportActionBar().setTitle("D.light Lead");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ButterKnife.bind(this);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        //Title of the form
        //Employee email
        salesRepEmail  = (EditText) findViewById(R.id.editTextSalesRepEmail);

        //Employee ID
        editTextSalesRepNAME = (EditText) findViewById(R.id.editTextSalesRepNAME);

        input_shop =  findViewById(R.id.input_shop);
        lin_product =  findViewById(R.id.lin_product);

        buttonSaveLead = (Button) findViewById(R.id.buttonSaveLead);
        buttonSaveLead.setOnClickListener(Lead.this);

        //Region
        salesRepRegion = (Spinner) findViewById(R.id.spinnerRegion);
        String[] regions = {"Select Region","North","East","West & South"};
         ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,regions);
        salesRepRegion.setAdapter(adapter);

        //Lead Cat
        spinnerlead_cat = (Spinner) findViewById(R.id.spinnerlead_cat);
       final   String[] lead_cate_arr = {"Select Lead Category","Dealer","Customer"};
        ArrayAdapter<String> adapterr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,lead_cate_arr);
        spinnerlead_cat.setAdapter(adapterr);
        spinnerlead_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(lead_cate_arr[i].equalsIgnoreCase("Dealer")){
                    input_shop.setVisibility(View.VISIBLE);
                    lin_product.setVisibility(View.GONE);
                }
               else if(lead_cate_arr[i].equalsIgnoreCase("Customer")){
                    input_shop.setVisibility(View.GONE);
                    lin_product.setVisibility(View.VISIBLE);
                }
            }
             @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //State
        dealerState = (Spinner) findViewById(R.id.spinnerState);
        String[] states_of_india = {"Select State","Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Chattisgarh","Goa","Gujarat","Haryana",
                "Himachal Pradesh", "Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya",
                "Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telagana","Tripura","Uttaranchal","Uttar Pradesh",
                "West Bengal"};
        ArrayAdapter<String> adapterState = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,states_of_india);
        dealerState.setAdapter(adapterState);

        //Shop name
        dealerShopName = (EditText) findViewById(R.id.editTextDealerShopName);

        //Owner name
        dealerName = (EditText) findViewById(R.id.edit_dealer_cust);

        //dealing with which brands

        //Phone number
        dealerPhoneNo = (EditText) findViewById(R.id.editTextContactNo);

        //Address
        dealerAddress1 = (EditText) findViewById(R.id.editTextDealerAddress1);
        dealerAddress2 = (EditText) findViewById(R.id.editTextDealerAddress2);

        //City
        dealerCity = (EditText) findViewById(R.id.editTextCityName);


        //Pincode
        //dealerPincode = (EditText) findViewById(R.id.editPincode);

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


        progressDialog = new ProgressDialog(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
        //startLocationUpdates();

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
                                ActivityCompat.requestPermissions(Lead.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
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
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
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
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                textViewLocation.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                        + "\n" + "Longitude = " + longitude + "\nLocation Name : "+getLocationName(latti,longi));

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                textViewLocation.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
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
        resumeone_time=false;
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
        Toast.makeText(Lead.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
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
           /* else if (TextUtils.isEmpty(editTextSalesRepNAME.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter your employee name",Toast.LENGTH_LONG).show();
            }*/

            /*else if(salesRepRegion.getSelectedItem().toString() == "SELECT REGION" )
            {
                Toast.makeText(this,"Please select employee region"+salesRepRegion.getSelectedItem().toString().trim(),Toast.LENGTH_LONG).show();
            }*/
            else if(spinnerlead_cat.getSelectedItem().toString() == "Select Lead Category" )
            {
                Toast.makeText(this,"Please select Lead Category",Toast.LENGTH_LONG).show();
            }
           /* else if (TextUtils.isEmpty(dealerShopName.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's name",Toast.LENGTH_LONG).show();
            }*/

          /*  else if (TextUtils.isEmpty(dealerName.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter proprietor/owner's name",Toast.LENGTH_LONG).show();
            }*/


            else if (TextUtils.isEmpty(dealerPhoneNo.getText().toString().trim()) || TextUtils.getTrimmedLength(dealerPhoneNo.getText().toString())<10)

            {
                Toast.makeText(this,"Please enter dealer's phone number. It can't be less than 10 digits.",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(dealerCity.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's city",Toast.LENGTH_LONG).show();
            }
           /* else if (TextUtils.isEmpty(dealerAddress1.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's address 1",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(dealerAddress2.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's address 2",Toast.LENGTH_LONG).show();
            }*/

           /* else if (dealerState.getSelectedItem().toString().trim()=="Select State")
            {
                Toast.makeText(this,"Please select state",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(dealerPincode.getText().toString().trim()) || TextUtils.getTrimmedLength(dealerPincode.getText())!=6)
            {
                Toast.makeText(this,"Please enter dealer's pincode. It should be of 6 digits.",Toast.LENGTH_LONG).show();
            }*/
            else if (dealerState.getSelectedItem().toString().trim()=="Select Visit Level")
            {
                Toast.makeText(this,"Please select visit level",Toast.LENGTH_LONG).show();
            }
           /* else if (TextUtils.isEmpty(detailsDiscussion.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter details of discussion",Toast.LENGTH_LONG).show();
            }*/
            else {
                if(lattitude!=null&&longitude!=null)
                    saveLead();
                else{
                    fun_fatchLocation();
                    Toast.makeText(this,getResources().getString(R.string.gps_check),Toast.LENGTH_LONG).show();
                }
            }
        }
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
    public void saveLead()
    {
        progressDialog.setMessage("Saving Lead! Please wait...");
        progressDialog.show();

        String str_salesRepEmail = salesRepEmail.getText().toString().trim();
        Log.d("Sparsh App","Sales Rep Email : "+str_salesRepEmail);
        String str_salesRepID = editTextSalesRepNAME.getText().toString().trim();
        String str_salesRepRegion = salesRepRegion.getSelectedItem().toString();
      /*  String str_salesRepRegionID="1";
        if (str_salesRepRegion=="North")
            str_salesRepRegionID="1";
       else if (str_salesRepRegion=="East")
            str_salesRepRegionID="2";
        else if (str_salesRepRegion=="West & South")
            str_salesRepRegionID="3";*/
        String str_salesRepRegionID="";


        String str_dealerShopName = dealerShopName.getText().toString().trim();
        String str_dealerName = dealerName.getText().toString().trim();


         String str_dealerPhone = dealerPhoneNo.getText().toString().trim();
        String str_address1 = dealerAddress1.getText().toString().trim();
        String str_address2 = dealerAddress2.getText().toString().trim();
        String str_dealerCity = dealerCity.getText().toString().trim();
        String str_dealerState = dealerState.getSelectedItem().toString();
        // String str_dealerPincode = dealerPincode.getText().toString().trim();

        final String payLoad = "{\r\n    \t\"operation\": \"create\",\r\n    \t\"recordtype\": \"lead\",\r\n    \t\"companyname\": \""+
                str_dealerShopName+"\",\r\n    \t\"custentity_sales_rep_id\":\""+""+"\",\r\n    \t\"custentity_sales_rep_region\": \""+""+"\",\r\n    \t\"custentity_owner_name\": \""+str_dealerName+"\",\r\n    \t\"leadsource\":\""+""+"\",\r\n    \t\"custentity_lead_status_dealer\": \""+""+"\",\r\n    \t\"custentity_dealer_type\":\""+"2"+"\",\r\n    \t\"custentity_current_dealer_brands\":\""+""+"\",\r\n    \t\"custentitylead_created_by\":\""+
                str_salesRepEmail+"\",\r\n    \t\"phone\": \""+str_dealerPhone+"\",\r\n    \t\"custentity3\":\""+""+"\",\r\n    \t\"custentitycustentity_created_from\": 1,\r\n    \t\"comments\": \""+""+"\",\r\n    \t\"note\": \""+
                ""+"\",\r\n    \t\"notetype\": 9,\r\n    \t\"custentitycust_longitude\":\""+longitude+
                "\",\r\n    \t\"custentitycust_lattitude\":\""+lattitude+"\",\r\n    \t\"shippingaddress\": [{\r\n    \t\t\"addr1\":\""+
                str_address1+"\",\r\n    \t\t\"addr2\":\""+str_address2+"\",\r\n    \t\t\"city\": \""+str_dealerCity+
                "\",\r\n    \t\t\"country\": \"India\",\r\n    \t\t\"state\": \""+str_dealerState+"\",\r\n    \t\t\"custrecord_address_longitude\":\""+
                longitude+"\",\r\n    \t\t\"custrecord_address_latitude\":\""+lattitude+"\",\r\n    \t\t\"zip\": \""+""+"\"\r\n    \t}]\r\n    }";

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
                                        alertDialogBuilder = new AlertDialog.Builder(Lead.this);


                                        alertDialogBuilder.setTitle("Lead Saved");
                                        alertDialogBuilder.setMessage("Your lead has been saved successfully with \nID - "+JSON_recordid+".");
                                        //alertDialog.setIcon(R.drawable.logo);
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Lead.this, LoginActivity.class));
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
                                        alertDialogBuilder = new AlertDialog.Builder(Lead.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your lead has not been saved. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Lead.this, LoginActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();


                                        Toast.makeText(Lead.this,"Error in saving lead. Please check your data again!",Toast.LENGTH_LONG).show();

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
                                alertDialogBuilder = new AlertDialog.Builder(Lead.this);

                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Lead.this, LoginActivity.class));
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
                            alertDialogBuilder = new AlertDialog.Builder(Lead.this);


                            alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err1);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Lead.this, LoginActivity.class));
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
