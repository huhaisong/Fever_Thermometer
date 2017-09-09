package com.sanyecao.hu.fever_thermometer.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sanyecao.hu.fever_thermometer.R;
import com.sanyecao.hu.fever_thermometer.mode.database.a.DatabaseController;
import com.sanyecao.hu.fever_thermometer.mode.database.bean.TemperatureRecodeBean;
import com.sanyecao.hu.fever_thermometer.utils.TimeUtils;

import java.util.Date;
import java.util.List;

/**
 * 体温网格表
 * Created by huhaisong on 2017/8/18 14:11.
 */

public class FeverTemperatureGridView extends View {

    //-------------View相关-------------
    //View自身的宽和高
    private int mHeight;
    private int mWidth;

    //网格距离边框的距离
    private static final float topPadding = 100;
    private static final float leftPadding = 100;
    private static final float bottomPadding = 50;
    private static final float rightPadding = 50;

    //-------------统计图相关-------------
    //x轴的条目
    private int xNum = 25;
    //y轴的条目
    private int yNum = 21;
    //y轴条目之间的距离
    private int ySize;
    //x轴条目之间的距离
    private int xSize;

    private String[] xStr = new String[]{"00", "04", "08", "12", "16", "20", "24", "(h)"};
    private String[] yStr = new String[]{"35", "36", "37", "38", "39", "(°C)"};

    //-------------画笔相关-------------
    private Paint gridLinePaint;
    private Paint textPaint;
    private Paint gridDepthLinePaint;
    private Paint temperatureLinePaint;

    //当前网格表对应的时间和宝宝id
    private String time = "2017-9-7";
    private int babyId = 1;

    public FeverTemperatureGridView(Context context) {
        super(context);
    }

    public FeverTemperatureGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeverTemperatureGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取高宽值
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

        mWidth = width;
        ySize = xSize = (int) ((mWidth - leftPadding - rightPadding) / (xNum - 1));
        mHeight = (int) (ySize * (yNum - 1) + topPadding + bottomPadding);

        //设置这样就是一个正方形了
        setMeasuredDimension(mWidth, mHeight);
    }

    private static final String TAG = "FeverTimeGridView";

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.e(TAG, "onSizeChanged: ");


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: ");
        initPaint();
        drawGrid(canvas);
        //画黑点
