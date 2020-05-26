package com.dlightindia.dddcapture;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.scribejava.core.model.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import constant.Constant;

public class IFCCalling extends AppCompatActivity implements View.OnClickListener {

     private EditText editTextSalesRepEmail;
    private EditText edit_mobno;
    private TextView edit_shopanme;
    private TextView edit_shopkeeper_anme;
    private TextView edit_distict;
    private TextView edit_state;
    private TextView txt_enter_mobile_caption;
    private EditText edit_reason_typeing;
    private EditText edit_remarks;
    private Button buttonsubmit;


    private Spinner spinner_notcontable;
    private Spinner spinner_already_working;
    private Spinner spinner_not_intrested;
    private Spinner spinner_lead_status;
    private Spinner spinner_order_status;
    private Spinner spinner_reason;
    private Spinner spinner_no_of_calling;
    CheckBox chk_physically;

    private TextView txt_reason_for_order_status;
    private TextView text_interested;

    private LinearLayout lin_after_mob;
    private LinearLayout lin_notcontable;
    private LinearLayout lin_contable;
    private LinearLayout lin_order_reason;
     private RelativeLayout rel_reason_spiner_layout;
     LinearLayout lin_lead_form;

    RadioButton radio_notconatctable,radio_conatctable;
    RadioButton radio_already_working,radio_not_interested,radio_interested;
    TextView txt_already_working,txt_not_intrested,txt_interested_uper_label;
    RelativeLayout rel_spiner_already_working,rel_not_interested_spiner,rel_intrested_dropdown;

