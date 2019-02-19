package com.dlightindia.dddcapture;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.view.View;

public class AddNewLead_WebForm extends AppCompatActivity {

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lead__web_form);



        WebView myWebView = (WebView) findViewById(R.id.webViewWebForm);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(true);
        myWebView.loadUrl("https://forms.na2.netsuite.com/app/site/crm/externalleadpage.nl/compid.5025835/.f?formid=38&h=AACffht_Ja846IQ7BgYI4nUWMBXZDylfuaw&redirect_count=1&did_javascript_redirect=T");
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setMax(100);
        progressBar.setMin(0);

        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });
        myWebView.setWebViewClient(new WebViewClient() {


            public void onPageStarted(WebView view, String url) {
                progressBar.setVisibility(View.VISIBLE);


            }



            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
