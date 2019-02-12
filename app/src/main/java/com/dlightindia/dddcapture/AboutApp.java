package com.dlightindia.dddcapture;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;


public class AboutApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_about_app);

        /*WebView myWebView = (WebView) findViewById(R.id.webViewAbout);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://www.dlight.com/about/");*/

        TextView txtDetails = (TextView) findViewById(R.id.textAboutLong);
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

    }
}
