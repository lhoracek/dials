package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lhoracek on 1/25/16.
 */
public class RPMView extends View {

    private Paint p        = new Paint();
    private float textSize = 20f;
    private int   rpmLimit = 12;
    private RectF mRectF   = new RectF();
    private int d = 0;

    //========================
    public RPMView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //========================
    public RPMView(Context ctx) {
        super(ctx);
    }

    //========================
    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh) {
        super.onSizeChanged(nw, nh, ow, oh);
        d = nw>nh?nh:nw;
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
        mRectF.set(0,0,d,d);
    }


    //========================
    @Override
    public void onDraw(Canvas c) {
        p.setColor(Color.RED);
        drawArc(c, 30, 90, p);
    }



    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees,
        Paint paint) {
        if (sweepDegrees <= 0 || sweepDegrees > 180) {
            return;
        }
        Path path = new Path();
        path.reset();
        path.arcTo(mRectF, startAngle, sweepDegrees);
        path.arcTo(new RectF(0,0,20,20), startAngle + sweepDegrees, -sweepDegrees);
        // innerCircle.
        path.close();
        canvas.drawPath(path, paint);
    }

}
