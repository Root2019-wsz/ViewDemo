package com.wuchen.viewdemo;


import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.blankj.utilcode.util.SizeUtils;

public class CustomView extends View {

    private Paint outPaint;

    private Paint innerPaint;

    private Paint mTextPaint;

    private RectF oval;

    //最大进度
    private int max = 10;
    //当前进度
    private int progress = 6;
    //文本内容
    private String text = "得分 "+ progress + "/" + max;
    //圆弧宽度
    private int roundWidth = 40;

    private int mCircleRadius = SizeUtils.dp2px(6);
    private Paint mCirclePaint;
    private float[] pos =new float[2];
    private final int[] colors = {Color.parseColor("#95ACFF"),Color.parseColor("#4379FF")};

    private int viewWidth; //宽度--控件所占区域

    private float nowPro = 0;//用于动画


    private ValueAnimator animator;


    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, context);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs, context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs, context);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private void initAttrs(AttributeSet attr, Context context) {

        outPaint = new Paint();
        outPaint.setColor(Color.parseColor("#F5F6FA"));
        outPaint.setAntiAlias(true);
        outPaint.setStyle(Paint.Style.STROKE);
        outPaint.setStrokeCap(Paint.Cap.ROUND);
        outPaint.setStrokeWidth(SizeUtils.dp2px(12));

        innerPaint = new Paint();
        innerPaint.setColor(Color.parseColor("#667CFF"));
        innerPaint.setAntiAlias(true);
        innerPaint.setStyle(Paint.Style.STROKE);
        innerPaint.setStrokeCap(Paint.Cap.ROUND);
        innerPaint.setStrokeWidth(SizeUtils.dp2px(12));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#131936"));
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setTextSize(SizeUtils.sp2px(20));

        //动画
        animator = ValueAnimator.ofFloat(0, progress);
        animator.setDuration(1800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                nowPro = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, (widthSpecSize / 2) + (int) (Math.cos(20) * (widthSpecSize / 2)));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;//得到宽度以此来计算控件所占实际大小
        //计算画布所占区域
        oval = new RectF();
        oval.left = roundWidth + getPaddingLeft();
        oval.top = roundWidth + getPaddingTop();
        oval.right = viewWidth - roundWidth - getPaddingRight();
        oval.bottom = viewWidth - roundWidth - getPaddingBottom();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Path outerPath = new Path();
        outerPath.arcTo(oval,180,180);
        canvas.drawPath(outerPath,outPaint);

        SweepGradient sweepGradient = new SweepGradient(getWidth()/2,getWidth()/2,Color.parseColor("#FFFFFF"),Color.parseColor("#0047F7"));
        innerPaint.setShader(sweepGradient);

        canvas.drawArc(oval, 180, 180 * nowPro / max, false, innerPaint); //绘制圆弧

        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL);

        PathMeasure pathMeasure = new PathMeasure(outerPath,false);
        boolean posTan = pathMeasure.getPosTan(pathMeasure.getLength() * nowPro / max, pos, null);
        canvas.drawCircle(pos[0],pos[1],mCircleRadius,mCirclePaint);

        float textWidth = mTextPaint.measureText(text);
        canvas.drawText(text, viewWidth / 2 - textWidth / 2, viewWidth / 2, mTextPaint);
    }
}