package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import cz.lhoracek.android.dials.R;

/**
 * Created by lhoracek on 1/25/16.
 */
public abstract class ValueView extends View {
    protected float mValue, mMinValue, mMaxValue;
    protected Paint mPaint = new Paint();

    protected int mColor          = Color.WHITE;
    protected int mColorOff       = Color.GRAY;
    protected int mScaleColor     = Color.WHITE;
    protected int mWarningColor     = Color.TRANSPARENT;

    protected int mWarningMaxValue = Integer.MAX_VALUE;
    protected int mWarningMinValue = Integer.MIN_VALUE;

    public ValueView(Context context) {
        super(context);
    }

    public ValueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
    }

    public ValueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ValueView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttributes(context, attrs);
    }

    public void setValue(float value) {
        this.mValue = value;
        invalidate();
    }

    public float getMinValue() {
        return mMinValue;
    }

    public float getMaxValue() {
        return mMaxValue;
    }

    public int getWarningMaxValue() {
        return mWarningMaxValue;
    }

    public void setWarningMaxValue(int mWarningMaxValue) {
        this.mWarningMaxValue = mWarningMaxValue;
    }

    public int getWarningMinValue() {
        return mWarningMinValue;
    }

    public void setWarningMinValue(int mWarningMinValue) {
        this.mWarningMinValue = mWarningMinValue;
    }

    protected void readAttributes(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ValueView);
        mColor = a.getColor(R.styleable.ValueView_scaleColor, Color.WHITE);
        mColorOff = a.getColor(R.styleable.ValueView_scaleOffColor, Color.BLACK);
        mScaleColor = a.getColor(R.styleable.ValueView_scaleLineColor, Color.WHITE);
        mWarningColor = a.getColor(R.styleable.ValueView_warningColor, Color.TRANSPARENT);

        mMinValue = a.getFloat(R.styleable.ValueView_minValue, 0f);
        mMaxValue = a.getFloat(R.styleable.ValueView_maxValue, 0f);
        a.recycle();
    }


    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
    }
}
