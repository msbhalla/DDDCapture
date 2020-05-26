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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;
import java.util.Locale;

public class SepRegister extends AppCompatActivity implements View.OnClickListener {

     private EditText edit_cust_name;
    private EditText edit_cust_mobno;
    private EditText edit_cust_add;
    private EditText edit_cust_city;
    private EditText edit_cust_pin;
    private EditText edit_cust_aadhar;
    private EditText edit_cust_district;
    private EditText edit_sep_emailid;
    private EditText edit_sep_area;
    private EditText edit_arcode;
     FirebaseAuth firebaseAuth;
    private Spinner spinner_state;
    private Spinner spinner_model;
     private Button buttonsubmit;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
     private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sep_registration);
        getSupportActionBar().setTitle("d.light SEP Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          edit_cust_name  = (EditText) findViewById(R.id.edit_cust_name);
        edit_cust_mobno  = (EditText) findViewById(R.id.edit_cust_mobno);
        edit_cust_add  = (EditText) findViewById(R.id.edit_cust_add);
        edit_cust_city  = (EditText) findViewById(R.id.edit_cust_city);
        edit_cust_pin  = (EditText) findViewById(R.id.edit_cust_pin);
        edit_cust_aadhar  = (EditText) findViewById(R.id.edit_cust_aadhar);
        edit_cust_district  = (EditText) findViewById(R.id.edit_cust_district);
        edit_sep_emailid  = (EditText) findViewById(R.id.edit_sep_emailid);
        edit_sep_area  = (EditText) findViewById(R.id.edit_sep_area);
        edit_arcode  = (EditText) findViewById(R.id.edit_arcode);

        spinner_state  =  (Spinner) findViewById(R.id.spinner_state);
        spinner_model  =  (Spinner) findViewById(R.id.spinner_model);
         buttonsubmit  = (Button) findViewById(R.id.buttonsubmit);
        buttonsubmit.setOnClickListener(this);
        String[] states_of_india = {"Select State","Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Chattisgarh","Goa","Gujarat","Haryana",
                "Himachal Pradesh", "Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya",
                "Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telagana","Tripura","Uttaranchal","Uttar Pradesh",
                "West Bengal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,states_of_india);
        spinner_state.setAdapter(adapter);

        String[] model = {"Select Model","Attached to Dealer/SD","Self working","Aggregator","Other"};
        ArrayAdapter<String> adapter_model = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,model);
        spinner_model.setAdapter(adapter_model);

        progressDialog = new ProgressDialog(this);


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.buttonsubmit){
            fun_chkValidation();
        }
    }

    private void fun_chkValidation(){

         if (TextUtils.isEmpty(edit_cust_name.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter SEP name",Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(edit_cust_mobno.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter SEP Mobile Number",Toast.LENGTH_LONG).show();
        }
         else if (edit_cust_mobno.getText().toString().trim().length()<10)
         {
             Toast.makeText(this,"Please enter 10 digit Mobile Number",Toast.LENGTH_LONG).show();
         }
         else if (TextUtils.isEmpty(edit_cust_aadhar.getText().toString().trim()))
         {
             Toast.makeText(this,"Please enter SEP Aadhar Number",Toast.LENGTH_LONG).show();
         }
         else if (edit_cust_aadhar.getText().toString().trim().length()<12)
         {
             Toast.makeText(this,"Please enter 12 digit Aadhar Number",Toast.LENGTH_LONG).show();
         }
         else if(spinner_state.getSelectedItem().toString() == "Select State" )
         {
             Toast.makeText(this,"Please Select State",Toast.LENGTH_LONG).show();
         }
         else if (TextUtils.isEmpty(edit_cust_district.getText().toString().trim()))
         {
             Toast.makeText(this,"Please enter SEP district",Toast.LENGTH_LONG).show();
         }

        else if (TextUtils.isEmpty(edit_cust_city.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter SEP city",Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(edit_cust_pin.getText().toString().trim()))
        {
            Toast.makeText(this,"Please enter SEP pincode",Toast.LENGTH_LONG).show();
        }
         else if (edit_cust_pin.getText().toString().trim().length()<6)
         {
             Toast.makeText(this,"Please enter 6 digit pin code",Toast.LENGTH_LONG).show();
         }
         else if (TextUtils.isEmpty(edit_cust_add.getText().toString().trim()))
         {
             Toast.makeText(this,"Please enter SEP Address",Toast.LENGTH_LONG).show();
         }

         else if (TextUtils.isEmpty(edit_sep_area.getText().toString().trim()))
         {
             Toast.makeText(this,"Please enter SEP Area covering",Toast.LENGTH_LONG).show();
         }
         else if(spinner_model.getSelectedItem().toString() == "Select Model" )
         {
             Toast.makeText(this,"Please Select Model",Toast.LENGTH_LONG).show();
         }
         else if(TextUtils.isEmpty(edit_arcode.getText().toString().trim()))
         {
             Toast.makeText(this,"Please enter Arcode",Toast.LENGTH_LONG).show();
         }
         else if (edit_cust_pin.getText().toString().trim().length()<6)
         {
             Toast.makeText(this,"Please enter 6 Arcode",Toast.LENGTH_LONG).show();
         }
         else{
             save_sepsales();
         }

    }
   public void save_sepsales()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
          progressDialog.setMessage("SEP registering! Please wait...");
        progressDialog.show();
         String userEmail = user.getEmail();
        String  cust_name = edit_cust_name.getText().toString().trim();
        String state=spinner_state.getSelectedItem().toString();
        String model=spinner_model.getSelectedItem().toString();
        String cust_mob = edit_cust_mobno.getText().toString().trim();
        String cust_address = edit_cust_add.getText().toString().trim();
        String cust_city = edit_cust_city.getText().toString().trim();
        String cust_pincode = edit_cust_pin.getText().toString().trim();
        String aadhar = edit_cust_aadhar.getText().toString().trim();
        String district = edit_cust_district.getText().toString().trim();
        String sep_email = edit_sep_emailid.getText().toString().trim();
        String area_covering = edit_sep_area.getText().toString().trim();
        String arcode = edit_arcode.getText().toString().trim();


        final String payLoad = "{\r\n    \t\"operation\": \"register\",\r\n    \t\"recordtype\": \"sep_register\",\r\n    \t\"custrecord_user_email_\": \""+
                userEmail+"\",\r\n    \t\"custrecord_sep_name\":\""+cust_name+"\",\r\n    \t\"custrecord_sep_aadhar\": \""+aadhar+"\",\r\n    " +
                "\t\"custrecord_sep_mobile\": \""+cust_mob+"\",\r\n    \t\"custrecord_sep_address\":\""+cust_address+"\",\r\n    \t\"custrecord_sep_city\": \""+cust_city+"\",\r\n  " +
                "\t\"customer_pincode\":\""+cust_pincode+"\",\r\n" +
                "\t\"custrecord_sep_district\": \""+district+"\",\r\n    \t\"custrecord_sep_pindoe\":\""+cust_pincode+"\",\r\n    \t\"custrecord_sep_email\":\""+sep_email+"\",\r\n " +
                "\t\"custrecord_sep_area_covering\": \""+area_covering+"\",\r\n    \t\"custrecord_sep_state\": \""+state+"\",\r\n" +
                "\t\"custrecord_sep_model\": \""+model+"\",\r\n   \t\"custrecord_sep_arcode\":\""+arcode+"\"\r\n   }";

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
                                        alertDialogBuilder = new AlertDialog.Builder(SepRegister.this);


                                        alertDialogBuilder.setTitle("SEP Registration Saved");
                                        alertDialogBuilder.setMessage("SEP Registration has been saved successfully with \nID - "+JSON_recordid+".");
                                        //alertDialog.setIcon(R.drawable.logo);
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(SepRegister.this, LoginActivity.class));
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
                                        alertDialogBuilder = new AlertDialog.Builder(SepRegister.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("SEP Registration has not been saved. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(SepRegister.this, LoginActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();


                                        Toast.makeText(SepRegister.this,"Error in saving SEP Registration. Please check your data again!",Toast.LENGTH_LONG).show();

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
                                alertDialogBuilder = new AlertDialog.Builder(SepRegister.this);

                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("SEP Registration has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(SepRegister.this, LoginActivity.class));
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
                            alertDialogBuilder = new AlertDialog.Builder(SepRegister.this);


                            alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage("SEP Registration has not been saved. Error: "+err1);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(SepRegister.this, LoginActivity.class));
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
