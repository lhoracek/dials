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
public class DialView extends ValueView {

    private static final float WIDTH       = 0.2f;
    private static final int   START_ANGLE = 180;
    private static final int   SWEEP_ANGLE = 180;

    public DialView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialView(Context ctx) {
        super(ctx);
    }


    @Override
    public void onDraw(Canvas c) {
        mPaint.setColor(mColor);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        drawArcs(c, mPaint);
        int xPos = (c.getWidth() / 2);
        int yPos = (int) ((c.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(getHeight() * WIDTH);
        c.drawText(Float.toString(mValue), xPos, yPos, mPaint);
    }

    private void drawArcs(Canvas canvas, Paint paint) {
        float range = mMaxValue - mMinValue;
        float value = Math.max(0,mValue - mMinValue) / range;

        drawArc(canvas, START_ANGLE, SWEEP_ANGLE, mPaint, mColorOff);
        drawArc(canvas, START_ANGLE, SWEEP_ANGLE * value, mPaint, mColor);
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees, Paint paint, int mainColor) {
        int ovalWidth = getWidth();
        int ovalHeight = getHeight();

        paint.setColor(mainColor);
        Path path = new Path();
        path.arcTo(new RectF(ovalWidth * WIDTH, ovalHeight * WIDTH, ovalWidth - (ovalWidth * WIDTH), ovalHeight - (ovalHeight * WIDTH)), startAngle + sweepDegrees, -sweepDegrees);
        path.arcTo(new RectF(0, 0, ovalWidth, ovalHeight), startAngle, sweepDegrees);
        // innerCircle.
        path.close();
        canvas.drawPath(path, paint);
    }
}