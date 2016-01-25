package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cz.lhoracek.android.dials.R;

/**
 * Created by lhoracek on 1/25/16.
 */
public class RPMView extends View {

    private static final float WIDTH          = 0.07f;
    private static final int   STRAIGHT_LINES = 9;
    private static final float STEP           = 2f;
    private static final float PAUSE          = 0.5f;
    private static final int   START_ANGLE    = 195;
    private static final int   SWEEP_ANGLE    = 90;

    private Paint mPaint       = new Paint();
    private int   mColor       = Color.WHITE;
    private int   mColorAccent = Color.WHITE;

    private int mColorOff       = Color.GRAY;
    private int mColorOffAccent = Color.WHITE;

    private int mMinValue = 0;
    private int mMaxValue = 13000;
    private int mValue    = 6000;

    public RPMView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readColor(context, attrs);
    }

    public RPMView(Context ctx) {
        super(ctx);
    }

    private void readColor(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.DialView);
        mColor = a.getColor(R.styleable.DialView_scaleColor, Color.WHITE);
        mColorAccent = a.getColor(R.styleable.DialView_scaleColorAccent, Color.WHITE);
        mColorOff = a.getColor(R.styleable.DialView_scaleOffColor, Color.BLACK);
        mColorOffAccent = a.getColor(R.styleable.DialView_scaleOffColorAccent, Color.BLACK);
        a.recycle();
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
    }

    @Override
    public void onDraw(Canvas c) {
        mPaint.setColor(mColor);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        drawArcs(c, mPaint);
    }

    public void setValue(int value) {
        this.mValue = value;
        invalidate();
    }

    private void drawArcs(Canvas canvas, Paint paint) {
        int degreeRPM = mMaxValue / SWEEP_ANGLE;
        for (int i = 0; i < (SWEEP_ANGLE); i += STEP) {
            drawArc(canvas, i + START_ANGLE, STEP - PAUSE, mPaint, (mValue > ((i + 1) * degreeRPM)) ? mColor : mColorOff, (mValue > ((i + 1) * degreeRPM)) ? mColorAccent : mColorOffAccent);
        }
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees, Paint paint, int mainColor, int accentcolor) {
        int ovalWidth = getWidth() * 3 / 2;
        int ovalHeight = getHeight() * 2;

        paint.setColor(mainColor);
        Path path = new Path();
        path.arcTo(new RectF(ovalWidth * WIDTH, ovalWidth * WIDTH, ovalWidth * (1 - WIDTH), ovalHeight - (ovalWidth * WIDTH)), startAngle + sweepDegrees, -sweepDegrees);
        path.arcTo(new RectF(0, 0, ovalWidth, ovalHeight), startAngle, sweepDegrees);
        // innerCircle.
        path.close();
        canvas.drawPath(path, paint);

        paint.setColor(accentcolor);
        Path path2 = new Path();
        path2.arcTo(new RectF(ovalWidth * (WIDTH + 5 * (WIDTH * WIDTH)), ovalWidth * (WIDTH + 5 * (WIDTH * WIDTH)), ovalWidth * (1 - (WIDTH + 5 * (WIDTH * WIDTH))), ovalHeight - (ovalWidth * (WIDTH + 5 * (WIDTH * WIDTH)))), startAngle, sweepDegrees);
        path2.arcTo(new RectF(ovalWidth * WIDTH, ovalWidth * WIDTH, ovalWidth * (1 - WIDTH), ovalHeight - (ovalWidth * WIDTH)), startAngle + sweepDegrees, -sweepDegrees);

        // innerCircle.
        path2.close();
        canvas.drawPath(path2, paint);

    }
}