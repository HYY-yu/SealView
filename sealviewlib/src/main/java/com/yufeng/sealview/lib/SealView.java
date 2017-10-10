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

  private int viewWidth;
    private int viewHeight;

    private Paint paint;
    private Path pathString;
    private RectF rectF;

    // -- 定制
    private int radius;
    private int color;
    private int rotate;

    private float firstTextSize;
    private float secondTextSize;
    private String textFirst;
    private String textSecond;

    private int bigOvalWidth = 4;  // 大圆宽度

    private int smallOvalWidth = 1; // 小圆宽度

    private int bigSmallMargin = 10; //大圆小圆的间距

    private float starWidthMultiple = 1; //星星大小
    private float firstTextMarginMultiple = 1; //字体位置
    private float secondTextMarginMultiple = 1; 

    private Typeface firstTextTypeface;
    private Typeface secondTypeface;

    private String firstTextPath;
    private String secondTextPath;

    private boolean showFirstBold;
    private boolean showSecondBold;
    private boolean showSecondUnderline;
    private boolean showSecondDeleteline;

    private int secondTextAlign = 1;

    float tempR;

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
        firstTextSize = array.getDimensionPixelSize(R.styleable.SealView_seal_textSize, 18);
        secondTextSize = firstTextSize - 4;
        textFirst = array.getString(R.styleable.SealView_seal_textFirst);
        textSecond = array.getString(R.styleable.SealView_seal_textSecond);
        rotate = array.getInt(R.styleable.SealView_seal_rotate, 0);

        array.recycle();

        paint = new Paint();
        pathString = new Path();
        rectF = new RectF();

        tempR = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                300, context.getResources().getDisplayMetrics()) / 3f;

    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;

        //绑定radius和ViewWidth
        radius = viewWidth / 3;
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
        paint.setStrokeWidth(bigOvalWidth * (radius / tempR));
        canvas.drawCircle(centerX, centerY, radius, paint);

        //绘制小圆
        if (smallOvalWidth > 0) {
            paint.setStrokeWidth(smallOvalWidth * (radius / tempR));
            canvas.drawCircle(centerX, centerY, radius - (bigSmallMargin * (radius / tempR)), paint);
        }

        //绘制五角星
        float width = radius * 3 * starWidthMultiple;


        float outR = width / 2 / 5;
        float inR = outR * sin(18) / sin(180 - 36 - 18);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Path path = getCompletePath(outR, inR);

        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.rotate(-18);
        canvas.drawPath(path, paint);
        canvas.restore();

        paint.setStyle(Paint.Style.FILL);

        float sweepAngle;
        float startAngle;
        int rString = (int) (radius * 0.8 * firstTextMarginMultiple);
        rectF.set(centerX - rString, centerY - rString, centerX + rString, centerY + rString);

        //绘制上半部分字符
        if (!TextUtils.isEmpty(textFirst)) {
            paint.setTextSize(firstTextSize * (radius / tempR));
            paint.setFakeBoldText(showFirstBold);
//            paint.setUnderlineText(showFirstUnderline);
//            paint.setStrikeThruText(showFirstDeleteline);

            paint.setTypeface(Typeface.DEFAULT);
            if (!TextUtils.isEmpty(firstTextPath)) {
                firstTextTypeface = getTypefaceFromPath(firstTextPath);
            }

            if (firstTextTypeface != null) {
                paint.setTypeface(firstTextTypeface);
            }

            float textW = paint.measureText(textFirst);

            pathString.reset();
            sweepAngle = 180 * textW / (3.1415f * rString);
            startAngle = 90 - sweepAngle / 2;

            pathString.arcTo(rectF, 180 + startAngle, sweepAngle, true);

            canvas.drawTextOnPath(textFirst, pathString, 0, 0, paint);
        }

        //绘制下半部分字符
        if (!TextUtils.isEmpty(textSecond)) {
            paint.setTextSize(secondTextSize * (radius / tempR));
            paint.setFakeBoldText(showSecondBold);
            paint.setUnderlineText(showSecondUnderline);
            paint.setStrikeThruText(showSecondDeleteline);

            paint.setTypeface(Typeface.DEFAULT);
            if (!TextUtils.isEmpty(secondTextPath)) {
                secondTypeface = getTypefaceFromPath(secondTextPath);
            }

            if (secondTypeface != null) {
                paint.setTypeface(secondTypeface);
            }

            float textWidth = paint.measureText(textSecond);

            if (secondTextAlign == 1) {
                //弧
                rString = (int) (radius * 0.8 * secondTextMarginMultiple);
                rectF.set(centerX - rString, centerY - rString, centerX + rString, centerY + rString);

                sweepAngle = 180 * textWidth / (3.1415f * rString);
                startAngle = 90 + sweepAngle / 2;
                pathString.reset();
                pathString.arcTo(rectF, startAngle, -sweepAngle, true);

                canvas.drawTextOnPath(textSecond, pathString, 0, 0, paint);
            } else {
                //横线
                paint.setTextAlign(Paint.Align.CENTER);
                Paint.FontMetrics fm = paint.getFontMetrics();
                float textHeight = (int) Math.ceil(fm.descent - fm.ascent);

                float textAlign = (float) (radius * 0.5 * secondTextMarginMultiple);

                canvas.drawText(textSecond, centerX, centerY + textAlign + textHeight * 0.5f, paint);

                paint.setTextAlign(Paint.Align.LEFT);

            }
        }

        canvas.restore();

    }

    public static Typeface getTypefaceFromPath(String textTypefacePath) {
        File file = new File(textTypefacePath);

        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        return Typeface.createFromFile(file);
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

    public String getFirstTextPath() {
        return firstTextPath;
    }

    public void setFirstTextPath(String firstTextPath) {
        this.firstTextPath = firstTextPath;
    }

    public String getSecondTextPath() {
        return secondTextPath;
    }

    public void setSecondTextPath(String secondTextPath) {
        this.secondTextPath = secondTextPath;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getFirstTextSize() {
        return firstTextSize;
    }

    public void setFirstTextSize(int textSize) {
        //SP
        float newSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getContext().getResources().getDisplayMetrics());
        this.firstTextSize = newSize;
    }

    public String getTextFirst() {
        return textFirst;
    }

    public void setTextFirst(String textFirst) {
        this.textFirst = textFirst;
    }

    public String getTextSecond() {
        return textSecond;
    }

    public void setTextSecond(String textSecond) {
        this.textSecond = textSecond;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public void setStarWidthMultiple(float starWidthMultiple) {
        this.starWidthMultiple = starWidthMultiple;
    }

    public float getStarWidthMultiple() {
        return starWidthMultiple;
    }

    public void setFirstTextTypeface(Typeface firstTextTypeface) {
        this.firstTextTypeface = firstTextTypeface;
    }

    public Typeface getFirstTextTypeface() {
        return firstTextTypeface;
    }

    public void setSecondTypeface(Typeface secondTypeface) {
        this.secondTypeface = secondTypeface;
    }

    public Typeface getSecondTypeface() {
        return secondTypeface;
    }

    public float getSecondTextMarginMultiple() {
        return secondTextMarginMultiple;
    }

    public void setSecondTextMarginMultiple(float secondTextMarginMultiple) {
        this.secondTextMarginMultiple = secondTextMarginMultiple;
    }

    public int getSecondTextAlign() {
        return secondTextAlign;
    }

    public void setSecondTextAlign(int secondTextAlign) {
        this.secondTextAlign = secondTextAlign;
    }

    public float getSecondTextSize() {
        return secondTextSize;
    }

    public void setSecondTextSize(float secondTextSize) {
        float newSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, secondTextSize, getContext().getResources().getDisplayMetrics());
        this.secondTextSize = newSize;
    }

    public boolean isShowSecondBold() {
        return showSecondBold;
    }

    public void setShowSecondBold(boolean showSecondBold) {
        this.showSecondBold = showSecondBold;
    }

    public boolean isShowSecondUnderline() {
        return showSecondUnderline;
    }

    public void setShowSecondUnderline(boolean showSecondUnderline) {
        this.showSecondUnderline = showSecondUnderline;
    }

    public boolean isShowSecondDeleteline() {
        return showSecondDeleteline;
    }

    public void setShowSecondDeleteline(boolean showSecondDeleteline) {
        this.showSecondDeleteline = showSecondDeleteline;
    }

    public boolean getShowFirstBold() {
        return showFirstBold;
    }

    public void setShowFirstBold(boolean showFirstBold) {
        this.showFirstBold = showFirstBold;
    }

    public float getFirstTextMarginMultiple() {
        return firstTextMarginMultiple;
    }

    public void setFirstTextMarginMultiple(float firstTextMarginMultiple) {
        this.firstTextMarginMultiple = firstTextMarginMultiple;
    }

    public int getBigSmallMargin() {
        return bigSmallMargin;
    }

    public void setBigSmallMargin(int bigSmallMargin) {
        this.bigSmallMargin = bigSmallMargin;
    }

    public int getBigOvalWidth() {
        return bigOvalWidth;
    }

    public void setBigOvalWidth(int bigOvalWidth) {
        this.bigOvalWidth = bigOvalWidth;
    }

    public int getSmallOvalWidth() {
        return smallOvalWidth;
    }

    public void setSmallOvalWidth(int smallOvalWidth) {
        this.smallOvalWidth = smallOvalWidth;
    }
}
