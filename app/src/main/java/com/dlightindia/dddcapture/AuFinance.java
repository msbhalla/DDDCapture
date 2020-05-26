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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

public class AuFinance extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private EditText salesRepEmail;
    private EditText edit_cust_name;
    private EditText edit_cust_mobno;
    private EditText edit_cust_add;
    private EditText edit_cust_city;
    private EditText edit_cust_pin;
    private EditText edit_product;
    private EditText edit_product2;
    private EditText edit_product3;
    private EditText edit_product_qty;
    private EditText edit_product_qty2;
    private EditText edit_product_qty3;
    private EditText edit_dealer_name;
    private EditText edit_dealer_mobile;
    private EditText edit_dealer_add;
    private EditText edit_dealer_city;
    private EditText edit_dealer_pincode;
    private EditText edit_dealer_state;
    private EditText edit_remarks;
    private EditText edit_leadstatus;
    private Spinner spinner_cust_type;
     private FirebaseAuth firebaseAuth;
    private TextView textViewLocation;
    private Button buttonsubmit;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private LocationManager locationManager=null;
     boolean resumeone_time=true;
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";
    //location
    String lattitude,longitude;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.au_finance);
        getSupportActionBar().setTitle("D.light Au  Finance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        salesRepEmail  = (EditText) findViewById(R.id.editTextSalesRepEmail);
        edit_cust_name  = (EditText) findViewById(R.id.edit_cust_name);
        edit_cust_mobno  = (EditText) findViewById(R.id.edit_cust_mobno);
        edit_cust_add  = (EditText) findViewById(R.id.edit_cust_add);
        edit_cust_city  = (EditText) findViewById(R.id.edit_cust_city);
        edit_cust_pin  = (EditText) findViewById(R.id.edit_cust_pin);
        edit_product  = (EditText) findViewById(R.id.edit_product);
        edit_product2  = (EditText) findViewById(R.id.edit_product2);
        edit_product3  = (EditText) findViewById(R.id.edit_product3);
        edit_product_qty  = (EditText) findViewById(R.id.edit_product_qty);
        edit_product_qty2  = (EditText) findViewById(R.id.edit_product_qty2);
        edit_product_qty3  = (EditText) findViewById(R.id.edit_product_qty3);
        edit_dealer_name  = (EditText) findViewById(R.id.edit_dealer_name);
        edit_dealer_mobile  = (EditText) findViewById(R.id.edit_dealer_mobile);
        edit_dealer_add  = (EditText) findViewById(R.id.edit_dealer_add);
        edit_dealer_city  = (EditText) findViewById(R.id.edit_dealer_city);
        edit_dealer_pincode  = (EditText) findViewById(R.id.edit_dealer_pincode);
        edit_dealer_state  = (EditText) findViewById(R.id.edit_dealer_state);
        edit_remarks  = (EditText) findViewById(R.id.edit_remarks);
        edit_leadstatus  = (EditText) findViewById(R.id.edit_leadstatus);
        spinner_cust_type  =  (Spinner) findViewById(R.id.spinner_cust_type);
        textViewLocation  = (TextView) findViewById(R.id.textViewLocation);
        buttonsubmit  = (Button) findViewById(R.id.buttonsubmit);
        buttonsubmit.setOnClickListener(this);
         String[] regions = {"Select Customer Type","Commercial","Household"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,regions);
        spinner_cust_type.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();
         //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        salesRepEmail.setText(user.getEmail());
        salesRepEmail.setEnabled(false);
        edit_leadstatus.setText("Open");
        edit_leadstatus.setEnabled(false);

        progressDialog = new ProgressDialog(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
     }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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

    @Override
    public void onLocationChanged(Location location) {
        textViewLocation.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onClick(View view) {
     if(view.getId()==R.id.buttonsubmit){
         fun_chkValidation();


     }
    }
    private void fun_chkValidation(){
        if (TextUtils.isEmpty(salesRepEmail.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter your email id",Toast.LENGTH_LONG).show();

        }
            else if (TextUtils.isEmpty(edit_cust_name.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter Customer name",Toast.LENGTH_LONG).show();
            }

             else if(spinner_cust_type.getSelectedItem().toString() == "Select Customer Type" )
            {
                Toast.makeText(this,"Please Select Customer Type",Toast.LENGTH_LONG).show();
            }

             else if (TextUtils.isEmpty(edit_cust_mobno.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter Customer Mobile Number",Toast.LENGTH_LONG).show();
            }

           else if (TextUtils.isEmpty(edit_cust_add.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter Customer Address",Toast.LENGTH_LONG).show();
            }
        else if (TextUtils.isEmpty(edit_cust_city.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter Customer city",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(edit_cust_pin.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter Customer pincode",Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(edit_product.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter product",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(edit_product_qty.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter quantity",Toast.LENGTH_LONG).show();
        }
         else if (TextUtils.isEmpty(edit_dealer_name.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter dealer name",Toast.LENGTH_LONG).show();
        }
             else if (TextUtils.isEmpty( edit_dealer_mobile.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's mobile",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(edit_dealer_add.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's address",Toast.LENGTH_LONG).show();
            }

        else if (TextUtils.isEmpty(edit_dealer_city.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter dealer's city",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(edit_dealer_pincode.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter dealer's pincode",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(edit_dealer_state.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter dealer's state",Toast.LENGTH_LONG).show();
        }
         else {
            if(lattitude!=null&&longitude!=null)
                saveAU_finance();
            else{
                fun_fatchLocation();
                Toast.makeText(this,getResources().getString(R.string.gps_check),Toast.LENGTH_LONG).show();
            }
        }
    }
    public void saveAU_finance()
    {
        progressDialog.setMessage("Saving Lead! Please wait...");
        progressDialog.show();
         String str_salesRepEmail = salesRepEmail.getText().toString().trim();
        String salesRepEmailS = salesRepEmail.getText().toString().trim();
        String  cust_name = edit_cust_name.getText().toString().trim();
        String cust_type=spinner_cust_type.getSelectedItem().toString();
        String cust_mob = edit_cust_mobno.getText().toString().trim();
        String cust_address = edit_cust_add.getText().toString().trim();
        String cust_city = edit_cust_city.getText().toString().trim();
        String cust_pincode = edit_cust_pin.getText().toString().trim();
        String product = edit_product.getText().toString().trim();
        String product2 = edit_product2.getText().toString().trim();
        String product3 = edit_product3.getText().toString().trim();
        String quntity = edit_product_qty.getText().toString().trim();
        String quntity2 = edit_product_qty2.getText().toString().trim();
        String quntity3 = edit_product_qty3.getText().toString().trim();
        String dealer_name = edit_dealer_name.getText().toString().trim();
        String dealer_mob = edit_dealer_mobile.getText().toString().trim();
        String dealer_city = edit_dealer_city.getText().toString().trim();
        String dealer_pincode = edit_dealer_pincode.getText().toString().trim();
        String dealer_state = edit_dealer_state.getText().toString().trim();
        String reamrks = edit_remarks.getText().toString().trim();
        String lead_status = edit_leadstatus.getText().toString().trim();

        final String payLoad = "{\r\n    \t\"operation\": \"create\",\r\n    \t\"recordtype\": \"au_finance\",\r\n    \t\"custrecord_emp_name\": \""+
                salesRepEmailS+"\",\r\n    \t\"custrecord_customer_name\":\""+cust_name+"\",\r\n    \t\"customer_type\": \""+cust_type+"\",\r\n    " +
                "\t\"customer_mobile\": \""+cust_mob+"\",\r\n    \t\"customer_address\":\""+cust_address+"\",\r\n    \t\"customer_city\": \""+cust_city+"\",\r\n  " +
                "\t\"customer_pincode\":\""+cust_pincode+"\",\r\n    \t\"product\":\""+product+"\",\r\n    \t\"prodduct2\":\""+product2+"\",\r\n" +
                "\t\"product3\": \""+product3+"\",\r\n    \t\"quntity\":\""+quntity+"\",\r\n    \t\"quntity2\":\""+quntity2+"\",\r\n " +
                "\t\"quntity3\": \""+quntity3+"\",\r\n    \t\"dealer_name\": \""+dealer_name+"\",\r\n" +
                "\t\"dealer_mob\": \""+dealer_mob+"\",\r\n    \t\"dealer_city\":\""+dealer_city+
                "\",\r\n    \t\"dealer_pincode\":\""+dealer_pincode+"\",\r\n     \t\"dealer_state\":\""+dealer_state+"\",\r\n  \t\"remarks\":\""+reamrks+"\",\r\n   \t\"lead_status\":\""+lead_status+"\"\r\n   }";

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

                             final String JSON_recordid = responseJSONObbject.getString("recordid").trim();

                            if (JSON_message.equalsIgnoreCase("success"))
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(AuFinance.this);


                                        alertDialogBuilder.setTitle("AuFinance Saved");
                                        alertDialogBuilder.setMessage("Your AuFinance has been saved successfully with \nID - "+JSON_recordid+".");
                                        //alertDialog.setIcon(R.drawable.logo);
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(AuFinance.this, LoginActivity.class));
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
                                        alertDialogBuilder = new AlertDialog.Builder(AuFinance.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your AuFinance has not been saved. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(AuFinance.this, LoginActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();


                                        Toast.makeText(AuFinance.this,"Error in saving AuFinance. Please check your data again!",Toast.LENGTH_LONG).show();

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
                                alertDialogBuilder = new AlertDialog.Builder(AuFinance.this);

                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(AuFinance.this, LoginActivity.class));
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
                            alertDialogBuilder = new AlertDialog.Builder(AuFinance.this);


                            alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err1);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(AuFinance.this, LoginActivity.class));
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
