package cz.lhoracek.android.dials.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lhoracek on 1/25/16.
 */
public class RPMView extends View {

    private Paint p = new Paint();
    private float textSize = 20f;
    private int rpmLimit = 12000;
    //========================
    public RPMView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //========================
    public RPMView(Context ctx){super(ctx);}

    //========================
    @Override
    public void onSizeChanged(int nw, int nh, int ow, int oh){
        super.onSizeChanged(nw, nh, ow, oh);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);
    }


    //========================
    @Override
    public void onDraw(Canvas c){

        for(int i=0;i<rpmLimit;i++){
            int perc = Integer.parseInt(data[i][1]);
            int pdeg = (perc*360)/100;
            ea = sa+pdeg;
            p.setColor(Color.parseColor(RColors[ci++]));
            if(ci==RColors.length)
                ci=0;
            c.drawArc(rec, sa+2, pdeg-2, true, p);
            sa = ea;
        }

//        //== draw circle in center
//        p.setColor(Color.WHITE);
//        c.drawCircle(rec.right/2, rec.bottom/2, (int)(0.8*(d/2)) , p);
//
//        //== write text
//        p.setColor(Color.BLACK);
//        p.setFakeBoldText(true);
//        p.setTextSize(textSize);
//        sa=0;ea=0;ci=0;
//        double ra = 0; // Radian angle
//        for(int i=0;i<size;i++){
//            p.setColor(Color.parseColor(RColors[ci++]));
//            if(ci==RColors.length)
//                ci=0;
//            int perc = Integer.parseInt(data[i][1]);
//            int pdeg = (perc*360)/100;
//            ea = sa+pdeg; //== in degrees
//            ra = (sa+pdeg/2)*Math.PI/180;
//            int x = (int)(rec.right/2+(((rec.right/2)*.5)*Math.cos(ra)));
//            int y = (int)(rec.right/2+(((rec.right/2)*.5)*Math.sin(ra)));
//            String text = data[i][0];
//            c.drawText(text, x-p.measureText(text)/2, y, p);
//            text = data[i][1]+"%";
//            c.drawText(text, x-p.measureText(text)/2, y-p.ascent()+p.descent(), p);
//            sa = ea;
        }
    }
}
