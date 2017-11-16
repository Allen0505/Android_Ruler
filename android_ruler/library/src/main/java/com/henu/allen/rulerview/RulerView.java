package com.henu.allen.rulerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import Util.Utils;

/**
 * Created by licheng on 2017/11/14.
 */

public class RulerView extends View {
    private static final String TAG = RulerView.class.getSimpleName();

    private Context mContext;
    private float mShortLineHeight;//短线的高度
    private float mHighLineHeight;//长线的高度
    private float mShortLineWidth;//短线的宽度
    private float mHighLineWidth;//长线的宽度
    private int mSmallPartitionCount;//两个长线间间隔数量
    private float mIndicatorHalfWidth;//指示器的宽度的一半
    private float mIndicatorTextTopMargin;//指示器数字距离上边的距离
    private float mLineTopMargin;//短线长线的上边距
    private int mMinValue;//起止数值
    private int mMaxValue;//结束值
    private int mPartitionValue;//两个长线之间相差多少值
    private float mPartitionWidth;//长线间隔宽度
    private int mOriginValue;//设置的初始值
    private int mOriginValueSmall;//设置初始偏移量
    private int mCurrentValue;//当前值
    private int mScaleTextsize;//刻度的大小
    protected int mMinVelocity;//最小速度
    private Paint mBgPaint;//背景画笔
    private Paint mShortLinePaint;//短线画笔
    private Paint mHighLinePaint;//长线画笔
    private Paint mIndicatorTextPaint;//数字画笔
    private Paint mIndicatorViewPaint;//指示器画笔
    private int mBgColor;//背景颜色
    private int mTextColor;//数字颜色
    private int mIndicatorColor;//指示器颜色
    private int mHighLineColor;//长线颜色
    private int mShortLineColor;//短线颜色
    //向右最大偏移量
    private float mRightOffset;
    //向左最大偏移量
    private float mLeftOffset;
    //移动的距离
    private float mMoveX = 0f;

    private float mWidth, mHeight;//控件的长宽

    private Scroller mScroller;//滚动计算器
    protected VelocityTracker mVelocityTracker;//滑动速度追踪器

    private OnValueChangeListener listener;//设置监听器

