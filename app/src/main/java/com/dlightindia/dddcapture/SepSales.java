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

import com.dlightindia.dddcapture.databinding.SepSaleBinding;
import com.dlightindia.dddcapture.databinding.SkuBinding;
import com.github.scribejava.core.model.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import constant.Constant;

public class SepSales extends AppCompatActivity {
    String userEmail;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    SepSaleBinding skuBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skuBinding  = DataBindingUtil.setContentView(this, R.layout.sep_sale);
        getSupportActionBar().setTitle("SEP Sales");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
         //getting current user

        skuBinding.buttonsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Arcode=skuBinding.editArcode.getText().toString().trim();
                if(!Arcode.equalsIgnoreCase("")) {
                    if(Arcode.length()==10) {
                        create_sku();
                    }else{
                        Constant.showToast(SepSales.this,"Please enter 10 digit correct mobile no");
                    }

                }else{
                    Constant.showToast(SepSales.this,"Please enter mobile no");
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void create_sku()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
         userEmail = user.getEmail();
        progressDialog.setMessage("Creating SEP sale! Please wait...");
        progressDialog.show();
        String arcode= skuBinding.editArcode.getText().toString().trim();

        final String payLoad = "{\r\n    \t\"operation\": \"register\",\r\n    \t\"recordtype\": \"sep_sales\",\r\n    \t\"a2_quan\": \""+
                skuBinding.editA2Quntity.getText().toString().trim()+"#"+skuBinding.editA2Value.getText().toString().trim()+"\",\r\n    \t\"s3_quan\":\""+skuBinding.editS3Quntity.getText().toString().trim()+"#"+skuBinding.editS3Value.getText().toString().trim()+"\",\r\n    \t\"s30_quan\": \""+skuBinding.editS30Quntity.getText().toString().trim()+"#"+skuBinding.editS30Value.getText().toString().trim()+"\"," +
                "\r\n    \t\"s200_quan\": \""+skuBinding.editS200Quntity.getText().toString().trim()+"#"+skuBinding.editS200Value.getText().toString().trim()+"\",\r\n    \t\"sf20_quan\":\""+skuBinding.editSf20Quntity.getText().toString().trim()+"#"+skuBinding.editSf20Value.getText().toString().trim()+"\"," +
                "\r\n    \t\"st100_quan\": \""+skuBinding.editSt100Quntity.getText().toString().trim()+"#"+skuBinding.editSt100Value.getText().toString().trim()+"\",\r\n    \t\"s500b_quan\":\""+skuBinding.editS500bQuntity.getText().toString().trim()+"#"+skuBinding.editS500bValue.getText().toString().trim()+"\"," +
                "\r\n    \t\"d333_quan\":\""+skuBinding.editD333Quntity.getText().toString().trim()+"#"+skuBinding.editD333Value.getText().toString().trim()+"\",\r\n    \t\"solar_10_quan\":\""+ skuBinding.editSoler10Quntity.getText().toString().trim()+"#"+skuBinding.editSoler10Value.getText().toString().trim()+"\"," +
                "\r\n    \t\"solar_20_quan\": \""+skuBinding.editSoler20Quntity.getText().toString().trim()+"#"+skuBinding.editSoler20Value.getText().toString().trim()+"\",\r\n    \t\"solar_50_quan\":\""+skuBinding.editSoler50Quntity.getText().toString().trim()+"#"+skuBinding.editSoler50Value.getText().toString().trim()+"\"," +
                "\r\n    \t\"solar_100_quan\": \""+skuBinding.editSoler100Quntity.getText().toString().trim()+"#"+skuBinding.editSoler100Value.getText().toString().trim()+"\",\r\n    \t\"solar_160_quan\": \""+skuBinding.editSoler160Quntity.getText().toString().trim()+"#"+skuBinding.editSoler160Value.getText().toString().trim()+"\"," +
                "\r\n    \t\"solar_320_quan\": \""+ skuBinding.editSoler320Quntity.getText().toString().trim()+"#"+skuBinding.editSoler320Value.getText().toString().trim()+"\",\r\n    \t\"invertor_quan\":\""+skuBinding.editSolerInvertorQuntity.getText().toString().trim()+"#"+skuBinding.editSolerInvertorValue.getText().toString().trim()+
                "\",\r\n    \t\"a1_quan\":\""+skuBinding.editA1Quntity.getText().toString().trim()+"#"+skuBinding.editA1Value.getText().toString().trim()+"\",\r\n    \t\"s20_quantity\": \""+skuBinding.editS20Quntity.getText().toString().trim()+"#"+skuBinding.editS20Value.getText().toString().trim()+"\",\r\n    \t\"s100_quan\":\""+
                skuBinding.editS100Quntity.getText().toString().trim()+"#"+skuBinding.editS100Value.getText().toString().trim()+"\",\r\n    \t\t\"s300b_quan\":\""+ skuBinding.editS300bQuntity.getText().toString().trim()+"#"+skuBinding.editS300bValue.getText().toString().trim()+"\",\r\n    \t\"s320_quan\": \""+ skuBinding.editS320Quntity.getText().toString().trim()+"#"+skuBinding.editS320Value.getText().toString().trim()+
                "\",\r\n    \t\t\"s330t_quan\": \""+skuBinding.editD330tQuntity.getText().toString().trim()+"#"+skuBinding.editD330tValue.getText().toString().trim()+"\",\r\n    \t\t\"s400_quan\": \""+skuBinding.editS400Quntity.getText().toString().trim()+"#"+skuBinding.editS400Value.getText().toString().trim()+"\",\r\n    \t\t\"s500_quan\":\""+
                skuBinding.editS500Quntity.getText().toString().trim()+"#"+skuBinding.editS500Value.getText().toString().trim()+"\",\r\n    \t\t\"sf30_quan\":\""+skuBinding.editSf30Quntity.getText().toString().trim()+"#"+skuBinding.editSf30Value.getText().toString().trim()+"\",\r\n    \t\"s500r_quan\": \""+skuBinding.editS500rQuntity.getText().toString().trim()+"#"+skuBinding.editS500rValue.getText().toString().trim()+"\"," +
                "\r\n    \t\"user_email\":\""+userEmail+"\",\r\n   \t\"mobileno\":\""+arcode+"\",\r\n   \t\"s550_quan\":  \""+skuBinding.editS550Quntity.getText().toString().trim()+"#"+skuBinding.editS550Value.getText().toString().trim()+"\"\r\n   }";
        final CallNetSuiteAPI callNetSuiteAPI = new CallNetSuiteAPI();



        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
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

                                        alertDialogBuilder = new AlertDialog.Builder(SepSales.this);
                                        alertDialogBuilder.setCancelable(false);
                                        alertDialogBuilder.setTitle("Success");
                                        alertDialogBuilder.setMessage("SEP sale was updated successfully.");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });


                            else if (JSON_message.equalsIgnoreCase("SEP sale record has not been updated."))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(SepSales.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your closing stock record was not created. Try again!");
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
                                alertDialogBuilder = new AlertDialog.Builder(SepSales.this);


                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your closing stock record has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(SepSales.this, LoginActivity.class));
                                    }
                                });
                                alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

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
                            alertDialogBuilder = new AlertDialog.Builder(SepSales.this);


                            alertDialogBuilder.setTitle("Failed");
                            alertDialogBuilder.setMessage("Your closing stock record has not been saved. Error: "+err);
                            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(SepSales.this, LoginActivity.class));
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
