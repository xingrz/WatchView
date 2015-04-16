package me.xingrz.watchview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class WatchView extends View implements Runnable {

    private static final double ROUND = 2d * Math.PI;

    private static final double QUARTER = 1d / 4d;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Calendar calendar = new GregorianCalendar();

    private final Paint paintSecond = new Paint();
    private final Paint paintMinute = new Paint();
    private final Paint paintHour = new Paint();

    public WatchView(Context context) {
        this(context, null);
    }

    public WatchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupPaint(paintSecond, Color.RED, 2);
        setupPaint(paintMinute, Color.BLACK, 3);
        setupPaint(paintHour, Color.BLACK, 5);
    }

    private void setupPaint(Paint paint, int color, float widthDp) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(widthDp * getResources().getDisplayMetrics().density);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void run() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        invalidate();
        handler.postDelayed(this, 1000 / 60);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 圆心的 x, y 坐标
        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2;

        // 半径
        int radius = Math.min(x, y);

        // 为了让秒针连续转动，所以秒针的角度肯定不是从「秒」这个整数里来
        // 刚好秒针走一圈是 1 分钟，那么，就用分乘以「一圈（2π）」就是秒针要走的弧度数了

        float millis = calendar.get(Calendar.MILLISECOND) / 1000f;
        float second = (calendar.get(Calendar.SECOND) + millis) / 60f;
        float minute = (calendar.get(Calendar.MINUTE) + second) / 60f;
        float hour = (calendar.get(Calendar.HOUR) + minute) / 12f;

        drawHand(canvas, paintHour, x, y, radius * 0.5f, hour);
        drawHand(canvas, paintMinute, x, y, radius * 0.7f, minute);
        drawHand(canvas, paintSecond, x, y, radius * 0.9f, second);
    }

    private void drawHand(Canvas canvas, Paint paint, float x, float y, float length, float round) {
        // 三角函数的坐标轴是以 3 点方向为 0 的，所以记得要减去四分之一个圆周哦
        double radians = (round - QUARTER) * ROUND;

        canvas.drawLine(
                x,
                y,
                x + (float) Math.cos(radians) * length,
                y + (float) Math.sin(radians) * length,
                paint);
    }

}
