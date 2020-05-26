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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class Sales extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private EditText salesRepEmail;
    private EditText salesRepID;
     private EditText dealerName;
    private EditText dealingBrands;
    private EditText dealerPhoneNo;
    private EditText dealerCity;
    private EditText dealerAddress1;
    private EditText dealerAddress2;
    private EditText detailsDiscussion;
    private EditText edit_product,edit_product_qty,editAmount;
     private Spinner salesRepRegion;
    private Spinner dealerState,spinnerDis;
     private FirebaseAuth firebaseAuth;
    private TextView textViewLocation;
    private Button buttonSaveLead;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private LocationManager locationManager=null;
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";
    //location
    boolean resumeone_time=true;
    String lattitude,longitude;

    protected Context context;
     String type;

    private static final int REQUEST_LOCATION = 1;
    RadioButton radio_dealer,radio_customer,radio_lead,radio_sale;
    EditText edit_shopname,editother_dist;
    TextInputLayout txtinput_shopname,txt_inout_otherdist;
    String lead_or_sale="lead";
    String dealer_or_customer="customer";
    int district_select_pos=0;
    ArrayAdapter<String> dis_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales);
        getSupportActionBar().setTitle("d.light Sales");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type="NewLead";
        // ButterKnife.bind(this);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        //Title of the form

        //Employee email
        salesRepEmail  = (EditText) findViewById(R.id.editTextSalesRepEmail);

        //Employee ID
        salesRepID = (EditText) findViewById(R.id.editTextSalesRepID);
        edit_product = findViewById(R.id.edit_product);
        edit_product_qty = findViewById(R.id.edit_product_qty);
        editAmount = findViewById(R.id.editAmount);
        buttonSaveLead = (Button) findViewById(R.id.buttonSaveLead);
        buttonSaveLead.setOnClickListener(Sales.this);

        //Region
        salesRepRegion = (Spinner) findViewById(R.id.spinnerRegion);
        String[] regions = {"Select Region","North","East","West & South"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,regions);
        salesRepRegion.setAdapter(adapter);

        final String[] district_first = {"Select District"};
        final String[] district_of_up = {"Select District","Bahraich","Basti","Faizabad","Fatehpur","Gonda","Other"};
        final String[] district_of_odisha = {"Select District","Balangir","Jajapur","Kandhamal","Sambalpur","Sundargarh","Other"};
        final String[] district_of_bihar = {"Select District","Bhojpur","Katihar","Muzaffarpur","Pashchim Champaran","Patna","Purnia","Other"};

        spinnerDis = (Spinner) findViewById(R.id.spinnerDis);
        dis_adapter = new ArrayAdapter<String>(Sales.this, android.R.layout.simple_spinner_dropdown_item, district_first);
        spinnerDis.setAdapter(dis_adapter);
        spinnerDis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerDis.getSelectedItem().toString().trim()=="Other"){
                    txt_inout_otherdist.setVisibility(View.VISIBLE);
                }else{
                    txt_inout_otherdist.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         //State
        dealerState = (Spinner) findViewById(R.id.spinnerState);
        String[] states_of_india = {"Select State","Uttar Pradesh","Odisha","Bihar"};
        ArrayAdapter<String> adapterState = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,states_of_india);
        dealerState.setAdapter(adapterState);
        dealerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                district_select_pos=position;
                if (district_select_pos == 1) {
                  dis_adapter = new ArrayAdapter<String>(Sales.this, android.R.layout.simple_spinner_dropdown_item, district_of_up);

                } else if (district_select_pos == 2) {
                    dis_adapter = new ArrayAdapter<String>(Sales.this, android.R.layout.simple_spinner_dropdown_item, district_of_odisha);
                } else if (district_select_pos == 3) {
                    dis_adapter = new ArrayAdapter<String>(Sales.this, android.R.layout.simple_spinner_dropdown_item, district_of_bihar);
                }
                spinnerDis.setAdapter(dis_adapter);
            }
             @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Owner name
        dealerName = (EditText) findViewById(R.id.editTextDealerName);

        //dealing with which brands
        dealingBrands = (EditText) findViewById(R.id.editTextDealingInBrands);

        //Phone number
        dealerPhoneNo = (EditText) findViewById(R.id.editTextContactNo);

        //Address
        dealerAddress1 = (EditText) findViewById(R.id.editTextDealerAddress1);
        dealerAddress2 = (EditText) findViewById(R.id.editTextDealerAddress2);

        //City
        dealerCity = (EditText) findViewById(R.id.editTextCityName);

        edit_shopname = (EditText) findViewById(R.id.edit_shopname);
        txtinput_shopname = (TextInputLayout) findViewById(R.id.txtinput_shopname);
        editother_dist = (EditText) findViewById(R.id.editother_dist);
        txt_inout_otherdist = (TextInputLayout) findViewById(R.id.txt_inout_otherdist);

        //Pincode

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
        radio_dealer = (RadioButton) findViewById(R.id.radio_dealer);
        radio_dealer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dealer_or_customer="dealer";
                    txtinput_shopname.setVisibility(View.VISIBLE);

                }else{
                    dealer_or_customer="customer";
                    txtinput_shopname.setVisibility(View.GONE);

                 }

            }
        });
        radio_customer = (RadioButton) findViewById(R.id.radio_customer);

        radio_lead = (RadioButton) findViewById(R.id.radio_lead);
        radio_lead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lead_or_sale="lead";
                }

            }
        });
        radio_sale = (RadioButton) findViewById(R.id.radio_sale);
        radio_sale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lead_or_sale="sale";
                }
            }
        });

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
        Toast.makeText(Sales.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
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

            if (TextUtils.isEmpty(dealerName.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter customer/owner name",Toast.LENGTH_LONG).show();
            }
            else if (radio_dealer.isChecked())
            {
                if (TextUtils.isEmpty(edit_shopname.getText().toString().trim()))
                {
                    Toast.makeText(this,"Please enter shop name",Toast.LENGTH_LONG).show();

                } else{

                     if (TextUtils.isEmpty(dealerPhoneNo.getText().toString().trim()) || TextUtils.getTrimmedLength(dealerPhoneNo.getText().toString())<10)

                    {
                        Toast.makeText(this,"Please enter phone number. It can't be less than 10 digits.",Toast.LENGTH_LONG).show();
                    }


                    else if (TextUtils.isEmpty(dealerAddress1.getText().toString().trim()))
                    {
                        Toast.makeText(this,"Please enter address 1",Toast.LENGTH_LONG).show();
                    }

                      else if (TextUtils.isEmpty(dealerCity.getText().toString().trim()))
                      {
                          Toast.makeText(this,"Please enter city",Toast.LENGTH_LONG).show();
                      }
                    else if (dealerState.getSelectedItem().toString().trim()=="Select State")
                    {
                        Toast.makeText(this,"Please select state",Toast.LENGTH_LONG).show();
                    }
                    else if (spinnerDis.getSelectedItem().toString().trim()=="Select District")
                    {
                        Toast.makeText(this,"Please select district",Toast.LENGTH_LONG).show();
                    }
                    else if(spinnerDis.getSelectedItem().toString().trim() == "Other")
                    {
                        if (TextUtils.isEmpty(editother_dist.getText().toString().trim()))
                        {
                            Toast.makeText(this,"Please enter other district name",Toast.LENGTH_LONG).show();

                        }else{
                              if (TextUtils.isEmpty(edit_product.getText().toString().trim()))
                            {
                                Toast.makeText(this,"Please enter product",Toast.LENGTH_LONG).show();
                            }
                            else if (TextUtils.isEmpty(edit_product_qty.getText().toString().trim()))
                            {
                                Toast.makeText(this,"Please enter quantity",Toast.LENGTH_LONG).show();
                            }

                            else if (TextUtils.isEmpty(editAmount.getText().toString().trim()))
                            {
                                Toast.makeText(this,"Please enter amount",Toast.LENGTH_LONG).show();
                            }

                            else {
                                if(lattitude!=null&&longitude!=null)
                                    saveLead();
                                else   {
                                    fun_fatchLocation();
                                    Toast.makeText(this,getResources().getString(R.string.gps_check),Toast.LENGTH_LONG).show();
                                }

                            }


                        }
                    }
                    else if (TextUtils.isEmpty(edit_product.getText().toString().trim()))
                    {
                        Toast.makeText(this,"Please enter product",Toast.LENGTH_LONG).show();
                    }
                    else if (TextUtils.isEmpty(edit_product_qty.getText().toString().trim()))
                    {
                        Toast.makeText(this,"Please enter quantity",Toast.LENGTH_LONG).show();
                    }

                    else if (TextUtils.isEmpty(editAmount.getText().toString().trim()))
                    {
                        Toast.makeText(this,"Please enter amount",Toast.LENGTH_LONG).show();
                    }

                    else {
                        if(lattitude!=null&&longitude!=null)
                            saveLead();
                        else   {
                            fun_fatchLocation();
                            Toast.makeText(this,getResources().getString(R.string.gps_check),Toast.LENGTH_LONG).show();
                        }

                    }


                 }
             }

            else if (TextUtils.isEmpty(dealerName.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter customer/owner name",Toast.LENGTH_LONG).show();
            }


            else if (TextUtils.isEmpty(dealerPhoneNo.getText().toString().trim()) || TextUtils.getTrimmedLength(dealerPhoneNo.getText().toString())<10)

            {
                Toast.makeText(this,"Please enter phone number. It can't be less than 10 digits.",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(dealerAddress1.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter address 1",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(dealerCity.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter city",Toast.LENGTH_LONG).show();
            }
            else if (dealerState.getSelectedItem().toString().trim()=="Select State")
            {
                Toast.makeText(this,"Please select state",Toast.LENGTH_LONG).show();
            }
            else if (spinnerDis.getSelectedItem().toString().trim()=="Select District")
            {
                Toast.makeText(this,"Please select district",Toast.LENGTH_LONG).show();
            }
            else if(spinnerDis.getSelectedItem().toString().trim() == "Other")
            {
                if (TextUtils.isEmpty(editother_dist.getText().toString().trim()))
                {
                    Toast.makeText(this,"Please enter other district name",Toast.LENGTH_LONG).show();

                }else{
                    if (TextUtils.isEmpty(edit_product.getText().toString().trim()))
                    {
                        Toast.makeText(this,"Please enter product",Toast.LENGTH_LONG).show();
                    }
                    else if (TextUtils.isEmpty(edit_product_qty.getText().toString().trim()))
                    {
                        Toast.makeText(this,"Please enter quantity",Toast.LENGTH_LONG).show();
                    }

                    else if (TextUtils.isEmpty(editAmount.getText().toString().trim()))
                    {
                        Toast.makeText(this,"Please enter amount",Toast.LENGTH_LONG).show();
                    }

                    else {
                        if(lattitude!=null&&longitude!=null)
                            saveLead();
                        else   {
                            fun_fatchLocation();
                            Toast.makeText(this,getResources().getString(R.string.gps_check),Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }
            else if (TextUtils.isEmpty(edit_product.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter product",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(edit_product_qty.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter quantity",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(editAmount.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter amount",Toast.LENGTH_LONG).show();
            }

            else {
                if(lattitude!=null&&longitude!=null)
                    saveLead();
             else   {
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
        progressDialog.setMessage("Saving sales! Please wait...");
        progressDialog.show();
          //String str_salesRepID = salesRepID.getText().toString().trim();
        String str_salesRepRegion = salesRepRegion.getSelectedItem().toString();
        String str_salesRepRegionID="1";
        if (str_salesRepRegion=="North")
            str_salesRepRegionID="1";
        if (str_salesRepRegion=="East")
            str_salesRepRegionID="2";
        if (str_salesRepRegion=="West & South")
            str_salesRepRegionID="3";


         String str_dealerName = dealerName.getText().toString().trim();


        String str_dealerPhone = dealerPhoneNo.getText().toString().trim();
        String str_address1 = dealerAddress1.getText().toString().trim();
        String str_address2 = dealerAddress2.getText().toString().trim();
        String str_dealerCity = dealerCity.getText().toString().trim();
        String str_dealerState = dealerState.getSelectedItem().toString();
        String salesRepEmailStr = salesRepEmail.getText().toString();
        String remarks = detailsDiscussion.getText().toString();
        String shop_name="",district_name="";
        if(dealer_or_customer.equalsIgnoreCase("dealer")){
            shop_name=edit_shopname.getText().toString();
        }else{
            shop_name="";
        }
        if(spinnerDis.getSelectedItem().toString()=="Other"){

            district_name=editother_dist.getText().toString().trim();
        }else{
            district_name=spinnerDis.getSelectedItem().toString();
        }


        final String payLoad = "{\r\n    \t\"operation\": \"create\",\r\n    \t\"recordtype\": \"salesapp\",\r\n    \t\"name\":\""+""+"\",\r\n   \t\"dealername\": \""+str_dealerName+"\"," +
                "\r\n    \t\"email\":\""+salesRepEmailStr+"\",\r\n    \t\"sale_or_lead\": \""+lead_or_sale+"\",\r\n   \t\"product\": \""+edit_product.getText().toString()+"\"," +
                "\r\n    \t\"custentity_dealer_type\":\""+"2"+"\",\r\n    \t\"quantity\":\""+edit_product_qty.getText().toString()+"\"," +
                "\r\n    \t\"amount\":\""+ editAmount.getText().toString()+"\",\r\n    \t\"phone\": \""+str_dealerPhone+"\",\r\n    \t\"shop_name\":\""+shop_name+"\"," +
                "\r\n    \t\"custentitycustentity_created_from\": 1,\r\n    \t\"remarks\": \""+remarks+"\",\r\n    \t\"district_name\": \""+ district_name+"\"," +
                "\r\n    \t\"notetype\": 9,\r\n    \t\"geo\":\""+longitude+","+longitude+ "\",\r\n        \t\t\"address\":\""+ str_address1+"\"," +
                "\r\n    \t\t\"address2\":\""+str_address2+"\",\r\n    \t\t\"city\": \""+str_dealerCity+ "\",\r\n    \t\t\"country\": \"India\"," +
                "\r\n    \t\t\"state\": \""+str_dealerState+"\",\r\n    \t\t\"custrecord_address_longitude\":\""+ longitude+"\"," +
                "\r\n    \t\t\"custrecord_address_latitude\":\""+lattitude+"\",\r\n   \t\"type\": \""+type+"\",  \t\t\"dealer_or_customer\": \""+dealer_or_customer+"\"\r\n  }";

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

                            Log.d("Sparsh App", "sales ID -" + JSON_recordid);

                            if (JSON_message.equalsIgnoreCase("success"))
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(Sales.this);


                                        alertDialogBuilder.setTitle("sales Saved");
                                        alertDialogBuilder.setMessage("Your sales has been saved successfully with \nID - "+JSON_recordid+".");
                                        //alertDialog.setIcon(R.drawable.logo);
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Sales.this, LoginActivity.class));
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
                                        alertDialogBuilder = new AlertDialog.Builder(Sales.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your sales has not been saved. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Sales.this, LoginActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();


                                        Toast.makeText(Sales.this,"Error in saving sales. Please check your data again!",Toast.LENGTH_LONG).show();

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
                                alertDialogBuilder = new AlertDialog.Builder(Sales.this);


                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Sales.this, LoginActivity.class));
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
                            alertDialogBuilder = new AlertDialog.Builder(Sales.this);


                            alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err1);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Sales.this, LoginActivity.class));
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
