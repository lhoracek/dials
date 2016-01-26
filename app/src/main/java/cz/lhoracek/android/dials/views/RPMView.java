package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by lhoracek on 1/25/16.
 */
public class RPMView extends ValueView {

    private static final float WIDTH          = 0.085f;
    private static final float SMALLER_WIDTH  = 0.0f;
    private static final float SCALE_WIDTH    = 0.088f;
    private static final int   STRAIGHT_LINES = 9;
    private static final float STEP           = 2f;
    private static final float PAUSE          = 0.5f;
    private static final int   START_ANGLE    = 195;
    private static final int   SWEEP_ANGLE    = 90;
    private              int   mOvalWidth     = 0;
    private              int   mOvalHeight    = 0;

    public RPMView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RPMView(Context ctx) {
        super(ctx);
    }

    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        mOvalWidth = getWidth() * 3 / 2;
        mOvalHeight = getHeight() * 2;
    }

    @Override
    public void onDraw(Canvas c) {
        mPaint.setColor(mColor);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        drawArcs(c, mPaint);
    }

    private void drawArcs(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(mScaleColor);
        canvas.drawArc(new RectF(mOvalWidth * SCALE_WIDTH, mOvalWidth * SCALE_WIDTH, mOvalWidth * (1 - SCALE_WIDTH), mOvalHeight - (mOvalWidth * SCALE_WIDTH)), START_ANGLE, SWEEP_ANGLE, false, paint);

        // TODO paint dividers

        paint.setStyle(Paint.Style.FILL);
        int degreeRPM = (int) (mMaxValue / SWEEP_ANGLE);
        for (int i = 0; i < (SWEEP_ANGLE); i += STEP) {
            drawArc(canvas, i + START_ANGLE, STEP - PAUSE, mPaint, (mValue > ((i + 1) * degreeRPM)) ? mColor : mColorOff, (mValue > ((i + 1) * degreeRPM)) ? mColorAccent : mColorOffAccent);
        }
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees, Paint paint, int mainColor, int accentcolor) {
        paint.setColor(mainColor);
        Path path = new Path();
        path.arcTo(new RectF(mOvalWidth * WIDTH, mOvalWidth * WIDTH, mOvalWidth * (1 - WIDTH), mOvalHeight - (mOvalWidth * WIDTH)), startAngle + sweepDegrees, -sweepDegrees);
        path.arcTo(new RectF(0, 0, mOvalWidth, mOvalHeight), startAngle, sweepDegrees);
        // innerCircle.
        path.close();
        canvas.drawPath(path, paint);

        if (SMALLER_WIDTH > 0) {
            paint.setColor(accentcolor);
            Path path2 = new Path();
            path2.arcTo(new RectF(mOvalWidth * SMALLER_WIDTH, mOvalWidth * SMALLER_WIDTH, mOvalWidth * (1 - SMALLER_WIDTH), mOvalHeight - (mOvalWidth * SMALLER_WIDTH)), startAngle, sweepDegrees);
            path2.arcTo(new RectF(mOvalWidth * WIDTH, mOvalWidth * WIDTH, mOvalWidth * (1 - WIDTH), mOvalHeight - (mOvalWidth * WIDTH)), startAngle + sweepDegrees, -sweepDegrees);
            // innerCircle.
            path2.close();
            canvas.drawPath(path2, paint);
        }
    }
}