    private ProgressDialog progressDialog;
     private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=182&deploy=1";
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    private String lead_internalid=null;
    private String str_leadid=null;
    private String str_shopkeepername=null;
    private String str_shopname=null;
    private String str_state=null;
     private String str_districts=null;
    Vibrator vibrator;
     private FirebaseAuth firebaseAuth;
     String intrested_str_comma="";
    String check_phsically="F";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ifc_calling_new);
        getSupportActionBar().setTitle("D.light IFC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        editTextSalesRepEmail  =  (EditText) findViewById(R.id.editTextSalesRepEmail);
        edit_mobno  =  (EditText) findViewById(R.id.edit_mobno);
        edit_mobno.addTextChangedListener(mTextEditorWatcher);
        edit_shopanme  =  (TextView) findViewById(R.id.edit_shopanme);
        edit_shopkeeper_anme  =  (TextView) findViewById(R.id.edit_shopkeeper_anme);
        edit_distict  =  (TextView) findViewById(R.id.edit_distict);
        edit_state  =  (TextView) findViewById(R.id.edit_state);
        edit_reason_typeing  =  (EditText) findViewById(R.id.edit_reason_typeing);
        edit_remarks  =  (EditText) findViewById(R.id.edit_remarks);

        spinner_notcontable = (Spinner) findViewById(R.id.spinner_notcontable);
        spinner_already_working = (Spinner) findViewById(R.id.spinner_already_working);
        spinner_not_intrested = (Spinner) findViewById(R.id.spinner_not_intrested);
        spinner_lead_status = (Spinner) findViewById(R.id.spinner_lead_status);
        spinner_order_status = (Spinner) findViewById(R.id.spinner_order_status);
        spinner_reason = (Spinner) findViewById(R.id.spinner_reason);
        spinner_no_of_calling = (Spinner) findViewById(R.id.spinner_no_of_calling);

        txt_reason_for_order_status = (TextView) findViewById(R.id.txt_reason_for_order_status);

        text_interested = (TextView) findViewById(R.id.text_interested);
        chk_physically = findViewById(R.id.chk_physically);
        chk_physically.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    check_phsically="T";
                }else{
                    check_phsically="F";
                }
            }
        });

        txt_already_working = (TextView) findViewById(R.id.txt_already_working);
        txt_not_intrested = (TextView) findViewById(R.id.txt_not_intrested);
        txt_enter_mobile_caption = (TextView) findViewById(R.id.txt_enter_mobile_caption);
        txt_interested_uper_label = (TextView) findViewById(R.id.txt_interested_uper_label);
        rel_spiner_already_working = (RelativeLayout) findViewById(R.id.rel_spiner_already_working);
        rel_not_interested_spiner = (RelativeLayout) findViewById(R.id.rel_not_interested_spiner);
        rel_intrested_dropdown = (RelativeLayout) findViewById(R.id.rel_intrested_dropdown);

        radio_conatctable =   findViewById(R.id.radio_conatctable);
        radio_notconatctable = findViewById(R.id.radio_notconatctable);

        radio_already_working = findViewById(R.id.radio_already_working);
        radio_not_interested = findViewById(R.id.radio_not_interested);
        radio_interested = findViewById(R.id.radio_interested);

        lin_lead_form = (LinearLayout) findViewById(R.id.lin_lead_form);
        lin_after_mob = (LinearLayout) findViewById(R.id.lin_after_mob);
        lin_notcontable = (LinearLayout) findViewById(R.id.lin_notcontable);
        lin_contable = (LinearLayout) findViewById(R.id.lin_contable);
        lin_order_reason = (LinearLayout) findViewById(R.id.lin_order_reason);
         rel_reason_spiner_layout = (RelativeLayout) findViewById(R.id.rel_reason_spiner_layout);
         buttonsubmit = (Button) findViewById(R.id.buttonsubmit);

         progressDialog = new ProgressDialog(this);

        rel_intrested_dropdown.setOnClickListener(this);
        radio_conatctable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    lin_contable.setVisibility(View.VISIBLE);
                    lin_notcontable.setVisibility(View.GONE);

                    txt_already_working.setVisibility(View.GONE);
                    rel_spiner_already_working.setVisibility(View.GONE);
                    txt_not_intrested.setVisibility(View.GONE);
                    rel_not_interested_spiner.setVisibility(View.GONE);
                    txt_interested_uper_label.setVisibility(View.GONE);
                    rel_intrested_dropdown.setVisibility(View.GONE);
                    spinner_notcontable.setSelection(0);

                }

            }
        });
        radio_notconatctable.setOnClickListener(this);
        radio_notconatctable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    lin_contable.setVisibility(View.GONE);
                    lin_notcontable.setVisibility(View.VISIBLE);

                    spinner_already_working.setSelection(0);
                    spinner_not_intrested.setSelection(0);
                    spinner_lead_status.setSelection(0);
                    spinner_order_status.setSelection(0);
                    spinner_reason.setSelection(0);
                    intrested_str_comma="";
                    text_interested.setText("");
                    lin_lead_form.setVisibility(View.GONE);
                    ((RadioGroup) findViewById(R.id.radio_group)).clearCheck();
                   // radio_already_working.clear
                  //  radio_already_working.setChecked(false);
                   // radio_not_interested.setChecked(false);
                  //  radio_interested.setChecked(false);

                }
            }
        });

        buttonsubmit.setOnClickListener(this);

        String[] str_count={"1","2","3","4","5","6","7","8","9"};
        ArrayAdapter<String> adapterCount = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,str_count);
        spinner_no_of_calling.setAdapter(adapterCount);

        String[] not_cont_opt={"","Incorrect no./Wrong no.","Incoming not available/Not in service","Not reachable/Out of coverage","Switched off","Call not answered/Not picked/Busy","Other"};
        ArrayAdapter<String> adapterState = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,not_cont_opt);
        spinner_notcontable.setAdapter(adapterState);

        String[] already_work={"","as direct distributor","as redistributor/retailer","Re-distributor to direct distributor"};
        ArrayAdapter<String> adapterWork = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,already_work);
        spinner_already_working.setAdapter(adapterWork);


        String[] not_ins={"","High cost","Margin issue","No demand","Not deals in solar products","Others"};
        ArrayAdapter<String> adapterNOt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,not_ins);
        spinner_not_intrested.setAdapter(adapterNOt);


        String[] lead_status={"","Document collected","Next date given","Others"};
        ArrayAdapter<String> adapterLead = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,lead_status);
        spinner_lead_status.setAdapter(adapterLead);
        spinner_lead_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id)
            {
               if(i==2||i==3){
                   lin_order_reason.setVisibility(View.GONE);
               }else{
                   lin_order_reason.setVisibility(View.VISIBLE);
               }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        String[] order_status={"","Generated","Postponed","Denied"};
        ArrayAdapter<String> adapterorder = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,order_status);
        spinner_order_status.setAdapter(adapterorder);
        spinner_order_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id)
            {
                if(i!=0){
                    String selectedItem = parent.getItemAtPosition(i).toString();
                    txt_reason_for_order_status.setText("Reason for "+selectedItem);
                    if(i==1){
                        txt_reason_for_order_status.setText("ARcode-Order value");
                    }
                }

                if(i==0){
                }else if(i==1){

                    edit_reason_typeing.setVisibility(View.VISIBLE);
                    edit_reason_typeing.setHint("Type ARcode-Order status");
                    rel_reason_spiner_layout.setVisibility(View.GONE);

                }else if(i==2){
                    edit_reason_typeing.setVisibility(View.VISIBLE);
                    rel_reason_spiner_layout.setVisibility(View.GONE);
                    edit_reason_typeing.setHint("");

                }else if(i==3){
                    edit_reason_typeing.setVisibility(View.GONE);
                    rel_reason_spiner_layout.setVisibility(View.VISIBLE);

                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        String[] reason={"","High cost","Margin issue","No demand","Not deals in solar products","Others"};
        ArrayAdapter<String> adapterReason = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,reason);
        spinner_reason.setAdapter(adapterReason);

        radio_already_working.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //radio_already_working.setChecked(true);
                    txt_already_working.setVisibility(View.VISIBLE);
                    rel_spiner_already_working.setVisibility(View.VISIBLE);
                    txt_not_intrested.setVisibility(View.GONE);
                    rel_not_interested_spiner.setVisibility(View.GONE);
                    txt_interested_uper_label.setVisibility(View.GONE);
                    rel_intrested_dropdown.setVisibility(View.GONE);
                    lin_lead_form.setVisibility(View.GONE);
                    spinner_lead_status.setSelection(0);
                    spinner_order_status.setSelection(0);
                    spinner_reason.setSelection(0);
                    spinner_not_intrested.setSelection(0);
                     intrested_str_comma="";
                    text_interested.setText("");
                }
            }
        });
        radio_not_interested.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    txt_already_working.setVisibility(View.GONE);
                    rel_spiner_already_working.setVisibility(View.GONE);
                    txt_not_intrested.setVisibility(View.VISIBLE);
                    rel_not_interested_spiner.setVisibility(View.VISIBLE);
                    txt_interested_uper_label.setVisibility(View.GONE);
                    rel_intrested_dropdown.setVisibility(View.GONE);
                    lin_lead_form.setVisibility(View.GONE);
                    spinner_lead_status.setSelection(0);
                    spinner_order_status.setSelection(0);
                    spinner_reason.setSelection(0);
                    spinner_already_working.setSelection(0);
                    intrested_str_comma="";
                    text_interested.setText("");
                }
            }
        });
        radio_interested.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    txt_already_working.setVisibility(View.GONE);
                    rel_spiner_already_working.setVisibility(View.GONE);
                    txt_not_intrested.setVisibility(View.GONE);
                    rel_not_interested_spiner.setVisibility(View.GONE);
                    txt_interested_uper_label.setVisibility(View.VISIBLE);
                    rel_intrested_dropdown.setVisibility(View.VISIBLE);
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        editTextSalesRepEmail.setText(user.getEmail());
        editTextSalesRepEmail.setEnabled(false);
    }
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         }
         public void onTextChanged(CharSequence s, int start, int before, int count) {
             if(s.length()==0){
                 txt_enter_mobile_caption.setText("Provide mobile number (10 digits)");
             }else{
                 txt_enter_mobile_caption.setText("Provide mobile number ("+s.length()+" digits)");
                 if(s.length()==10){
                     vibrator.vibrate(100);
                     searchLead(edit_mobno.getText().toString());
                 }
             }


         }
         public void afterTextChanged(Editable s) {
        }
    };
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void searchLead(String mob)
    {
        progressDialog.setMessage("Searching Lead! Please wait...");
        progressDialog.show();

        final String payLoad = "{\r\n\t\"operation\": \"search\",\r\n\t\"recordtype\": \"ifc\",\r\n\t\"mobile\": \""+mob+"\",\r\n \t\"columns\": [{\r\n\t\t\r\n\t\t\"custrecord_shopkeeper_name\": \"custrecord_shopkeeper_name\",\r\n\t\t\"custrecord_shop_name\":\"custrecord_shop_name\",\r\n\t\t\"custrecord_state\":\"custrecord_state\",\r\n\t\t\"custrecord_district\":\"custrecord_district\",\r\n\t\t\r\n\t\t\r\n\t}]\r\n\r\n}";

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
                                        showResults(responseJSONObbject);

                                    }
                                });

                            else if (JSON_message.equalsIgnoreCase("lead has not been generated."))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        alertDialogBuilder = new AlertDialog.Builder(IFCCalling.this);

                                        alertDialogBuilder.setTitle("Failed");
                                        alertDialogBuilder.setMessage("Your mobile number was not found. Try again!");
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
                 for (int i=0; i<searchObject.length();i++) {
                     lin_after_mob.setVisibility(View.VISIBLE);
                    jObj = searchObject.getJSONObject(i);
                    lead_internalid = jObj.optString("id");
                     cObj = jObj.getJSONObject("columns");
                     str_shopkeepername = cObj.optString("custrecord_shopkeeper_name");
                     str_shopname = cObj.optString("custrecord_shop_name");
                     str_state = cObj.optString("custrecord_state");
                     str_districts = cObj.optString("custrecord_district");

                     edit_shopkeeper_anme.setText(str_shopkeepername);
                      edit_shopanme.setText(str_shopname);
                      edit_state.setText(str_state);
                      edit_distict.setText(str_districts);
                 }
                hideKeyboard();

            }
        }
        catch(Exception e)
        {
             Log.d("Sparsh App Exception",e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
             case R.id.rel_intrested_dropdown:
                 showDialog();
                break;
            case R.id.buttonsubmit:
                if(radio_conatctable.isChecked()) {
                    if(radio_already_working.isChecked()){
                        if (!spinner_already_working.getSelectedItem().toString().equalsIgnoreCase("")) {
                            if (!edit_remarks.getText().toString().equalsIgnoreCase("")) {
                                 updateLead();
                             } else {
                                Constant.showToast(this, "Please enter remarks");
                            }
                         } else {
                            Constant.showToast(this, "Please select relationship with d.light ");
                        }
                    }else if(radio_not_interested.isChecked()){
                        if (!spinner_not_intrested.getSelectedItem().toString().equalsIgnoreCase("")) {
                            if (!edit_remarks.getText().toString().equalsIgnoreCase("")) {
                                 updateLead();
                             } else {
                                Constant.showToast(this, "Please type remarks/Any support required");
                            }
                        }else {
                            Constant.showToast(this, "Please select reason for not interested");
                        }
                    }else if(radio_interested.isChecked()){
                             if(!intrested_str_comma.equalsIgnoreCase("")){
                                 if (!spinner_lead_status.getSelectedItem().toString().equalsIgnoreCase("")) {
                                     if (spinner_lead_status.getSelectedItem().toString().equalsIgnoreCase("Document collected")) {
                                         if (!spinner_order_status.getSelectedItem().toString().equalsIgnoreCase("")) {
                                             if (spinner_order_status.getSelectedItem().toString().equalsIgnoreCase("Generated")) {
                                                 if (!edit_reason_typeing.getText().toString().equalsIgnoreCase("")) {

                                                     updateLead();
                                                  } else {
                                                     Constant.showToast(this, "Please enter ARcode-Order value");
                                                 }
                                             } else if (spinner_order_status.getSelectedItem().toString().equalsIgnoreCase("Postponed")) {
                                                 if (!edit_reason_typeing.getText().toString().equalsIgnoreCase("")) {
                                                     if (!edit_remarks.getText().toString().equalsIgnoreCase("")) {
                                                         updateLead();
                                                      } else {
                                                         Constant.showToast(this, "Please type remarks/Support required");
                                                     }

                                                 } else {
                                                     Constant.showToast(this, "Please type reason for postponed");
                                                 }

                                             } else if (spinner_order_status.getSelectedItem().toString().equalsIgnoreCase("Denied")) {
                                                 if (!spinner_reason.getSelectedItem().toString().equalsIgnoreCase("")) {
                                                     if (!edit_remarks.getText().toString().equalsIgnoreCase("")) {

                                                         updateLead();
                                                      } else {
                                                         Constant.showToast(this, "Please type remarks/Support required");
                                                     }

                                                 } else {
                                                     Constant.showToast(this, "Please select reason for denied");
                                                 }

                                             }
                                         } else {
                                             Constant.showToast(this, "Please select order status");
                                         }

                                     }else if (spinner_lead_status.getSelectedItem().toString().equalsIgnoreCase("Next date given")) {
                                         if (!edit_remarks.getText().toString().equalsIgnoreCase("")) {
                                             updateLead();
                                         } else {
                                             Constant.showToast(this, "Please type remarks/Any support required");
                                         }
                                     }else if (spinner_lead_status.getSelectedItem().toString().equalsIgnoreCase("Others")) {
                                         if (!edit_remarks.getText().toString().equalsIgnoreCase("")) {
                                             updateLead();
                                         } else {
                                             Constant.showToast(this, "Please type remarks/Any support required");
                                         }
                                     }

                                 }else {
                                     Constant.showToast(this, "Please select lead status");
                                 }



                             }else{
                                 Constant.showToast(this, "Please select status");
                             }

                        }else{
                        Constant.showToast(this, "Please select any option");
                    }

                }else if(radio_notconatctable.isChecked()) {
                    if (!spinner_notcontable.getSelectedItem().toString().equalsIgnoreCase("")) {
                        updateLead();

                    }else {
                        Constant.showToast(this, "Please select reason for not contactable");
                    }

                }else{
                    Constant.showToast(this, "Please select contactable or not contactable");
                }

                break;
        }
    }

    public void updateLead()
    {
         progressDialog.setMessage("Updating lead! Please wait...");
         progressDialog.show();
        String mobStr = edit_mobno.getText().toString().trim();
        String user_email = editTextSalesRepEmail.getText().toString().trim();
        String remarks_str = edit_remarks.getText().toString().trim();
        String str_already="";
        String str_notintrs="";
        String str_leadstatus="";
        String str_orderstatus="";
        String str_reason="";
        String str_count_calling="";
        String str_spin_not_contable="";
        str_spin_not_contable=spinner_notcontable.getSelectedItem().toString();
        str_already=spinner_already_working.getSelectedItem().toString();
        str_notintrs=spinner_not_intrested.getSelectedItem().toString();
        str_leadstatus=spinner_lead_status.getSelectedItem().toString();
        str_orderstatus=spinner_order_status.getSelectedItem().toString();
        str_count_calling=spinner_no_of_calling.getSelectedItem().toString();

        if(str_orderstatus=="Generated"){
            str_reason=edit_reason_typeing.getText().toString().trim();
        }else if(str_orderstatus=="Postponed"){
            str_reason=edit_reason_typeing.getText().toString().trim();

    }else if(str_orderstatus=="Denied"){
            str_reason=spinner_reason.getSelectedItem().toString();
        }

        final String payLoad = "{\r\n    \t\"operation\": \"update\",\r\n    \t\"recordtype\": \"ifc\",\r\n    \t\"mobile\": \""+
                mobStr+"\",\r\n    \t\"user_email\":\""+user_email+"\",\r\n    \t\"not_contactable\":\""+str_spin_not_contable+"\",\r\n    \t\"contacted\": \""+mobStr+"\",\r\n    " +
                "\t\"already_working\": \""+str_already+"\",\r\n    \t\"calling_count\":\""+str_count_calling+"\",\r\n    \t\"physicaly_peresnt\":\""+check_phsically+"\",\r\n    \t\"not_intrested\":\""+str_notintrs+"\",\r\n    \t\"intrested\": \""+intrested_str_comma+"\",\r\n  " +
                    "\t\"lead_status\":\""+str_leadstatus+"\",\r\n    \t\"order_status\":\""+str_orderstatus+"\",\r\n    \t\"reason\":\""+str_reason+"\",\r\n   \t\"remarks\":\""+remarks_str+"\"\r\n   }";

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

                                            alertDialogBuilder = new AlertDialog.Builder(IFCCalling.this);
                                            alertDialogBuilder.setCancelable(false);
                                             alertDialogBuilder.setTitle("Success");
                                            alertDialogBuilder.setMessage("Your lead was updated successfully.");
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


                                else if (JSON_message.equalsIgnoreCase("lead has not been generated."))
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            alertDialogBuilder = new AlertDialog.Builder(IFCCalling.this);


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
                                    alertDialogBuilder = new AlertDialog.Builder(IFCCalling.this);


                                    alertDialogBuilder.setTitle("Failed");
                                    alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(IFCCalling.this, LoginActivity.class));
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
                                alertDialogBuilder = new AlertDialog.Builder(IFCCalling.this);


                                alertDialogBuilder.setTitle("Failed");
                                alertDialogBuilder.setMessage("Your lead has not been saved. Error: "+err);
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(IFCCalling.this, LoginActivity.class));
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
public void showDialog(){
    final Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(false);
    dialog.setContentView(R.layout.dialog_intersted);
    final List<String> list_interested = new ArrayList<String>();
    CheckBox checkBox_price = (CheckBox) dialog.findViewById(R.id.checkBox_price);
    CheckBox checkBox_live = (CheckBox) dialog.findViewById(R.id.checkBox_live);
    CheckBox checkBox_vago = (CheckBox) dialog.findViewById(R.id.checkBox_vago);
    if(intrested_str_comma!=null&&!intrested_str_comma.equalsIgnoreCase("")){
       String[] arr= intrested_str_comma.split(",");
        for (int i = 0; i <arr.length ; i++) {
            list_interested.add(arr[i]);
            if(arr[i].equalsIgnoreCase("Price list shared")){
                checkBox_price.setChecked(true);
            } else if(arr[i].equalsIgnoreCase("Live demonstration given")){
                checkBox_live.setChecked(true);
            }else if(arr[i].equalsIgnoreCase("Negotiation")){
                checkBox_vago.setChecked(true);
            }
        }
    }
    checkBox_price.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
          if(b){
              list_interested.add("Price list shared");
          }else{
              list_interested.remove("Price list shared");
          }
        }
    });
    checkBox_live.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                list_interested.add("Live demonstration given");
            }else{
                list_interested.remove("Live demonstration given");
            }
        }
    });
    checkBox_vago.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b){
                list_interested.add("Negotiation");
            }else{
                list_interested.remove("Negotiation");
            }
        }
    });
    Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
    btn_ok.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(list_interested!=null&&list_interested.size()>0){
                intrested_str_comma= android.text.TextUtils.join(",", list_interested);
                text_interested.setText(intrested_str_comma);
                dialog.cancel();
                txt_already_working.setVisibility(View.GONE);
                rel_spiner_already_working.setVisibility(View.GONE);
                txt_not_intrested.setVisibility(View.GONE);
                rel_not_interested_spiner.setVisibility(View.GONE);
                lin_lead_form.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(IFCCalling.this, "Please check any option", Toast.LENGTH_SHORT).show();
            }

         }
    });
    Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
    btn_cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
         dialog.cancel();
        }
    });

    dialog.show();

}
    public void hideKeyboard( ) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view =  getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
