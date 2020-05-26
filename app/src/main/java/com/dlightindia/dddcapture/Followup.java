package com.dlightindia.dddcapture;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dlightindia.dddcapture.databinding.SkuBinding;
import com.github.scribejava.core.model.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import constant.Constant;

public class Followup extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    String userEmail;
    RadioButton radio_interested,radio_notinterested,radio_purchased,radio_van,radio_dealer,radio_vle,radio_others;
    LinearLayout lin_prod_qty;
    EditText edit_cust_mobno,edit_product,edit_product_qty,editAmount,edit_reamrks;
    Button buttonSaveLead;
    String intersed_radio_value="";
    String van_realted_radio_value="";
    String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followup);
        getSupportActionBar().setTitle("d.light Followup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type="FollowUp";
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        radio_interested=findViewById(R.id.radio_interested);
        radio_notinterested=findViewById(R.id.radio_notinterested);
        radio_purchased=findViewById(R.id.radio_purchased);
        radio_van=findViewById(R.id.radio_van);
        radio_dealer=findViewById(R.id.radio_dealer);
        radio_vle=findViewById(R.id.radio_vle);
        radio_others=findViewById(R.id.radio_others);

        lin_prod_qty=findViewById(R.id.lin_prod_qty);
        edit_cust_mobno=findViewById(R.id.edit_cust_mobno);
        edit_product=findViewById(R.id.edit_product);
        edit_product_qty=findViewById(R.id.edit_product_qty);
        editAmount=findViewById(R.id.editAmount);
        edit_reamrks=findViewById(R.id.edit_reamrks);
        buttonSaveLead=findViewById(R.id.buttonSaveLead);


        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail=user.getEmail();
        buttonSaveLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!intersed_radio_value.equalsIgnoreCase("")){
                    validation();
                }else{
                    Constant.showToast(Followup.this,"Select category");
                }


            }
        });
        radio_interested.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lin_prod_qty.setVisibility(View.GONE);
                    intersed_radio_value="Interested";
                }
            }
        });
        radio_notinterested.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lin_prod_qty.setVisibility(View.GONE);
                    intersed_radio_value="Not Interested";
                }
            }
        });
        radio_purchased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lin_prod_qty.setVisibility(View.VISIBLE);
                    intersed_radio_value="Already Purchased";
                }
            }
        });

        radio_van.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                     van_realted_radio_value="VAN";
                }
            }
        });
        radio_dealer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                     van_realted_radio_value="Dealer";
                }
            }
        });
        radio_vle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                     van_realted_radio_value="VLE/SEP";
                }
            }
        });
        radio_others.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                     van_realted_radio_value="Others";
                }
            }
        });
     }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void validation(){

        if(lin_prod_qty.getVisibility()==View.VISIBLE){

            if(!edit_cust_mobno.getText().toString().trim().equalsIgnoreCase("")){
                if(edit_cust_mobno.getText().toString().trim().length()==10){
                if(!edit_product.getText().toString().trim().equalsIgnoreCase("")){
                    if(!edit_product_qty.getText().toString().trim().equalsIgnoreCase("")){
                        if(!editAmount.getText().toString().trim().equalsIgnoreCase("")){
                            if(!van_realted_radio_value.equalsIgnoreCase("")){
                                if(!edit_reamrks.getText().toString().trim().equalsIgnoreCase("")){

                                      saveLead();

                                }else{
                                    Constant.showToast(this,"Enter remarks");
                                }
                            }else{
                                Constant.showToast(this,"Select purchase from");
                            }
                        }else{
                            Constant.showToast(this,"Enter amount");
                        }


                    }else{
                        Constant.showToast(this,"Enter quantity");
                    }


                }else{
                    Constant.showToast(this,"Enter product");
                }
                }else{
                    Constant.showToast(this,"Enter correct mobile no.");
                }
            }else{
                Constant.showToast(this,"Enter mobile no.");
            }

        }else{

            if(!edit_cust_mobno.getText().toString().trim().equalsIgnoreCase("")){
                if(edit_cust_mobno.getText().toString().trim().length()==10){
                if(!edit_reamrks.getText().toString().trim().equalsIgnoreCase("")){

                    saveLead();

                }else{
                    Constant.showToast(this,"Enter remarks");
                }
                }else{
                    Constant.showToast(this,"Enter correct mobile no.");
                }
             }else{
                Constant.showToast(this,"Enter mobile no.");
            }

        }

    }

    public void saveLead()
    {
        progressDialog.setMessage("Saving sales! Please wait...");
        progressDialog.show();

        final String payLoad = "{\r\n    \t\"operation\": \"create\",\r\n    \t\"recordtype\": \"salesapp\",\r\n    \t\"name\":\""+""+"\",\r\n   \t\"dealername\": \""+""+"\"," +
                "\r\n    \t\"email\":\""+userEmail+"\",\r\n    \t\"sale_or_lead\": \""+""+"\",\r\n   \t\"product\": \""+edit_product.getText().toString()+"\"," +
                "\r\n    \t\"custentity_dealer_type\":\""+"2"+"\",\r\n    \t\"quantity\":\""+edit_product_qty.getText().toString()+"\"," +
                "\r\n    \t\"amount\":\""+ editAmount.getText().toString()+"\",\r\n    \t\"phone\": \""+edit_cust_mobno.getText().toString()+"\",\r\n    \t\"shop_name\":\""+""+"\"," +
                "\r\n    \t\"custentitycustentity_created_from\": 1,\r\n    \t\"remarks\": \""+edit_reamrks.getText().toString()+"\",\r\n    \t\"district_name\": \""+ ""+"\"," +
                "\r\n    \t\"notetype\": 9,\r\n    \t\"type\":\""+type+"\",\r\n        \t\t\"category\":\""+ ""+"\"," +
                "\r\n    \t\t\"purchase_from\":\""+van_realted_radio_value+"\",\r\n    \t\t\"city\": \""+""+ "\",\r\n    \t\t\"country\": \"India\"," +
                "\r\n    \t\t\"intersed_radio_value\": \""+intersed_radio_value+"\",\r\n    \t\t\"custrecord_address_longitude\":\""+ ""+"\"," +
                "\r\n    \t\t\"custrecord_address_latitude\":\""+""+"\",\r\n   \t\t\"dealer_or_customer\": \""+""+"\"\r\n  }";

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
                                        alertDialogBuilder = new AlertDialog.Builder(Followup.this);


                                        alertDialogBuilder.setTitle("sales Saved");
                                        alertDialogBuilder.setMessage("Your sales has been saved successfully with \nID - "+JSON_recordid+".");
                                        //alertDialog.setIcon(R.drawable.logo);
                                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Followup.this, LoginActivity.class));
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
                                        alertDialogBuilder = new AlertDialog.Builder(Followup.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your sales has not been saved. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(Followup.this, LoginActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();


                                        Toast.makeText(Followup.this,"Error in saving sales. Please check your data again!",Toast.LENGTH_LONG).show();

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
                                alertDialogBuilder = new AlertDialog.Builder(Followup.this);


                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Followup.this, LoginActivity.class));
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
                            alertDialogBuilder = new AlertDialog.Builder(Followup.this);
                              alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err1);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Followup.this, LoginActivity.class));
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
