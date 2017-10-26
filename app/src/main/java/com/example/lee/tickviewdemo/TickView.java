package com.example.lee.tickviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by lee on 2017/10/25.
 */

public class TickView extends View {

    private Paint gray_paint;
    private Paint yellow_paint;
    private Paint white_paint;

    //当前进度
    private int currentProgress;

    //线宽
    private float width_line;

    //半径
    private float radius;

    private int color_unselector;
    private int color_loading;
    private int color_success;
    private int duration_loading;
    private float tickOffset = 10;

    //毫秒
    private static final int DURATION_TIME = 1500;
    //dp
    private static final int DEFAULT_WIDTH_LINE = 3;
    //dp
    private static final int DEFAULT_RADIUS = 20;

    //长宽高
    private float width;
    private float height;

    private RectF rectF;

    //中心点
    private float center;

    private Path linePath;

    //直径
    private float diameter;

    //打勾第一点
    private float lineX1;
    private float lineY1;

    //打勾第二点
    private float lineX2;
    private float lineY2;

    //打勾第三点
    private float lineX3;
    private float lineY3;

    private float radius_success;

    private long duration_one_time;

    private PathMeasure pathMeasure;


    private static final int ANIMATOR = 001;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ANIMATOR:
                    invalidate();

                    break;
            }
        }
    };

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;

        invalidate();
    }

    public float getWidth_line() {
        return width_line;
    }

    public void setWidth_line(float width_line) {
        this.width_line = width_line;
    }

    public TickView(Context context) {
        this(context, null);
    }

    public TickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.TickView);

        duration_loading = type.getInt(R.styleable.TickView_druation_loading, DURATION_TIME);
        width_line = type.getDimension(R.styleable.TickView_width_line, DensityUtils.dip2px(context, DEFAULT_WIDTH_LINE));
        color_unselector = type.getColor(R.styleable.TickView_color_unselect, 0xFF4081);
        color_success = type.getColor(R.styleable.TickView_color_load_success, 0xadadad);
        color_loading = type.getColor(R.styleable.TickView_color_loading, 0xFF4081);
        radius = type.getDimension(R.styleable.TickView_radius, DensityUtils.dip2px(context, DEFAULT_RADIUS));
        initPaint();

    }

    private void initPaint() {

        gray_paint = new Paint();
        gray_paint.setColor(color_unselector);
        gray_paint.setAntiAlias(true);
        gray_paint.setStyle(Paint.Style.STROKE);
        gray_paint.setStrokeWidth(width_line);

        yellow_paint = new Paint();
        yellow_paint.setColor(color_loading);
        yellow_paint.setAntiAlias(true);
        yellow_paint.setStyle(Paint.Style.STROKE);
        yellow_paint.setStrokeWidth(width_line);

        white_paint = new Paint();
        white_paint.setColor(Color.parseColor("#ffffff"));
        white_paint.setAntiAlias(true);
        white_paint.setStyle(Paint.Style.FILL);
        white_paint.setStrokeWidth(width_line);

        duration_one_time = duration_loading / 10;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthsize = MeasureSpec.getSize(widthMeasureSpec);
//        int widthmode = MeasureSpec.getMode(widthMeasureSpec);

        int heightsize = MeasureSpec.getSize(heightMeasureSpec);
//        int heightmode = MeasureSpec.getMode(heightMeasureSpec);

//        switch (widthmode) {
//            case MeasureSpec.AT_MOST:
//                //match_parent
//
//                break;
//            case MeasureSpec.EXACTLY:
//                //确定值
//                break;
//            case MeasureSpec.UNSPECIFIED:
//                //wrap_content
//
//                break;
//        }

        int min = Math.min(widthsize, heightsize);
        setMeasuredDimension(min, min);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        diameter = (Math.min(width, height) - (2 * width_line));

        if (radius > (diameter / 2)) {
            radius = diameter / 2;
        }

        center = diameter / 2;

        rectF = new RectF(center - radius, center - radius, center + radius, radius + center);


        if (currentProgress < 100) {
            radius_success = radius;
            drawUnStart(canvas);
            drawLoading(canvas);
        }

        if (currentProgress >= 100) {
            drawSuccess(canvas);
        }

    }

    //加载成功动画
    private void drawSuccess(Canvas canvas) {

        yellow_paint.setStyle(Paint.Style.FILL);
        yellow_paint.setColor(color_success);
        canvas.drawCircle(center, center, radius, yellow_paint);

        radius_success -= 10;
        if (radius_success >= 0) {
            canvas.drawCircle(center, center, radius_success, white_paint);
            handler.sendEmptyMessageDelayed(ANIMATOR, duration_one_time);
        } else {
            white_paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(linePath, white_paint);

            handler.removeMessages(ANIMATOR);
        }


    }


    /**
     * 未开始
     *
     * @param canvas
     */
    private void drawUnStart(Canvas canvas) {
        canvas.drawArc(rectF, 90, 360, false, gray_paint);
        if (linePath != null) {
            linePath.reset();
        } else {
            linePath = new Path();
        }


        lineX1 = (center - radius * 2 / 3) + tickOffset;
        lineY1 = center;

        lineX2 = center;
        lineY2 = (center + radius / 3) + tickOffset;

        lineX3 = (center + radius * 2 / 3) - tickOffset;
        lineY3 = (center - radius / 3) - tickOffset;

        linePath.moveTo(lineX1, lineY1);
        linePath.lineTo(lineX2, lineY2);
        linePath.lineTo(lineX3, lineY3);

        if (pathMeasure != null) {
            pathMeasure.setPath(linePath, false);
        } else {
            pathMeasure = new PathMeasure(linePath, false);
        }

        canvas.drawPath(linePath, gray_paint);
//        canvas.drawLine(lineX1, lineY1, lineX2, lineY2, gray_paint);
//
//        canvas.drawLine(lineX2 - tickOffset, lineY2 + tickOffset, lineX3, lineY3, gray_paint);
    }


    private void drawLoading(Canvas canvas) {

        float angle = (float) ((currentProgress * 1.0) / 100 * 360);


        canvas.drawArc(rectF, 90, angle, false, yellow_paint);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        width = w;
        height = h;

        super.onSizeChanged(w, h, oldw, oldh);
    }
}
