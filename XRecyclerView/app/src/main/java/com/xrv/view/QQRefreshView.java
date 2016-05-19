package com.xrv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * qq refresh
 * Created by xxoo on 2016/5/19.
 */
public class QQRefreshView extends FrameLayout implements Runnable {

    private float defaultRadius;// 起始圆的半径

    private float mStartY;// 起点坐标
    private float mStartX;// 起点坐标

    private float x; // 手势坐标
    private float y = 0; // 手势坐标


    private float mRadius;// 定点圆半径


    private boolean isStop = false;// 判断圆是否隐藏
    private boolean isStart = false;// 判断是否开启旋转圆圈
    private Paint mCirclePaint;
    private Paint mScalePaint;
    private Path mPath;
    private int mStep = 3;// 小圆半径递减step
    private int mCirclePos; // 旋转圆的位置
    private int mCircleDis; // 旋转棍子的长度
    private int mDegree;// 选择的角度


    private Thread mThread;

    public QQRefreshView(Context context) {
        super(context);
        init();
    }

    public QQRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QQRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        x = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;// 为了居中
        mStartX = x;
        int height = this.getLayoutParams().height;
        defaultRadius = mRadius = height / 4f;// 可以自行调整
        mStartY = height / 4;// 起点至少为圆半径长度，也就是（圆点）位置
        mCirclePos = height / 4;// 和上面保持一致
        if(mCirclePos < 20){// 如果头部高度过低，则缩短旋转刻度的大小
            mCircleDis  = mCirclePos / 3;
        }else{
            mCircleDis = 10;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isStop) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
            resetStartY();
            drawScale(canvas);
            drawRunScale(canvas, mDegree);
        }
        else {
            calculate();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
            canvas.drawPath(mPath, mCirclePaint);
            canvas.drawCircle(mStartX, mStartY, mRadius, mCirclePaint);
            mRadius -= mStep;// 确保上面的圆大于下面的圆
            canvas.drawCircle(x, y, mRadius, mCirclePaint);
            super.onDraw(canvas);
        }
    }

    @Override
    public void run() {
        while (isStart) {
            try {
                Thread.sleep(100);
                runTime(mDegree);
                mDegree += 1;
                if (mDegree > 11) {
                    mDegree = 0;
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实时更新距离，确保圆与圆之间的联系能断掉
     */
    public void setVY(float my) {
        postInvalidate();
        if (my < 0) {// 下拉操作
            y += -my / 5;// 可以自行调整
        }
        else {// 回弹操作
            y -= my / 5;// 可以自行调整
        }
    }

    public void start() {
        if (mThread == null) {
            mThread = new Thread(this);
            isStart = true;
            mThread.start();
        }
    }

    public void stop() {
        if (mThread != null) {
            isStart = false;
            isStop = false;
            mThread.interrupt();
            mThread = null;
        }
    }

    /**
     * 设置旋转角度
     */
    private void runTime(int i) {
        this.mDegree = i;
        postInvalidate();
    }

    /**
     * 绘制底层棍子
     */
    private void drawScale(Canvas canvas) {
        for (int i = 0; i < 12; i++) {
            drawBaseStick(canvas);
        }
    }

    /**
     * 绘制基础棍子
     * @param canvas
     */
    private void drawBaseStick(Canvas canvas){
        canvas.drawLine(mStartX - 10 * (mCirclePos / 20 + 1), mCirclePos, mStartX - (10 * (mCirclePos / 20 + 1) + mCircleDis), mCirclePos, mScalePaint);
        canvas.rotate(30, mStartX, mCirclePos);
    }


    /**
     * 绘制看似旋转的棍子
     */
    private void drawRunScale(Canvas canvas, int degree) {
        int pre, next;

        switch (degree) {
            case 0:
                pre = 11;
                next = 1;
                break;
            case 11:
                pre = 10;
                next = 0;
                break;
            default:
                pre = degree - 1;
                next = degree + 1;
        }
        for (int j = 0; j < 12; j++) {
            if (j == pre) {
                mScalePaint.setColor(Color.parseColor("#D3D3D3"));
                drawBaseStick(canvas);
            }
            else if (j == degree) {
                mScalePaint.setColor(Color.parseColor("#808080"));
                drawBaseStick(canvas);
            }
            else if (j == next) {
                mScalePaint.setColor(Color.parseColor("#696969"));
                drawBaseStick(canvas);
            }
            else {
                mScalePaint.setColor(Color.parseColor("#A9A9A9"));
                drawBaseStick(canvas);
            }
        }
    }

    /**
     * 初始化准备
     */
    private void init() {
        mPath = new Path();
        /** 圆的 paint */
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setStrokeWidth(2);
        mCirclePaint.setColor(Color.GRAY);
        /** 棍子的 paint */
        mScalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStyle(Paint.Style.FILL);
        mScalePaint.setColor(Color.GRAY);
        mScalePaint.setStrokeWidth(5);
    }

    /**
     * 利用贝塞尔
     */
    private void calculate() {
        mRadius =  mRadius == 0 ? 0 : defaultRadius - y / 5f ; // 可以自行调整
        if (y >= mStartY * 3) {
            isStop = true;
        }
        else if (y <= mStartY) {
            resetStartY();
            isStop = false;
        }
        else {

            float offsetX = mRadius;

            float x1 = mStartX - offsetX;
            float y1 = mStartY;
            float x4 = mStartX + offsetX;
            float y4 = mStartY;

            float x2 = mStartX - offsetX;
            float y2 = y;
            float x3 = mStartX + offsetX;
            float y3 = y;

            mPath.reset();
            mPath.moveTo(x1, y1);
            mPath.quadTo(mStartX, mStartY, x2 + mStep, y2);
            mPath.lineTo(x3 - mStep, y3);
            mPath.quadTo(mStartX, mStartY, x4, y4);
            mPath.lineTo(x1, y1);
        }
    }

    /**
     * 重置Y起点
     */
    private void resetStartY() {
        y = mStartY;
    }
}