//        drawPoint(canvas);
        drawText(canvas);
        drawLine(canvas);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        gridLinePaint = new Paint();
        gridLinePaint.setAntiAlias(true);
        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_color));
        gridLinePaint.setStrokeWidth(1);
        textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_color));
        textPaint.setAntiAlias(true);
        gridDepthLinePaint = new Paint();
        gridDepthLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));
        gridDepthLinePaint.setAntiAlias(true);
        gridDepthLinePaint.setStyle(Paint.Style.STROKE);
        gridDepthLinePaint.setStrokeWidth(3);
        temperatureLinePaint = new Paint();
        temperatureLinePaint.setAntiAlias(true);
        temperatureLinePaint.setStyle(Paint.Style.STROKE);
        temperatureLinePaint.setStrokeWidth(1);
        temperatureLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.orange));
    }

    /**
     * 画网格
     *
     * @param canvas 画布
     */
    private void drawGrid(Canvas canvas) {
        Path path = new Path();
        //横轴
        for (int i = 0; i < yNum; i++) {
            if (i == yNum - 1) {
                path.moveTo(leftPadding, i * ySize + topPadding);
                path.lineTo((xNum - 1) * (xSize) + leftPadding, i * ySize + topPadding);
                canvas.drawPath(path, gridDepthLinePaint);
            }
            if ((i + 2) % 4 == 0) {
                path.moveTo(leftPadding, i * ySize + topPadding);
                path.lineTo((xNum - 1) * (xSize) + leftPadding, i * ySize + topPadding);
                canvas.drawPath(path, gridDepthLinePaint);
            }
        }

        //纵抽
        for (int i = 0; i < xNum; i++) {
            if (i % 4 == 0) {
                path.moveTo(i * xSize + leftPadding, topPadding);
                path.lineTo(i * xSize + leftPadding, (yNum - 1) * (ySize) + topPadding);
                canvas.drawPath(path, gridDepthLinePaint);
            }
        }

        for (int i = 0; i < yNum; i++) {
            if ((i + 2) % 4 != 0) {
                path.moveTo(leftPadding, i * ySize + topPadding);
                path.lineTo((xNum - 1) * (xSize) + leftPadding, i * ySize + topPadding);
                canvas.drawPath(path, gridLinePaint);
            }
        }

        for (int i = 0; i < xNum; i++) {
            if (i % 4 != 0) {
                path.moveTo(i * xSize + leftPadding, topPadding);
                path.lineTo(i * xSize + leftPadding, (yNum - 1) * (ySize) + topPadding);
                canvas.drawPath(path, gridLinePaint);
            }
        }
    }

    /**
     * 画文字
     *
     * @param canvas 画布
     */
    private void drawText(Canvas canvas) {
        Rect rect = new Rect();
        textPaint.getTextBounds(xStr[xStr.length - 1], 0, xStr[xStr.length - 1].length(), rect);
        float textWidth = rect.width();
        float textHeight = rect.height();
        //画横轴的文字
        canvas.drawText(xStr[xStr.length - 1], -textWidth + xSize * (xNum - 1) + rightPadding + leftPadding, (yNum - 1) * ySize + topPadding + textHeight, textPaint);
        for (int i = 0; i < xStr.length * 4 - 5; i += 4) {
            textPaint.getTextBounds(xStr[i / 4], 0, xStr[i / 4].length(), rect);
            textWidth = rect.width();
            canvas.drawText(xStr[i / 4], -textWidth / 2 + leftPadding + xSize * i, (yNum - 1) * ySize + topPadding + textHeight, textPaint);
        }

        //画纵轴的文字
        textPaint.getTextBounds(yStr[yStr.length - 1], 0, yStr[yStr.length - 1].length(), rect);
        textWidth = rect.width();
        textHeight = rect.height();
        canvas.drawText(yStr[yStr.length - 1], leftPadding - textWidth, topPadding - textHeight / 2, textPaint);
        for (int i = 0; i < yStr.length * 4 - 5; i += 4) {
            //测量文字的宽高
            textPaint.getTextBounds(yStr[i / 4], 0, yStr[i / 4].length(), rect);
            textHeight = rect.height();
            canvas.drawText(yStr[i / 4], leftPadding - textWidth, (yNum - 1) * ySize + topPadding - (i + 2) * (ySize) + textHeight / 2, textPaint);
        }
    }

    /**
     * 画体温折线
     *
     * @param canvas 画布
     */
    private void drawLine(Canvas canvas) {
        Path path = new Path();
        List<TemperatureRecodeBean> temperatures = DatabaseController.getmInstance().queryTemperatureRecodeByBabyIdAndTimeOrderByTime(babyId, time);
        if (temperatures == null || temperatures.size() == 0)
            return;
        Date date = TimeUtils.string2Date(temperatures.get(0).getTime());
        int[] times;
        times = TimeUtils.getByDate(date);
        path.moveTo((times[3] + times[4] / 60) * xSize + leftPadding, (float) (topPadding + (yNum - 1) * ySize - (temperatures.get(0).getTemperature() - 34.5) * ySize / 0.25));
        float x;
        float y;
        for (TemperatureRecodeBean item : temperatures) {
            Log.e(TAG, "drawLine: " + item.toString());
            date = TimeUtils.string2Date(item.getTime());
            times = TimeUtils.getByDate(date);
            if (item.getTime().split(" ")[1].split(":")[0].equals("24"))
                times[3] = 24;
            x = (times[3] + times[4] / 60) * xSize + leftPadding;
            y = (float) (topPadding + (yNum - 1) * ySize - (item.getTemperature() - 34.5) * ySize / 0.25);
            path.lineTo(x, y);
            canvas.drawPath(path, temperatureLinePaint);
        }
    }


    public void setDataAndBaby(String time, Long babyId) {
        this.time = time;
        this.babyId = Integer.parseInt(String.valueOf(babyId));
        Log.e(TAG, "setDataAndBaby: " + this.babyId + "," + time);
        invalidate();
    }

    private void drawBitmap(Canvas canvas) {
//        canvas.drawBitmap(mImage, null, rect, mPaint);
    }

    private Rect rect = new Rect();
}