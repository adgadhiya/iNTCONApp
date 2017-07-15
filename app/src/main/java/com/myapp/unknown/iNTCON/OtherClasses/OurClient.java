package com.myapp.unknown.iNTCON.OtherClasses;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by UNKNOWN on 12/17/2016.
 */
public class OurClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
