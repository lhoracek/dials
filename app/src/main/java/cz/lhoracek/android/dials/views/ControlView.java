package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import cz.lhoracek.android.dials.R;

/**
 * Created by lhoracek on 1/25/16.
 */
public class ControlView extends View {

    private Paint    mPaint    = new Paint();
    private int      mColor    = Color.WHITE;
    private int      mColorOff = Color.WHITE;
    private boolean  mEnabled  = false;
    private Drawable mIcon     = null;

    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
    }

    public ControlView(Context ctx) {
        super(ctx);
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        invalidate();    }

    private void readAttributes(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ValueView);
        mColor = a.getColor(R.styleable.ValueView_scaleColor, Color.WHITE);
        mColorOff = a.getColor(R.styleable.ValueView_scaleOffColor, Color.WHITE);
        mIcon = a.getDrawable(R.styleable.ValueView_controlIcon);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c) {
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        int d = Math.min(getWidth(), getHeight());

        mPaint.setColor(mColorOff);
        c.drawCircle(getWidth() / 2, getHeight() / 2, d / 2, mPaint);

        if (mEnabled) {
            mPaint.setColor(mColor);
            c.drawCircle(getWidth() / 2, getHeight() / 2, d / 2, mPaint);
        }

        mPaint.setTextAlign(Paint.Align.CENTER);
        mIcon.setBounds((int) (getWidth() * 0.2f), (int) (getHeight() * 0.2f), (int) (getWidth() * 0.8f), (int) (getHeight() * 0.8f));
        mIcon.draw(c);
    }
}