    public interface OnValueChangeListener {
        void onValueChange(int intVal, float fltval);
    }
    public void setValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initAttrs(context,attrs);

    }

    /**
     * 初始化参数值
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        // 开启硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        mMaxValue = typedArray.getInteger(R.styleable.RulerView_max_value, 100);
        mMinValue = typedArray.getInteger(R.styleable.RulerView_min_value, 0);
        mScaleTextsize = typedArray.getDimensionPixelOffset(R.styleable.RulerView_scale_text_size, 44);
        mTextColor = typedArray.getColor(R.styleable.RulerView_scale_text_color, Color.WHITE);
        mIndicatorColor = typedArray.getColor(R.styleable.RulerView_tag_color,Color.WHITE);
        mBgColor = typedArray.getColor(R.styleable.RulerView_background_color, Color.YELLOW);
        mHighLineColor = typedArray.getColor(R.styleable.RulerView_heightline_color, Color.WHITE);
        mShortLineColor = typedArray.getColor(R.styleable.RulerView_shortline_color, Color.WHITE);
        mOriginValue = typedArray.getInteger(R.styleable.RulerView_start_location, 50);
        mShortLineHeight = typedArray.getDimensionPixelOffset(R.styleable.RulerView_shortline_length, 9);
        mHighLineHeight = typedArray.getDimensionPixelOffset(R.styleable.RulerView_heightline_length, 18);
        recaculate();
        invalidate();
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        mScroller = new Scroller(context);

        mMinVelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();
        initAttrs(context,attrs);
        initValue();

        initPaint();
        recaculate();
        invalidate();
    }

    private void initPaint() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColor);

        mShortLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShortLinePaint.setColor(mShortLineColor);
        mShortLinePaint.setStrokeWidth(mShortLineWidth);

        mHighLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighLinePaint.setColor(mHighLineColor);
        mHighLinePaint.setStrokeWidth(mHighLineWidth);

        mIndicatorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorTextPaint.setColor(mTextColor);
        mIndicatorTextPaint.setTextSize(mScaleTextsize);

        mIndicatorViewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorViewPaint.setColor(mIndicatorColor);
    }

    private void initValue() {

        mIndicatorHalfWidth = Utils.convertDpToPixel(mContext, 9);
        mPartitionWidth = Utils.convertDpToPixel(mContext, 140.3f);
        mHighLineWidth = Utils.convertDpToPixel(mContext, 1.67f);
        mShortLineWidth = Utils.convertDpToPixel(mContext, 1.67f);
        mLineTopMargin = Utils.convertDpToPixel(mContext, 0.33f);
        mIndicatorTextTopMargin = Utils.convertDpToPixel(mContext, 15f);

        mSmallPartitionCount = 10;
        mOriginValueSmall = 0;
        mPartitionValue = 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        drawIndicator(canvas);

        drawLinePartition(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * 绘制背景
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth, mHeight, mBgPaint);
    }

    /**
     * 绘制指示器
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mWidth / 2 - mIndicatorHalfWidth, 0);
        path.lineTo(mWidth / 2, mIndicatorHalfWidth);
        path.lineTo(mWidth / 2 + mIndicatorHalfWidth, 0);
        canvas.drawPath(path, mIndicatorViewPaint);
    }

    private float mOffset = 0f;

    private void drawLinePartition(Canvas canvas) {
        //计算半个屏幕能有多少个partition
        int halfCount = (int) (mWidth / 2 / mPartitionWidth);
        //根据偏移量计算当前应该指向什么值
        mCurrentValue = mOriginValue - (int) (mMoveX / mPartitionWidth) * mPartitionValue;
        //相对偏移量是多少, 相对偏移量就是假设不加入数字来指示位置， 范围是0 ~ mPartitionWidth的偏移量
        mOffset = mMoveX - (int) (mMoveX / mPartitionWidth) * mPartitionWidth;

        if (null != listener) {
            listener.onValueChange(mCurrentValue, - (mOffset / (mPartitionWidth / mSmallPartitionCount)));
        }

        // draw high line and  short line
        for (int i = -halfCount - 1; i <= halfCount + 1; i++) {
            int val = mCurrentValue + i * mPartitionValue;
            //只绘出范围内的图形
            if (val >= mMinValue && val <= mMaxValue) {
                //画长的刻度
                float startx = mWidth / 2 + mOffset + i * mPartitionWidth;
                if (startx > 0 && startx < mWidth) {
                    canvas.drawLine(startx, 0 + mLineTopMargin,
                            startx, 0 + mLineTopMargin + mHighLineHeight, mHighLinePaint);

                    //画刻度值
                    canvas.drawText(val + "", startx - mIndicatorTextPaint.measureText(val + "") / 2,
                            0 + mLineTopMargin + mHighLineHeight + mIndicatorTextTopMargin + Utils.calcTextHeight(mIndicatorTextPaint, val + ""), mIndicatorTextPaint);
                }

                //画短的刻度
                if (val != mMaxValue) {
                    for (int j = 1; j < mSmallPartitionCount; j++) {
                        float location=mWidth / 2 + mOffset + i * mPartitionWidth + j * mPartitionWidth / mSmallPartitionCount;
                        float start_x = location;
                        if (start_x > 0 && start_x < mWidth) {
                            canvas.drawLine(location, 0 + mLineTopMargin,
                                    location, 0 + mLineTopMargin + mShortLineHeight, mShortLinePaint);
                        }
                    }
                }

            }

        }
    }

    private boolean isActionUp = false;
    private float mLastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float xPosition = event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isActionUp = false;
                mScroller.forceFinished(true);
                if (null != animator) {
                   animator.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                float off = xPosition - mLastX;

                if ((mMoveX <= mRightOffset) && off < 0 || (mMoveX >= mLeftOffset) && off > 0) {

                } else {
                    mMoveX += off;
                    postInvalidate();//异步刷新UI
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isActionUp = true;
                f = true;
                countVelocityTracker(event);
                return false;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private ValueAnimator animator;

    private boolean isCancel = false;

    private void startAnim() {
        isCancel = false;
        float smallWidth = mPartitionWidth / mSmallPartitionCount;
        float neededMoveX;
        //四舍五入判断距离最近的刻度执行动画
        if (mMoveX < 0) {
            neededMoveX = (int) (mMoveX / smallWidth - 0.5f) * smallWidth;
        } else {
            neededMoveX = (int) (mMoveX / smallWidth + 0.5f) * smallWidth;
        }
        animator = new ValueAnimator().ofFloat(mMoveX, neededMoveX);
        animator.setDuration(500);//动画延迟500ms
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isCancel) {
                    mMoveX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCancel = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private boolean f = true;

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            float off = mScroller.getFinalX() - mScroller.getCurrX();
            off = off * functionSpeed();
            //判断左划还是右划
            if ((mMoveX <= mRightOffset) && off < 0) {
                mMoveX = mRightOffset;
            } else if ((mMoveX >= mLeftOffset) && off > 0) {
                mMoveX = mLeftOffset;
            } else {
                mMoveX += off;
                if (mScroller.isFinished()) {
                    startAnim();
                } else {
                    mLastX = mScroller.getFinalX();
                    postInvalidate();
                }
            }

        } else {
            if (isActionUp && f) {
                startAnim();
                f = false;

            }
        }
    }

    /**
     * 控制滑动速度
     * @return
     */
    private float functionSpeed() {
        return 0.2f;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000, 3000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        } else {

        }
    }

    public void setStartValue(int mStartValue) {
        this.mMinValue = mStartValue;
        recaculate();
        invalidate();
    }

    public void setEndValue(int mEndValue) {
        this.mMaxValue = mEndValue;
        recaculate();
        invalidate();
    }

    public void setPartitionValue(int mPartitionValue) {
        this.mPartitionValue = mPartitionValue;
        recaculate();
        invalidate();
    }

    public void setPartitionWidthInDP(float mPartitionWidth) {
        this.mPartitionWidth = Utils.convertDpToPixel(mContext, mPartitionWidth);
        recaculate();
        invalidate();
    }

    public void setmValue(int mValue) {
        this.mCurrentValue = mValue;
        invalidate();
    }

    public void setSmallPartitionCount(int mSmallPartitionCount) {
        this.mSmallPartitionCount = mSmallPartitionCount;
        recaculate();
        invalidate();
    }

    public void setOriginValue(int mOriginValue) {
        this.mOriginValue = mOriginValue;
        recaculate();
        invalidate();
    }

    public void setOriginValueSmall(int small) {
        this.mOriginValueSmall = small;
        recaculate();
        invalidate();
    }

    private void recaculate() {
        mMoveX = -mOriginValueSmall * (mPartitionWidth / mSmallPartitionCount);
        mRightOffset = -1 * (mMaxValue - mOriginValue) * mPartitionWidth / mPartitionValue;
        mLeftOffset = -1 * (mMinValue - mOriginValue) * mPartitionWidth / mPartitionValue;
    }
}

