package com.myapp.unknown.iNTCON.OtherClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by UNKNOWN on 1/27/2017.
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredWidth();

        setMeasuredDimension(width,height);
    }
}
