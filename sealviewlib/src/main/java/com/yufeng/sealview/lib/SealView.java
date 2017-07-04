package com.yufeng.sealview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yufeng on 17-7-4.
 */

public class SealView extends View {

    private int radius;
    private int color;

    private int textSize;
    private String textFirst;
    private String textSecond;

    private int viewWidth;
    private int viewHeight;

    private Paint paint;
    private Path pathString;
    private RectF rectF;

    private int rotate;

    public SealView(Context context) {
        this(context, null);
    }

    public SealView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SealView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SealView);
        radius = array.getDimensionPixelOffset(R.styleable.SealView_seal_radius, 90);
        color = array.getColor(R.styleable.SealView_seal_color, Color.RED);
        textSize = array.getDimensionPixelSize(R.styleable.SealView_seal_textSize, 18);
        textFirst = array.getString(R.styleable.SealView_seal_textFirst);
        textSecond = array.getString(R.styleable.SealView_seal_textSecond);
        rotate = array.getInt(R.styleable.SealView_seal_rotate, 0);

        array.recycle();

        paint = new Paint();
        pathString = new Path();
        rectF = new RectF();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = viewWidth / 2;
        int centerY = viewHeight / 2;

        //绘制时间字符串
        canvas.save();
        canvas.rotate(rotate, centerX, centerY);
        paint.setAntiAlias(true);
        paint.setColor(color);

        //绘制外围大圆
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        canvas.drawCircle(centerX, centerY, radius, paint);

        //绘制小圆
        paint.setStrokeWidth(1);
        canvas.drawCircle(centerX, centerY, radius - 10, paint);

        //绘制五角星
        int width = radius * 3;

        float outR = width / 2 / 5;
        float inR = outR * sin(18) / sin(180 - 36 - 18);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Path path = getCompletePath(outR, inR);

        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(-18);
        canvas.drawPath(path, paint);
        canvas.restore();

        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);

        float sweepAngle;
        float startAngle;
        int rString = radius - 30;

        //绘制上半部分字符
        if (!TextUtils.isEmpty(textFirst)) {
            float textW = paint.measureText(textFirst);

            rectF.set(centerX - rString, centerY - rString, centerX + rString, centerY + rString);
            pathString.reset();
            sweepAngle = 180 * textW / (3.1415f * rString);
            startAngle = 90 - sweepAngle / 2;

            pathString.arcTo(rectF, 180 + startAngle, sweepAngle, true);

            canvas.drawTextOnPath(textFirst, pathString, 0, 0, paint);
        }

        //绘制下半部分字符
        if (!TextUtils.isEmpty(textSecond)) {
            paint.setTextSize(textSize - 2);
            float textWidth = paint.measureText(textSecond);

            sweepAngle = 180 * textWidth / (3.1415f * rString);
            startAngle = 90 + sweepAngle / 2;
            pathString.reset();
            pathString.arcTo(rectF, startAngle, -sweepAngle, true);

            canvas.drawTextOnPath(textSecond, pathString, 0, 0, paint);
        }

        canvas.restore();

    }

    private Path getCompletePath(float outR, float inR) {
        Path path = new Path();

        path.moveTo(outR * cos(72 * 0), outR * sin(72 * 0));

        path.moveTo(outR * cos(72 * 0), outR * sin(72 * 0));
        path.lineTo(inR * cos(72 * 0 + 36), inR * sin(72 * 0 + 36));
        path.lineTo(outR * cos(72 * 1), outR * sin(72 * 1));
        path.lineTo(inR * cos(72 * 1 + 36), inR * sin(72 * 1 + 36));
        path.lineTo(outR * cos(72 * 2), outR * sin(72 * 2));
        path.lineTo(inR * cos(72 * 2 + 36), inR * sin(72 * 2 + 36));
        path.lineTo(outR * cos(72 * 3), outR * sin(72 * 3));
        path.lineTo(inR * cos(72 * 3 + 36), inR * sin(72 * 3 + 36));
        path.lineTo(outR * cos(72 * 4), outR * sin(72 * 4));
        path.lineTo(inR * cos(72 * 4 + 36), inR * sin(72 * 4 + 36));
        path.close();
        return path;
    }

    static float cos(int num) {
        return (float) Math.cos(num * Math.PI / 180);
    }

    static float sin(int num) {
        return (float) Math.sin(num * Math.PI / 180);
    }
}
