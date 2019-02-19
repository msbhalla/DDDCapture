package com.dlightindia.dddcapture;

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

import org.json.JSONObject;


public class AddNewLead extends AppCompatActivity implements View.OnClickListener {
   private TextView textView;
   private EditText salesRepEmail;
   private EditText salesRepID;
   private Spinner salesRepRegion;
   private EditText dealerShopName;
   private EditText dealerName;
   private Spinner infoSource;
   private EditText dealerPhoneNo;
   private EditText dealerCity;
   private EditText dealerDistrict;
   private Spinner visitLevel;
   private EditText detailsDiscussion;
   private Spinner dealerState;
   private EditText dealerPincode;
   private Spinner leadStatus;
   private FirebaseAuth firebaseAuth;
   private Button buttonSaveLead;
   private ProgressDialog progressDialog;
   private AlertDialog.Builder alertDialogBuilder;
   private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lead);

        textView  = (TextView) findViewById(R.id.textViewAddLead);
        salesRepEmail  = (EditText) findViewById(R.id.editTextSalesRepEmail);
        salesRepID = (EditText) findViewById(R.id.editTextSalesRepID);

        buttonSaveLead = (Button) findViewById(R.id.buttonSaveLead);
        buttonSaveLead.setOnClickListener(AddNewLead.this);

        salesRepRegion = (Spinner) findViewById(R.id.spinnerRegion);
        String[] regions = {"Select Region","North","East","West & South"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,regions);
        salesRepRegion.setAdapter(adapter);

        dealerState = (Spinner) findViewById(R.id.spinnerState);
        String[] states_of_india = {"Select State","Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Chattisgarh","Goa","Gujarat","Haryana",
                "Himachal Pradesh", "Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya",
                "Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telagana","Tripura","Uttaranchal","Uttar Pradesh",
                "West Bengal"};
        ArrayAdapter<String> adapterState = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,states_of_india);
        dealerState.setAdapter(adapterState);


        dealerShopName = (EditText) findViewById(R.id.editTextDealerShopName);
        dealerName = (EditText) findViewById(R.id.editTextDealerName);

        infoSource = (Spinner) findViewById(R.id.spinnerSourceInfo);
        String[] source_of_information = {"Select Information Source","Individual Visit","Newspaper Ad","Branding","Others"};
        ArrayAdapter<String> adapterInfoSource = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,source_of_information);
        infoSource.setAdapter(adapterInfoSource);

        dealerPhoneNo = (EditText) findViewById(R.id.editTextContactNo);
        dealerCity = (EditText) findViewById(R.id.editTextCityName);
        dealerDistrict = (EditText) findViewById(R.id.editTextDistrict);
        dealerPincode = (EditText) findViewById(R.id.editPincode);

        visitLevel = (Spinner) findViewById(R.id.spinnerVisitLevel);
        String[] visit_levels={"Select Visit Level","First","Second","Third"};
        ArrayAdapter<String> adapterVisitLevels = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,visit_levels);
        visitLevel.setAdapter(adapterVisitLevels);

        detailsDiscussion = (EditText) findViewById(R.id.editTextDetails);

        leadStatus = (Spinner) findViewById(R.id.spinnerLeadStatus);
        String[] lead_status={"Select Lead Status","Cold","Hot","Converted","Declined"};
        ArrayAdapter<String> adapterLeadStatus = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,lead_status);
        leadStatus.setAdapter(adapterLeadStatus);


        //Prefilling email ID
        //initializing firebase authentication object
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


        progressDialog = new ProgressDialog(this);

    }



    public void onClick(View view) {
        //if SaveLead is pressed
        if(view == buttonSaveLead){

            if (TextUtils.isEmpty(salesRepEmail.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter your email id",Toast.LENGTH_LONG).show();

            }
            else if (TextUtils.isEmpty(salesRepID.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter your employee id",Toast.LENGTH_LONG).show();
            }
            else if(salesRepRegion.getSelectedItem().toString().trim().toUpperCase() == "SELECT REGION" )
            {
                Toast.makeText(this,"Please select region"+salesRepRegion.getSelectedItem().toString().trim(),Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(dealerShopName.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's shop name",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(dealerName.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's name",Toast.LENGTH_LONG).show();
            }
            else if (infoSource.getSelectedItem().toString().trim()=="Select Information Source")
            {
                Toast.makeText(this,"Please select information source",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(dealerPhoneNo.getText().toString().trim()) || TextUtils.getTrimmedLength(dealerPhoneNo.getText().toString())<10)

            {
                Toast.makeText(this,"Please enter dealer's phone number. It can't be less than 10 digits.",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(dealerCity.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter dealer's city",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(dealerDistrict.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter district",Toast.LENGTH_LONG).show();
            }

            else if (dealerState.getSelectedItem().toString().trim()=="Select State")
            {
                Toast.makeText(this,"Please select state",Toast.LENGTH_LONG).show();
            }

            else if (TextUtils.isEmpty(dealerPincode.getText().toString().trim()) || TextUtils.getTrimmedLength(dealerPincode.getText())!=6)
            {
                Toast.makeText(this,"Please enter dealer's pincode. It should be of 6 digits.",Toast.LENGTH_LONG).show();
            }
            else if (dealerState.getSelectedItem().toString().trim()=="Select Visit Level")
            {
                Toast.makeText(this,"Please select visit level",Toast.LENGTH_LONG).show();
            }
            else if (TextUtils.isEmpty(detailsDiscussion.getText().toString().trim()))
            {
                Toast.makeText(this,"Please enter details of discussion",Toast.LENGTH_LONG).show();
            }
            else if (leadStatus.getSelectedItem().toString().trim()=="Select Lead Status")
            {
                Toast.makeText(this,"Please select lead status",Toast.LENGTH_LONG).show();
            }
            else {


                saveLead();
                //closing activity

                //starting login activity
//            startActivity(new Intent(this, LoginActivity.class));
            }
        }
    }

    public void saveLead()
    {
        progressDialog.setMessage("Saving Lead! Please wait...");
        progressDialog.show();

        String str_salesRepEmail = salesRepEmail.getText().toString().trim();
        String str_salesRepID = salesRepID.getText().toString().trim();
        String str_salesRepRegion = salesRepRegion.getSelectedItem().toString();
        String str_salesRepRegionID="1";
        if (str_salesRepRegion=="North")
            str_salesRepRegionID="1";
        if (str_salesRepRegion=="East")
            str_salesRepRegionID="2";
        if (str_salesRepRegion=="West & South")
            str_salesRepRegionID="3";


        String str_dealerShopName = dealerShopName.getText().toString().trim();
        String str_dealerName = dealerName.getText().toString().trim();
        String str_infosource = infoSource.getSelectedItem().toString();
        String str_infosourceID="5";
        if (str_infosource=="Branding")
            str_infosourceID="4";
        if (str_infosource=="Individual Visit")
            str_infosourceID="2";
        if (str_infosource=="Newspaper Ad")
            str_infosourceID="3";
        if (str_infosource=="Others")
            str_infosourceID="5";


        String str_dealerPhone = dealerPhoneNo.getText().toString().trim();
        String str_dealerCity = dealerCity.getText().toString().trim();
        String str_dealerDistrict = dealerDistrict.getText().toString().trim();
        String str_dealerState = dealerState.getSelectedItem().toString();
        String str_visitLevel = visitLevel.getSelectedItem().toString();
        String str_visitLevelID="1";
        if (str_visitLevel=="First")
            str_visitLevelID="1";
        if (str_visitLevel=="Second")
            str_visitLevelID="2";
        if (str_visitLevel=="Third")
            str_visitLevelID="3";



        String str_detailsDiscussion = detailsDiscussion.getText().toString().trim();
        String str_LeadStatus = leadStatus.getSelectedItem().toString().trim();
        String str_leadStatusID="2";
        if (str_LeadStatus=="Hot")
            str_leadStatusID="1";
        if (str_LeadStatus=="Cold")
            str_leadStatusID="2";
        if (str_LeadStatus=="Successfully Closed")
            str_leadStatusID="3";
        if (str_LeadStatus=="Declined")
            str_leadStatusID="4";



        String str_dealerPincode = dealerPincode.getText().toString().trim();

        /*final String payLoad = "{\r\n\t\"operation\": \"create\",\r\n \t\"recordtype\": \"lead\",\r\n \t\"companyname\": \""+str_dealerShopName+
                "\",\r\n   \t\"phone\": \""+str_dealerPhone+"\",\r\n \t\"email\": \""+str_salesRepEmail+"\",\r\n \t\"comments\":\""+
                str_detailsDiscussion+"\",\r\n \t\"note\":\""+str_detailsDiscussion+"\",\r\n \t\"notetype\":9,\r\n \t\"shippingaddress\": [{\r\n \t\t\"city\": \""+
                str_dealerCity+"\",\r\n \t\t\"country\": \"India\",\r\n \t\t\"state\": \""+str_dealerState+"\",\r\n \t\t\"zip\": \""+
                str_dealerPincode+"\",\r\n \t}]\r\n }\r\n";*/

        final String payLoad = "{\r\n\t\"operation\": \"create\",\r\n \t\"recordtype\": \"lead\",\r\n \t\"companyname\": \""+str_dealerShopName+
        "\",\r\n \t\"custentity_sales_rep_id\" : \""+str_salesRepID+"\",\r\n \t\"custentity_sales_rep_region\" : "+str_salesRepRegionID+
                ",\r\n \t\"custentity_owner_name\" : \""+str_dealerName+"\",\r\n \t\"campaigncategory\" : "+str_infosourceID+
                ",\r\n \t\"leadstatus\":"+str_leadStatusID+",\r\n \t\r\n   \t\"phone\": \""+str_dealerPhone+
                "\",\r\n   \t\"custentity3\" :"+str_visitLevelID+",\r\n   \t\"custentitycustentity_created_from\":1,\r\n \t\"comments\":\""+
                str_detailsDiscussion+"\",\r\n \t\"note\":\""+str_detailsDiscussion+"\",\r\n \t\"notetype\":9,\r\n \t\"shippingaddress\": [{\r\n \t\t\"city\": \""+
                str_dealerCity+"\",\r\n \t\t\"country\": \"India\",\r\n \t\t\"state\": \""+str_dealerState+"\",\r\n \t\t\"zip\": \""+str_dealerPincode+
                "\",\r\n \t}]\r\n }\r\n";





        final CallNetSuiteAPI callNetSuiteAPI = new CallNetSuiteAPI();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    Response response = callNetSuiteAPI.main(payLoad);
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
                                               alertDialogBuilder = new AlertDialog.Builder(AddNewLead.this);


                                               alertDialogBuilder.setTitle("Lead Saved");
                                               alertDialogBuilder.setMessage("Your lead has been saved successfully with ID "+JSON_recordid);
                                               //alertDialog.setIcon(R.drawable.logo);
                                               alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       startActivity(new Intent(AddNewLead.this, LoginActivity.class));
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
                                        alertDialogBuilder = new AlertDialog.Builder(AddNewLead.this);


                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your lead has not been saved. Try again!");
                                        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(AddNewLead.this, LoginActivity.class));
                                            }
                                        });
                                        alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();


                                        Toast.makeText(AddNewLead.this,"Error in saving lead. Please check your data again!",Toast.LENGTH_LONG).show();

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


}
