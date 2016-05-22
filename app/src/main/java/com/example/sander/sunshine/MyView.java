package com.example.sander.sunshine;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class MyView extends View {

public String mWindDirection;
public String mWindSpeed;
public Paint paint;

    public MyView(Context context){
        super(context);

    }
    public MyView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        TypedArray array=context.obtainStyledAttributes(attributeSet,R.styleable.MyView,0,0);
        try {
            mWindDirection=array.getString(R.styleable.MyView_winddirection);
            mWindSpeed=array.getString(R.styleable.MyView_windspeed);
        }finally {
            array.recycle();
        }
    }
    public MyView(Context context,AttributeSet attributeSet,int defaultStyle){
        super(context,attributeSet,defaultStyle);
    }
    public void setWindDirection(String windDirection) {
      mWindDirection=windDirection;
        invalidate();
        requestLayout();
    }
    public void setWindSpeed(String windSpeed){
        mWindSpeed=windSpeed;
        invalidate();
        requestLayout();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        paint=new Paint();
        int hSpecMode=MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize=MeasureSpec.getSize(heightMeasureSpec);
        int mHeight=hSpecSize;
        if (hSpecMode==MeasureSpec.EXACTLY)
            mHeight=hSpecSize;

        else if (hSpecMode==MeasureSpec.AT_MOST){

        }

        int WSpecMode=MeasureSpec.getMode(widthMeasureSpec);
        int WSpecSize=MeasureSpec.getSize(widthMeasureSpec);
        int mWidth=WSpecSize;

        if (WSpecMode==MeasureSpec.EXACTLY)
            mWidth=WSpecSize;
        else if (WSpecMode==MeasureSpec.AT_MOST) {

        }
       setMeasuredDimension(mWidth,mHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(140, 140, 140, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(140, 140, 100, paint);
        paint.setColor(Color.RED);
        canvas.drawText(mWindSpeed,80,250,paint);
        switch (mWindDirection) {
            case "N":
                canvas.drawLine(140, 140, 140, 20, paint);
                break;
            case "S":
                canvas.drawLine(140, 140, 140, 260, paint);
                break;
            case "NE":
                canvas.drawLine(140, 140, 220, 40, paint);
                break;
            case "SE":
                canvas.drawLine(140,140,220,230,paint);
                break;
            case "SW":
                canvas.drawLine(140,140,60,230,paint);
                break;
            case "NW":
                canvas.drawLine(140,140,60,50,paint);
                break;
            case "W":
                canvas.drawLine(140,140,260,140,paint);
                break;
            case "E":
                canvas.drawLine(140,140,20,140,paint);
                break;
            default:
                canvas.drawText("direction unknown",140,140,paint);
        }


        paint.setColor(Color.BLACK);





    }

}
