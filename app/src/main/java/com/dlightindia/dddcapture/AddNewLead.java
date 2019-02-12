package com.dlightindia.dddcapture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class AddNewLead extends AppCompatActivity {
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
   private Spinner leadStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lead);

        textView  = (TextView) findViewById(R.id.textViewAddLead);
        salesRepEmail  = (EditText) findViewById(R.id.editTextSalesRepEmail);
        salesRepID = (EditText) findViewById(R.id.editTextSalesRepID);

        salesRepRegion = (Spinner) findViewById(R.id.spinnerState);
        String[] states_of_india = {"Select State","Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Chattisgarh","Goa","Gujarat","Haryana",
        "Himachal Pradesh", "Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya",
        "Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telagana","Tripura","Uttaranchal","Uttar Pradesh",
        "West Bengal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,states_of_india);
        salesRepRegion.setAdapter(adapter);

        dealerShopName = (EditText) findViewById(R.id.editTextDealerShopName);
        dealerName = (EditText) findViewById(R.id.editTextDealerName);

        infoSource = (Spinner) findViewById(R.id.spinnerSourceInfo);
        String[] source_of_information = {"Select Information Source","Individual Visit","Newspaper Ad","Branding","Others"};
        ArrayAdapter<String> adapterInfoSource = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,source_of_information);
        infoSource.setAdapter(adapterInfoSource);

        dealerPhoneNo = (EditText) findViewById(R.id.editTextContactNo);
        dealerCity = (EditText) findViewById(R.id.editTextCityName);
        dealerDistrict = (EditText) findViewById(R.id.editTextDistrict);

        visitLevel = (Spinner) findViewById(R.id.spinnerVisitLevel);
        String[] visit_levels={"Select Visit Level","First","Second","Third"};
        ArrayAdapter<String> adapterVisitLevels = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,visit_levels);
        visitLevel.setAdapter(adapterVisitLevels);

        detailsDiscussion = (EditText) findViewById(R.id.editTextDetails);

        leadStatus = (Spinner) findViewById(R.id.spinnerLeadStatus);
        String[] lead_status={"Select Lead Status","Cold","Hot","Converted","Declined"};
        ArrayAdapter<String> adapterLeadStatus = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,lead_status);
        leadStatus.setAdapter(adapterLeadStatus);













    }
}
