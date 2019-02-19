package com.dlightindia.dddcapture;


import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

public class NetSuiteAPI extends DefaultApi10a{
    @Override
    public String getRequestTokenEndpoint() {
        return null;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    private static class InstanceHolder {
        private static final NetSuiteAPI INSTANCE = new NetSuiteAPI();
    }
    public static NetSuiteAPI instance() {
        return InstanceHolder.INSTANCE;
    }
    @Override
    protected String getAuthorizationBaseUrl() {
        return null;
    }
}
