package com.myapp.unknown.iNTCON.OtherClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by UNKNOWN on 10/5/2016.
 */
public class ThreeHFourthWImageView extends ImageView {

    public ThreeHFourthWImageView(Context context) {
        super(context);
    }

    public ThreeHFourthWImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeHFourthWImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();

        int height = (int) (width * 0.75);

        setMeasuredDimension(width,height);
    }
}
