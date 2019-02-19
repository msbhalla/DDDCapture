package com.dlightindia.dddcapture;

import android.util.Log;

import java.util.ConcurrentModificationException;
import java.util.Scanner;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.apis.FoursquareApi;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class CallNetSuiteAPI {
    private static final String CONSUMER_KEY = "4a05dfc2629a294d8531ba08df0cfa066aaeb223c3f27c22908815b824b854f4";
    private static final String CONSUMER_SECRET = "ab302cb8d8f4ae9db76b64b10707853968056860560d1d199e8ee808d1810a67";
    private static final String TOKEN_ID = "4fbb9340a52ac4dfec1f16149e0f38dc1523d5d132be295892ebc3d975d714a6";
    private static final String TOKEN_SECRET = "f95c5e361429802c694561a37fbaca58ff76920e8246e1c53f9ac9516910add8";
    private static final String REST_URL = "https://5025835.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=181&deploy=1";
    private static final String REALM = "5025835";
  //  private static OAuthService service = getService();
   // private static Token accessToken = getToken();

    CallNetSuiteAPI()
    {

    }

    public static Response main (String payloadFromForm) throws IOException, InterruptedException, ExecutionException
    {
        final OAuth10aService service = new ServiceBuilder(CONSUMER_KEY).userAgent("Suitescript-call").apiSecret(CONSUMER_SECRET).build(NetSuiteAPI.instance());
        final Scanner in = new Scanner(System.in);
        Log.d("Sparsh App","NetSuite Oauth Workflow");
        Log.d("Sparsh App","Fetching the request token");
        OAuth1AccessToken accessToken = new OAuth1AccessToken(TOKEN_ID, TOKEN_SECRET);
        Log.d("Sparsh App","Got the access token");

        OAuthRequest request = new OAuthRequest(Verb.POST,REST_URL);

        //Adding headers
        request.addHeader("Content-type","application/json");
        request.addHeader("Action","application/postscript");
        request.addHeader("User-Agent-x","Suitescript-call");
        Log.d("Sparsh App","Headers :"+request.getHeaders());
        request.setRealm(REALM);

        //Adding payload
        String payLoad = payloadFromForm;
        request.setPayload(payLoad);
        Log.d("Sparsh App","Body :"+request.getStringPayload());
        service.signRequest(accessToken,request);
        Log.d("Sparsh App","Request signed");
        final Response response = service.execute(request);
        Log.d("Sparsh App","Lets see what we got as response...");
        String code = response.getCode()+"\n"+response.getBody();
        Log.d("Sparsh App",code);

        try {
            JSONObject responseJSONObbject = new JSONObject(response.getBody());
            if (responseJSONObbject != null) {
                String JSON_message = responseJSONObbject.getString("Message");

                Log.d("Sparsh App", "Got it -" + JSON_message);
                String JSON_recordid = responseJSONObbject.getString("recordid");

                Log.d("Sparsh App", "Lead ID -" + JSON_recordid);
            }
        }
        catch (Exception e)
        {
            Log.d("Sparsh App", e.getMessage());
        }

        return response;
    }



    public static void main (String... args) throws IOException, InterruptedException, ExecutionException
    {
        final OAuth10aService service = new ServiceBuilder(CONSUMER_KEY).userAgent("Suitescript-call").apiSecret(CONSUMER_SECRET).build(NetSuiteAPI.instance());
        final Scanner in = new Scanner(System.in);
        Log.d("Sparsh App","NetSuite Oauth Workflow");
        Log.d("Sparsh App","Fetching the request token");
        OAuth1AccessToken accessToken = new OAuth1AccessToken(TOKEN_ID, TOKEN_SECRET);
        Log.d("Sparsh App","Got the access token");

        OAuthRequest request = new OAuthRequest(Verb.POST,REST_URL);

        //Adding headers
        request.addHeader("Content-type","application/json");
        request.addHeader("Action","application/postscript");
        request.addHeader("User-Agent-x","Suitescript-call");
        Log.d("Sparsh App","Headers :"+request.getHeaders());
        request.setRealm(REALM);

        //Adding payload
        String payLoad = "{\r\n\t\"operation\": \"create\",\r\n \t\"recordtype\": \"lead\",\r\n \t\"companyname\": \"Test Lead 22\",\r\n   \t\"phone\": \"9650038038\",\r\n \t\"email\": \"abc@abc22.com\",\r\n \t\"comments\":\"This is a sample comment\",\r\n \t\"note\":\"Now, this is working fine\",\r\n \t\"notetype\":9,\r\n \t\"shippingaddress\": [{\r\n \t\t\"city\": \"GURUGRAM\",\r\n \t\t\"country\": \"India\",\r\n \t\t\"state\": \"Delhi\",\r\n \t\t\"zip\": \"90001\",\r\n \t}]\r\n }\r\n";
        request.setPayload(payLoad);
        Log.d("Sparsh App","Body :"+request.getStringPayload());
        service.signRequest(accessToken,request);
        Log.d("Sparsh App","Request signed");
        final Response response = service.execute(request);
        Log.d("Sparsh App","Lets see what we got as response...");
        String code = response.getCode()+"\n"+response.getBody();
        Log.d("Sparsh App",code);

        try {
            JSONObject responseJSONObbject = new JSONObject(response.getBody());
            if (responseJSONObbject != null) {
                String JSON_message = responseJSONObbject.getString("Message");

                Log.d("Sparsh App", "Got it -" + JSON_message);
                String JSON_recordid = responseJSONObbject.getString("recordid");

                Log.d("Sparsh App", "Lead ID -" + JSON_recordid);
            }
        }
        catch (Exception e)
        {
            Log.d("Sparsh App", e.getMessage());
            }
        }
    }




