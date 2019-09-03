package com.blankj.swipepanel;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     author: blankj
 *     blog  : http://blankj.com
 *     time  : 2019/03/22
 *     desc  :
 * </pre>
 */
public class SwipePanel extends FrameLayout {

    private static final String TAG = "SwipePanel";

    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;

    @IntDef({LEFT, TOP, RIGHT, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    private static final float TRIGGER_PROGRESS = 0.95f;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;

    private float halfSize;
    private float unit;

    private int mTouchSlop;

    private Path[] mPath = new Path[4];
    private int[] mPaintColor = new int[4];
    private int[] mEdgeSizes = new int[4];
    private Drawable[] mDrawables = new Drawable[4];
    private boolean[] mIsStart = new boolean[4];
    private float[] mDown = new float[4];
    private float[] progresses = new float[4];
    private float[] preProgresses = new float[4];
    private boolean[] mIsCenter = new boolean[4];
    private boolean[] mEnabled = {true, true, true, true};

    private float mDownX;
    private float mDownY;
    private float mCurrentX;
    private float mCurrentY;
    private Rect mRect = new Rect();

    private boolean mIsEdgeStart;
    private int mStartDirection = -1;

    private int mLimit;

    private OnFullSwipeListener mListener;

    private OnProgressChangedListener mProgressListener;

    public SwipePanel(@NonNull Context context) {
        this(context, null);
    }

    public SwipePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int edgeSlop = ViewConfiguration.get(context).getScaledEdgeSlop();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        halfSize = dp2px(72);
        unit = halfSize / 16;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwipePanel);

            setLeftSwipeColor(ta.getColor(R.styleable.SwipePanel_leftSwipeColor, Color.BLACK));
            setTopSwipeColor(ta.getColor(R.styleable.SwipePanel_topSwipeColor, Color.BLACK));
            setRightSwipeColor(ta.getColor(R.styleable.SwipePanel_rightSwipeColor, Color.BLACK));
            setBottomSwipeColor(ta.getColor(R.styleable.SwipePanel_bottomSwipeColor, Color.BLACK));

            setLeftEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_leftEdgeSize, edgeSlop));
            setTopEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_topEdgeSize, edgeSlop));
            setRightEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_rightEdgeSize, edgeSlop));
            setBottomEdgeSize(ta.getDimensionPixelSize(R.styleable.SwipePanel_bottomEdgeSize, edgeSlop));

            setLeftDrawable(ta.getDrawable(R.styleable.SwipePanel_leftDrawable));
            setTopDrawable(ta.getDrawable(R.styleable.SwipePanel_topDrawable));
            setRightDrawable(ta.getDrawable(R.styleable.SwipePanel_rightDrawable));
            setBottomDrawable(ta.getDrawable(R.styleable.SwipePanel_bottomDrawable));

            setLeftCenter(ta.getBoolean(R.styleable.SwipePanel_isLeftCenter, false));
            setTopCenter(ta.getBoolean(R.styleable.SwipePanel_isTopCenter, false));
            setRightCenter(ta.getBoolean(R.styleable.SwipePanel_isRightCenter, false));
            setBottomCenter(ta.getBoolean(R.styleable.SwipePanel_isBottomCenter, false));

            setLeftEnabled(ta.getBoolean(R.styleable.SwipePanel_isLeftEnabled, true));
            setTopEnabled(ta.getBoolean(R.styleable.SwipePanel_isTopEnabled, true));
            setRightEnabled(ta.getBoolean(R.styleable.SwipePanel_isRightEnabled, true));
            setBottomEnabled(ta.getBoolean(R.styleable.SwipePanel_isBottomEnabled, true));

            ta.recycle();
        }
    }

    public void setLeftSwipeColor(int color) {
        setSwipeColor(color, LEFT);
    }

    public void setTopSwipeColor(int color) {
        setSwipeColor(color, TOP);
    }

    public void setRightSwipeColor(int color) {
        setSwipeColor(color, RIGHT);
    }

    public void setBottomSwipeColor(int color) {
        setSwipeColor(color, BOTTOM);
    }

    private void setSwipeColor(int color, int direction) {
        mPaintColor[direction] = color;
    }

    public void setLeftEdgeSize(int size) {
        mEdgeSizes[LEFT] = size;
    }

    public void setTopEdgeSize(int size) {
        mEdgeSizes[TOP] = size;
    }

    public void setRightEdgeSize(int size) {
        mEdgeSizes[RIGHT] = size;
    }

    public void setBottomEdgeSize(int size) {
        mEdgeSizes[BOTTOM] = size;
    }

    public void setLeftDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, LEFT);
    }

    public void setTopDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, TOP);
    }

    public void setRightDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, RIGHT);
    }

    public void setBottomDrawable(@DrawableRes int drawableId) {
        setDrawable(drawableId, BOTTOM);
    }

    private void setDrawable(int drawableId, int direction) {
        mDrawables[direction] = getDrawable(getContext(), drawableId);
    }

    public void setLeftDrawable(Drawable drawable) {
        setDrawable(drawable, LEFT);
    }

    public void setTopDrawable(Drawable drawable) {
        setDrawable(drawable, TOP);
    }

    public void setRightDrawable(Drawable drawable) {
        setDrawable(drawable, RIGHT);
    }

    public void setBottomDrawable(Drawable drawable) {
        setDrawable(drawable, BOTTOM);
    }

    private void setDrawable(Drawable drawable, int direction) {
        mDrawables[direction] = drawable;
    }

    public Drawable getLeftDrawable() {
        return mDrawables[LEFT];
    }

    public Drawable getTopDrawable() {
        return mDrawables[TOP];
    }

    public Drawable getRightDrawable() {
        return mDrawables[RIGHT];
    }

    public Drawable getBottomDrawable() {
        return mDrawables[BOTTOM];
    }

    public void setLeftCenter(boolean isCenter) {
        setCenter(isCenter, LEFT);
    }

    public void setTopCenter(boolean isCenter) {
        setCenter(isCenter, TOP);
    }

    public void setRightCenter(boolean isCenter) {
        setCenter(isCenter, RIGHT);
    }

    public void setBottomCenter(boolean isCenter) {
        setCenter(isCenter, BOTTOM);
    }

    private void setCenter(boolean isCenter, int direction) {
        mIsCenter[direction] = isCenter;
    }

    public void setLeftEnabled(boolean enabled) {
        setEnabled(enabled, LEFT);
    }

    public void setTopEnabled(boolean enabled) {
        setEnabled(enabled, TOP);
    }

    public void setRightEnabled(boolean enabled) {
        setEnabled(enabled, RIGHT);
    }

    public void setBottomEnabled(boolean enabled) {
        setEnabled(enabled, BOTTOM);
    }

    private void setEnabled(boolean enabled, int direction) {
        mEnabled[direction] = enabled;
    }

    public void wrapView(@NonNull View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            int i = group.indexOfChild(view);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            group.removeViewAt(i);
            group.addView(this, i, layoutParams);
            addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    public void setOnFullSwipeListener(OnFullSwipeListener listener) {
        mListener = listener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        mProgressListener = listener;
    }

    public boolean isOpen(int direction) {
        return progresses[direction] >= TRIGGER_PROGRESS;
    }

    public void close() {
        close(true);
    }

    public void close(@Direction int direction) {
        close(direction, true);
    }

    public void close(boolean isAnim) {
        if (isAnim) {
            animClose();
        } else {
            progresses[LEFT] = 0;
            progresses[TOP] = 0;
            progresses[RIGHT] = 0;
            progresses[BOTTOM] = 0;
            postInvalidate();
        }
    }

    public void close(@Direction int direction, boolean isAnim) {
        if (isAnim) {
            animClose(direction);
        } else {
            progresses[direction] = 0;
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mLimit = Math.min(mWidth, mHeight) / 3;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawPath(canvas);
    }

    private void drawPath(Canvas canvas) {
        drawPath(canvas, LEFT);
        drawPath(canvas, TOP);
        drawPath(canvas, RIGHT);
        drawPath(canvas, BOTTOM);
    }

    private void drawPath(Canvas canvas, int direction) {
        if (mPath[direction] == null || progresses[direction] <= 0) return;
        updatePaint(direction);
        canvas.drawPath(getPath(direction), mPaint);
        drawIcon(canvas, direction);
    }

    private Path getPath(int direction) {
        if (preProgresses[direction] != progresses[direction]) {
            mPath[direction].reset();
            float edge, pivot = mDown[direction];
            int mark;
            if (direction == LEFT) {
                edge = 0;
                mark = 1;
            } else if (direction == TOP) {
                edge = 0;
                mark = 1;
            } else if (direction == RIGHT) {
                edge = mWidth;
                mark = -1;
            } else {
                edge = mHeight;
                mark = -1;
            }
            if (direction == LEFT || direction == RIGHT) {
                curPathX = edge;
                curPathY = pivot - halfSize;
            } else {
                curPathX = pivot - halfSize;
                curPathY = edge;
            }
            mPath[direction].moveTo(curPathX, curPathY);

            quad(edge, pivot - halfSize, direction);
            quad(edge + progresses[direction] * unit * mark, pivot - halfSize + 5 * unit, direction);// 1, 5
            quad(edge + progresses[direction] * 10 * unit * mark, pivot, direction);// 10, 16
            quad(edge + progresses[direction] * unit * mark, pivot + halfSize - 5 * unit, direction);
            quad(edge, pivot + halfSize, direction);
            quad(edge, pivot + halfSize, direction);
        }
        return mPath[direction];
    }

    private void drawIcon(Canvas canvas, int direction) {
        if (mDrawables[direction] == null) return;
        int dWidth = mDrawables[direction].getIntrinsicWidth();
        int dHeight = mDrawables[direction].getIntrinsicHeight();
        int fitSize = (int) (progresses[direction] * 5 * unit);

        int width, height, deltaWidth = 0, deltaHeight = 0;

        if (dWidth >= dHeight) {
            width = fitSize;
            height = width * dHeight / dWidth;
            deltaHeight = fitSize - height;
        } else {
            height = fitSize;
            width = height * dWidth / dHeight;
            deltaWidth = fitSize - width;
        }

        if (direction == LEFT) {
            mRect.left = (int) (0 + progresses[direction] * unit * 1 + deltaWidth / 2 * 1);
            mRect.top = (int) (mDown[LEFT] - height / 2);
            mRect.right = mRect.left + width;
            mRect.bottom = mRect.top + height;
        } else if (direction == RIGHT) {
            mRect.right = (int) (mWidth + progresses[direction] * unit * -1 + deltaWidth / 2 * -1);
            mRect.top = (int) (mDown[RIGHT] - height / 2f);
            mRect.left = mRect.right - width;
            mRect.bottom = mRect.top + height;
        } else if (direction == TOP) {
            mRect.left = (int) (mDown[TOP] - width / 2);
            mRect.top = (int) (0 + progresses[direction] * unit * 1 + deltaHeight / 2 * 1);
            mRect.right = mRect.left + width;
            mRect.bottom = mRect.top + height;
        } else {
            mRect.left = (int) (mDown[BOTTOM] - width / 2);
            mRect.bottom = (int) (mHeight + progresses[direction] * unit * -1 + deltaHeight / 2 * -1);
            mRect.top = mRect.bottom - height;
            mRect.right = mRect.left + width;
        }
        mDrawables[direction].setBounds(mRect);
        mDrawables[direction].draw(canvas);
    }

    private void quad(float pathX, float pathY, int direction) {
        float preX = curPathX;
        float preY = curPathY;
        if (direction == LEFT || direction == RIGHT) {
            curPathX = pathX;
            curPathY = pathY;
        } else {
            //noinspection SuspiciousNameCombination
            curPathX = pathY;
            //noinspection SuspiciousNameCombination
            curPathY = pathX;
        }
        mPath[direction].quadTo(preX, preY, (preX + curPathX) / 2, (preY + curPathY) / 2);
    }

    private float curPathX;
    private float curPathY;

    private void updatePaint(int direction) {
        mPaint.setColor(mPaintColor[direction]);
        float alphaProgress = progresses[direction];
        if (alphaProgress < 0.25f) {
            alphaProgress = 0.25f;
        } else if (alphaProgress > 0.75f) {
            alphaProgress = 0.75f;
        }
        mPaint.setAlpha((int) (alphaProgress * 255));
    }

    private void animClose() {
        animClose(LEFT);
        animClose(TOP);
        animClose(RIGHT);
        animClose(BOTTOM);
    }

    private void animClose(final int direction) {
        if (progresses[direction] > 0) {
            final ValueAnimator anim = ValueAnimator.ofFloat(progresses[direction], 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progresses[direction] = (float) anim.getAnimatedValue();
                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(direction, progresses[direction], false);
                    }
                    postInvalidate();
                }
            });
            anim.setDuration(100).start();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mDownX = ev.getX();
            mDownY = ev.getY();
            mIsStart[LEFT] = mEnabled[LEFT] && mDrawables[LEFT] != null && !isOpen(LEFT) && mDownX <= mEdgeSizes[LEFT];
            mIsStart[TOP] = mEnabled[TOP] && mDrawables[TOP] != null && !isOpen(TOP) && mDownY <= mEdgeSizes[TOP];
            mIsStart[RIGHT] = mEnabled[RIGHT] && mDrawables[RIGHT] != null && !isOpen(RIGHT) && mDownX >= getWidth() - mEdgeSizes[RIGHT];
            mIsStart[BOTTOM] = mEnabled[BOTTOM] && mDrawables[BOTTOM] != null && !isOpen(BOTTOM) && mDownY >= getHeight() - mEdgeSizes[BOTTOM];
            mIsEdgeStart = mIsStart[LEFT] || mIsStart[TOP] || mIsStart[RIGHT] || mIsStart[BOTTOM];
            if (mIsEdgeStart) {
                mStartDirection = -1;
            }
            return true;
        }
        if (mIsEdgeStart) {
            if (action == MotionEvent.ACTION_MOVE) {
                mCurrentX = ev.getX();
                mCurrentY = ev.getY();
                if (mStartDirection == -1) {
                    float deltaX = mCurrentX - mDownX;
                    float deltaY = mCurrentY - mDownY;
                    float disX = Math.abs(deltaX);
                    float disY = Math.abs(deltaY);
                    if (disX > mTouchSlop || disY > mTouchSlop) {
                        if (disX >= disY) {
                            if (mIsStart[LEFT] && deltaX > 0) {
                                decideDirection(LEFT);
                            } else if (mIsStart[RIGHT] && deltaX < 0) {
                                decideDirection(RIGHT);
                            }
                        } else {
                            if (mIsStart[TOP] && deltaY > 0) {
                                decideDirection(TOP);
                            } else if (mIsStart[BOTTOM] && deltaY < 0) {
                                decideDirection(BOTTOM);
                            }
                        }
                    }
                }
                if (mStartDirection != -1) {
                    float preProgress = preProgresses[mStartDirection];
                    preProgresses[mStartDirection] = progresses[mStartDirection];
                    progresses[mStartDirection] = calculateProgress();
                    if (Math.abs(preProgress - progresses[mStartDirection]) > 0.01) {
                        postInvalidate();
                        if (mProgressListener != null) {
                            mProgressListener.onProgressChanged(mStartDirection, progresses[mStartDirection], true);
                        }
                    } else {
                        preProgresses[mStartDirection] = preProgress;
                    }
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                if (mStartDirection != -1) {
                    mCurrentX = ev.getX();
                    mCurrentY = ev.getY();
                    progresses[mStartDirection] = calculateProgress();
                    if (isOpen(mStartDirection)) {
                        if (mListener != null) {
                            mListener.onFullSwipe(mStartDirection);
                        }
                    } else {
                        close(mStartDirection, true);
                    }
                }
            }
        }
        return true;
    }

    private void decideDirection(int direction) {
        if (direction == LEFT || direction == RIGHT) {
            if (mIsCenter[direction]) {
                mDown[direction] = mHeight / 2f;
            } else {
                if (mDownY < halfSize) {
                    mDown[direction] = halfSize;
                } else if (mDownY >= mHeight - halfSize) {
                    mDown[direction] = mHeight - halfSize;
                } else {
                    mDown[direction] = mDownY;
                }
            }
        } else {
            if (mIsCenter[direction]) {
                mDown[direction] = mWidth / 2f;
            } else {
                if (mDownX < halfSize) {
                    mDown[direction] = halfSize;
                } else if (mDownX >= mWidth - halfSize) {
                    mDown[direction] = mWidth - halfSize;
                } else {
                    mDown[direction] = mDownX;
                }
            }
        }
        mStartDirection = direction;
        if (mPath[direction] == null) {
            mPath[direction] = new Path();
        }
        preProgresses[direction] = 0;
        cancelChildViewTouch();
        requestDisallowInterceptTouchEvent(true);
    }

    private float calculateProgress() {
        if (mStartDirection == LEFT) {
            float deltaX = mCurrentX - mDownX;
            if (deltaX <= 0) return 0;
            return Math.min(deltaX / mLimit, 1);
        } else if (mStartDirection == TOP) {
            float deltaY = mCurrentY - mDownY;
            if (deltaY <= 0) return 0;
            return Math.min(deltaY / mLimit, 1);
        } else if (mStartDirection == RIGHT) {
            float deltaX = mCurrentX - mDownX;
            if (deltaX >= 0) return 0;
            return Math.min(-deltaX / mLimit, 1);
        } else {
            float deltaY = mCurrentY - mDownY;
            if (deltaY >= 0) return 0;
            return Math.min(-deltaY / mLimit, 1);
        }
    }

    private void cancelChildViewTouch() {
        final long now = SystemClock.uptimeMillis();
        final MotionEvent cancelEvent = MotionEvent.obtain(now, now,
                MotionEvent.ACTION_CANCEL, 0.0f, 0.0f, 0);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).dispatchTouchEvent(cancelEvent);
        }
        cancelEvent.recycle();
    }

    private static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static final Object LOCK = new Object();

    private static TypedValue sTempValue;

    private static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getDrawable(id);
        } else if (Build.VERSION.SDK_INT >= 16) {
            return context.getResources().getDrawable(id);
        } else {
            final int resolvedId;
            synchronized (LOCK) {
                if (sTempValue == null) {
                    sTempValue = new TypedValue();
                }
                context.getResources().getValue(id, sTempValue, true);
                resolvedId = sTempValue.resourceId;
            }
            return context.getResources().getDrawable(resolvedId);
        }
    }

    public interface OnFullSwipeListener {
        void onFullSwipe(@Direction int direction);
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(@Direction int direction, float progress, boolean isTouch);
    }
}
