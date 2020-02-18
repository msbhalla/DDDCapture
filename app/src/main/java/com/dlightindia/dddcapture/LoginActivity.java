package com.dlightindia.dddcapture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    //defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    private TextView textForgotPwd;
    private TextView textPrivacyPolicy;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //       WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_login);
         //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
             finish();
             startActivity(new Intent(getApplicationContext(), ProfileActivityNew.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
         editTextPassword = (EditText) findViewById(R.id.editTextPassword);
         buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup = (TextView) findViewById(R.id.textViewSignUp);
        textForgotPwd = (TextView) findViewById(R.id.textViewForgotPassword);
         textPrivacyPolicy = (TextView) findViewById(R.id.textViewPrivacyPolicyLogin);


        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textForgotPwd.setOnClickListener(this);
        textPrivacyPolicy.setOnClickListener(this);
    }


    private void forgetPassword() {
        String email = editTextEmail.getText().toString().trim();
        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Invoking password reset email. Please Wait...");
        progressDialog.show();

        FirebaseAuth.getInstance().sendPasswordResetEmail(editTextEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                @Override
                                               public void onComplete(Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                       Toast.makeText(LoginActivity.this, "Please check your email", Toast.LENGTH_LONG).show();
                                                        return;
                                                   }
                                                   else
                                                   {
                                                       Toast.makeText(LoginActivity.this, "Can't reset. "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                       editTextEmail.requestFocus();
                                                       return;
                                                   }
                                               }
                                           }
                    );
        }


    //method for user login
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Logging In! Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull 
                        if (task.isSuccessful()) {
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivityNew.class));
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogin();
        }

        if (view == textViewSignup) {
           // finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if (view == textForgotPwd) {
            forgetPassword();

        }
        if (view == textPrivacyPolicy)
        {
            Uri uri = Uri.parse("https://system.na2.netsuite.com/core/media/media.nl?id=344027&c=5025835&h=c8b11903b1c96e92afae&_xt=.html");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


        }


    }
}
