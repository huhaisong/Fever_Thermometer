package com.sanyecao.hu.fever_thermometer.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.sanyecao.hu.fever_thermometer.R;

/**
 * Created by huhaisong on 2017/8/19 10:50.
 */

public class CircleView extends View {

    private Paint mPaint = new Paint();
    private float mRadius;
    private int mWidth;
    private int mHeight;

    /**
     * 这个是从代码中创建自定义控件时间调用
     *
     * @param context
     */
    public CircleView(Context context) {
        this(context, null);
        circleColor = ContextCompat.getColor(getContext(), R.color.blue);
        stringColor = ContextCompat.getColor(getContext(), R.color.white);
    }

    /**
     * 这个是从xml中创建自定义控件时间调用
     *
     * @param context
     * @param attrs
     */
    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        circleColor = ContextCompat.getColor(getContext(), R.color.blue);
        stringColor = ContextCompat.getColor(getContext(), R.color.white);
    }

    /**
     * 这个是从xml中创建自定义控件时间调用，并且带有Style
     * AttributeSet 可以得到这个控件的所有属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        circleColor = ContextCompat.getColor(getContext(), R.color.blue);
        stringColor = ContextCompat.getColor(getContext(), R.color.white);
    }

    private int circleColor;
    private int stringColor;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setDither(true); //防抖
        mPaint.setAntiAlias(true); //抗锯齿
        mPaint.setStrokeWidth(50); //设置画笔的线宽
        mPaint.setColor(circleColor);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth * 1 / 2, mPaint);

        mPaint.setColor(stringColor);
        Rect rect = new Rect();
        mPaint.setTextSize(80);
        mPaint.getTextBounds(content, 0, content.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        canvas.drawText(content, mWidth / 2 - textWidth / 2, mHeight / 2 + textHeight / 2, mPaint);
    }

    private static final String TAG = "CircleView";
    private String content = "Unconnected";


    public void setText(String content) {
        this.content = content;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int hightSize = MeasureSpec.getSize(heightMeasureSpec);
        int hightMode = MeasureSpec.getMode(heightMeasureSpec);

        //拿到宽和高的最小值，也就是宽
        int width = Math.min(widthSize, heightMeasureSpec);

        //根据测量模式细节处理
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = hightSize;
        } else if (hightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        //设置这样就是一个正方形了
        setMeasuredDimension(width / 2, width / 2);
        mWidth = width / 2;
        mHeight = width / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setContent(String string) {
        content = string;
        invalidate();
    }

    public void setCircleColor(int color) {
        this.circleColor = color;
        invalidate();
    }

    public void setStringColor(int color) {
        this.stringColor = color;
        invalidate();
    }
}
