package com.myapp.unknown.iNTCON.NavigationView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import com.myapp.unknown.iNTCON.OtherClasses.OurClient;
import com.myapp.unknown.iNTCON.R;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView browser = (WebView) findViewById(R.id.privacy_policy_webView);

        browser.setWebViewClient(new OurClient());
        browser.getSettings().setSupportMultipleWindows(true);
        browser.getSettings().setLoadWithOverviewMode(true);
        browser.setHorizontalScrollBarEnabled(false);
        browser.loadUrl("https://message-mi.firebaseapp.com/");

        browser.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

    }
}
