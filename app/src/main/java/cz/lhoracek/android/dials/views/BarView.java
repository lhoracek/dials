package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by lhoracek on 1/25/16.
 */
public class BarView extends ValueView {

    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarView(Context ctx) {
        super(ctx);
    }

    @Override
    public void onDraw(Canvas c) {
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(mColorOff);
        c.drawRect(new Rect(0, 0, getWidth(), getHeight()), mPaint);

        float range = mMaxValue - mMinValue;
        float value = Math.max(0,mValue - mMinValue) / range;
        mPaint.setColor(mColor);
        c.drawRect(new Rect(0, (int) (getHeight() * (1 - value)), getWidth(), getHeight()), mPaint);
    }
}