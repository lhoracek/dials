package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * Created by lhoracek on 1/25/16.
 */
public class RPMView extends ValueView {

    private static final float WIDTH = 0.085f;
    private static final float SCALE_WIDTH = 0.088f;
    private static final float STEP = 3f;
    private static final float PAUSE = 0.5f;
    private static final int START_ANGLE = 195;
    private static final int SWEEP_ANGLE = 90;
    private int mOvalWidth = 0;
    private int mOvalHeight = 0;

    Path path = new Path();
    RectF mRectF = new RectF();

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
        super.onDraw(c);
    }

    private void drawArcs(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(mScaleColor);
        mRectF.set(mOvalWidth * SCALE_WIDTH, mOvalWidth * SCALE_WIDTH, mOvalWidth * (1 - SCALE_WIDTH), mOvalHeight - (mOvalWidth * SCALE_WIDTH));
        canvas.drawArc(mRectF, START_ANGLE, SWEEP_ANGLE - PAUSE, false, paint);

        int stepThousand = mMaxValue > 6000 ? 2 : 1;
        int numberThousand = stepThousand * 2;
        float partDegrees = SWEEP_ANGLE / ((mMaxValue - mMinValue) / (stepThousand * 1000));
        float degree = 0;
        int thousand = 0;
        while (degree <= SWEEP_ANGLE) {
            for (int i = 0; i < 10; i += 3) {
                paint.setColor(mScaleColor);
                mRectF.set(mOvalWidth * SCALE_WIDTH + i, mOvalWidth * SCALE_WIDTH + i, mOvalWidth * (1 - SCALE_WIDTH) - i, mOvalHeight - (mOvalWidth * SCALE_WIDTH) - i);
                canvas.drawArc(mRectF, START_ANGLE + degree, 0.5f, false, paint);
            }

            if (thousand % 4 == 0) {
                path.reset();
                mRectF.set(mOvalWidth * SCALE_WIDTH + 33, mOvalWidth * SCALE_WIDTH + 33, mOvalWidth * (1 - SCALE_WIDTH) - 33, mOvalHeight - (mOvalWidth * SCALE_WIDTH) - 33);
                path.arcTo(mRectF, START_ANGLE + degree + 1, 20);
                path.close();
                Color.argb(128, Color.red(mScaleColor), Color.green(mScaleColor), Color.blue(mScaleColor));
                paint.setColor(Color.GRAY);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTextSize(30);
                canvas.drawTextOnPath(String.valueOf(thousand), path, 0, 0, paint);
            }

            degree += partDegrees;
            thousand += stepThousand;
        }

        paint.setStyle(Paint.Style.FILL);
        int degreeRPM = (int) (mMaxValue / SWEEP_ANGLE);
        for (int i = 0; i < (SWEEP_ANGLE); i += STEP) {
            drawArc(canvas, i + START_ANGLE, STEP - PAUSE, mPaint, (mValue > ((i + 1) * degreeRPM)) ? ((((i + 1) * degreeRPM) > mWarningMaxValue) ? mWarningColor :mColor) : mColorOff);
        }
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees, Paint paint, int mainColor) {
        paint.setColor(mainColor);
        path.reset();
        mRectF.set(mOvalWidth * WIDTH, mOvalWidth * WIDTH, mOvalWidth * (1 - WIDTH), mOvalHeight - (mOvalWidth * WIDTH));
        path.arcTo(mRectF, startAngle + sweepDegrees, -sweepDegrees);
        mRectF.set(0, 0, mOvalWidth, mOvalHeight);
        path.arcTo(mRectF, startAngle, sweepDegrees);
        // innerCircle.
        path.close();
        canvas.drawPath(path, paint);
    }
}