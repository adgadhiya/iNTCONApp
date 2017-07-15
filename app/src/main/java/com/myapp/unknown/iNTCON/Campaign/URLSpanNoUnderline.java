package com.myapp.unknown.iNTCON.Campaign;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by UNKNOWN on 12/17/2016.
 */
public class URLSpanNoUnderline extends URLSpan {

    public URLSpanNoUnderline(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
