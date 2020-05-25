package com.qfzn.roundcheckbox;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Checkable;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * 创建者     王威拓
 * 创建时间   2019/12/12 13:36
 * 描述
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class RoundCheckBox extends View implements Checkable {
    private static final int DEF_ANIM_DURATION = 300;
    private static final int COLOR_SELECTED = Color.parseColor("#D81B60");
    private static final int COLOR_UNSELECTED = Color.GRAY;

    private int mColorTick;
    private int mColorRound;
    private int mColorBack;
    private int mColorSelected;
    private int mColorUnSelected;
    private int mColorShadowSelected;
    private int mColorShadowUnSelected;

    private int   mWidth;
    private int   mHeight;
    private Paint mTickPaint;
    private Paint mBackPaint;
    private Paint mRoundPaint;

    private Point mCenterPoint;
    private Point[] mTickPoints;

    private int mStrokeWidth;
    private int mAnimDuration;

    private Path mTickLeftPath;
    private Path mTickRightPath;
    private Path mRoundPath;

    private boolean mChecked;
    private boolean mDraTick;
    private boolean mShowShadow;

    private float mRoundVal;
    private float mTickVal;
    private float mBackVal;

    private OnCheckedChangeListener mListener;


    public RoundCheckBox(Context context) {
        this(context,null);
    }

    public RoundCheckBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public RoundCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCheckBox, defStyleAttr, 0);
        mColorTick = typedArray.getColor(R.styleable.RoundCheckBox_rcb_colorTick, Color.WHITE);
        mColorRound = typedArray.getColor(R.styleable.RoundCheckBox_rcb_colorRound, 0);
        mColorSelected = typedArray.getColor(R.styleable.RoundCheckBox_rcb_colorSelected, COLOR_SELECTED);
        mColorUnSelected = typedArray.getColor(R.styleable.RoundCheckBox_rcb_colorUnSelected, COLOR_UNSELECTED);
        mColorShadowSelected = typedArray.getColor(R.styleable.RoundCheckBox_rcb_colorShadowSelected, COLOR_SELECTED);
        mColorShadowUnSelected = typedArray.getColor(R.styleable.RoundCheckBox_rcb_colorShadowUnSelected, COLOR_UNSELECTED);
        mAnimDuration = typedArray.getInt(R.styleable.RoundCheckBox_rcb_animDuration, DEF_ANIM_DURATION);
        mShowShadow = typedArray.getBoolean(R.styleable.RoundCheckBox_rcb_showShadow, true);
        mChecked = typedArray.getBoolean(R.styleable.RoundCheckBox_rcb_checked, false);
        typedArray.recycle();
        initConfig();
    }


    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RoundCheckBox checkBox, boolean isChecked);
    }


    private void initConfig(){
        mTickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTickPaint.setColor(mColorTick);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);

        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setStyle(Paint.Style.FILL);

        mRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRoundPaint.setColor(mColorUnSelected);
        mRoundPaint.setStyle(Paint.Style.FILL);

        mCenterPoint = new Point();
        mTickPoints = new Point[3];
        mTickPoints[0] = new Point();
        mTickPoints[1] = new Point();
        mTickPoints[2] = new Point();

        mTickLeftPath = new Path();
        mTickRightPath = new Path();
        mRoundPath = new Path();
        mRoundPath.setFillType(Path.FillType.EVEN_ODD);

        if (mShowShadow){
            setBackShadow(isChecked()?mColorShadowSelected:mColorShadowUnSelected);
        }

        mRoundVal = isChecked() ? 0f : 1.0f;
        mTickVal = isChecked() ? 1.0f : 0f;
        mColorBack = isChecked() ? mColorSelected : mColorUnSelected;
        mBackVal = 1.0f;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
                mDraTick = false;
                if (isChecked()){
                    checkedAnimation();
                }else {
                    unCheckedAnimation();
                }
            }
        });
    }


    private void checkedAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0f);
        animator.setDuration(mAnimDuration/2);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mRoundVal = (float) valueAnimator.getAnimatedValue();
                mColorBack = getHsvColor(1.0f - mRoundVal ,mColorUnSelected,mColorSelected);
                postInvalidate();
            }
        });
        animator.start();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                mDraTick = true;
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(mAnimDuration/2);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mTickVal = (float) valueAnimator.getAnimatedValue();
                        postInvalidate();
                    }
                });
                animator.start();
            }
        }, mAnimDuration/2);

        ValueAnimator backAnimator = ValueAnimator.ofFloat(1.0f,0.8f,1.0f);
        backAnimator.setDuration(mAnimDuration);
        backAnimator.setInterpolator(new LinearInterpolator());
        backAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBackVal = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        backAnimator.start();

        if (mShowShadow){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBackShadow(mColorShadowSelected);
                }
            },mAnimDuration);
        }
    }

    private void unCheckedAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1.0f);
        animator.setDuration(mAnimDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mRoundVal = (float) valueAnimator.getAnimatedValue();
                mColorBack = mColorUnSelected;
                postInvalidate();
            }
        });
        animator.start();

        ValueAnimator backAnimator = ValueAnimator.ofFloat(1.0f,0.8f,1.0f);
        backAnimator.setDuration(mAnimDuration);
        backAnimator.setInterpolator(new LinearInterpolator());
        backAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBackVal = (float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        backAnimator.start();

        if (mShowShadow){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBackShadow(mColorShadowUnSelected);
                }
            },mAnimDuration);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        int mWidth = dp2px(32);
        int mHeight = dp2px(32);

        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mCenterPoint.x = getMeasuredWidth() / 2;
        mCenterPoint.y = getMeasuredHeight() / 2;

        if (mWidth == mHeight){
            mTickPoints[0].x = Math.round((float) getMeasuredWidth() / 32 * 10);
            mTickPoints[0].y = Math.round((float) getMeasuredHeight() / 32 * 16);
            mTickPoints[1].x = Math.round((float) getMeasuredWidth() / 32 * 14);
            mTickPoints[1].y = Math.round((float) getMeasuredHeight() / 32 * 20);
            mTickPoints[2].x = Math.round((float) getMeasuredWidth() / 32 * 21);
            mTickPoints[2].y = Math.round((float) getMeasuredHeight() / 32 * 13);
            mStrokeWidth = mWidth / 16;
        }else {
            int min = Math.min(mWidth , mHeight);
            mTickPoints[0].x = Math.round((float) getMeasuredWidth() / 2 - (float) min / 32 * 6);
            mTickPoints[0].y = Math.round((float) getMeasuredHeight() / 2);
            mTickPoints[1].x = Math.round((float) getMeasuredWidth() / 2 - (float) min / 32 * 2);
            mTickPoints[1].y = Math.round((float) getMeasuredHeight() / 2 + (float) min /32 * 4);
            mTickPoints[2].x = Math.round((float) getMeasuredWidth() / 2 + (float) min / 32 * 5);
            mTickPoints[2].y = Math.round((float) getMeasuredHeight() / 2 - (float) min /32 * 3);
            mStrokeWidth = min / 16;
        }
        mTickPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mColorRound == 0){//未定义色值则用裁剪方式绘制,显示背景
            mRoundPath.reset();
            float backRadius = ((float) Math.min(mWidth, mHeight) / 32 * 11) * mBackVal;
            mRoundPath.addCircle(mCenterPoint.x, mCenterPoint.y,backRadius, Path.Direction.CW);

            float roundRadius = ((float) Math.min(mWidth, mHeight) / 32 * 11 - (float) mStrokeWidth) * mRoundVal;
            mRoundPath.addCircle(mCenterPoint.x, mCenterPoint.y,roundRadius, Path.Direction.CW);

            mBackPaint.setColor(mColorBack);
            canvas.drawPath(mRoundPath,mBackPaint);
        }else {
            mBackPaint.setColor(mColorBack);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y,
                    ((float) Math.min(mWidth, mHeight) / 32 * 11) * mBackVal, mBackPaint);

            float radius = ((float) Math.min(mWidth, mHeight) / 32 * 11 - (float) mStrokeWidth) * mRoundVal;
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, radius, mRoundPaint);
        }
        if (mDraTick && isChecked()){
            mTickLeftPath.reset();
            //drawLeftTick
            mTickLeftPath.moveTo(mTickPoints[1].x , mTickPoints[1].y);
            float stopLeftX = ((float) mTickPoints[0].x - mTickPoints[1].x) * mTickVal+ mTickPoints[1].x;
            float stopLeftY = ((float) mTickPoints[0].y - mTickPoints[1].y) * mTickVal+ mTickPoints[1].y;
            mTickLeftPath.lineTo(stopLeftX,stopLeftY);
            canvas.drawPath(mTickLeftPath, mTickPaint);

            mTickRightPath.reset();
            //drawRightTick
            mTickRightPath.moveTo(mTickPoints[1].x , mTickPoints[1].y);
            float stopRightX = ((float) mTickPoints[2].x - mTickPoints[1].x) * mTickVal+ mTickPoints[1].x;
            float stopRightY = ((float) mTickPoints[2].y - mTickPoints[1].y) * mTickVal+ mTickPoints[1].y;
            mTickRightPath.lineTo(stopRightX,stopRightY);
            canvas.drawPath(mTickRightPath, mTickPaint);
        }
    }


    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        invalidate();

        mRoundVal = isChecked() ? 0f : 1.0f;
        mTickVal = isChecked() ? 1.0f : 0f;
        mColorBack = isChecked() ? mColorSelected : mColorUnSelected;
        mBackVal = 1.0f;
        mDraTick = isChecked();

        if (mListener != null) {
            mListener.onCheckedChanged(this, mChecked);
        }
    }

    public void setChecked(boolean checked, boolean animate) {
        if (animate) {
            if (mChecked != checked){
                mChecked = checked;
                mDraTick = false;
                if (checked){
                    checkedAnimation();
                }else {
                    unCheckedAnimation();
                }
                if (mListener != null) {
                    mListener.onCheckedChanged(this, mChecked);
                }
            }
        } else {
            this.setChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        this.setChecked(!mChecked);
    }

    private void setBackShadow(@ColorInt int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(new RippleDrawable(ColorStateList.valueOf(color),
                    null,null));
        }
    }

    static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    private static int getHsvColor(float fraction, Integer startValue, Integer endValue) {
        float[] startHsv = new float[3];
        float[] endHsv = new float[3];
        float[] outHsv = new float[3];

        // 把 ARGB 转换成 HSV
        Color.colorToHSV(startValue, startHsv);
        Color.colorToHSV(endValue, endHsv);


        // 计算当前动画完成度（fraction）所对应的颜色值
        if (endHsv[0] - startHsv[0] > 180) {
            endHsv[0] -= 360;
        } else if (endHsv[0] - startHsv[0] < -180) {
            endHsv[0] += 360;
        }
        outHsv[0] = startHsv[0] + (endHsv[0] - startHsv[0]) * fraction;
        if (outHsv[0] > 360) {
            outHsv[0] -= 360;
        } else if (outHsv[0] < 0) {
            outHsv[0] += 360;
        }
        outHsv[1] = startHsv[1] + (endHsv[1] - startHsv[1]) * fraction;
        outHsv[2] = startHsv[2] + (endHsv[2] - startHsv[2]) * fraction;
        // 计算当前动画完成度（fraction）所对应的透明度
        int alpha = startValue >> 24 + (int) ((endValue >> 24 - startValue >> 24) * fraction);
        // 把 HSV 转换回 ARGB 返回
        return Color.HSVToColor(alpha, outHsv);
    }

}
