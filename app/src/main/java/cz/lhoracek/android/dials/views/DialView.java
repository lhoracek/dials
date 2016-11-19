package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.text.DecimalFormat;

/**
 * Created by lhoracek on 1/25/16.
 */
public class DialView extends ValueView {

    private static final float WIDTH       = 0.2f;
    private static final int   START_ANGLE = 150;
    private static final int   SWEEP_ANGLE = 240;

    public DialView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialView(Context ctx) {
        super(ctx);
    }

    DecimalFormat formatter = new DecimalFormat("#,###.0");

    RectF mRectF = new RectF();
    Path mPath = new Path();

    @Override
    public void onDraw(Canvas c) {
        mPaint.setColor(mColor);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        drawArcs(c, mPaint);
        int xPos = (c.getWidth() / 2);
        int yPos = (int) ((c.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(getHeight() * WIDTH * 1.5f);

        // TODO formatting withou new instances (caching?)
        c.drawText(mValue > 0 ? formatter.format(mValue) : "", xPos, yPos, mPaint);
    }

    private void drawArcs(Canvas canvas, Paint paint) {
        float range = mMaxValue - mMinValue;
        float value = Math.max(0, mValue - mMinValue) / range;

        drawArc(canvas, START_ANGLE, SWEEP_ANGLE, mPaint, mColorOff);
        drawArc(canvas, START_ANGLE, SWEEP_ANGLE * value, mPaint, mColor);
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees, Paint paint, int mainColor) {
        int ovalWidth = getWidth();
        int ovalHeight = getHeight();

        paint.setColor(mainColor);

        mPath.reset();
        mRectF.set(ovalWidth * WIDTH, ovalHeight * WIDTH, ovalWidth - (ovalWidth * WIDTH), ovalHeight - (ovalHeight * WIDTH));
        mPath.arcTo(mRectF, startAngle + sweepDegrees, -sweepDegrees);
        mRectF.set(0, 0, ovalWidth, ovalHeight);
        mPath.arcTo(mRectF, startAngle, sweepDegrees);
        // innerCircle.
        mPath.close();
        canvas.drawPath(mPath, paint);
    }
}