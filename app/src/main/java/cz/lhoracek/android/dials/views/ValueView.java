package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
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
    protected int mColorAccent    = Color.WHITE;
    protected int mColorOff       = Color.GRAY;
    protected int mColorOffAccent = Color.WHITE;

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

    public ValueView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttributes(context, attrs);
    }

    public void setValue(float value) {
        this.mValue = value;
        invalidate();
    }

    private void readAttributes(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ValueView);
        mColor = a.getColor(R.styleable.ValueView_scaleColor, Color.WHITE);
        mColorAccent = a.getColor(R.styleable.ValueView_scaleColorAccent, Color.WHITE);
        mColorOff = a.getColor(R.styleable.ValueView_scaleOffColor, Color.BLACK);
        mColorOffAccent = a.getColor(R.styleable.ValueView_scaleOffColorAccent, Color.BLACK);


        mMinValue = a.getFloat(R.styleable.ValueView_minValue, 0f);
        mMaxValue = a.getFloat(R.styleable.ValueView_maxValue, 0f);
        a.recycle();
    }


    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
    }
}
