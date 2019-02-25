package com.dlightindia.dddcapture;

import android.content.RestrictionEntry;
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

import java.util.Iterator;

public class SearchLeadActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener  {

    private Button buttonSearchLead;
    private EditText editTextSearchLeadID;
    private TextView textViewSearchLeadIDResults;
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

    @Override
    public boolean isVoiceInteraction() {
        return super.isVoiceInteraction();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lead);

        textViewSearchLeadIDResults  = (TextView) findViewById(R.id.textViewLeadSearchResult);
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
    }

    public void searchLead()
    {
        progressDialog.setMessage("Searching Lead! Please wait...");
        progressDialog.show();

        String str_searchLeadID = editTextSearchLeadID.getText().toString().trim();

        final String payLoad = "{\r\n\t\"operation\": \"search\",\r\n\t\"recordtype\": \"lead\",\r\n\r\n\t\"filters\": [{\r\n \t\t\"entityid\": \""+str_searchLeadID+"\",\r\n \t\t}],\r\n \t\"columns\": [{\r\n\t\t\r\n\t\t\"entityid\": \"entityid\",\r\n\t\t\"companyname\":\"companyname\",\r\n\t\t\"custentity_owner_name\":\"custentity_owner_name\",\r\n\t\t\"custentity_lead_status_dealer\":\"custentity_lead_status_dealer\",\r\n\t\t\"custentity3\":\"custentity3\"\r\n\t\t\r\n\t}]\r\n\r\n}";

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

                    str_ownername = cObj.getString("custentity_owner_name");
                    JSONObject leadStatusObject = cObj.getJSONObject("custentity_lead_status_dealer");
                    str_leadStatus = leadStatusObject.getString("name");
                    int visitLevelsDone=0;
                    if (cObj.has("custentity3")) {
                        JSONObject visitLevelObject = cObj.getJSONObject("custentity3");
                        str_visitLevel = visitLevelObject.getString("name");
                        Log.d("Sparsh App","Visit Levels -"+str_visitLevel);

                        if (str_visitLevel.toLowerCase().equals("first"))
                            visitLevelsDone = 1;
                        if (str_visitLevel.toLowerCase().equals("second"))
                            visitLevelsDone = 2;
                        if (str_visitLevel.toLowerCase().equals("third"))
                            visitLevelsDone = 3;
                    }

                    String resultString ="";

                    resultString = "Below are the details:\n\nLead ID : "+ str_leadid+
                            "\nDealer Name : " + str_companyname+
                            "\nOwner Name : " + str_ownername ;


                        resultString = resultString + "\nVisits so far : " + visitLevelsDone;
                                       textViewSearchLeadIDResults.setText(resultString);
                    spinnerLeadStatusUpdate.setSelection(((ArrayAdapter)spinnerLeadStatusUpdate.getAdapter()).getPosition(str_leadStatus));
                    buttonUpdateLead.setVisibility(View.VISIBLE);
                    editTextUpdateComments.setVisibility(View.VISIBLE);
                    spinnerLeadStatusUpdate.setVisibility(View.VISIBLE);
                    spinnerLeadStatusUpdate.requestFocus();
                    spinnerLeadStatusUpdate.setPrompt("Lead Status");


                }
            }
        }
        catch(Exception e)
        {
            //Toast.makeText(SearchLeadActivity.this,"Exception: " + e.getMessage(),Toast.LENGTH_LONG).show();
            Log.d("Sparsh App Exception",e.getMessage());
        }
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
        final String payLoad = "{\r\n\t\"operation\": \"update\",\r\n\t\"recordtype\": \"lead\",\r\n\t\"id\" : \""+
              lead_internalid+"\",\r\n\t\"custentity_lead_status_dealer\": "+leadStatus+",\r\n\t\"custentity3\": "+visitLevel+",\r\n\t\"note\": \""+editTextUpdateComments.getText().toString().trim()+"\",\r\n\t\"notetype\": 9\r\n}";


        final CallNetSuiteAPI callNetSuiteAPI = new CallNetSuiteAPI();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    Response response = callNetSuiteAPI.main(payLoad,REST_UPDATE_URL);
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
