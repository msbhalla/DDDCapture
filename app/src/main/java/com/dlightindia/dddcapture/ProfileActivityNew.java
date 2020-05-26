package com.dlightindia.dddcapture;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import constant.Constant;

public class ProfileActivityNew  extends AppCompatActivity implements View.OnClickListener {

     private FirebaseAuth firebaseAuth;
     private TextView textViewUserEmail;
     LinearLayout lin_newlead,lin_updatelead,lin_marketvisit,lin_ifc_sale,lin_au_finance,lin_about,lin_ifc,lin_sku,lin_sep;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_new);
        getSupportActionBar().setTitle("D.light Home");
        lin_newlead =   findViewById(R.id.lin_newlead);
        lin_updatelead =   findViewById(R.id.lin_updatelead);
        lin_marketvisit =   findViewById(R.id.lin_marketvisit);
        lin_ifc_sale =   findViewById(R.id.lin_ifc_sale);
        lin_au_finance =   findViewById(R.id.lin_au_finance);
        lin_about =   findViewById(R.id.lin_about);
        lin_ifc =   findViewById(R.id.lin_ifc);
        lin_sku =   findViewById(R.id.lin_sku);
        lin_sep =   findViewById(R.id.lin_sep);

        lin_newlead.setOnClickListener(this);
        lin_updatelead.setOnClickListener(this);
        lin_marketvisit.setOnClickListener(this);
        lin_ifc_sale.setOnClickListener(this);
        lin_au_finance.setOnClickListener(this);
        lin_about.setOnClickListener(this);
        lin_ifc.setOnClickListener(this);
        lin_sku.setOnClickListener(this);
        lin_sep.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
         if(firebaseAuth.getCurrentUser() == null){
             finish();
             startActivity(new Intent(this, LoginActivity.class));
        }
         FirebaseUser user = firebaseAuth.getCurrentUser();
         textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
         textViewUserEmail.setText("Welcome "+user.getEmail());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_btn, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mybutton) {
            firebaseAuth.signOut();
             finish();
             startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
     switch (view.getId()){
         case R.id.lin_newlead:
             Constant.openActivity(ProfileActivityNew.this,Lead.class);
         break;
         case R.id.lin_updatelead:
             Constant.openActivity(ProfileActivityNew.this,SearchLeadActivity.class);
             break;
         case R.id.lin_marketvisit:
             Constant.openActivity(ProfileActivityNew.this,MarketWorking.class);
             break;
         case R.id.lin_ifc_sale:
              alertDialog_dis();
              break;
         case R.id.lin_au_finance:
             Constant.openActivity(ProfileActivityNew.this,AuFinance.class);
             break;
         case R.id.lin_about:
             Constant.openActivity(ProfileActivityNew.this,AboutApp.class);
             break;
         case R.id.lin_ifc:
             Constant.openActivity(ProfileActivityNew.this,IFCCalling.class);
             break;
         case R.id.lin_sku:
             Constant.openActivity(ProfileActivityNew.this,SKU.class);
             break;
         case R.id.lin_sep:
             alertDialog_sep();
              break;
     }
    }
    protected void alertDialog_sep() {
         final AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage("Please Select")
                .setCancelable(false)
                .setPositiveButton("SEP Registration", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Constant.openActivity(ProfileActivityNew.this,SepRegister.class);

                     }
                })
                .setNegativeButton("SEP Sale", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Constant.openActivity(ProfileActivityNew.this,SepSales.class);
                      }
                });
        final AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }
    protected void alertDialog_dis() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Select")
                .setCancelable(false)
                .setPositiveButton("Followup Remarks", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Constant.openActivity(ProfileActivityNew.this,Followup.class);
                    }
                })
                .setNegativeButton("New Lead/Sale", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Constant.openActivity(ProfileActivityNew.this,Sales.class);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again for exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
